package au.org.ala.userdetails

import au.org.ala.auth.PreAuthorise
import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

import java.sql.Timestamp

@PreAuthorise
class UserController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def userService

    def index() {
        redirect(action: "list", params: params)
    }

    def addRole() {
        def user = User.get(params.userId)
        [user:user, roles:Role.all]
    }

    def list(Integer max) {
        if(params.q){
            def q = "%"+ params.q + "%"
            def userList = User.findAllByEmailLikeOrLastNameLikeOrFirstNameLike(q,q,q)
            [userInstanceList: userList, userInstanceTotal: userList.size(), q:params.q]
        } else {
            params.max = Math.min(max ?: 20, 5000)
            [userInstanceList: User.list(params), userInstanceTotal: User.count()]
        }
    }

    def create() {
        [userInstance: new User(params)]
    }

    def save() {
        def userInstance = new User(params)
        userInstance.created = new Timestamp(System.currentTimeMillis())
        if (params.locked == null) userInstance.locked = false
        if (params.activated == null) userInstance.activated = false
        // Keep the username and email address in sync
        userInstance.userName = userInstance.email

        if (!userInstance.save(flush: true)) {
            render(view: "create", model: [userInstance: userInstance])
            return
        }

        userService.updateProperties(userInstance, params)

        flash.message = message(code: 'default.created.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
        redirect(action: "show", id: userInstance.id)
    }

    def show(Long id) {
        def userInstance = User.get(id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "list")
            return
        }

        String resetPasswordUrl = userService.getResetPasswordUrl(userInstance)

        [userInstance: userInstance, resetPasswordUrl: resetPasswordUrl]
    }

    def edit(Long id) {
        def userInstance = User.get(id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "list")
            return
        }
        [userInstance: userInstance, props:userInstance.propsAsMap(), stateMap:userService.retrieveMapOfStates()]
    }

    def update(Long id, Long version) {
        def userInstance = User.get(id)
        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (userInstance.version > version) {
                userInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'user.label', default: 'User')] as Object[],
                          "Another user has updated this User while you were editing")
                render(view: "edit", model: [userInstance: userInstance])
                return
            }
        }

        if (userInstance.email != params.email) {
            params.userName = params.email
        }

        userInstance.properties = params

        if (!userInstance.save(flush: true)) {
            render(view: "edit", model: [userInstance: userInstance])
            return
        }

        userService.updateProperties(userInstance, params)

        flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), userInstance.id])
        redirect(action: "show", id: userInstance.id)
    }

    def delete(Long id) {

        def userInstance = User.get(id)

        if (!userInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "list")
            return
        }

        try {
            userService.deleteUser(userInstance)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "list")
        } catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'user.label', default: 'User'), id])
            redirect(action: "show", id: id)
        }

    }
}
