package org.iish.acquisition.domain

/**
 * Contracts that could hold for a collection.
 */
class Contract {
	static final long YES_ID = 1L
	static final long NA_ID = 2L
	static final long NOT_YET_THERE_ID = 3L
	static final long UNKNOWN_ID = 4L

	String name

	static hasMany = [
			collections: Collection
	]

	static constraints = {
		name blank: false, maxSize: 255, unique: true
	}

	static mapping = {
		table 'contracts'
		sort 'id'
		cache true
	}

	@Override
	String toString() {
		return name
	}
}
