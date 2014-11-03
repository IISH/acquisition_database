package org.iish.acquisition.domain

/**
 * Represents a location of a collection.
 */
class Location {
	String cabinet
	String shelf

	static belongsTo = [
			collection: Collection,
			depot     : Depot
	]

	static constraints = {
		depot nullable: true
		cabinet nullable: true, maxSize: 1000
		shelf nullable: true, maxSize: 255
	}

	static mapping = {
		table 'locations'

		collection fetch: 'join'
		depot fetch: 'join'
	}

	/**
	 * Simple check to see if this is an empty location.
	 * @return Whether this location is empty.
	 */
	boolean isEmpty() {
		return (!depot && !cabinet && !shelf)
	}

	/**
	 * Returns a more detailed string of the location.
	 * @return A more detailed string of the location.
	 */
	String toDetailedString() {
		List<String> details = []
		if (depot) {
			details << depot.toString()
		}
		if (cabinet) {
			details << cabinet
		}
		if (shelf) {
			details << shelf
		}

		return details.join(', ')
	}

	@Override
	String toString() {
		return depot
	}
}
