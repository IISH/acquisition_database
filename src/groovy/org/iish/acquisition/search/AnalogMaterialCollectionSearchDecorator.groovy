package org.iish.acquisition.search

/**
 * Decorator to implement search criteria for analog materials.
 */
class AnalogMaterialCollectionSearchDecorator extends CollectionSearchDecorator {

	AnalogMaterialCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for analog materials.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> where = collectWithIndex(getCollectionSearchCommand().analog) { it, i ->
			"am.materialType.id = :analogMaterial$i"
		}

		return addToListAsString(super.getWhere(), where, ' OR ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for analog materials.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Map<String, Object> parameters = collectEntriesWithIndex(getCollectionSearchCommand().analog) { it, i ->
			["analogMaterial$i": it]
		}

		return addToMap(super.getParameters(), parameters)
	}
}
