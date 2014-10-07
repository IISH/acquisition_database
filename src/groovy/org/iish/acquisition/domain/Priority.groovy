package org.iish.acquisition.domain

/**
 * Describes the possible priority levels.
 */
enum Priority {
	HIGH(1, 'High'), MEDIUM(2, 'Medium'), LOW(3, 'Low')

	int id
	String name

	Priority(int id, String name) {
		this.id = id
		this.name = name
	}

	/**
	 * Returns a priority level by its id.
	 * @param id The id in question.
	 * @return The matching priority level, if found.
	 */
	static Priority getById(Integer id) {
		values().find { it.id == id }
	}

	@Override
	String toString() {
		return name
	}
}