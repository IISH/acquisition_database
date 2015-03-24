package org.iish.acquisition.domain

import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin
import org.junit.Test

@TestMixin(IntegrationTestMixin)
class DigitalMaterialStatusSpec {

	@Test
	void testWithoutFolder() {
		setUp1()
		List<Collection> withoutFolder1 = DigitalMaterialStatus.getWithoutFolder()

		assert withoutFolder1.size() == 2
		assert withoutFolder1*.objectRepositoryPID.contains('10622/BULK00001')
		assert withoutFolder1*.objectRepositoryPID.contains('10622/BULK00003')

		// ------------------------------------------------------------------------------------------- //

		setUp2()
		List<Collection> withoutFolder2 = DigitalMaterialStatus.getWithoutFolder()

		assert withoutFolder2.size() == 2
		assert withoutFolder2*.objectRepositoryPID.contains('10622/BULK00001')
		assert withoutFolder2*.objectRepositoryPID.contains('10622/BULK00006')

		// ------------------------------------------------------------------------------------------- //

		setUp3()
		List<Collection> withoutFolder3 = DigitalMaterialStatus.getWithoutFolder()

		assert withoutFolder3.size() == 3
		assert withoutFolder3*.objectRepositoryPID.contains('10622/BULK00001')
		assert withoutFolder3*.objectRepositoryPID.contains('10622/BULK00006')
		assert withoutFolder3*.objectRepositoryPID.contains('10622/BULK00008')

		// ------------------------------------------------------------------------------------------- //

		CollectionSetUp.cleanUpCollections()
	}

	@Test
	void testReadyForBackup() {
		setUpTimer()

		setUp1()
		List<Collection> readyForBackup1 = DigitalMaterialStatus.getReadyForBackup()

		assert readyForBackup1.size() == 1
		assert readyForBackup1*.objectRepositoryPID.contains('10622/BULK00005') // Ready for backup

		// ------------------------------------------------------------------------------------------- //

		setUp2()
		List<Collection> readyForBackup2 = DigitalMaterialStatus.getReadyForBackup()

		assert readyForBackup2.size() == 1
		assert readyForBackup2*.objectRepositoryPID.contains('10622/BULK00005') // Ready for backup
		assert !readyForBackup2*.objectRepositoryPID.contains('10622/BULK00007') // Timer expired

		// ------------------------------------------------------------------------------------------- //

		setUp3()
		List<Collection> readyForBackup3 = DigitalMaterialStatus.getReadyForBackup()

		assert readyForBackup3.size() == 0
		assert !readyForBackup3*.objectRepositoryPID.contains('10622/BULK00005') // Backup is now running
		assert !readyForBackup3*.objectRepositoryPID.contains('10622/BULK00007') // Timer expired

		// ------------------------------------------------------------------------------------------- //

		CollectionSetUp.cleanUpCollections()
	}

	@Test
	void testReadyForIngest() {
		setUpTimer()

		setUp1()
		List<Collection> readyForIngest1 = DigitalMaterialStatus.getReadyForIngest()

		assert readyForIngest1.size() == 3
		assert readyForIngest1*.objectRepositoryPID.contains('10622/BULK00002') // Ready for permanent storage (not failed)
		assert readyForIngest1*.objectRepositoryPID.contains('10622/BULK00003') // Timer expired
		assert readyForIngest1*.objectRepositoryPID.contains('10622/BULK00004') // Ready for permanent storage (failed)

		// ------------------------------------------------------------------------------------------- //

		setUp2()
		List<Collection> readyForIngest2 = DigitalMaterialStatus.getReadyForIngest()

		assert readyForIngest2.size() == 4
		assert readyForIngest2*.objectRepositoryPID.contains('10622/BULK00002') // Ready for permanent storage (not failed)
		assert !readyForIngest2*.objectRepositoryPID.contains('10622/BULK00003') // Time expired, but failure
		assert readyForIngest2*.objectRepositoryPID.contains('10622/BULK00004') // Ready for permanent storage (failed)
		assert readyForIngest2*.objectRepositoryPID.contains('10622/BULK00006') // Timer expired
		assert readyForIngest2*.objectRepositoryPID.contains('10622/BULK00007') // Timer expired

		// ------------------------------------------------------------------------------------------- //

		setUp3()
		List<Collection> readyForIngest3 = DigitalMaterialStatus.getReadyForIngest()

		assert readyForIngest3.size() == 4
		assert readyForIngest3*.objectRepositoryPID.contains('10622/BULK00002') // Ready for permanent storage (not failed)
		assert !readyForIngest3*.objectRepositoryPID.contains('10622/BULK00003') // Time expired, but failure
		assert readyForIngest3*.objectRepositoryPID.contains('10622/BULK00004') // Ready for permanent storage (failed)
		assert readyForIngest3*.objectRepositoryPID.contains('10622/BULK00006') // Timer expired
		assert readyForIngest3*.objectRepositoryPID.contains('10622/BULK00007') // Timer expired
		assert !readyForIngest3*.objectRepositoryPID.contains('10622/BULK00008') // Ingest already started

		// ------------------------------------------------------------------------------------------- //

		CollectionSetUp.cleanUpCollections()
	}

	static void setUp1() {
		Calendar calendar = Calendar.getInstance()

		// ------------------------------------------------------------------------------------------- //

		// Override method to make sure a static date 01/07/2014 is used (months start with 0)  (2014, 06, 01)
		// Timer expired for collections created on: June 3                                     (2014, 05, 03)
		// Extended timer expired for collections created on: May 6                             (2014, 04, 06)
		//
		// Collection created on: June 12 (no extended timer)                                   (2014, 05, 12)
		// TIMER NOT EXPIRED!

		Collection collection1 = CollectionSetUp.setUpCollection([
				name                 : 'Test collection 1',
				objectRepositoryPID  : '10622/BULK00001',
				digitalMaterialStatus: new DigitalMaterialStatus(
						statusCode: DigitalMaterialStatusCode.
								get(DigitalMaterialStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION),
				)
		])
		collection1.save(flush: true, validate: false)

		calendar.set(2014, 05, 12)
		collection1.setDateCreated(calendar.getTime())
		collection1.save(flush: true, validate: false)

		// ------------------------------------------------------------------------------------------- //

		// Override method to make sure a static date 01/07/2014 is used (months start with 0)  (2014, 06, 01)
		// Timer expired for collections created on: June 3                                     (2014, 05, 03)
		// Extended timer expired for collections created on: May 6                             (2014, 04, 06)
		//
		// Collection created on: June 13 (no extended timer)                                   (2014, 05, 13)
		// TIMER NOT EXPIRED!

		Collection collection2 = CollectionSetUp.setUpCollection([
				name                 : 'Test collection 2',
				objectRepositoryPID  : '10622/BULK00002',
				dateCreated          : calendar.getTime(),
				digitalMaterialStatus: new DigitalMaterialStatus(
						statusCode: DigitalMaterialStatusCode.
								get(DigitalMaterialStatusCode.READY_FOR_PERMANENT_STORAGE),
				)
		])
		collection2.save(flush: true, validate: false)

		calendar.set(2014, 05, 13)
		collection2.setDateCreated(calendar.getTime())
		collection2.save(flush: true, validate: false)

		// ------------------------------------------------------------------------------------------- //

		// Override method to make sure a static date 01/07/2014 is used (months start with 0)  (2014, 06, 01)
		// Timer expired for collections created on: June 3                                     (2014, 05, 03)
		// Extended timer expired for collections created on: May 6                             (2014, 04, 06)
		//
		// Collection created on: June 2 (no extended timer)                                    (2014, 05, 02)
		// TIMER EXPIRED!

		Collection collection3 = CollectionSetUp.setUpCollection([
				name                 : 'Test collection 3',
				objectRepositoryPID  : '10622/BULK00003',
				digitalMaterialStatus: new DigitalMaterialStatus(
						statusCode: DigitalMaterialStatusCode.
								get(DigitalMaterialStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION),
				)
		])
		collection3.save(flush: true, validate: false)

		calendar.set(2014, 05, 02)
		collection3.setDateCreated(calendar.getTime())
		collection3.save(flush: true, validate: false)

		// ------------------------------------------------------------------------------------------- //

		// Override method to make sure a static date 01/07/2014 is used (months start with 0)  (2014, 06, 01)
		// Timer expired for collections created on: June 3                                     (2014, 05, 03)
		// Extended timer expired for collections created on: May 6                             (2014, 04, 06)
		//
		// Collection created on: June 14 (no extended timer, but failure)                      (2014, 05, 14)
		// TIMER NOT EXPIRED!

		Collection collection4 = CollectionSetUp.setUpCollection([
				name                 : 'Test collection 4',
				objectRepositoryPID  : '10622/BULK00004',
				digitalMaterialStatus: new DigitalMaterialStatus(
						statusCode: DigitalMaterialStatusCode.
								get(DigitalMaterialStatusCode.READY_FOR_PERMANENT_STORAGE),
						lastActionFailed: true
				)
		])
		collection4.save(flush: true, validate: false)

		calendar.set(2014, 05, 14)
		collection4.setDateCreated(calendar.getTime())
		collection4.save(flush: true, validate: false)

		// ------------------------------------------------------------------------------------------- //

		// Override method to make sure a static date 01/07/2014 is used (months start with 0)  (2014, 06, 01)
		// Timer expired for collections created on: June 3                                     (2014, 05, 03)
		// Extended timer expired for collections created on: May 6                             (2014, 04, 06)
		//
		// Collection created on: June 15 (no extended timer, but failure)                      (2014, 05, 15)
		// TIMER NOT EXPIRED!

		Collection collection5 = CollectionSetUp.setUpCollection([
				name                 : 'Test collection 5',
				objectRepositoryPID  : '10622/BULK00005',
				digitalMaterialStatus: new DigitalMaterialStatus(
						statusCode: DigitalMaterialStatusCode.
								get(DigitalMaterialStatusCode.MATERIAL_UPLOADED),
						lastActionFailed: true
				)
		])
		collection5.save(flush: true, validate: false)

		calendar.set(2014, 05, 15)
		collection5.setDateCreated(calendar.getTime())
		collection5.save(flush: true, validate: false)
	}

	static void setUp2() {
		Calendar calendar = Calendar.getInstance()

		// ------------------------------------------------------------------------------------------- //

		// Override method to make sure a static date 01/07/2014 is used (months start with 0)  (2014, 06, 01)
		// Timer expired for collections created on: June 3                                     (2014, 05, 03)
		// Extended timer expired for collections created on: May 6                             (2014, 04, 06)
		//
		// Collection created on: May 6 (extended timer)                                        (2014, 04, 06)
		// TIMER EXPIRED!

		Collection collection6 = CollectionSetUp.setUpCollection([
				name                 : 'Test collection 6',
				objectRepositoryPID  : '10622/BULK00006',
				digitalMaterialStatus: new DigitalMaterialStatus(
						statusCode: DigitalMaterialStatusCode.
								get(DigitalMaterialStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION),
						ingestDelayed: true
				)
		])
		collection6.save(flush: true, validate: false)

		calendar.set(2014, 04, 06)
		collection6.setDateCreated(calendar.getTime())
		collection6.save(flush: true, validate: false)

		// ------------------------------------------------------------------------------------------- //

		// Override method to make sure a static date 01/07/2014 is used (months start with 0)  (2014, 06, 01)
		// Timer expired for collections created on: June 3                                     (2014, 05, 03)
		// Extended timer expired for collections created on: May 6                             (2014, 04, 06)
		//
		// Collection created on: May 5 (extended timer)                                        (2014, 04, 05)
		// TIMER EXPIRED!

		Collection collection7 = CollectionSetUp.setUpCollection([
				name                 : 'Test collection 7',
				objectRepositoryPID  : '10622/BULK00007',
				digitalMaterialStatus: new DigitalMaterialStatus(
						statusCode: DigitalMaterialStatusCode.
								get(DigitalMaterialStatusCode.MATERIAL_UPLOADED),
						ingestDelayed: true
				)
		])
		collection7.save(flush: true, validate: false)

		calendar.set(2014, 04, 05)
		collection7.setDateCreated(calendar.getTime())
		collection7.save(flush: true, validate: false)

		// ------------------------------------------------------------------------------------------- //

		// BEFORE:
		//
		// Override method to make sure a static date 01/07/2014 is used (months start with 0)  (2014, 06, 01)
		// Timer expired for collections created on: June 3                                     (2014, 05, 03)
		// Extended timer expired for collections created on: May 6                             (2014, 04, 06)
		//
		// Collection created on: June 2 (no extended timer)                                    (2014, 05, 02)
		// TIMER EXPIRED!
		//
		// NOW:
		//
		// (failure)
		// TIMER NOT EXPIRED!

		Collection collection3 = Collection.findByObjectRepositoryPID('10622/BULK00003')
		collection3.digitalMaterialStatus.statusCode =
				DigitalMaterialStatusCode.get(DigitalMaterialStatusCode.FOLDER_CREATED)
		collection3.digitalMaterialStatus.lastActionFailed = true
		collection3.save(flush: true, validate: false)
	}

	static void setUp3() {
		Calendar calendar = Calendar.getInstance()

		// ------------------------------------------------------------------------------------------- //

		// Override method to make sure a static date 01/07/2014 is used (months start with 0)  (2014, 06, 01)
		// Timer expired for collections created on: June 3                                     (2014, 05, 03)
		// Extended timer expired for collections created on: May 6                             (2014, 04, 06)
		//
		// Collection created on: May 4 (no extended timer)                                     (2014, 04, 04)
		// TIMER EXPIRED!

		Collection collection8 = CollectionSetUp.setUpCollection([
				name                 : 'Test collection 8',
				objectRepositoryPID  : '10622/BULK00008',
				dateCreated          : calendar.getTime(),
				digitalMaterialStatus: new DigitalMaterialStatus(
						statusCode: DigitalMaterialStatusCode.
								get(DigitalMaterialStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION),
						startIngest: new Date()
				)
		])
		collection8.save(flush: true, validate: false)

		calendar.set(2014, 04, 04)
		collection8.setDateCreated(calendar.getTime())
		collection8.save(flush: true, validate: false)

		// ------------------------------------------------------------------------------------------- //

		// BEFORE:
		//
		// Override method to make sure a static date 01/07/2014 is used (months start with 0)  (2014, 06, 01)
		// Timer expired for collections created on: June 3                                     (2014, 05, 03)
		// Extended timer expired for collections created on: May 6                             (2014, 04, 06)
		//
		// Collection created on: June 15 (no extended timer, but failure)                      (2014, 05, 15)
		// TIMER NOT EXPIRED!
		//
		// NOW:
		// TIMER NOT EXPIRED!

		Collection collection5 = Collection.findByObjectRepositoryPID('10622/BULK00005')
		collection5.digitalMaterialStatus.statusCode =
				DigitalMaterialStatusCode.get(DigitalMaterialStatusCode.BACKUP_RUNNING)
		collection5.save(flush: true, validate: false)
	}

	static void setUpTimer() {
		// Override method to make sure a static date 01/07/2014 is used (months start with 0)  (2014, 06, 01)
		// Timer expired for collections created on: June 3                                     (2014, 05, 03)
		// Extended timer expired for collections created o: May 6                              (2014, 04, 06)
		DigitalMaterialStatus.metaClass.static.getLatestCreationDateInitialExpired = {
			Calendar calendar = Calendar.getInstance()
			calendar.set(2014, 06, 01)
			return DigitalMaterialStatus.getLatestCreationDateInitialExpired(calendar.getTime())
		}

		DigitalMaterialStatus.metaClass.static.getLatestCreationDateExtendedExpired = {
			Calendar calendar = Calendar.getInstance()
			calendar.set(2014, 06, 01)
			return DigitalMaterialStatus.getLatestCreationDateExtendedExpired(calendar.getTime())
		}
	}
}
