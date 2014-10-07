package org.iish.acquisition.search

/**
 * Decorator to implement search criteria for digital materials.
 */
class DigitalMaterialCollectionSearchDecorator extends CollectionSearchDecorator {

	DigitalMaterialCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for digital materials.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> where = collectWithIndex(getCollectionSearchCommand().digital) { it, i ->
			"dm.materialType.id = :digitalMaterial$i"
		}

		return addToListAsString(super.getWhere(), where, ' OR ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for digital materials.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Map<String, Object> parameters = collectEntriesWithIndex(getCollectionSearchCommand().digital) { it, i ->
			["digitalMaterial$i": it]
		}

		return addToMap(super.getParameters(), parameters)
	}
}
