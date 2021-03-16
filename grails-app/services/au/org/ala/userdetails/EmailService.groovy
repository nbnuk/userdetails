package au.org.ala.userdetails

import au.org.ala.auth.PasswordResetFailedException

class EmailService {

    def grailsApplication

    static transactional = false

    def sendPasswordReset(user, authKey, emailSubject=null, emailTitle = null, emailBody=null, password=null) throws PasswordResetFailedException {

        if (!emailSubject) {
            emailSubject = "Reset your password"
        }
        if (!emailTitle) {
            emailTitle = "Reset your password"
        }

        if (!emailBody) {
            emailBody =
"""
        Please click the link below to reset your NBN Atlas password.
        This will take you to a form where you can provide a new
        password for your account.

"""
        }
        try {
            sendMail {
              from grailsApplication.config.emailSenderTitle+"<" + grailsApplication.config.emailSender + ">"
              subject emailSubject
              to user.email
              body (view: '/email/resetPassword',
                    plugin:"email-confirmation",
                    model:[link:getServerUrl() + "resetPassword/" +  user.id +  "/"  + authKey, emailTitle: emailTitle, emailBody: emailBody, password: password ]
              )
            }
        } catch (Exception ex) {
            throw new PasswordResetFailedException(ex)
        }
    }

    def sendAccountActivation(user, authKey){
        // Shim to allow for email debugging for spamcheck purposes
        if(user.email == "investigation06@btinternet.com"){
            sendMail {
                from grailsApplication.config.emailSenderTitle+"<" + grailsApplication.config.emailSender + ">"
                subject "3832 / CS0164567"
                to user.email
                body (view: '/email/activateAccount',
                        plugin:"email-confirmation",
                        model:[link:getServerUrl() + "activateAccount/" + user.id + "/"  + authKey, orgNameLong: grailsApplication.config.skin.orgNameLong ]
                )
            }
        } else {



            sendMail {
                from grailsApplication.config.emailSenderTitle+"<" + grailsApplication.config.emailSender + ">"
                subject "Activate your account"
                to user.email
                body (view: '/email/activateAccount',
                        plugin:"email-confirmation",
                        model:[link:getServerUrl() + "activateAccount/" + user.id + "/"  + authKey, orgNameLong: grailsApplication.config.skin.orgNameLong ]
                )
            }
        }
    }

    def sendAccountActivationSuccess(def user, def activatedAlerts) {
        sendMail {
            from grailsApplication.config.emailSenderTitle+"<" + grailsApplication.config.emailSender + ">"
            subject "Account activated successfully"
            to user.email
            body (view: '/email/activateAccountSuccess',
                    plugin:"email-confirmation",
                    model:[activatedAlerts: activatedAlerts, alertsUrl: grailsApplication.config.alerts.url]
            )
        }
    }

    def sendGeneratedPassword(user, generatedPassword){
        sendMail {
          from grailsApplication.config.emailSenderTitle+"<" + grailsApplication.config.emailSender + ">"
          subject "Accessing your account"
          to user.email
          body (view: '/email/accessAccount',
                plugin:"email-confirmation",
                model:[link:getLoginUrl(user.email), generatedPassword: generatedPassword]
          )
        }
    }

    def getLoginUrl(email){
            grailsApplication.config.security.cas.loginUrl  +
                    "?email=" + email +
                    "&service=" + URLEncoder.encode(getMyProfileUrl(),"UTF-8")
    }

    def getMyProfileUrl(){
            grailsApplication.config.grails.serverURL  +
                    "/myprofile/"

    }

    def getServerUrl(){
        grailsApplication.config.grails.serverURL +
                    "/registration/"
    }
}
