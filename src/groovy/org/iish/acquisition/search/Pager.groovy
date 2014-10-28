package org.iish.acquisition.search

/**
 * Provides the previous and the next id for a simple pager.
 */
class Pager {
	Long previousId
	Long nextId

	/**
	 * Sets the previous id.
	 * @param previousId The previous id.
	 */
	void setPreviousId(Long previousId) {
		this.previousId = previousId
	}

	/**
	 * Sets the next id.
	 * @param nextId The next id.
	 */
	void setNextId(Long nextId) {
		this.nextId = nextId
	}

	/**
	 * Returns the previous id.
	 * @return The previous id.
	 */
	Long getPreviousId() {
		return previousId
	}

	/**
	 * Returns the next id.
	 * @return The next id.
	 */
	Long getNextId() {
		return nextId
	}
}
