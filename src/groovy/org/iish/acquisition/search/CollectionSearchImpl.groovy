package org.iish.acquisition.search

import org.iish.acquisition.command.CollectionSearchCommand
import org.iish.acquisition.domain.Collection

/**
 * Implementation of the base class to search for collections.
 */
class CollectionSearchImpl extends AbstractCollectionSearch {
	protected static final MAIN_SELECT = 'SELECT DISTINCT c_main'
	protected static final ID_SELECT = 'SELECT DISTINCT c_main.id'
	protected static final COUNT_SELECT = 'SELECT COUNT(DISTINCT c_main.id)'

	private final CollectionSearchCommand collectionSearchCommand

	/**
	 * Creates the base class to search for collections.
	 * @param collectionSearchCommand The search parameters.
	 */
	CollectionSearchImpl(CollectionSearchCommand collectionSearchCommand) {
		this.collectionSearchCommand = collectionSearchCommand
	}

	/**
	 * Returns the collection search command.
	 * @return The collection search command.
	 */
	@Override
	CollectionSearchCommand getCollectionSearchCommand() {
		return collectionSearchCommand
	}

	/**
	 * Returns the HQL WHERE criteria to use.
	 * Will be concatenated with 'AND'.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		return []
	}

	/**
	 * Returns the HQL ORDER BY fields and sort order to use.
	 * @return A list with fields and sort orders, which can be added to the HQL ORDER BY clause.
	 */
	@Override
	protected List<String> getSort() {
		return []
	}

	/**
	 * Returns a map with the values to use for the parameters identified in a query.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		return [:]
	}

	/**
	 * Returns all matching collections (or projections), for the given where, sort and parameters data.
	 * @param where The HQL WHERE criteria statements to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param parameters The parameters to apply on the query.
	 * @return All matching collections (or projections), based on the given data.
	 */
	@Override
	protected List getResultsFor(List<String> where, List<String> sort, Map<String, Object> parameters) {
		String query = createHqlQueryForCriteria(MAIN_SELECT, sort, where)
		return Collection.executeQuery(query, getMapWhereKeyIsString(parameters))
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
	protected PagedResultList getPaginatedResultsFor(List<String> where, List<String> sort,
	                                                 Map<String, Object> parameters, int max, int offset) {
		// We want to eager fetch the results.
		// But together with pagination, it causes problems how to limit the result set.
		// So, we will handle pagination ourselves using three, rather than two queries.
		PagedResultList pagedResultList = new PagedResultList()
		Map<String, Object> params = getMapWhereKeyIsString(parameters)

		// First query: apply a 'select distinct' on the collection ids, and count them to obtain the total count
		pagedResultList.setTotalCount(getTotalCount(where, sort, params))

		// Second query: apply a 'select distinct' on the collection ids and set the 'max' and 'offset' for pagination
		List<Long> ids = getMatchingCollectionIds(where, sort, params, max, offset)

		// Third query: using the collection ids, obtain the required collection records with eager fetching
		pagedResultList.addAll(getCollectionsForIds(ids, sort))

		return pagedResultList
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
		Pager pager = new Pager()
		Map<String, Object> params = getMapWhereKeyIsString(parameters)
		List<Long> ids = getMatchingCollectionIds(where, sort, params)

		Long previousId = null
		for (Long collectionId : ids) {
			if (id == collectionId) {
				pager.setPreviousId(previousId)
			}
			else if (id == previousId) {
				pager.setNextId(collectionId)
			}

			previousId = collectionId
		}

		return pager
	}

	/**
	 * Returns the total number of matching collections with the given criteria.
	 * @param where The HQL WHERE criteria statements to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param parameters The parameters to apply on the query.
	 * @return The total number of matching collections.
	 */
	private static int getTotalCount(List<String> where, List<String> sort, Map<String, Object> parameters) {
		String queryCount = createHqlQueryForCriteria(COUNT_SELECT, sort, where)
		return (Integer) Collection.executeQuery(queryCount, parameters).first()
	}

	/**
	 * Returns all matching collection ids, with the given criteria and pagination options.
	 * @param where The HQL WHERE criteria statements to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param parameters The parameters to apply on the query.
	 * @param max The maximum number of results to return.
	 * @param offset The offset from the first result.
	 * @return All matching collection ids for the current page, based on the paginateParams and the given criteria.
	 */
	private static List<Long> getMatchingCollectionIds(List<String> where, List<String> sort,
	                                                   Map<String, Object> parameters, int max, int offset) {
		String queryIds = createHqlQueryForCriteria(ID_SELECT, sort, where)
		return Collection.executeQuery(queryIds, parameters, [max: max, offset: offset]) as List<Long>
	}

	/**
	 * Returns all matching collection ids, with the given criteria.
	 * @param where The HQL WHERE criteria statements to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param parameters The parameters to apply on the query.
	 * @return All matching collection ids for the current page, based on the given criteria.
	 */
	private static List<Long> getMatchingCollectionIds(List<String> where, List<String> sort,
	                                                   Map<String, Object> parameters) {
		String queryIds = createHqlQueryForCriteria(ID_SELECT, sort, where)
		return Collection.executeQuery(queryIds, parameters) as List<Long>
	}

	/**
	 * Returns a subset of the matching collections using the given list of collection ids.
	 * @param ids The list of ids matching collections to obtain from the complete result set.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @return All the matching collections.
	 */
	private static List<Collection> getCollectionsForIds(List<Long> ids, List<String> sort) {
		List<Collection> results = []
		if (ids.size() > 0) {
			int i = 0
			Map<String, Long> idParameters = ids.collectEntries { ["id${i++}": it] }

			String queryCollections = createHqlQueryForIds(MAIN_SELECT, sort, ids.size())
			results = Collection.executeQuery(queryCollections, getMapWhereKeyIsString(idParameters))
		}

		return results
	}

	/**
	 * Creates an HQL query on collections using the given select, sort and where.
	 * @param select The HQL SELECT clause to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param where The HQL WHERE criteria to use.
	 * @return An HQL query on collections.
	 */
	private static String createHqlQueryForCriteria(String select, List<String> sort, List<String> where) {
		String whereQuery = (where.size() > 0) ? "WHERE (${where.join(') AND (')})" : ''

		return createHqlWithSubSelect(select, sort, """
			SELECT c.id
			FROM Collection AS c
			LEFT JOIN c.analogMaterialCollection AS amc
			LEFT JOIN amc.materials AS am
			LEFT JOIN am.materialType AS amt
			LEFT JOIN c.digitalMaterialCollection AS dmc
			LEFT JOIN dmc.materials AS dm
			LEFT JOIN dm.materialType AS dmt
			LEFT JOIN c.miscMaterialCollection AS mmc
			LEFT JOIN mmc.materials AS mm
			LEFT JOIN mm.materialType AS mmt
			LEFT JOIN c.locations AS l
			LEFT JOIN l.depot AS d
			$whereQuery
		""")
	}

	/**
	 * Creates an HQL query on collections using the given select, sort and the number of necessary collection ids.
	 * @param select The HQL SELECT clause to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param nrOfIds The number of ids to add of the collections to return.
	 * @return An HQL query on collections.
	 */
	private static String createHqlQueryForIds(String select, List<String> sort, int nrOfIds) {
		String idParameters = (0..<nrOfIds).collect { ":id$it" }.join(',')
		return createHqlWithSubSelect(select, sort, idParameters)
	}

	/**
	 * Creates an HQL query on collections using the given select, sort and sub-select query to use.
	 * @param select The HQL SELECT clause to use.
	 * @param sort The HQL ORDER BY fields and sort order to use.
	 * @param subSelect The HQL sub-select query to use.
	 * @return An HQL query on collections.
	 */
	private static String createHqlWithSubSelect(String select, List<String> sort, String subSelect) {
		String fetch = 'FETCH'
		String sortQuery = (sort.size() > 0) ? "ORDER BY ${sort.join(',')}" : 'ORDER BY c_main.id DESC'

		// Only eager fetch when we actually return collection instances
		if (!MAIN_SELECT.equals(select)) {
			fetch = ''
			// Do not sort if we perform a count
			if (!ID_SELECT.equals(select)) {
				sortQuery = ''
			}
		}

		return """
			$select
			FROM Collection AS c_main
			LEFT JOIN $fetch c_main.analogMaterialCollection AS amc_main
			LEFT JOIN $fetch amc_main.materials AS am_main
			LEFT JOIN $fetch am_main.materialType AS amt_main
			LEFT JOIN $fetch c_main.digitalMaterialCollection AS dmc_main
			LEFT JOIN $fetch dmc_main.materials AS dm_main
			LEFT JOIN $fetch dm_main.materialType AS dmt_main
			LEFT JOIN $fetch c_main.miscMaterialCollection AS mmc_main
			LEFT JOIN $fetch mmc_main.materials AS mm_main
			LEFT JOIN $fetch mm_main.materialType AS mmt_main
			LEFT JOIN $fetch c_main.locations AS l_main
			LEFT JOIN $fetch l_main.depot AS d_main
			LEFT JOIN $fetch c_main.ingestDepotStatus AS ids_main
			WHERE c_main.id IN ( $subSelect )
			$sortQuery
		"""
	}

	/**
	 * Makes sure that the keys of the map are instances of String. (Not GStrings for example)
	 * @param map The map of which the keys should be strings.
	 * @return The map, but now certainly with string keys.
	 */
	private static Map<String, Object> getMapWhereKeyIsString(Map map) {
		return map.collectEntries { key, value ->
			[(key.toString()): value]
		} as Map<String, Object>
	}
}