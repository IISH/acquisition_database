package org.iish.acquisition.search

/**
 * Search the collections.
 */
interface CollectionSearch {

	/**
	 * Returns all matching collections (or projections), without pagination.
	 * @return All matching collections (or projections).
	 */
	List getResults()

	/**
	 * Returns all matching Collections with pagination.
	 * @param max The maximum number of results to return.
	 * @param offset The offset from the first result.
	 * @return All matching collections for the current page, based on the given max and offset.
	 */
	PagedResultList getPaginatedResults(int max, int offset)
}