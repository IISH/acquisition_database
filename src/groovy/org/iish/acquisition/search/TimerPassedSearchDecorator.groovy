package org.iish.acquisition.search

import org.iish.acquisition.domain.DigitalMaterialStatus

/**
 * Decorator to implement search criteria for digital material of which the timer has passed (or not).
 */
class TimerPassedSearchDecorator extends CollectionSearchDecorator {

	TimerPassedSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for whether the timer has passed (or not).
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> where = super.getWhere()
		Boolean timerPassed = getCollectionSearchCommand().timerPassed

		if (timerPassed != null) {
			String passedOrNot = timerPassed ? '<' : '>'
			where << """
				(dms.ingestDelayed = false AND dms.timerStarted $passedOrNot :dateTimeExpiredInitial) OR
				(dms.ingestDelayed = true  AND dms.timerStarted $passedOrNot :dateTimeExpiredExtended)
			"""
		}

		return where
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand
	 * for whether the timer has passed (or not).
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Map<String, Object> parameters = super.getParameters()
		Boolean timerPassed = getCollectionSearchCommand().timerPassed

		if (timerPassed != null) {
			parameters.put('dateTimeExpiredInitial', DigitalMaterialStatus.getLatestCreationDateInitialExpired())
			parameters.put('dateTimeExpiredExtended', DigitalMaterialStatus.getLatestCreationDateExtendedExpired())
		}

		return parameters
	}
}
