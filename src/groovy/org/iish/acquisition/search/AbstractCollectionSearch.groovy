package org.iish.acquisition.search

import org.iish.acquisition.command.CollectionSearchCommand
import org.iish.acquisition.domain.Collection
import org.iish.acquisition.domain.MaterialType

/**
 * Decorator pattern implementation to search the collections using a CollectionSearchCommand.
 * @see org.iish.acquisition.command.CollectionSearchCommand
 */
abstract class AbstractCollectionSearch implements CollectionSearch {

	/**
	 * Returns all matching collections (or projections), without pagination.
	 * @return All matching collections (or projections).
	 */
	@Override
	List<Collection> getResults() {
		getResultsFor(getWhere(), getSort(), getParameters())
	}

	/**
	 * Returns all matching Collections with pagination.
	 * @param max The maximum number of results to return.
	 * @param offset The offset from the first result.
	 * @return All matching collections for the current page, based on the given max and offset.
	 */
	@Override
	PagedResultList getPaginatedResults(int max, int offset) {
		getPaginatedResultsFor(getWhere(), getSort(), getParameters(), max, offset)
	}

	/**
	 * Returns the previous and the next id for the given id in the results.
	 * @param id The id in the results to look for.
	 * @return The pager with the previous and the next id.
	 */
	@Override
	Pager getPagedResults(long id) {
		getPagedResultsFor(getWhere(), getSort(), getParameters(), id)
	}

	/**
	 * Returns all matching analog material types.
	 * @return All matching analog material types.
	 */
	@Override
	List<MaterialType> getMatchingAnalogMaterials() {
		return getMaterialTypesFor(getWhere(), getSort(), getParameters(), 'amt_main')
	}

	/**
	 * Returns all matching digital material types.
	 * @return All matching digital material types.
	 */
	@Override
	List<MaterialType> getMatchingDigitalMaterials() {
		return getMaterialTypesFor(getWhere(), getSort(), getParameters(), 'dmt_main')
	}

	/**
	 * Returns the collection search command.
	 * @return The collection search command.
	 */
	abstract CollectionSearchCommand getCollectionSearchCommand()

	/**
	 * Returns the HQL WHERE criteria to use.
	 * Will be concatenated with 'AND'.
	 * @return A list of HQL WHERE criteria.
	 */
	abstract protected List<String> getWhere()

	/**
	 * Returns the HQL ORDER BY fields and sort order to use.
	 * @return A list with fields and sort orders, which can be added to the HQL ORDER BY clause.
	 */
	abstract protected List<String> getSort()

	/**
	 * Returns a map with the values to use for the parameters identified in a query.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	abstract protected Map<String, Object> getParameters()

	/**
	 * Returns all matching collections (or projections), for the given where, sort and parameters data.
	 * @param where The HQL WHERE criteria statements to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param parameters The parameters to apply on the query.
	 * @return All matching collections (or projections), based on the given data.
	 */
	abstract protected List<Collection> getResultsFor(List<String> where, List<String> sort, Map<String, Object> parameters)

	/**
	 * Returns all matching collections paginated, for the given where, sort and parameters data.
	 * @param where The HQL WHERE criteria statements to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param parameters The parameters to apply on the query.
	 * @param max The maximum number of results to return.
	 * @param offset The offset from the first result.
	 * @return All matching, paginated collections, based on the given data.
	 */
	abstract protected PagedResultList getPaginatedResultsFor(List<String> where, List<String> sort,
	                                                          Map<String, Object> parameters, int max, int offset)

	/**
	 * Returns the previous and the next id for the given id in the results,
	 * for the given where, sort and parameters data.
	 * @param where The HQL WHERE criteria statements to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param parameters The parameters to apply on the query.
	 * @param id The id in the results to look for.
	 * @return The pager with the previous and the next id.
	 */
	abstract protected Pager getPagedResultsFor(List<String> where, List<String> sort, Map<String, Object> parameters,
	                                            long id)

	/**
	 * Returns all matching material types, for the given where, sort, parameters and group by data.
	 * @param where The HQL WHERE criteria statements to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param parameters The parameters to apply on the query.
	 * @param groupByColumn The group by column.
	 * @return All matching collections (or projections), based on the given data.
	 */
	abstract protected List<MaterialType> getMaterialTypesFor(List<String> where, List<String> sort,
	                                                          Map<String, Object> parameters, String groupByColumn)
}