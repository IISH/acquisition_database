package org.iish.acquisition.search

/**
 * Decorator to implement search criteria for the status of digital material of a collection.
 */
class DigitalStatusCollectionSearchDecorator extends CollectionSearchDecorator {

	DigitalStatusCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for the status of digital material of a collection.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> where = collectWithIndex(getCollectionSearchCommand().statusDigital) { it, i ->
			"dms.statusCode.id = :digitalStatus$i"
		}

		return addToListAsString(super.getWhere(), where, ' OR ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand
	 * for the status of digital material of a collection.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Map<String, Object> parameters = collectEntriesWithIndex(getCollectionSearchCommand().statusDigital) { it, i ->
			["digitalStatus$i": it]
		}

		return addToMap(super.getParameters(), parameters)
	}
}
