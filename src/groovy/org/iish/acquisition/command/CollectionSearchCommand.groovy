package org.iish.acquisition.command

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
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
	Boolean collectionLevelReady
	List<Long> analog
	List<Long> digital
	List<Long> misc
	List<Integer> priority
	List<Integer> level

	// For sorting of the search results
	String sort
	String order

	// Provides a simple way to test for the existence of a search
	Integer search = 0

	/**
	 * Tests if 'search' equals 1, because in that case, a search was performed.
	 * @return Whether a search was performed.
	 */
	boolean isASearch() {
		search == 1
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
	 * Splits a string of values (separated by spaces) into a list of individual values.
	 * Also allows wildcards '*'.
	 * @param property The name of the property of this object to split into a list of values.
	 * @return A list of individual values.
	 */
	List<String> getAsListOfValuesAdvanced(String property) {
		List<String> values = []
		getAsListOfValues(property).each { String value ->
			// Only do a regex search if this value is not a number
			if (!value.isLong()) {
				// Find all repeating wildcards and replace them by a single wildcard
				value = value.replaceAll('(\\*)\\1+', '*')
				// Anything else but alphanumeric characters and the wildcard character is not allowed
				value = value.replaceAll('[^\\p{Alnum}\\*]', '')

				// If the value is empty or only a single wildcard, ignore it
				if (!value.isEmpty() && !value.equals('*')) {
					// Match the start of a word, unless a wildcard was placed there
					if (!value.startsWith('*')) {
						value = '[[:<:]]' + value
					}
					else {
						value = value.substring(1)
					}

					// Match the end of a word, unless a wildcard was placed there
					if (!value.endsWith('*')) {
						value = value + '[[:>:]]'
					}
					else {
						value = value.substring(0, value.length() - 1)
					}

					// Now replace all occurrences of a wildcard inside a word
					value = value.replace('*', '[[:alnum:]]*')
				}
			}

			values << value
		}

		return values
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
		collectionSearch = new AnalogMaterialCollectionSearchDecorator(collectionSearch)
		collectionSearch = new DigitalMaterialCollectionSearchDecorator(collectionSearch)
		collectionSearch = new MiscMaterialCollectionSearchDecorator(collectionSearch)
		collectionSearch = new PriorityCollectionSearchDecorator(collectionSearch)
		collectionSearch = new LevelCollectionSearchDecorator(collectionSearch)
		collectionSearch = new SortCollectionSearchDecorator(collectionSearch)

		return collectionSearch
	}

	/**
	 * Creates the default collection search parameters, with the status 'Not processed'.
	 * @param params If the params are also given, the default parameter values are also applied to this map.
	 * @return The CollectionSearchCommand object.
	 */
	static CollectionSearchCommand getDefaultCollectionSearchCommand(GrailsParameterMap params) {
		Map defaultValues = [
				keyword             : null,
				acquisitionTypeId   : null,
				acquisitionId       : null,
				collectionName      : null,
				location            : null,
				cabinet             : null,
				fromDate            : null,
				toDate              : null,
				contactPerson       : null,
				status              : [Status.NOT_PROCESSED_ID],
				collectionLevelReady: null,
				statusDigital       : null,
				analog              : null,
				digital             : null,
				misc                : null,
				priority            : null,
				level               : null,
				sort                : null,
				order               : null,
				search              : 1
		]

		params?.putAll(defaultValues)
		new CollectionSearchCommand(defaultValues)
	}

	/**
	 * Creates the 'timer not passed' collection search parameters.
	 * @param params If the params are also given, the default parameter values are also applied to this map.
	 * @return The CollectionSearchCommand object.
	 */
	static CollectionSearchCommand getTimerNotPassedCollectionSearchCommand(GrailsParameterMap params) {
		Map defaultValues = [
				keyword             : null,
				acquisitionTypeId   : null,
				acquisitionId       : null,
				collectionName      : null,
				location            : null,
				cabinet             : null,
				fromDate            : null,
				toDate              : null,
				contactPerson       : null,
				status              : null,
				collectionLevelReady: null,
				statusDigital       : null,
				analog              : null,
				digital             : null,
				misc                : null,
				priority            : null,
				level               : null,
				sort                : null,
				order               : null,
				search              : 1
		]

		params?.putAll(defaultValues)
		new CollectionSearchCommand(defaultValues)
	}

	/**
	 * Creates the 'timer passed' collection search parameters.
	 * @param params If the params are also given, the default parameter values are also applied to this map.
	 * @return The CollectionSearchCommand object.
	 */
	static CollectionSearchCommand getTimerPassedCollectionSearchCommand(GrailsParameterMap params) {
		Map defaultValues = [
				keyword             : null,
				acquisitionTypeId   : null,
				acquisitionId       : null,
				collectionName      : null,
				location            : null,
				cabinet             : null,
				fromDate            : null,
				toDate              : null,
				contactPerson       : null,
				status              : null,
				collectionLevelReady: null,
				statusDigital       : null,
				analog              : null,
				digital             : null,
				misc                : null,
				priority            : null,
				level               : null,
				sort                : null,
				order               : null,
				search              : 1
		]

		params?.putAll(defaultValues)
		new CollectionSearchCommand(defaultValues)
	}
}
