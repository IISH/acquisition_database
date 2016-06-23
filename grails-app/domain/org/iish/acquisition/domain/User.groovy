package org.iish.acquisition.domain

import org.springframework.ldap.core.DirContextOperations

/**
 * Represents a user that may (or may not) have access to this application.
 */
class User {
	String login
	String lastName
	String firstName
	String email
	boolean mayReceiveEmail = false

	static hasMany = [
			collectionsAdded: Collection
	]

	static constraints = {
		login blank: false, unique: true
		lastName nullable: true
		firstName nullable: true
		email nullable: true
	}

	static mapping = {
		table 'users'
        sort 'login'
	}

    /**
     * Look for read/write users by checking their readonly authority,
     * @return All read/write users.
     */
    static List<User> getReadWriteUsers() {
        return list().findAll { user ->
            !user.authorities.isEmpty() && !user.authorities.find { it.authority == Authority.ROLE_READONLY }
        }
    }

    /**
     * Look for read-only users by checking their readonly authority,
     * @return All read-only users.
     */
    static List<User> getReadOnlyUsers() {
        return list().findAll { user ->
            !user.authorities.isEmpty() && user.authorities.find { it.authority == Authority.ROLE_READONLY }
        }
    }

    /**
	 * Returns all authorities given to this user.
	 * @return All authorities given to this user.
	 */
	Set<Authority> getAuthorities() {
		UserAuthority.findAllByUser(this).collect { it.authority }
	}

	/**
	 * Updates the user with data from Active Directory.
	 * @param ctx The context object which contains the user information.
	 */
	void update(DirContextOperations ctx) {
        if (ctx.attributeExists('sn')) {
            lastName = ctx.getStringAttribute('sn')
        }

        if (ctx.attributeExists('givenname')) {
            firstName = ctx.getStringAttribute('givenname')
        }

        if (ctx.attributeExists('mail')) {
            email = ctx.getStringAttribute('mail')
        }

		save(flush: true)
	}

	@Override
	String toString() {
		if (firstName && lastName) {
			return "$firstName $lastName"
		}
		return login
	}
}
