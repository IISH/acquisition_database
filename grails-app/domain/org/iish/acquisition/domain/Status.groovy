package org.iish.acquisition.domain

/**
 * The statuses a collection may have.
 */
class Status {
	static final long NOT_PROCESSED_ID = 1L
	static final long IN_PROCESS_ID = 2L
	static final long COLLECTION_LEVEL_READY_ID = 3L
	static final long PROCESSED_ID = 4L
	static final long WONT_BE_PROCESSED_ID = 5L

	String status

	static hasMany = [
			collections: Collection
	]

	static constraints = {
		status blank: false, maxSize: 255, unique: true
	}

	static mapping = {
		table 'statuses'
		sort 'id'
		cache true
	}

	@Override
	String toString() {
		return status
	}
}
