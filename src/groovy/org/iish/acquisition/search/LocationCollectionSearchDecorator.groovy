package org.iish.acquisition.search

/**
 * Decorator to implement search criteria for depot locations.
 */
class LocationCollectionSearchDecorator extends CollectionSearchDecorator {

	LocationCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for depot locations.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> where = collectWithIndex(getCollectionSearchCommand().location) { it, i ->
			"l.depot.id = :location$i"
		}

		return addToListAsString(super.getWhere(), where, ' OR ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for depot locations.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Map<String, Object> parameters = collectEntriesWithIndex(getCollectionSearchCommand().location) { it, i ->
			["location$i": it]
		}

		return addToMap(super.getParameters(), parameters)
	}
}
