package org.iish.acquisition.security

import grails.plugin.springsecurity.userdetails.GrailsUser
import org.springframework.security.core.GrantedAuthority

/**
 * Extends the GrailsUser with support for the first and last name of the user.
 */
class AcquisitionUser extends GrailsUser {
	final String lastName
	final String firstName
	final String email

	AcquisitionUser(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked, Collection<GrantedAuthority> authorities, long id,
			String lastName, String firstName, String email) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, id)

		this.lastName = lastName
		this.firstName = firstName
		this.email = email
	}
}
