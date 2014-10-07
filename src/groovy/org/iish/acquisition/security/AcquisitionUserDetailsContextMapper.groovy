package org.iish.acquisition.security

import org.iish.acquisition.domain.User
import org.springframework.ldap.core.DirContextAdapter
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper

/**
 * Operations to map a UserDetails object to and from a Spring LDAP DirContextOperations implementation.
 * Used by LdapUserDetailsManager when loading and saving/creating user information,
 * and also by the LdapAuthenticationProvider to allow customization of the user data loaded during authentication.
 * It also updates the user instance in the acquisition database.
 */
class AcquisitionUserDetailsContextMapper implements UserDetailsContextMapper {

	/**
	 * Creates a fully populated UserDetails object for use by the security framework.
	 * However, it also updates the user instance in the acquisition database.
	 * @param ctx The context object which contains the user information.
	 * @param username The user's supplied login name.
	 * @param authorities The user's authorities.
	 * @return The user object.
	 */
	@Override
	UserDetails mapUserFromContext(DirContextOperations ctx, String username,
			Collection<? extends GrantedAuthority> authorities) {
		User user = getAndUpdateUser(username, ctx)
		new AcquisitionUser(username, '', true, true, true, true, authorities, user.id, user.lastName,
				user.firstName, user.email)
	}

	/**
	 * Reverse of the above operation.
	 * Populates a context object from the supplied user object.
	 * Called when saving a user, for example.
	 */
	@Override
	void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		throw new IllegalStateException('We only retrieve data from Active Directory!')
	}

	/**
	 * Looks up the user in the acquisition database or creates a new one.
	 * It then updates his/her data based on the information from Active Directory.
	 * @param username The user's supplied login name.
	 * @param ctx The context object which contains the user information.
	 * @return The user as represented in the acquisition database.
	 */
	private static User getAndUpdateUser(String username, DirContextOperations ctx) {
		User.withTransaction {
			User user = User.findByLogin(username)
			if (!user) {
				user = new User(login: username)
			}

			updateUser(user, ctx)
			return user
		}
	}

	/**
	 * Updates the given user with the provided context object.
	 * @param user The user as represented in the acquisition database.
	 * @param ctx The context object which contains the user information.
	 */
	private static void updateUser(User user, DirContextOperations ctx) {
		user.lastName = ctx.originalAttrs.attrs['sn'].values[0]
		user.firstName = ctx.originalAttrs.attrs['givenname'].values[0]
		user.email = ctx.originalAttrs.attrs['mail'].values[0]

		user.save(flush: true)
	}
}
