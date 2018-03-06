package org.iish.acquisition.util

import org.iish.acquisition.domain.DigitalMaterialStatusCode

/**
 * Holds all predefined collection search filters.
 */
class CollectionSearchFilters {
	static final List<SearchFilter> SEARCH_FILTERS = [
			new SearchFilter(
					'status_first', 'page.collection.filters.statusFirst.label', [
					'sort'         : 'timer_deadline',
					'order'        : 'asc',
					'columns'      : ['name', 'timer_deadline', 'digital_status', 'analog_material', 'digital_material'],
					'statusDigital': DigitalMaterialStatusCode.
							findAllByIdLessThan(DigitalMaterialStatusCode.STAGINGAREA)*.id
			]),
			new SearchFilter(
					'status_second', 'page.collection.filters.statusSecond.label', [
					'sort'         : 'timer_deadline',
					'order'        : 'desc',
					'columns'      : ['name', 'timer_deadline', 'digital_status', 'analog_material', 'digital_material'],
					'statusDigital': DigitalMaterialStatusCode.
							findAllByIdGreaterThanEquals(DigitalMaterialStatusCode.STAGINGAREA)*.id
			]),
			new SearchFilter(
					'status_second', 'page.collection.filters.statusThird.label', [
					'sort'         : 'timer_deadline',
					'order'        : 'desc',
					'columns'      : ['name', 'timer_deadline', 'digital_status', 'analog_material', 'digital_material'],
					'statusDigital': DigitalMaterialStatusCode.
							findAllByIdGreaterThanEquals(DigitalMaterialStatusCode.AIP)*.id
			])
	]

	/**
	 * Defines a search filter.
	 */
	static final class SearchFilter {
		final String name
		final String nameKey
		final Map<String, Object> filter

		SearchFilter(String name, String nameKey, Map<String, Object> filter) {
			this.name = name
			this.nameKey = nameKey
			this.filter = filter
		}

		Map<String, Object> getFilter() {
			return filter + [search: 1, name: name]
		}
	}
}
