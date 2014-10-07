package org.iish.acquisition.search

/**
 * Decorator to implement search criteria for a keyword search.
 */
class KeywordCollectionSearchDecorator extends CollectionSearchDecorator {

	KeywordCollectionSearchDecorator(AbstractCollectionSearch collectionSearch) {
		super(collectionSearch)
	}

	/**
	 * Adds the search criteria for a keyword search.
	 * @return A list of HQL WHERE criteria.
	 */
	@Override
	protected List<String> getWhere() {
		List<String> where = []
		getCollectionSearchCommand().getAsListOfValues('keyword').eachWithIndex { it, i ->
			where << "c.name LIKE :keywordName$i"
			where << "c.content LIKE :keywordContent$i"
			where << "c.listsAvailable LIKE :keywordListsAvailable$i"
			where << "c.toBeDone LIKE :keywordToBeDone$i"
			where << "c.owner LIKE :keywordOwner$i"
			where << "c.contactPerson LIKE :keywordContactPerson$i"
			where << "c.remarks LIKE :keywordRemarks$i"
			where << "c.originalPackageTransport LIKE :keywordOriginalPackageTransport$i"

			where << "l.cabinet LIKE :keywordCabinet$i"
			where << "l.shelf LIKE :keywordShelf$i"
		}

		return addToListAsString(super.getWhere(), where, ' OR ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for a keyword search.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Map<String, Object> parameters = super.getParameters()
		getCollectionSearchCommand().getAsListOfValues('keyword').eachWithIndex { it, i ->
			String value = "%$it%"

			parameters << ["keywordName$i": value]
			parameters << ["keywordContent$i": value]
			parameters << ["keywordListsAvailable$i": value]
			parameters << ["keywordToBeDone$i": value]
			parameters << ["keywordOwner$i": value]
			parameters << ["keywordContactPerson$i": value]
			parameters << ["keywordRemarks$i": value]
			parameters << ["keywordOriginalPackageTransport$i": value]

			parameters << ["keywordCabinet$i": value]
			parameters << ["keywordShelf$i": value]
		}

		return parameters
	}
}
