package org.iish.acquisition.controller

import org.iish.acquisition.domain.User
import org.iish.acquisition.service.UserService
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 * The controller for admin related actions.
 */
class AdminController {
	UserService userService

	static defaultAction = 'index'

	/**
	 * Allows the admin to add/remove users and their roles.
	 */
	def index() {
		if (request.post) {
			try {
				userService.updateUsers(params)
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
		Map<User, String[]> usersAndRoles = User
				.list()
				.collectEntries { [(it): it.getAuthorities()*.authority as String[]] }
				.findAll { (it.value as String[]).length > 0 }

		render view: 'index', model: [usersAndRoles: usersAndRoles]
	}
}
