package org.iish.acquisition.search

import org.iish.acquisition.command.CollectionSearchCommand

/**
 * Decorator pattern implementation, all decorators should extend this class.
 */
abstract class CollectionSearchDecorator extends AbstractCollectionSearch {
	protected final AbstractCollectionSearch decoratedCollectionSearch

	CollectionSearchDecorator(AbstractCollectionSearch decoratedCollectionSearch) {
		this.decoratedCollectionSearch = decoratedCollectionSearch
	}

	/**
	 * Returns the collection search command from the parent object.
	 * @return The collection search command from the parent object.
	 */
	@Override
	CollectionSearchCommand getCollectionSearchCommand() {
		return decoratedCollectionSearch.getCollectionSearchCommand()
	}

	/**
	 * Returns the HQL WHERE criteria to use.
	 * Will be concatenated with 'AND'.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		return decoratedCollectionSearch.getWhere()
	}

	/**
	 * Returns the HQL ORDER BY fields and sort order to use.
	 * @return A list with fields and sort orders, which can be added to the HQL ORDER BY clause.
	 */
	@Override
	protected List<String> getSort() {
		return decoratedCollectionSearch.getSort()
	}

	/**
	 * Returns a map with the values to use for the parameters identified in a query.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		return decoratedCollectionSearch.getParameters()
	}

	/**
	 * Returns all matching collections (or projections), for the given where, sort and parameters data.
	 * @param where The HQL WHERE criteria statements to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param parameters The parameters to apply on the query.
	 * @return All matching collections (or projections), based on the given data.
	 */
	@Override
	protected final List getResultsFor(List<String> where, List<String> sort, Map<String, Object> parameters) {
		return decoratedCollectionSearch.getResultsFor(where, sort, parameters)
	}

	/**
	 * Returns all matching collections paginated, for the given where, sort and parameters data.
	 * @param where The HQL WHERE criteria statements to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param parameters The parameters to apply on the query.
	 * @param max The maximum number of results to return.
	 * @param offset The offset from the first result.
	 * @return All matching, paginated collections, based on the given data.
	 */
	@Override
	protected final PagedResultList getPaginatedResultsFor(List<String> where, List<String> sort,
	                                                       Map<String, Object> parameters, int max, int offset) {
		return decoratedCollectionSearch.getPaginatedResultsFor(where, sort, parameters, max, offset)
	}

	/**
	 * Returns the previous and the next id for the given id in the results,
	 * for the given where, sort and parameters data.
	 * @param where The HQL WHERE criteria statements to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param parameters The parameters to apply on the query.
	 * @param id The id in the results to look for.
	 * @return The pager with the previous and the next id.
	 */
	@Override
	protected final Pager getPagedResultsFor(List<String> where, List<String> sort, Map<String, Object> parameters,
	                                         Long id) {
		return decoratedCollectionSearch.getPagedResultsFor(where, sort, parameters, id)
	}

	/**
	 * Simple utility function to join a list and add it to another list in one go.
	 * @param originalList The original list.
	 * @param toAdd The list with values to add as a string to the original list.
	 * @param joinString The string to use to join the values of the toAdd list.
	 * @return The original list again.
	 */
	protected static List<String> addToListAsString(List<String> originalList, List<String> toAdd, String joinString) {
		if (toAdd?.size() > 0) {
			String joined = toAdd.join(joinString)
			originalList << joined
		}

		return originalList
	}

	/**
	 * Simple utility function to add a map to another map and return it in one go.
	 * @param originalMap The original map.
	 * @param toAdd The map to add to the original map.
	 * @return The original map again.
	 */
	protected static Map<String, Object> addToMap(Map<String, Object> originalMap, Map<String, Object> toAdd) {
		if (toAdd?.size() > 0) {
			originalMap << toAdd
		}

		return originalMap
	}

	/**
	 * Simple utility function to extend the collect() method,
	 * such that an index is sent as well while iterating the collection.
	 * @param self A collection.
	 * @param transform The closure used to transform each item of the collection.
	 * @return A List of the transformed values.
	 */
	protected static <T> List<T> collectWithIndex(Collection<?> self, Closure<T> transform) {
		int i = 0
		return self ? self.collect { transform(it, i++) } : []
	}

	/**
	 * Simple utility function to extend the collectEntries() method,
	 * such that an index is sent as well while iterating the collection.
	 * @param self A collection.
	 * @param transform The closure used for transforming, which has an item from self as the parameter and
	 * should return a Map.Entry, a Map or a two-element list containing the resulting key and value.
	 * @return A Map of the transformed entries.
	 */
	protected static <K, V> Map<K, V> collectEntriesWithIndex(Collection<?> self, Closure<?> transform) {
		int i = 0
		return self ? self.collectEntries { transform(it, i++) } : [:]
	}
}
