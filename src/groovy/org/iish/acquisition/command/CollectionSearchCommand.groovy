package org.iish.acquisition.command

import org.iish.acquisition.domain.Status
import org.iish.acquisition.search.*

/**
 * Command object which contains all valid collection search parameters.
 */
class CollectionSearchCommand {
	String keyword
	Integer acquisitionTypeId
	String acquisitionId
	String collectionName
	List<Long> location
	String cabinet
	Date fromDate
	Date toDate
	String contactPerson
	List<Long> status
	List<Long> statusDigital
	List<Integer> subStatusDigital
	Boolean collectionLevelReady
	List<Long> analog
	List<Long> digital
	List<Long> misc
	List<Integer> priority
	List<Integer> level
	Boolean timerPassed

	// For sorting of the search results
	String sort
	String order

	// Provides a simple way to test for the existence of a search
	Integer search = 0

	// Name of the columns to display
	List<String> columns

	/**
	 * Tests if 'search' equals 1, because in that case, a search was performed.
	 * @return Whether a search was performed.
	 */
	boolean isASearch() {
		search == 1
	}

	/**
	 * Returns the requested columns or the default columns if no columns were specified.
	 * @return The requested columns.
	 */
	List<String> getColumns() {
		if (!columns || columns.isEmpty()) {
			return ['name', 'analog_material', 'digital_material', 'date', 'location']
		}
		return columns
	}

	/**
	 * Splits a string of values (separated by spaces) into a list of individual values.
	 * @param property The name of the property of this object to split into a list of values.
	 * @return A list of individual values.
	 */
	List<String> getAsListOfValues(String property) {
		if (this."$property") {
			List<String> values = this."$property".split() as List<String>
			return values.findResults { String value ->
				!value.isAllWhitespace() ? value.trim() : null
			}
		}

		return []
	}

	/**
	 * Creates a new CollectionSearch object with all decorators using this command object.
	 * @return A CollectionSearch for this command object with all decorators.
	 */
	CollectionSearch getCollectionSearch() {
		CollectionSearch collectionSearch = new CollectionSearchImpl(this)

		collectionSearch = new KeywordCollectionSearchDecorator(collectionSearch)
		collectionSearch = new AcquisitionIdCollectionSearchDecorator(collectionSearch)
		collectionSearch = new NameCollectionSearchDecorator(collectionSearch)
		collectionSearch = new LocationCollectionSearchDecorator(collectionSearch)
		collectionSearch = new CabinetCollectionSearchDecorator(collectionSearch)
		collectionSearch = new DateCollectionSearchDecorator(collectionSearch)
		collectionSearch = new ContactPersonCollectionSearchDecorator(collectionSearch)
		collectionSearch = new StatusCollectionSearchDecorator(collectionSearch)
		collectionSearch = new CollectionLevelReadyCollectionSearchDecorator(collectionSearch)
		collectionSearch = new DigitalStatusCollectionSearchDecorator(collectionSearch)
		collectionSearch = new DigitalSubStatusCollectionSearchDecorator(collectionSearch)
		collectionSearch = new AnalogMaterialCollectionSearchDecorator(collectionSearch)
		collectionSearch = new DigitalMaterialCollectionSearchDecorator(collectionSearch)
		collectionSearch = new MiscMaterialCollectionSearchDecorator(collectionSearch)
		collectionSearch = new PriorityCollectionSearchDecorator(collectionSearch)
		collectionSearch = new LevelCollectionSearchDecorator(collectionSearch)
		collectionSearch = new TimerPassedSearchDecorator(collectionSearch)
		collectionSearch = new SortCollectionSearchDecorator(collectionSearch)

		return collectionSearch
	}

	/**
	 * Creates the default collection search parameters, with the status 'Not processed'.
	 * @param params If the params are also given, the default parameter values are also applied to this map.
	 * @return The CollectionSearchCommand object.
	 */
	static CollectionSearchCommand getDefaultCollectionSearchCommand(Map params) {
		Map defaultValues = getDefaultCollectionSearchParams()
		params?.putAll(defaultValues)

		return new CollectionSearchCommand(defaultValues)
	}

	/**
	 * Returns the default collection search params.
	 * @return The default collection search params.
	 */
	private static Map getDefaultCollectionSearchParams() {
		return [status: [Status.NOT_PROCESSED_ID], search: 1,
		        columns: ['name', 'analog_material', 'digital_material', 'date', 'location']]
	}
}
