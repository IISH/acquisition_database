package org.iish.acquisition.domain

/**
 * Represents an accrual or a new collection.
 */
enum Accrual {
	NEW(1, 'New'), ACCRUAL(2, 'Accrual')

	int id
	String name

	Accrual(int id, String name) {
		this.id = id
		this.name = name
	}

	/**
	 * Returns an accrual by its id.
	 * @param id The id in question.
	 * @return The matching accrual, if found.
	 */
	static Accrual getById(Integer id) {
		values().find { it.id == id }
	}

	@Override
	String toString() {
		return name
	}
}