package au.org.ala.userdetails

import au.org.ala.auth.UpdatePasswordCommand

/**
 * Controller that handles the interactions with general public.
 * Supports:
 *
 * 1. Account creation
 * 2. Account editing
 * 3. Password reset
 * 4. Account activation
 */
class RegistrationController {

    def simpleCaptchaService
    def emailService
    def authService
    def passwordService
    def userService

    def index() {
        redirect(action: 'createAccount')
    }

    def createAccount() {
        [
                stateMap: userService.retrieveMapOfStates(),
                userTypeMap: userService.retrieveArrayOfUserTypes()
        ]
    }

    def editAccount() {
        def user = userService.currentUser
        render(view: 'createAccount', model: [
                edit: true,
                user: user,
                props: user?.propsAsMap(),
                stateMap: userService.retrieveMapOfStates(),
                userTypeMap: userService.retrieveArrayOfUserTypes()
        ])
    }

    def passwordReset() {
        User user = User.get(params.userId?.toLong())
        if (!user) {
            render(view: 'accountError', model: [msg: "User not found with ID ${params.userId}"])
        } else if (user.tempAuthKey == params.authKey) {
            //keys match, so lets reset password
            render(view: 'passwordReset', model: [user: user, authKey: params.authKey])
        } else {
            render(view: 'authKeyExpired')
        }
    }

    def updatePassword(UpdatePasswordCommand cmd) {

        User user = User.get(cmd.userId)
        if (cmd.hasErrors()) {
            render(view: 'passwordReset', model: [user: user, authKey: cmd.authKey, errors:cmd.errors, passwordMatchFail: true])
        }
        else {
            withForm {
                if (user.tempAuthKey == params.authKey) {
                    //update the password
                    def success = passwordService.resetPassword(user, cmd.password)
                    if (success) {
                        userService.clearTempAuthKey(user)
                        redirect(controller: 'registration', action: 'passwordResetSuccess')
                        log.info("Password successfully reset for user: " + cmd.userId)
                    } else {
                        render(view: 'accountError', model: [msg: "Failed to reset password"])
                    }
                } else {
                    log.error "Password was not reset as AUTH_KEY did not match -- ${user.tempAuthKey} vs ${cmd.authKey}"
                    render(view: 'accountError', model: [msg: "Password was not reset as AUTH_KEY did not match"])
                }
            }
            .invalidToken {
                redirect(action: 'duplicateSubmit', model: [msg: ""])
            }
        }

    }

    /**
     * Used only to display the view.
     * This is required given that the view is not rendered directly by #disableAccount but rather a chain
     * of redirects:  userdetails logout, cas logout and finally this view
     */
    def accountDisabled() {
    }

    /** Displayed as a result of a password update with a duplicate form submission. */
    def duplicateSubmit() {
        [serverUrl: grailsApplication.config.grails.serverURL + '/myprofile']
    }

    def passwordResetSuccess() {
        [serverUrl: grailsApplication.config.grails.serverURL + '/myprofile']
    }

    def startPasswordReset() {
        //check for human
        boolean captchaValid = simpleCaptchaService.validateCaptcha(params.captcha)
        if (!captchaValid) {
            //send password reset link
            render(view: 'forgottenPassword', model: [email: params.email, captchaInvalid: true])
        } else {
            log.info("Starting password reset for email address: " + params.email)
            def user = User.findByEmail(params.email)
            if (user) {
                try {
                    userService.resetAndSendTemporaryPassword(user, null, null, null)
                    [:]
                } catch (Exception e) {
                    log.error("Problem starting password reset for email address: " + params.email)
                    log.error(e.getMessage(), e)
                    render(view: 'accountError', model: [msg: e.getMessage()])
                }
            } else {
                //send password reset link
                render(view: 'forgottenPassword', model: [email: params.email, captchaInvalid: false, invalidEmail: true])
            }
        }
    }

    def disableAccount() {
        def user = userService.currentUser

        log.debug("Disabling account for " + user)
        if (user) {
            def success = userService.disableUser(user)

            if (success) {
                redirect(controller: 'logout', action: 'logout', params: [casUrl: grailsApplication.config.security.cas.logoutUrl,
                                                                          appUrl: grailsApplication.config.grails.serverURL + '/registration/accountDisabled'])
            } else {
                render(view: "accountError", model: [msg: "Failed to disable user profile - unknown error"])
            }
        } else {
            render(view: "accountError", model: [msg: "The current user details could not be found"])
        }
    }

    def update() {
        def user = userService.currentUser
        log.debug("Updating account for " + user)

        if (user) {
            if (params.email != user.email) {
                // email address has changed, and username and email address must be kept in sync
                params.userName = params.email
            }

            def success = userService.updateUser(user, params)
            if (success) {
                redirect(controller: 'profile')
            } else {
                render(view: "accountError", model: [msg: "Failed to update user profile - unknown error"])
            }
        } else {
            render(view: "accountError", model: [msg: "The current user details could not be found"])
        }
    }

    def register() {
        withForm {
            //create user account...
            if (!params.email || userService.isEmailRegistered(params.email)) {
                def inactiveUser = !userService.isActive(params.email)
                render(view: 'createAccount', model: [edit: false, user: params, props: params, alreadyRegistered: true, inactiveUser: inactiveUser, stateMap:userService.retrieveMapOfStates(), userTypeMap: userService.retrieveArrayOfUserTypes()])
            } else {

                try {
                    //does a user with the supplied email address exist
                    def user = userService.registerUser(params)

                    //store the password
                    def success = passwordService.resetPassword(user, params.password)
                    if (success) {
                        //store the password
                        emailService.sendAccountActivation(user, user.tempAuthKey)
                        redirect(action: 'accountCreated', id: user.id)
                    } else {
                        render(view: "accountError", model: [msg: "Failed to reset password"])
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e)
                    render(view: "accountError", model: [msg: e.getMessage()])
                }
            }
        }
    }

    def accountCreated() {
        def user = User.get(params.id)
        render(view: 'accountCreated', model: [user: user, stateMap:userService.retrieveMapOfStates(), userTypeMap: userService.retrieveArrayOfUserTypes()])
    }

    def forgottenPassword() {}

    def activateAccount() {
        def user = User.get(params.userId)
        //check the activation key
        if (user.tempAuthKey == params.authKey) {
            userService.activateAccount(user)
            render(view: 'accountActivatedSuccessful', model: [user: user, stateMap:userService.retrieveMapOfStates(), userTypeMap: userService.retrieveArrayOfUserTypes()])
        } else {
            log.error('Auth keys did not match for user : ' + params.userId + ", supplied: " + params.authKey + ", stored: " + user.tempAuthKey)
            render(view: "accountError")
        }
    }
}
