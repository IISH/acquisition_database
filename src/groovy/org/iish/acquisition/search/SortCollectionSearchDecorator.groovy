package org.iish.acquisition.search

/**
 * Decorator to implement sorting of the search results on a single property.
 */
class SortCollectionSearchDecorator extends CollectionSearchDecorator {
	private static final List<String> SORT_FIELDS = ['name', 'analog_material', 'digital_material', 'date',
	                                                 'location', 'timer_deadline', 'digital_status']

	private String sortOrder = 'asc'

	SortCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Returns the HQL ORDER BY fields and sort order to use.
	 * @return A list with fields and sort orders, which can be added to the HQL ORDER BY clause.
	 */
	@Override
	protected List<String> getSort() {
		List<String> sort = super.getSort()
		String sortField = getCollectionSearchCommand().sort?.trim()?.toLowerCase()
		String order = getCollectionSearchCommand().order?.trim()?.toLowerCase()

		if (sortField && SORT_FIELDS.contains(sortField)) {
			sortOrder = order.equals('asc') ? 'ASC' : 'DESC'
			switch (sortField) {
				case 'name':
					sort.addAll(sortByCollectionName())
					break
				case 'analog_material':
					sort.addAll(sortByAnalogMaterial())
					break
				case 'digital_material':
					sort.addAll(sortByDigitalMaterial())
					break
				case 'date':
					sort.addAll(sortByDateOfArrival())
					break
				case 'location':
					sort.addAll(sortByLocation())
					break
				case 'timer_deadline':
					sort.addAll(sortByTimerDeadline())
					break
				case 'digital_status':
					sort.addAll(sortByDigitalStatus())
					break
			}
		}

		return sort
	}

	/**
	 * Applies sorting on the collection name.
	 * @return A list to apply sorting on the collection name.
	 */
	private List<String> sortByCollectionName() {
		return ["c_main.name $sortOrder"]
	}

	/**
	 * Applies sorting on analog material types.
	 * @return A list to apply sorting on analog material types.
	 */
	private List<String> sortByAnalogMaterial() {
		return ["amt_main.name $sortOrder"]
	}

	/**
	 * Applies sorting on digital material types.
	 * @return A list to apply sorting on digital material types.
	 */
	private List<String> sortByDigitalMaterial() {
		return ["dmt_main.name $sortOrder"]
	}

	/**
	 * Applies sorting on the date of arrival.
	 * @return A list with criteria to apply sorting on the date of arrival.
	 */
	private List<String> sortByDateOfArrival() {
		return ["c_main.dateOfArrival $sortOrder"]
	}

	/**
	 * Applies sorting on collection locations.
	 * @return A list with criteria to apply sorting on collection locations.
	 */
	private List<String> sortByLocation() {
		return ["d_main.name $sortOrder", "l_main.cabinet $sortOrder"]
	}

	/**
	 * Applies sorting on the timer deadline.
	 * @return A list with criteria to apply sorting on the timer deadline.
	 */
	private List<String> sortByTimerDeadline() {
		return ["dms_main.ingestDelayed $reverseOrder", "dms_main.timerStarted $sortOrder"]
	}

	/**
	 * Applies sorting on the digital status.
	 * @return A list with criteria to apply sorting on the digital status.
	 */
	private List<String> sortByDigitalStatus() {
		return ["dms_main.statusCode.id $sortOrder"]
	}

	/**
	 * Returns the reverse order.
	 * @return The reverse order.
	 */
	private String getReverseOrder() {
		return sortOrder.equalsIgnoreCase('ASC') ? 'DESC' : 'ASC'
	}
}
