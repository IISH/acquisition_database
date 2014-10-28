package org.iish.acquisition.search

/**
 * Decorator to implement search criteria for the collection level ready indicator.
 */
class CollectionLevelReadyCollectionSearchDecorator extends CollectionSearchDecorator {

	CollectionLevelReadyCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for the collection level ready indicator.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		if (getCollectionSearchCommand().collectionLevelReady != null) {
			return super.getWhere() + ['c.collectionLevelReady = :collectionLevelReady']
		}

		return super.getWhere()
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand
	 * for the collection level ready indicator.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		if (getCollectionSearchCommand().collectionLevelReady != null) {
			return super.getParameters() << ['collectionLevelReady': getCollectionSearchCommand().collectionLevelReady]
		}

		return super.getParameters()
	}
}
