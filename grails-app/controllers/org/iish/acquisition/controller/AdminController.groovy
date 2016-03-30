package org.iish.acquisition.controller

import org.iish.acquisition.domain.User
import org.iish.acquisition.service.UserService
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 * The controller for admin related actions.
 */
class AdminController {
	UserService userService

	static defaultAction = 'read-write'

	/**
	 * Allows the admin to add/remove read-write users and their roles.
	 */
	def 'read-write'() {
        render view: 'read-write', model: [usersAndRoles: handleRequest(true)]
	}

    /**
     * Allows the admin to add/remove read-only users and their roles.
     */
    def 'read-only'() {
        render view: 'read-only', model: [usersAndRoles: handleRequest(false)]
    }

    private Map<User, String[]> handleRequest(boolean readWrite) {
        if (request.post) {
            try {
                if (readWrite) {
                    userService.updateReadWriteUsers(params)
                }
                else {
                    userService.updateReadOnlyUsers(params)
                }

                flash.message = g.message(
                        code: 'default.updated.multiple.message',
                        args: [g.message(code: 'admin.users.label').toString().toLowerCase()]
                )
            }
            catch (UsernameNotFoundException unfe) {
                flash.status = 'error'
                flash.message = unfe.getMessage()
            }
        }

        // Only collect users with at least one role
        def list = (readWrite ? User.getReadWriteUsers() : User.getReadOnlyUsers())
                .collectEntries { [(it): it.getAuthorities()*.authority as String[]] }
                .findAll { (it.value as String[]).length > 0 }
        return list
    }
}
