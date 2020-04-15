package au.org.ala.userdetails

import au.org.ala.auth.BulkUserLoadResults
import au.org.ala.auth.PasswordResetFailedException
//import au.org.ala.ws.service.WebService
import grails.converters.JSON
import grails.plugin.cache.Cacheable
import grails.util.Environment
import grails.util.Metadata
import org.apache.http.HttpStatus

import java.sql.Timestamp

class UserService {

    def emailService
    def passwordService
    def authService
    def grailsApplication
    def messageSource
    def webService

    def updateUser(user, params){
        try {
            user.setProperties(params)
            user.activated = true
            user.locked = false
            user.save(failOnError: true, flush:true)
            updateProperties(user, params)
            true
        } catch (Exception e){
            log.error(e.getMessage(), e)
            false
        }
    }

    def disableUser(user){
        try {
            user.activated = false
            user.save(failOnError: true, flush: true)
            Map resp = webService.post("${grailsApplication.config.alerts.url}/api/alerts/user/${user.id}/unsubscribe", [:])
            if (resp.statusCode != HttpStatus.SC_OK) {
                log.error("Alerts returned ${resp} when trying to disable the user's alerts. " +
                        "The user has been disabled, but their alerts are still active.")
            }
            true
        } catch (Exception e){
            log.error(e.getMessage(), e)
            false
        }
    }

    def isActive(email) {
        def user = User.findByEmail(email.toLowerCase())
        user?.activated
    }

    def isEmailRegistered(email){
        def user = User.findByEmail(email.toLowerCase())
        if(user){
            true
        } else {
            false
        }
    }

    def activateAccount(user){
        Map resp = webService.post("${grailsApplication.config.alerts.url}/api/alerts/user/createAlerts", [:], [userId: user.id, email: user.email, firstName: user.firstName, lastName: user.lastName])
        if (resp.statusCode == HttpStatus.SC_CREATED) {
            emailService.sendAccountActivationSuccess(user, resp.resp)
        } else if (resp.statusCode != HttpStatus.SC_OK) {
            log.error("Alerts returned ${resp} when trying to create user alerts for " + user.id + " with email: " + user.email)
        }

        user.activated = true
        user.save(flush:true)
    }

    public BulkUserLoadResults bulkRegisterUsersFromFile(InputStream stream, Boolean firstRowContainsFieldNames, String primaryUsage, String emailSubject, String emailTitle, String emailBody) {

        def results = new BulkUserLoadResults()

        if (!stream) {
            results.message = "No data specified!"
            return results
        }

        int lineNumber = 0
        int expectedColumns = 4

        def roleUser = Role.findByRole("ROLE_USER")

        stream.eachCsvLine { tokens ->
            // email_address,first_name,surname,roles
            if (++lineNumber == 1 && firstRowContainsFieldNames) {
                // ignore...
            } else {

                if (tokens.size() == 1 && !tokens[0]?.trim()?.length()) {
                    return // skip empty lines
                }

                if (tokens.size() != expectedColumns) {
                    results.failedRecords << [lineNumber: lineNumber, tokens: tokens, reason: "Incorrect number of columns - expected ${expectedColumns}, got ${tokens.size()}"]
                    return
                }

                def emailAddress = tokens[0]
                // Check to see if this email address is already in use...
                def userInstance = User.findByEmail(emailAddress)
                def isNewUser = true

                def existingRoles = []
                if (userInstance) {
                    isNewUser = false
                    // keep track of their current roles
                    existingRoles.addAll(UserRole.findAllByUser(userInstance)*.role)
                } else {
                    userInstance = new User(email: emailAddress, userName: emailAddress, firstName: tokens[1], lastName: tokens[2])
                    userInstance.activated = true
                    userInstance.locked = false
                    userInstance.created = new Date().toTimestamp()
                }

                // Now add roles
                def roles = []
                if (!existingRoles.contains(roleUser)) {
                    roles << roleUser
                }
                if (tokens[3]?.trim()) {
                    def roleArray = tokens[3].trim()?.split(" ")
                    for (String roleName : roleArray) {
                        def role = Role.findByRole(roleName)
                        if (!role) {
                            results.failedRecords << [lineNumber: lineNumber, tokens: tokens, reason: "Specified role '${roleName} does not exist"]
                            return
                        } else {
                            if (!roles.contains(role) && !existingRoles.contains(role)) {
                                roles << role
                            }
                        }
                    }
                }

                String password = null

                if (isNewUser) {
                    userInstance.save(failOnError: true)
                    password = passwordService.generatePassword(userInstance)
                    results.userAccountsCreated++
                }

                roles?.each { role ->
                    def userRole = new UserRole(user: userInstance, role: role)
                    userRole.save(failOnError: true)
                }

                if (!isNewUser) {
                    results.warnings << [lineNumber: lineNumber, tokens: tokens, reason: "Email address already exists in database. Added roles ${roles}"]
                } else {
                    // User Properties
                    def userProps = [:]

                    userProps['primaryUserType'] = primaryUsage ?: 'Not specified'
                    userProps['secondaryUserType'] = 'Not specified'
                    userProps['bulkCreatedOn'] = new Date().format("yyyy-MM-dd HH:mm:ss")
                    setUserPropertiesFromMap(userInstance, userProps)

                    // Now send a temporary password to the user...
                    try {
                        resetAndSendTemporaryPassword(userInstance, emailSubject, emailTitle, emailBody, password)
                        passwordService
                    } catch (PasswordResetFailedException ex) {
                        // Catching the checked exception should prevent the transaction from failing
                        log.error("Failed to send temporary password via email!", ex)
                        results.warnings << [lineNumber: lineNumber, tokens: tokens, reason: "Failed to send password reset email. Check mail configuration"]
                    }
                }


            }
        }

        results.success = true

        return results
    }

    private setUserPropertiesFromMap(User user, Map properties) {

        properties.keySet().each { String propName ->
            def propValue = properties[propName] ?: ''
            def existingProperty = UserProperty.findByUserAndProperty(user, propName)
            if (existingProperty) {
                existingProperty.value = propValue
                existingProperty.save()
            } else {
                def newProperty = new UserProperty(user: user, property: propName, value: propValue)
                newProperty.save(failOnError: true)
            }
        }

    }

    def registerUser(params) throws Exception {

        //does a user with the supplied email address exist
        def user = new User(params)
        user.userName = params.email
        user.activated = false
        user.locked = false
        user.created = new Date().toTimestamp()
        user.tempAuthKey = UUID.randomUUID().toString()
        def createdUser = user.save(flush: true, failOnError: true)
        updateProperties(createdUser, params)

        //add a role of user
        def roleUser = Role.findByRole("ROLE_USER")
        new UserRole(user:user, role:roleUser).save(flush:true, failOnError: true)

        log.info("Newly created user: " + createdUser.id)
        createdUser
    }

    def updateProperties(user, params) {
        (new UserProperty(user: user, property: 'city', value: params.city ?: '')).save(flush: true)
        (new UserProperty(user: user, property: 'organisation', value: params.organisation ?: '')).save(flush: true)
        (new UserProperty(user: user, property: 'telephone', value: params.telephone ?: '')).save(flush: true)
        (new UserProperty(user: user, property: 'state', value: params.state ?: '')).save(flush: true)
        (new UserProperty(user: user, property: 'primaryUserType', value: params.primaryUserType ?: '')).save(flush: true)
        (new UserProperty(user: user, property: 'secondaryUserType', value: params.secondaryUserType ?: '')).save(flush: true)
    }

    def deleteUser(User user) {

        if (user) {
            // First need to delete any user properties
            UserProperty.executeUpdate("delete UserProperty where user = ${user.id}")
            //def userProperties = UserProperty.findAllByUser(user)
            //userProperties.each { userProp ->
            //    userProp.delete()
            //}
            // Then delete any roles
            UserRole.executeUpdate("delete UserRole where user= ${user.id}")
            //def userRoles = UserRole.findAllByUser(user)
            //userRoles.each { userRole ->
            //    userRole.delete()
            //}

            // Delete password
            Password.executeUpdate("delete Password where user = ${user.id}")
            //def passwords = Password.findAllByUser(user)
            //passwords.each { password ->
            //    password.delete()
            //}

            // and finally delete the use object
            User.executeUpdate("delete User where id = ${user.id}")
            //user.delete()
        }

    }

    def resetAndSendTemporaryPassword(User user, String emailSubject, String emailTitle, String emailBody, String password = null) throws PasswordResetFailedException {
        if (user) {
            //set the temp auth key
            user.tempAuthKey = UUID.randomUUID().toString()
            user.save(flush: true)
            //send the email
            emailService.sendPasswordReset(user, user.tempAuthKey, emailSubject, emailTitle, emailBody, password)
        }
    }

    def clearTempAuthKey(User user) {
        if (user) {
            //set the temp auth key
            user.tempAuthKey = null
            user.save(flush: true)
        }
    }

    /**
     * This service method returns the User object for the current user.
     */
    public User getCurrentUser() {

        def userId = authService.getUserId()
        if (userId == null) {
            // Problem. This might mean an expired cookie, or it might mean that this service is not in the authorised system list
            log.debug("Attempt to get current user returned null. This might indicating that this machine is not the authorised system list")
            return null
        }

        User user = null
        if(userId.toString().isLong()){
            user = User.get(userId.toLong())
            if (user == null && Environment.current != Environment.PRODUCTION) {
                // try looking up by email, as this may be a dev session, and the id's might not line up because this service is talking to the local database
                def email = authService.getEmail()
                if (email) {
                    user = User.findByEmail(email)
                }
            }
        } else {
            user = User.findByEmail(userId)
        }

        return user
    }

    String getResetPasswordUrl(User user) {
        if(user.tempAuthKey){
            emailService.getServerUrl() + "resetPassword/" +  user.id +  "/"  + user.tempAuthKey
        }
    }

    def findUsersForExport(List usersInRoles, includeInactive) {
        def roles = usersInRoles? Role.findAllByRoleInList(usersInRoles) : []
        def criteria = User.createCriteria()
        def results
            results = criteria.listDistinct {
                and {
                    if(roles) {
                        userRoles {
                            'in'('role', roles)
                        }
                    }
                    if(!includeInactive) {
                        eq('activated', true)
                    }
                }
            }
        results
    }

    /**
     * Calculate the number of active users (not locked and is activated), as well as the number
     * of active users 1 year ago (for comparison).
     *
     * @return Map jsonMap
     */
    @Cacheable('dailyCache')
    Map getUsersCounts(Locale locale) {
        Map jsonMap = [description: messageSource.getMessage("getUserCounts.description", null, locale?:Locale.default)]
        jsonMap.totalUsers = User.countByLockedAndActivated(false, true)
        // calculate number of users 1 year ago
        Calendar cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1); // minus 1 year
        Date oneYearAgoDate = cal.getTime()
        Timestamp oneYearAgoTimeStamp = new Timestamp(oneYearAgoDate.getTime())
        jsonMap.totalUsersOneYearAgo = User.countByLockedAndActivatedAndCreatedLessThan(false, true, oneYearAgoTimeStamp)
        log.debug "jsonMap = ${jsonMap as JSON}"
        jsonMap
    }

    def retrieveMapOfStates(){

        def appName = Metadata.current.'app.name'?:'userdetails'
        def configPath = "/data/${appName}/config/states.properties"

        //read the file which is in properties file format
        if(new File(configPath).exists()){
            def states = ['N/A':'N/A']
            new File(configPath).readLines().each { line ->
                int idx = line.indexOf('=')
                if(idx > 0){
                    states[line.substring(0, idx)] = line.substring(idx + 1)
                }
            }
            states
        } else {
            def defaultStateList =
                    [
                            'N/A':'N/A',
                            'ACT' : 'Australian Capital Territory',
                            'NSW' : 'New South Wales',
                            'WA': 'Western Australia',
                            'VIC':'Victoria',
                            'SA':'South Australia',
                            'TAS':'Tasmania',
                            'NT':'Northern Territory',
                            'QLD':'Queensland'
                    ]
            defaultStateList
        }
    }
}
