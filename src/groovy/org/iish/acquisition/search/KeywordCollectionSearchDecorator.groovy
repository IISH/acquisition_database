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
        if (collectionSearchCommand.keyword) {
            // Only do a full text search if this value is not a number, else match on id
            if (collectionSearchCommand.keyword.isLong()) {
                return addToListAsString(super.getWhere(), ["c.id = :keyword"], ' AND ')
            }
            else {
                List<String> whereOr = []

                whereOr << "MATCH(c.name, c.content, c.listsAvailable, c.toBeDone, c.owner, " +
                        "c.contactPerson, c.remarks, c.originalPackageTransport, :keyword) > 0"
                whereOr << "MATCH(l.cabinet, :keyword) > 0"

                return addToListAsString(super.getWhere(), [whereOr.join(' OR ')], ' AND ')
            }
        }
        return super.getWhere()
	}

	/**
	 * Adds the the parameters and values collected from the CollectionSearchCommand for a keyword search.
	 * @return A map with the values to use for the parameters identified in a query.
	 */
	@Override
	protected Map<String, Object> getParameters() {
		Map<String, Object> parameters = super.getParameters()
		if (collectionSearchCommand.keyword) {
			// If this value is a number, then match on id, otherwise it is an regex search
			if (collectionSearchCommand.keyword.isLong()) {
				parameters << ["keyword": collectionSearchCommand.keyword.toLong()]
			}
			else {
				parameters << ["keyword": collectionSearchCommand.keyword]
			}
		}

		return parameters
	}
}
