package org.iish.acquisition.command

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.iish.acquisition.domain.Status
import org.iish.acquisition.search.*

/**
 * Command object which contains all valid collection search parameters.
 */
class CollectionSearchCommand {
	String keyword
	String collectionName
	List<Long> location
	Date fromDate
	Date toDate
	String contactPerson
	List<Long> status
	List<Long> analog
	List<Long> digital

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
	 * Creates a new CollectionSearch object with all decorators using this command object.
	 * @return A CollectionSearch for this command object with all decorators.
	 */
	CollectionSearch getCollectionSearch() {
		CollectionSearch collectionSearch = new CollectionSearchImpl(this)

		collectionSearch = new KeywordCollectionSearchDecorator(collectionSearch)
		collectionSearch = new NameCollectionSearchDecorator(collectionSearch)
		collectionSearch = new LocationCollectionSearchDecorator(collectionSearch)
		collectionSearch = new DateCollectionSearchDecorator(collectionSearch)
		collectionSearch = new ContactPersonCollectionSearchDecorator(collectionSearch)
		collectionSearch = new StatusCollectionSearchDecorator(collectionSearch)
		collectionSearch = new AnalogMaterialCollectionSearchDecorator(collectionSearch)
		collectionSearch = new DigitalMaterialCollectionSearchDecorator(collectionSearch)
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
				keyword       : null,
				collectionName: null,
				location      : null,
				fromDate      : null,
				toDate        : null,
				contactPerson : null,
				status        : [Status.NOT_PROCESSED_ID],
				analog        : null,
				digital       : null,
				sort          : null,
				order         : null,
				search        : 1
		]

		params?.putAll(defaultValues)
		new CollectionSearchCommand(defaultValues)
	}
}
