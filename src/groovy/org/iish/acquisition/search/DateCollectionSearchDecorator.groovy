package org.iish.acquisition.search

/**
 * Decorator to implement search criteria for a date span.
 */
class DateCollectionSearchDecorator extends CollectionSearchDecorator {

	DateCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for a date span.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		Date from = getCollectionSearchCommand().fromDate
		Date to = getCollectionSearchCommand().toDate

		if (from && to) {
			return super.getWhere() + ['c.dateOfArrival >= :dateOfArrivalFrom', 'c.dateOfArrival <= :dateOfArrivalTo']
		}

		return super.getWhere()
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for a date span.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Date from = getCollectionSearchCommand().fromDate
		Date to = getCollectionSearchCommand().toDate

		if (from && to) {
			return super.getParameters() << ['dateOfArrivalFrom': from, 'dateOfArrivalTo': to]
		}

		return super.getParameters()
	}
}
