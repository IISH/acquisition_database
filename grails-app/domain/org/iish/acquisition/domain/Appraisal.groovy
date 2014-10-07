package org.iish.acquisition.domain

/**
 * Appraisals that could hold for a collection.
 */
class Appraisal {
	static final long YES_ID = 1L
	static final long NO_ID = 2L
	static final long NA_ID = 3L

	String name

	static hasMany = [
			collections: Collection
	]

	static constraints = {
		name blank: false, maxSize: 255, unique: true
	}

	static mapping = {
		table 'appraisals'
		sort 'id'
		cache true
	}

	@Override
	String toString() {
		return name
	}
}
