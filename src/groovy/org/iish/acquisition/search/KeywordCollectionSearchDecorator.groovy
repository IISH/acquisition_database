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

			// Only do a regex search if this value is not a number, else match on id
			if (it.isLong()) {
				whereOr << "c.id = :keywordId$i"
			}
			else {
				whereOr << "REGEXP(c.name, :keywordName$i) = 1"
				whereOr << "REGEXP(c.content, :keywordContent$i) = 1"
				whereOr << "REGEXP(c.listsAvailable, :keywordListsAvailable$i) = 1"
				whereOr << "REGEXP(c.toBeDone, :keywordToBeDone$i) = 1"
				whereOr << "REGEXP(c.owner, :keywordOwner$i) = 1"
				whereOr << "REGEXP(c.contactPerson, :keywordContactPerson$i) = 1"
				whereOr << "REGEXP(c.remarks, :keywordRemarks$i) = 1"
				whereOr << "REGEXP(c.originalPackageTransport, :keywordOriginalPackageTransport$i) = 1"
				whereOr << "REGEXP(l.cabinet, :keywordCabinet$i) = 1"
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
			// If this value is a number, then match on id, otherwise it is an regex search
			if (it.isLong()) {
				parameters << ["keywordId$i": it.toLong()]
			}
			else {
				parameters << ["keywordName$i": it]
				parameters << ["keywordContent$i": it]
				parameters << ["keywordListsAvailable$i": it]
				parameters << ["keywordToBeDone$i": it]
				parameters << ["keywordOwner$i": it]
				parameters << ["keywordContactPerson$i": it]
				parameters << ["keywordRemarks$i": it]
				parameters << ["keywordOriginalPackageTransport$i": it]
				parameters << ["keywordCabinet$i": it]
			}
		}

		return parameters
	}
}
