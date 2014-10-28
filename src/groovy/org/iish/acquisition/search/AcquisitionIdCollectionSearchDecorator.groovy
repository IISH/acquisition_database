package org.iish.acquisition.search

import org.iish.acquisition.domain.AcquisitionType

/**
 * Decorator to implement search criteria for the acquisition id.
 */
class AcquisitionIdCollectionSearchDecorator extends CollectionSearchDecorator {

	AcquisitionIdCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for the acquisition id.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		Integer acquisitionTypeId = getCollectionSearchCommand().acquisitionTypeId
		String acquisitionId = getCollectionSearchCommand().acquisitionId

		if (acquisitionTypeId && acquisitionId && !acquisitionId.isAllWhitespace()) {
			return super.getWhere() + ['c.acquisitionTypeId = :acquisitionTypeId', 'c.acquisitionId = :acquisitionId']
		}

		return super.getWhere()
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for the acquisition id.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Integer acquisitionTypeId = getCollectionSearchCommand().acquisitionTypeId
		String acquisitionId = getCollectionSearchCommand().acquisitionId

		if (acquisitionTypeId && acquisitionId && !acquisitionId.isAllWhitespace()) {
			return super.getParameters() << [
					'acquisitionTypeId': AcquisitionType.getById(acquisitionTypeId),
					'acquisitionId'    : acquisitionId.trim()
			]
		}

		return super.getParameters()
	}
}
