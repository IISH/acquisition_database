package org.iish.acquisition.search

/**
 * Decorator to implement search criteria for the status of a collection.
 */
class StatusCollectionSearchDecorator extends CollectionSearchDecorator {

	StatusCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for the status of a collection.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> where = collectWithIndex(getCollectionSearchCommand().status) { it, i ->
			"c.status.id = :status$i"
		}

		return addToListAsString(super.getWhere(), where, ' OR ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for the status of a collection.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Map<String, Object> parameters = collectEntriesWithIndex(getCollectionSearchCommand().status) { it, i ->
			["status$i": it]
		}

		return addToMap(super.getParameters(), parameters)
	}
}
