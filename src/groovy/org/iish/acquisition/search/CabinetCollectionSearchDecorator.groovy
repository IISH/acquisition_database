package org.iish.acquisition.search

/**
 * Decorator to implement search criteria for cabinets.
 */
class CabinetCollectionSearchDecorator extends CollectionSearchDecorator {

	CabinetCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for cabinets.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> collectionNames = getCollectionSearchCommand().getAsListOfValues('cabinet')
		List<String> where = collectWithIndex(collectionNames) { it, i ->
			"l.cabinet LIKE :cabinet$i"
		}

		return addToListAsString(super.getWhere(), where, ' OR ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for cabinets.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		List<String> collectionNames = getCollectionSearchCommand().getAsListOfValues('cabinet')
		Map<String, Object> parameters = collectEntriesWithIndex(collectionNames) { it, i ->
			["cabinet$i": "%$it%"]
		}

		return addToMap(super.getParameters(), parameters)
	}
}
