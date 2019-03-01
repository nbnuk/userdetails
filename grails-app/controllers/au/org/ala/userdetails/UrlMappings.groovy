package au.org.ala.userdetails

class UrlMappings {

    static mappings = {

        "/my-profile/"(controller: 'profile')
        "/my-profile"(controller: 'profile')
        "/myprofile/"(controller: 'profile')
        "/myprofile"(controller: 'profile')
        "/profile/$action?"(controller: 'profile')

        "/external/flickr"(controller: 'externalSite', action: 'flickr')
        "/ws/flickr"(controller: 'externalSite', action: 'flickr')
        "/ws/getUserStats"(controller:'externalSite', action: 'getUserStats')
        "/ws/admin/userRole/list"(controller:'userRole', action:'list', format:'json')

        "/registration/$action?/$id?"(controller: 'registration')

        "/registration/activateAccount/$userId/$authKey"(controller:'registration', action: 'activateAccount')
        "/registration/resetPassword/$userId/$authKey"(controller:'registration', action: 'passwordReset')

        "/registration/forgottenPassword"(controller:'registration', action: 'forgottenPassword')

        "/userDetails/findUser"(controller:'userDetails', action: 'findUser')
        "/userDetails/getUserDetails"(controller:'userDetails', action: 'getUserDetails')
        "/userDetails/getUserList"(controller:'userDetails', action: 'getUserList')
        "/userDetails/getUserListFull"(controller:'userDetails', action: 'getUserListFull')
        "/userDetails/getUserListWithIds"(controller:'userDetails', action: 'getUserListWithIds')
        "/userDetails/getUserDetailsFromIdList"(controller:'userDetails', action: 'getUserDetailsFromIdList')

        "/property/getProperty"(controller: 'property', action: 'getProperty')
        "/property/saveProperty"(controller: 'property', action: 'saveProperty')

        "/myprofile"(controller:'profile', action: 'index')
        "/simpleCaptcha/captcha"(controller:'simpleCaptcha', action:'captcha')
        "/simpleCaptcha/*"(controller:'simpleCaptcha')

        "/admin"(controller:'admin', action: 'index')
        "/admin/"(controller:'admin', action: 'index')
        "/admin/webservices"(controller:'admin', action: 'webservices')

        name fixUserList: "/admin/user/list"(controller: "user", action: "list")

//        "/admin/$controller"()
//        "/admin/$controller/$action?"()
        "/admin/$controller/$action?/$id?"()

        "/admin/$controller/$action\\?"()

        "/logout/logout"(controller: "logout", action: 'logout')
        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
