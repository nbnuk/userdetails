package userdetails

import grails.plugins.Plugin

class UserdetailsGrailsPlugin extends Plugin {

    def grailsVersion = "5.2.0 > *"

    def title = "User Details Plugin"
    def author = "Atlas of Living Australia"
    def description = 'Grails plugin providing user details management functionality for ALA applications.'

    def documentation = "https://github.com/AtlasOfLivingAustralia/userdetails"

    Closure doWithSpring() {
        { -> }
    }

    void doWithApplicationContext() {
    }
}
