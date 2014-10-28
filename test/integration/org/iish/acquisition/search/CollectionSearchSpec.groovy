package org.iish.acquisition.search

import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import org.iish.acquisition.command.CollectionSearchCommand
import org.iish.acquisition.domain.CollectionSetUp
import org.iish.acquisition.domain.Depot
import org.iish.acquisition.domain.MaterialType
import org.iish.acquisition.domain.Status
import org.junit.BeforeClass
import org.junit.Test

@TestMixin(IntegrationTestMixin)
class CollectionSearchSpec {

	@BeforeClass
	static void setUpData() {
		CollectionSetUp.setUpBootStrapData()
		CollectionSetUp.setUpCollections()
	}

	@Test
	void testKeywordSearch() {
		// MySQL has case insensitive LIKE, while H2 is case sensitive
		CollectionSearchCommand collectionSearchCommand1 = new CollectionSearchCommand()
		collectionSearchCommand1.keyword = 'keywords search for'

		CollectionSearch collectionSearch1 = new CollectionSearchImpl(collectionSearchCommand1)
		collectionSearch1 = new KeywordCollectionSearchDecorator(collectionSearch1)

		List<Collection> collections1 = collectionSearch1.getResults() as List<Collection>

		assert collections1.size() == 2
		assert collections1*.name.contains('First')
		assert collections1*.name.contains('Fourth')

		// ------------------------------------------------------------------------------------------- //

		CollectionSearchCommand collectionSearchCommand2 = new CollectionSearchCommand()
		collectionSearchCommand2.keyword = 'AAA'

		CollectionSearch collectionSearch2 = new CollectionSearchImpl(collectionSearchCommand2)
		collectionSearch2 = new KeywordCollectionSearchDecorator(collectionSearch2)

		List<Collection> collections2 = collectionSearch2.getResults() as List<Collection>

		assert collections2.size() == 3
		assert collections2*.name.contains('First')
		assert collections2*.name.contains('Fourth')
		assert collections2*.name.contains('Fifth')
	}

	@Test
	void testCollectionNameSearch() {
		CollectionSearchCommand collectionSearchCommand1 = new CollectionSearchCommand()
		collectionSearchCommand1.collectionName = 'First Fifth'

		CollectionSearch collectionSearch1 = new CollectionSearchImpl(collectionSearchCommand1)
		collectionSearch1 = new NameCollectionSearchDecorator(collectionSearch1)

		List<Collection> collections1 = collectionSearch1.getResults() as List<Collection>

		assert collections1.size() == 2
		assert collections1*.name.contains('First')
		assert collections1*.name.contains('Fifth')

		// ------------------------------------------------------------------------------------------- //

		CollectionSearchCommand collectionSearchCommand2 = new CollectionSearchCommand()
		collectionSearchCommand2.collectionName = 'Second ir'

		CollectionSearch collectionSearch2 = new CollectionSearchImpl(collectionSearchCommand2)
		collectionSearch2 = new NameCollectionSearchDecorator(collectionSearch2)

		List<Collection> collections2 = collectionSearch2.getResults() as List<Collection>

		assert collections2.size() == 3
		assert collections2*.name.contains('First')
		assert collections2*.name.contains('Second')
		assert collections2*.name.contains('Third')
	}

	@Test
	void testLocationSearch() {
		CollectionSearchCommand collectionSearchCommand1 = new CollectionSearchCommand()
		collectionSearchCommand1.location = [Depot.THIRD_FLOOR_ID, Depot.RANGEERTERREIN_ID]

		CollectionSearch collectionSearch1 = new CollectionSearchImpl(collectionSearchCommand1)
		collectionSearch1 = new LocationCollectionSearchDecorator(collectionSearch1)

		List<Collection> collections1 = collectionSearch1.getResults() as List<Collection>

		assert collections1.size() == 2
		assert collections1*.name.contains('First')
		assert collections1*.name.contains('Second')

		// ------------------------------------------------------------------------------------------- //

		CollectionSearchCommand collectionSearchCommand2 = new CollectionSearchCommand()
		collectionSearchCommand2.location = [Depot.FIFTH_FLOOR_ID]

		CollectionSearch collectionSearch2 = new CollectionSearchImpl(collectionSearchCommand2)
		collectionSearch2 = new LocationCollectionSearchDecorator(collectionSearch2)

		List<Collection> collections2 = collectionSearch2.getResults() as List<Collection>

		assert collections2.size() == 3
		assert collections2*.name.contains('First')
		assert collections2*.name.contains('Second')
		assert collections2*.name.contains('Fifth')
	}

	@Test
	void testDateRangeSearch() {
		Calendar from = Calendar.getInstance()
		Calendar to = Calendar.getInstance()

		// Unfortunately, the H2 database treats date ranges a bit different: >= becomes > and <= becomes <
		from.set(2013, 12, 31)
		to.set(2014, 04, 01)

		CollectionSearchCommand collectionSearchCommand1 = new CollectionSearchCommand()
		collectionSearchCommand1.fromDate = from.getTime()
		collectionSearchCommand1.toDate = to.getTime()

		CollectionSearch collectionSearch1 = new CollectionSearchImpl(collectionSearchCommand1)
		collectionSearch1 = new DateCollectionSearchDecorator(collectionSearch1)

		List<Collection> collections1 = collectionSearch1.getResults() as List<Collection>

		assert collections1.size() == 3
		assert collections1*.name.contains('First')
		assert collections1*.name.contains('Second')
		assert collections1*.name.contains('Third')

		// ------------------------------------------------------------------------------------------- //

		from.set(2014, 04, 01)
		to.set(2014, 10, 01)

		CollectionSearchCommand collectionSearchCommand2 = new CollectionSearchCommand()
		collectionSearchCommand2.fromDate = from.getTime()
		collectionSearchCommand2.toDate = to.getTime()

		CollectionSearch collectionSearch2 = new CollectionSearchImpl(collectionSearchCommand2)
		collectionSearch2 = new DateCollectionSearchDecorator(collectionSearch2)

		List<Collection> collections2 = collectionSearch2.getResults() as List<Collection>

		assert collections2.size() == 2
		assert collections2*.name.contains('Fourth')
		assert collections2*.name.contains('Fifth')
	}

	@Test
	void testContactPersonSearch() {
		CollectionSearchCommand collectionSearchCommand1 = new CollectionSearchCommand()
		collectionSearchCommand1.contactPerson = 'AB'

		CollectionSearch collectionSearch1 = new CollectionSearchImpl(collectionSearchCommand1)
		collectionSearch1 = new ContactPersonCollectionSearchDecorator(collectionSearch1)

		List<Collection> collections1 = collectionSearch1.getResults() as List<Collection>

		assert collections1.size() == 2
		assert collections1*.name.contains('Second')
		assert collections1*.name.contains('Third')

		// ------------------------------------------------------------------------------------------- //

		CollectionSearchCommand collectionSearchCommand2 = new CollectionSearchCommand()
		collectionSearchCommand2.contactPerson = 'AAA BC'

		CollectionSearch collectionSearch2 = new CollectionSearchImpl(collectionSearchCommand2)
		collectionSearch2 = new ContactPersonCollectionSearchDecorator(collectionSearch2)

		List<Collection> collections2 = collectionSearch2.getResults() as List<Collection>

		assert collections2.size() == 4
		assert collections2*.name.contains('First')
		assert collections2*.name.contains('Third')
		assert collections2*.name.contains('Fourth')
		assert collections2*.name.contains('Fifth')
	}

	@Test
	void testStatusSearch() {
		CollectionSearchCommand collectionSearchCommand1 = new CollectionSearchCommand()
		collectionSearchCommand1.status = [Status.NOT_PROCESSED_ID, Status.IN_PROCESS_ID]

		CollectionSearch collectionSearch1 = new CollectionSearchImpl(collectionSearchCommand1)
		collectionSearch1 = new StatusCollectionSearchDecorator(collectionSearch1)

		List<Collection> collections1 = collectionSearch1.getResults() as List<Collection>

		assert collections1.size() == 4
		assert collections1*.name.contains('First')
		assert collections1*.name.contains('Third')
		assert collections1*.name.contains('Fourth')
		assert collections1*.name.contains('Fifth')

		// ------------------------------------------------------------------------------------------- //

		CollectionSearchCommand collectionSearchCommand2 = new CollectionSearchCommand()
		collectionSearchCommand2.status = [Status.WONT_BE_PROCESSED_ID]

		CollectionSearch collectionSearch2 = new CollectionSearchImpl(collectionSearchCommand2)
		collectionSearch2 = new StatusCollectionSearchDecorator(collectionSearch2)

		List<Collection> collections2 = collectionSearch2.getResults() as List<Collection>

		assert collections2.size() == 1
		assert collections2*.name.contains('Second')
	}

	@Test
	void testAnalogMaterialSearch() {
		CollectionSearchCommand collectionSearchCommand1 = new CollectionSearchCommand()
		collectionSearchCommand1.analog = [MaterialType.ARCHIVE_ID, MaterialType.PERIODICALS_ID]

		CollectionSearch collectionSearch1 = new CollectionSearchImpl(collectionSearchCommand1)
		collectionSearch1 = new AnalogMaterialCollectionSearchDecorator(collectionSearch1)

		List<Collection> collections1 = collectionSearch1.getResults() as List<Collection>

		assert collections1.size() == 2
		assert collections1*.name.contains('First')
		assert collections1*.name.contains('Fifth')

		// ------------------------------------------------------------------------------------------- //

		CollectionSearchCommand collectionSearchCommand2 = new CollectionSearchCommand()
		collectionSearchCommand2.analog = [MaterialType.OTHER_ID]

		CollectionSearch collectionSearch2 = new CollectionSearchImpl(collectionSearchCommand2)
		collectionSearch2 = new AnalogMaterialCollectionSearchDecorator(collectionSearch2)

		List<Collection> collections2 = collectionSearch2.getResults() as List<Collection>

		assert collections2.size() == 1
		assert collections2*.name.contains('Fourth')
	}

	@Test
	void testDigitalMaterialSearch() {
		CollectionSearchCommand collectionSearchCommand1 = new CollectionSearchCommand()
		collectionSearchCommand1.digital = [MaterialType.EPHEMERA_ID]

		CollectionSearch collectionSearch1 = new CollectionSearchImpl(collectionSearchCommand1)
		collectionSearch1 = new DigitalMaterialCollectionSearchDecorator(collectionSearch1)

		List<Collection> collections1 = collectionSearch1.getResults() as List<Collection>

		assert collections1.size() == 2
		assert collections1*.name.contains('First')
		assert collections1*.name.contains('Second')

		// ------------------------------------------------------------------------------------------- //

		CollectionSearchCommand collectionSearchCommand2 = new CollectionSearchCommand()
		collectionSearchCommand2.digital = [MaterialType.BOOKS_ID, MaterialType.PERIODICALS_ID]

		CollectionSearch collectionSearch2 = new CollectionSearchImpl(collectionSearchCommand2)
		collectionSearch2 = new DigitalMaterialCollectionSearchDecorator(collectionSearch2)

		List<Collection> collections2 = collectionSearch2.getResults() as List<Collection>

		assert collections2.size() == 3
		assert collections2*.name.contains('Third')
		assert collections2*.name.contains('Fourth')
		assert collections2*.name.contains('Fifth')
	}

	@Test
	void testSortSearch() {
		CollectionSearchCommand collectionSearchCommand1 = new CollectionSearchCommand()
		collectionSearchCommand1.sort = 'name'
		collectionSearchCommand1.order = 'desc'

		CollectionSearch collectionSearch1 = new CollectionSearchImpl(collectionSearchCommand1)
		collectionSearch1 = new SortCollectionSearchDecorator(collectionSearch1)

		List<Collection> collections1 = collectionSearch1.getResults() as List<Collection>

		assert collections1.size() == 5
		assert collections1[0].name.contains('Third')
		assert collections1[1].name.contains('Second')
		assert collections1[2].name.contains('Fourth')
		assert collections1[3].name.contains('First')
		assert collections1[4].name.contains('Fifth')

		// ------------------------------------------------------------------------------------------- //

		CollectionSearchCommand collectionSearchCommand2 = new CollectionSearchCommand()
		collectionSearchCommand2.sort = 'date'
		collectionSearchCommand2.order = 'asc'

		CollectionSearch collectionSearch2 = new CollectionSearchImpl(collectionSearchCommand2)
		collectionSearch2 = new SortCollectionSearchDecorator(collectionSearch2)

		List<Collection> collections2 = collectionSearch2.getResults() as List<Collection>

		assert collections2.size() == 5
		assert collections2[0].name.contains('First')
		assert collections2[1].name.contains('Second')
		assert collections2[2].name.contains('Third')
		assert collections2[3].name.contains('Fourth')
		assert collections2[4].name.contains('Fifth')
	}

	@Test
	void testCombinationSearch() {
		Calendar from = Calendar.getInstance()
		Calendar to = Calendar.getInstance()

		from.set(2013, 12, 01)
		to.set(2014, 07, 01)

		CollectionSearchCommand collectionSearchCommand1 = new CollectionSearchCommand()
		collectionSearchCommand1.keyword = 'keywords'
		collectionSearchCommand1.collectionName = 'Fifth First Second'
		collectionSearchCommand1.location = [Depot.FIFTH_FLOOR_ID, Depot.REGIONAL_DESK_ID]
		collectionSearchCommand1.fromDate = from.getTime()
		collectionSearchCommand1.toDate = to.getTime()
		collectionSearchCommand1.contactPerson = 'AAA AB'
		collectionSearchCommand1.status = [Status.IN_PROCESS_ID, Status.NOT_PROCESSED_ID]
		collectionSearchCommand1.analog = [MaterialType.ARCHIVE_ID, MaterialType.PERIODICALS_ID]
		collectionSearchCommand1.digital = [MaterialType.BOOKS_ID, MaterialType.EPHEMERA_ID]
		collectionSearchCommand1.sort = 'date'
		collectionSearchCommand1.order = 'asc'

		CollectionSearch collectionSearch1 = collectionSearchCommand1.getCollectionSearch()
		List<Collection> collections1 = collectionSearch1.getResults() as List<Collection>

		assert collections1.size() == 2
		assert collections1[0].name.contains('First')
		assert collections1[1].name.contains('Fifth')

		// ------------------------------------------------------------------------------------------- //

		CollectionSearchCommand collectionSearchCommand2 = new CollectionSearchCommand()
		CollectionSearch collectionSearch2 = collectionSearchCommand2.getCollectionSearch()
		List<Collection> collections2 = collectionSearch2.getResults() as List<Collection>

		assert collections2.size() == 5
	}
}
