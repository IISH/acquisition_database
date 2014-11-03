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

			// Each keyword has to be matched with each property four times, see comment in getParameters()
			for (int j=0; j<4; j++) {
				whereOr << "c.name LIKE :keywordName$i$j"
				whereOr << "c.content LIKE :keywordContent$i$j"
				whereOr << "c.listsAvailable LIKE :keywordListsAvailable$i$j"
				whereOr << "c.toBeDone LIKE :keywordToBeDone$i$j"
				whereOr << "c.owner LIKE :keywordOwner$i$j"
				whereOr << "c.contactPerson LIKE :keywordContactPerson$i$j"
				whereOr << "c.remarks LIKE :keywordRemarks$i$j"
				whereOr << "c.originalPackageTransport LIKE :keywordOriginalPackageTransport$i$j"

				whereOr << "l.cabinet LIKE :keywordCabinet$i$j"
				whereOr << "l.shelf LIKE :keywordShelf$i$j"
			}

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
			// We have to match a single word, however, this word may appear (0) at the end, (1) at the start,
			// (2) somewhere in the middle or (3) it is the only word in the text.
			String[] values = ["% $it", "$it %", "% $it %", it]

			for (int j=0; j<4; j++) {
				String value = values[j]

				parameters << ["keywordName$i$j": value]
				parameters << ["keywordContent$i$j": value]
				parameters << ["keywordListsAvailable$i$j": value]
				parameters << ["keywordToBeDone$i$j": value]
				parameters << ["keywordOwner$i$j": value]
				parameters << ["keywordContactPerson$i$j": value]
				parameters << ["keywordRemarks$i$j": value]
				parameters << ["keywordOriginalPackageTransport$i$j": value]

				parameters << ["keywordCabinet$i$j": value]
				parameters << ["keywordShelf$i$j": value]
			}
		}

		return parameters
	}
}
