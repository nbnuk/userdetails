package au.org.ala.userdetails

import au.org.ala.auth.PreAuthorise
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

import javax.servlet.http.Cookie

@PreAuthorise
class AdminController {

    def passwordService
    def emailService
    def userService
    def exportService
    def profileService
    def authorisedSystemService

    def index() {}

    def webservices() {}

    def resetPasswordForUser(){}

    def sendPasswordResetEmail(){

       def user = User.findByEmail(params.email)
       if(user){
           def password = passwordService.generatePassword(user)
           //email to user
           emailService.sendGeneratedPassword(user, password)
           render(view:'userPasswordResetSuccess', model:[email:params.email])
       } else {
           render(view:'resetPasswordForUser', model:[email:params.email, emailNotRecognised:true])
       }
    }

    def bulkUploadUsers() {}

    def exportUsers() {
        def secondaryFields = profileService.allAvailableProperties
        String extraFields =  secondaryFields.join(",")

        render(view: 'exportUsers',
                model: [roles        : Role.list(),
                        primaryFields: grailsApplication.config.admin.export.csv.primary.fields,
                        extraFields  : extraFields])
    }

    def downloadUsersCsvFile() {
        if (authorisedSystemService.isAuthorisedSystem(request)) {
            //selectedRoles data type will be different depending on whether one or more option were selected
            def roleList = {
                if (!params.selectedRoles) {
                    return []
                } else if (params.selectedRoles instanceof String) {
                    return [params.selectedRoles]
                } else {
                    return params.selectedRoles as List
                }
            }.call()

            //1. Get data based on user inputs
            def userList = userService.findUsersForExport(roleList, params?.includeInactiveUsers)

            //2. Then prepare the format options
            String primaryFieldsProperty = grailsApplication.config.admin.export.csv.primary.fields
            def primaryFields = primaryFieldsProperty ? primaryFieldsProperty.split(',').collect { it as String } : []
            def fields = primaryFields

            def formatters = [:]
            if (params.includeExtraFields) {
                def secondaryFields = profileService.allAvailableProperties
                fields.addAll(secondaryFields)
                secondaryFields.each {
                    formatters[it] = { domain, value ->
                        String fieldName = it
                        domain.userProperties.find {
                            it.property == fieldName
                        }?.value
                    }
                }
            }

            if (params.includeRoles) {
                String roleFieldName = 'roles'
                formatters[roleFieldName] = { User domain, value ->
                    def result = ""
                    domain.userRoles.each {
                        result += it.role.role + " "
                    }
                    result
                }
                fields.add(roleFieldName)
            }

            log.debug("Export fields ${fields}")

            String fileName = "users-" + new Date().format("YYYYMMdd-HHmm")
            //3. And finally generate and send file to browser
            exportService.export("csv", response, fileName, "csv", userList, fields, [:], formatters, [:])
        } else {
            response.sendError(403)
        }
    }

    def loadUsersCSV() {
        if(request instanceof MultipartHttpServletRequest) {
            MultipartFile f = ((MultipartHttpServletRequest) request).getFile('userList')
            if (f && f.size > 0) {
                def allowedMimeTypes = ['text/plain', 'text/csv']
                if (!allowedMimeTypes.contains(f.getContentType())) {
                    flash.message = "The file must be one of: ${allowedMimeTypes}. Submitted file is of type ${f.getContentType()}"
                    redirect(action:"bulkUploadUsers")
                    return
                }

                def firstRow = (boolean) params.firstRowHasFieldNames
                def primaryUsage = params.primaryUsage as String
                def subject = params.emailSubject as String
                def title = params.emailTitle as String
                def body = params.emailBody as String

                def results = userService.bulkRegisterUsersFromFile(f.inputStream, firstRow, primaryUsage, subject, title, body)
                render(view:'loadUsersResults', model:[results: results])
                return
            } else {
                flash.message = "You must select a file to upload!"
            }
        }
        redirect(action:"bulkUploadUsers")
    }
}