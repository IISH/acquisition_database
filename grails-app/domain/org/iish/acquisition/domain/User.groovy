package org.iish.acquisition.domain

/**
 * Represents a user that may (or may not) have access to this application.
 */
class User {
	String login
	String lastName
	String firstName
	String email

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
	}

	/**
	 * Returns all authorities given to this user.
	 * @return All authorities given to this user.
	 */
	Set<Authority> getAuthorities() {
		UserAuthority.findAllByUser(this).collect { it.authority }
	}

	@Override
	String toString() {
		if (firstName && lastName) {
			return "$firstName $lastName"
		}
		return login
	}
}
