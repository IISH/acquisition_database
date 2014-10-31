package org.iish.acquisition.search

import org.iish.acquisition.domain.Priority

/**
 * Decorator to implement search criteria for the level of a collection.
 */
class LevelCollectionSearchDecorator extends CollectionSearchDecorator {

	LevelCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for the level of a collection.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> where = collectWithIndex(getCollectionSearchCommand().level) { it, i ->
			"c.level = :level$i"
		}

		return addToListAsString(super.getWhere(), where, ' OR ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for the level of a collection.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Map<String, Object> parameters = collectEntriesWithIndex(getCollectionSearchCommand().level) { it, i ->
			["level$i": Priority.getById(it)]
		}

		return addToMap(super.getParameters(), parameters)
	}
}
