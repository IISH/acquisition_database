package org.iish.acquisition.search

/**
 * A simple replacement for the PagedResultList implementation provided by Grails.
 * This implementation does not handle pagination by itself.
 * @see grails.orm.PagedResultList
 */
class PagedResultList extends ArrayList {
	protected int totalCount

	/**
	 * Returns the total count.
	 * @return The total count.
	 */
	int getTotalCount() {
		return totalCount
	}

	/**
	 * Sets the total count.
	 * @param totalCount The total count.
	 */
	void setTotalCount(int totalCount) {
		this.totalCount = totalCount
	}
}
