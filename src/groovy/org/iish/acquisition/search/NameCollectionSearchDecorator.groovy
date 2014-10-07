package org.iish.acquisition.search

/**
 * Decorator to implement search criteria for collection names.
 */
class NameCollectionSearchDecorator extends CollectionSearchDecorator {

	NameCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for collection names.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> collectionNames = getCollectionSearchCommand().getAsListOfValues('collectionName')
		List<String> where = collectWithIndex(collectionNames) { it, i ->
			"c.name LIKE :name$i"
		}

		return addToListAsString(super.getWhere(), where, ' OR ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for collection names.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		List<String> collectionNames = getCollectionSearchCommand().getAsListOfValues('collectionName')
		Map<String, Object> parameters = collectEntriesWithIndex(collectionNames) { it, i ->
			["name$i": "%$it%"]
		}

		return addToMap(super.getParameters(), parameters)
	}
}
