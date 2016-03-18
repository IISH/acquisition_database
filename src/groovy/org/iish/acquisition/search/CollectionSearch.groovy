package org.iish.acquisition.search

import org.iish.acquisition.domain.Collection
import org.iish.acquisition.domain.MaterialType

/**
 * Search the collections.
 */
interface CollectionSearch {

	/**
	 * Returns all matching collections (or projections), without pagination.
	 * @return All matching collections (or projections).
	 */
	List<Collection> getResults()

	/**
	 * Returns all matching Collections with pagination.
	 * @param max The maximum number of results to return.
	 * @param offset The offset from the first result.
	 * @return All matching collections for the current page, based on the given max and offset.
	 */
	PagedResultList getPaginatedResults(int max, int offset)

	/**
	 * Returns the previous and the next id for the given id in the results.
	 * @param id The id in the results to look for.
	 * @return The pager with the previous and the next id.
	 */
	Pager getPagedResults(long id)

	/**
	 * Returns all matching analog material types.
	 * @return All matching analog material types.
	 */
	List<MaterialType> getMatchingAnalogMaterials()

	/**
	 * Returns all matching digital material types.
	 * @return All matching digital material types.
	 */
	List<MaterialType> getMatchingDigitalMaterials()
}