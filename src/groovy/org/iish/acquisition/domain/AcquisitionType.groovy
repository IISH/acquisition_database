package org.iish.acquisition.domain

/**
 * The collection ID contains an acquisition type.
 */
enum AcquisitionType {
	COLLECTION(1, 'Coll'), NOT_APPLICABLE(2, 'n.a.')

	int id
	String name

	AcquisitionType(int id, String name) {
		this.id = id
		this.name = name
	}

	/**
	 * Returns an acquisition type by its id.
	 * @param id The id in question.
	 * @return The matching acquisition type, if found.
	 */
	static AcquisitionType getById(Integer id) {
		values().find { it.id == id }
	}

	@Override
	String toString() {
		return name
	}
}