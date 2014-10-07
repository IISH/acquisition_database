package org.iish.acquisition.security

import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import org.iish.acquisition.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Override of the user details service that uses domain classes to load users and roles.
 * However, the override is to allow users without passwords stored in the database.
 * Authentication is handled by Active Directory.
 */
class AcquisitionUserDetailsService extends GormUserDetailsService {

	/**
	 * Returns an AcquisitionUser with additional properties and no password.
	 * @param user The user in question.
	 * @param authorities The list of authorities granted to this user.
	 * @return The UserDetails object.
	 */
	@Override
	protected UserDetails createUserDetails(user, Collection<GrantedAuthority> authorities) {
		User dbUser = (User) user
		new AcquisitionUser(dbUser.login, '', true, true, true, true, authorities, dbUser.id, dbUser.lastName,
				dbUser.firstName, dbUser.email)
	}
}
