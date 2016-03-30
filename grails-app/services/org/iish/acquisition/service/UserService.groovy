package org.iish.acquisition.service

import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.iish.acquisition.domain.Authority
import org.iish.acquisition.domain.User
import org.iish.acquisition.domain.UserAuthority
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.ldap.search.LdapUserSearch

/**
 * A service responsible for user administration.
 */
@Transactional
class UserService {
	LdapUserSearch ldapUserSearch
	SpringSecurityService springSecurityService

	/**
	 * Add and remove roles for users.
	 * @param params The data as filled out by the admin.
	 */
	void updateUsers(GrailsParameterMap params) {
		Set<User> usersToRemove = new HashSet<>(User.list())

		int i = 0
		while (params["user[$i]"]) {
			GrailsParameterMap userData = params["user[${i++}]"] as GrailsParameterMap

			User user = usersToRemove.find { it.login.equals(userData.login) }
			usersToRemove.removeAll { it.login.equals(userData.login) }
			List<String> authoritiesToRemove = []

			if (!user) {
				User.withNewSession {
					user = new User(login: userData.login)
					DirContextOperations ctx = ldapUserSearch.searchForUser(user.login)

					user.mayReceiveEmail = true

					user.update(ctx)
					user.save(flush: true)
				}
			}
			else {
				authoritiesToRemove = UserAuthority.findAllByUser(user)*.authority*.authority
			}

			updateOffloaderRoles(user, authoritiesToRemove, userData.offloader?.toString())
			updateAdminRoles(user, authoritiesToRemove, userData.admin?.toString(), userData.offloader?.toString())

			authoritiesToRemove.each { String authority ->
				UserAuthority.remove(user, Authority.findByAuthority(authority))
			}
		}

		// Do not delete the user, to keep track of the users added collections.
		// However, delete all roles, so he/she cannot access the application any longer.
		usersToRemove.each { User user ->
			UserAuthority.removeAll(user)

		}

		// Update the security session, to correctly reflect the updated information
		springSecurityService.reauthenticate((springSecurityService.currentUser as User).login)
	}

	/**
	 * Updates the offloader roles of a given user.
	 * @param user The user of which to update the offloader roles.
	 * @param authoritiesToRemove The authorities that may be removed from the user.
	 * @param chosenRole The chosen offloader role for this user.
	 */
	private void updateOffloaderRoles(User user, List<String> authoritiesToRemove, String chosenRole) {
		switch (chosenRole) {
			case '1':
				addRoleToUser(user, Authority.findByAuthority(Authority.ROLE_OFFLOADER_1), authoritiesToRemove)
				break
			case '2':
				addRoleToUser(user, Authority.findByAuthority(Authority.ROLE_OFFLOADER_2), authoritiesToRemove)
				break
			case '3':
				addRoleToUser(user, Authority.findByAuthority(Authority.ROLE_OFFLOADER_3), authoritiesToRemove)
				break
		}
	}

	/**
	 * Updates the admin roles of a given user.
	 * @param user The user of which to update the offloader roles.
	 * @param authoritiesToRemove The authorities that may be removed from the user.
	 * @param chosenRole The chosen admin role for this user.
	 * @param offloaderRole The chosen offloader role for this user.
	 */
	private void updateAdminRoles(User user, List<String> authoritiesToRemove, String chosenRole,
			String offloaderRole) {
		switch (chosenRole) {
			case 'admin':
				addRoleToUser(user, Authority.findByAuthority(Authority.ROLE_ADMIN), authoritiesToRemove)
				break
			default:
				if (offloaderRole.equals('no')) {
					addRoleToUser(user, Authority.findByAuthority(Authority.ROLE_USER), authoritiesToRemove)
				}
		}
	}

	/**
	 * Adds a given role to the given user.
	 * @param user The user in question.
	 * @param authority The role in question.
	 * @param authoritiesToRemove The authorities that may be removed from the user.
	 */
	private void addRoleToUser(User user, Authority authority, List<String> authoritiesToRemove) {
		if (authoritiesToRemove.contains(authority.authority)) {
			authoritiesToRemove.remove(authority.authority)
		}
		else {
			UserAuthority.create(user, authority)
		}
	}
}
