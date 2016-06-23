package org.iish.acquisition.domain

/**
 * Describes the possible digital material status sub-codes.
 */
enum DigitalMaterialStatusSubCode {
	REQUESTED(1, 'Requested'), RUNNING(2, 'Running'), FINISHED(3, 'Finished'), FAILED(4, 'Failed')

	int id
	String name

	DigitalMaterialStatusSubCode(int id, String name) {
		this.id = id
		this.name = name
	}

	/**
	 * Returns a digital material status sub-code by its id.
	 * @param id The id in question.
	 * @return The matching digital material status sub-code, if found.
	 */
	static DigitalMaterialStatusSubCode getById(Integer id) {
		values().find { it.id == id }
	}

	@Override
	String toString() {
		return name
	}
}