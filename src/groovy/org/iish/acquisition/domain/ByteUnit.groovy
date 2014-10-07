package org.iish.acquisition.domain

/**
 * Possible units in bytes, to describe digital material.
 */
enum ByteUnit {
	MB(1, 'MB'), GB(2, 'GB'), TB(3, 'TB')

	int id
	String name

	ByteUnit(int id, String name) {
		this.id = id
		this.name = name
	}

	/**
	 * Returns a byte unit by its id.
	 * @param id The id in question.
	 * @return The matching byte unit, if found.
	 */
	static ByteUnit getById(Integer id) {
		values().find { it.id == id }
	}

	@Override
	String toString() {
		return name
	}
}