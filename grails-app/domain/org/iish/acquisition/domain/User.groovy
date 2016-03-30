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
        if (ctx.originalAttrs?.attrs) {
            if (ctx.originalAttrs.attrs['sn']?.values?.getAt(0)) {
                lastName = ctx.originalAttrs.attrs['sn'].values[0]
            }

            if (ctx.originalAttrs.attrs['givenname']?.values?.getAt(0)) {
                firstName = ctx.originalAttrs.attrs['givenname'].values[0]
            }

            if (ctx.originalAttrs.attrs['mail']?.values?.getAt(0)) {
                email = ctx.originalAttrs.attrs['mail'].values[0]
            }
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
