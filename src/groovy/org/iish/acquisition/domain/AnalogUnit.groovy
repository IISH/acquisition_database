package org.iish.acquisition.domain

/**
 * Possible units to describe the size of analog material.
 */
enum AnalogUnit {
	METER(1, 'meter'), NUMBER(2, 'items')

	int id
	String name

	AnalogUnit(int id, String name) {
		this.id = id
		this.name = name
	}

	/**
	 * Returns an analog unit by its id.
	 * @param id The id in question.
	 * @return The matching analog unit, if found.
	 */
	static AnalogUnit getById(Integer id) {
		values().find { it.id == id }
	}

	@Override
	String toString() {
		return name
	}
}