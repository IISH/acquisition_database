package org.iish.acquisition.domain

/**
 * The authorities (roles) of users of this application.
 */
class Authority {
    static final String ROLE_READONLY = 'ROLE_READONLY'
	static final String ROLE_USER = 'ROLE_USER'
	static final String ROLE_ADMIN = 'ROLE_ADMIN'

	static final String ROLE_OFFLOADER_1 = 'ROLE_OFFLOADER_1'
	static final String ROLE_OFFLOADER_2 = 'ROLE_OFFLOADER_2'
	static final String ROLE_OFFLOADER_3 = 'ROLE_OFFLOADER_3'

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
