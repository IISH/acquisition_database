package org.iish.acquisition.search

import org.iish.acquisition.domain.Priority

/**
 * Decorator to implement search criteria for the priority of a collection.
 */
class PriorityCollectionSearchDecorator extends CollectionSearchDecorator {

	PriorityCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for the priority of a collection.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> where = collectWithIndex(getCollectionSearchCommand().priority) { it, i ->
			"c.priority = :priority$i"
		}

		return addToListAsString(super.getWhere(), where, ' OR ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for the priority of a collection.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Map<String, Object> parameters = collectEntriesWithIndex(getCollectionSearchCommand().priority) { it, i ->
			["priority$i": Priority.getById(it)]
		}

		return addToMap(super.getParameters(), parameters)
	}
}
