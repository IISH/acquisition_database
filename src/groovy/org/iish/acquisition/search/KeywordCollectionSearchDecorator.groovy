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
		getCollectionSearchCommand().getAsListOfValuesAdvanced('keyword').eachWithIndex { it, i ->
			List<String> whereOr = []

			whereOr << "c.name LIKE :keywordName$i"
			whereOr << "c.content LIKE :keywordContent$i"
			whereOr << "c.listsAvailable LIKE :keywordListsAvailable$i"
			whereOr << "c.toBeDone LIKE :keywordToBeDone$i"
			whereOr << "c.owner LIKE :keywordOwner$i"
			whereOr << "c.contactPerson LIKE :keywordContactPerson$i"
			whereOr << "c.remarks LIKE :keywordRemarks$i"
			whereOr << "c.originalPackageTransport LIKE :keywordOriginalPackageTransport$i"

			whereOr << "l.cabinet LIKE :keywordCabinet$i"
			whereOr << "l.shelf LIKE :keywordShelf$i"

			where << "(${whereOr.join(' OR ')})"
		}

		return addToListAsString(super.getWhere(), where, ' AND ')
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for a keyword search.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Map<String, Object> parameters = super.getParameters()
		getCollectionSearchCommand().getAsListOfValuesAdvanced('keyword').eachWithIndex { it, i ->
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
