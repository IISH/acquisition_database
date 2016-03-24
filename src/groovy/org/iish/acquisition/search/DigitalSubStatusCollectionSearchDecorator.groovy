package org.iish.acquisition.search

import org.iish.acquisition.domain.DigitalMaterialStatusSubCode

/**
 * Decorator to implement search criteria for the sub status of digital material of a collection.
 */
class DigitalSubStatusCollectionSearchDecorator extends CollectionSearchDecorator {

	DigitalSubStatusCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for the sub status of digital material of a collection.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> where = collectWithIndex(getCollectionSearchCommand().subStatusDigital) { it, i ->
			"dms.statusSubCode = :digitalSubStatus$i"
		}

		return addToListAsString(super.getWhere(), where, ' OR ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand
	 * for the sub status of digital material of a collection.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Map<String, Object> parameters = collectEntriesWithIndex(getCollectionSearchCommand().subStatusDigital) { it, i ->
			["digitalSubStatus$i": DigitalMaterialStatusSubCode.getById(it)]
		}

		return addToMap(super.getParameters(), parameters)
	}
}
