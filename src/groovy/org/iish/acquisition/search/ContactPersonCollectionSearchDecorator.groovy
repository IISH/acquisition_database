package org.iish.acquisition.search

/**
 * Decorator to implement search criteria for contact persons.
 */
class ContactPersonCollectionSearchDecorator extends CollectionSearchDecorator {

	ContactPersonCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for contact persons.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> contactPersons = getCollectionSearchCommand().getAsListOfValues('contactPerson')
		List<String> where = collectWithIndex(contactPersons) { it, i ->
			"c.contactPerson LIKE :contactPerson$i"
		}

		return addToListAsString(super.getWhere(), where, ' OR ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for contact persons.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		List<String> contactPersons = getCollectionSearchCommand().getAsListOfValues('contactPerson')
		Map<String, Object> parameters = collectEntriesWithIndex(contactPersons) { it, i ->
			["contactPerson$i": "%$it%"]
		}

		return addToMap(super.getParameters(), parameters)
	}
}
