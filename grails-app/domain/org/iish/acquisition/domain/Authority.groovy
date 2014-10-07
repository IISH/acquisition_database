package org.iish.acquisition.domain

/**
 * The authorities (roles) of users of this application.
 */
class Authority {
	static final String ROLE_SUPER_ADMIN = 'ROLE_SUPER_ADMIN'
	static final String ROLE_ADMIN = 'ROLE_ADMIN'
	static final String ROLE_USER = 'ROLE_USER'

	String authority

	static constraints = {
		authority blank: false, unique: true
	}

	static mapping = {
		table 'authorities'
		cache true
	}

	@Override
	String toString() {
		return authority
	}
}
