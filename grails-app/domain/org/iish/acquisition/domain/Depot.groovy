package org.iish.acquisition.domain

/**
 * The depots where the collections may be located.
 */
class Depot {
	static final long FIFTH_FLOOR_ID = 1L
	static final long FOURTH_FLOOR_ID = 2L
	static final long THIRD_FLOOR_ID = 3L
	static final long ZERO_FLOOR_ID = 4L
	static final long SORTEERRUIMTE_ID = 5L
	static final long RANGEERTERREIN_ID = 6L
	static final long BG_DEPOT_ID = 7L
	static final long COLD_STORAGE_ID = 8L
	static final long DIGITAL_INGEST_DEPOT_ID = 9L
	static final long REGIONAL_DESK_ID = 10L
	static final long ELSEWHERE_ID = 11L

	String name

	static hasMany = [
			locations: Location
	]

	static constraints = {
		name blank: false, maxSize: 255, unique: true
	}

	static mapping = {
		table 'depots'
		sort 'id'
		cache true

		name index: 'depots_name_idx'
	}

	@Override
	String toString() {
		return name
	}
}
