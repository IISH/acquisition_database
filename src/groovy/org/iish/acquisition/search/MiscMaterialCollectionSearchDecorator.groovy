package org.iish.acquisition.search

/**
 * Decorator to implement search criteria for misc materials.
 */
class MiscMaterialCollectionSearchDecorator extends CollectionSearchDecorator {

	MiscMaterialCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for misc materials.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> where = collectWithIndex(getCollectionSearchCommand().misc) { it, i ->
			"mm.materialType.id = :miscMaterial$i"
		}

		return addToListAsString(super.getWhere(), where, ' OR ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for misc materials.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Map<String, Object> parameters = collectEntriesWithIndex(getCollectionSearchCommand().misc) { it, i ->
			["miscMaterial$i": it]
		}

		return addToMap(super.getParameters(), parameters)
	}
}
