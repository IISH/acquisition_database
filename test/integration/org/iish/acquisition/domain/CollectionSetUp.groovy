package org.iish.acquisition.domain

class CollectionSetUp {

	static void setUpCollections() {
		Calendar calendar = Calendar.getInstance()

		calendar.set(2014, 01, 01)
		Collection collection1 = setUpCollection([
				name         : 'First',
				content      : 'keywords that he might search for',
				dateOfArrival: calendar.getTime(),
				status       : Status.get(Status.NOT_PROCESSED_ID)
		])
		setUpAnalogMaterialCollection(collection1, [
				setUpAnalogMaterial(MaterialType.ARCHIVE_ID),
				setUpAnalogMaterial(MaterialType.BOOKS_ID)
		])
		setUpDigitalMaterialCollection(collection1, [
				setUpDigitalMaterial(MaterialType.DRAWINGS_ID),
				setUpDigitalMaterial(MaterialType.EPHEMERAL_ID)
		])
		setUpLocations(collection1, [
				setUpLocation(Depot.FIFTH_FLOOR_ID),
				setUpLocation(Depot.RANGEERTERREIN_ID)
		])
		collection1.save(flush: true)

		calendar.set(2014, 02, 02)
		Collection collection2 = setUpCollection([
				name         : 'Second',
				contactPerson: 'ABD',
				dateOfArrival: calendar.getTime()
		])
		setUpDigitalMaterialCollection(collection2, [
				setUpDigitalMaterial(MaterialType.EPHEMERAL_ID),
		])
		setUpLocations(collection2, [
				setUpLocation(Depot.BG_DEPOT_ID),
				setUpLocation(Depot.THIRD_FLOOR_ID),
				setUpLocation(Depot.FIFTH_FLOOR_ID)
		])
		collection2.save(flush: true)

		calendar.set(2014, 03, 03)
		Collection collection3 = setUpCollection([
				name         : 'Third',
				contactPerson: 'ABC',
				dateOfArrival: calendar.getTime(),
				status       : Status.get(Status.NOT_PROCESSED_ID)
		])
		setUpDigitalMaterialCollection(collection3, [
				setUpDigitalMaterial(MaterialType.MOVING_IMAGES_ID),
				setUpDigitalMaterial(MaterialType.PERIODICALS_ID),
				setUpDigitalMaterial(MaterialType.OTHER_ID)
		])
		collection3.save(flush: true)

		calendar.set(2014, 04, 04)
		Collection collection4 = setUpCollection([
				name         : 'Fourth',
				remarks      : 'this is very specific indeed',
				dateOfArrival: calendar.getTime(),
				status       : Status.get(Status.IN_PROCESS_ID)
		])
		setUpAnalogMaterialCollection(collection4, [
				setUpAnalogMaterial(MaterialType.OTHER_ID)
		])
		setUpDigitalMaterialCollection(collection4, [
				setUpDigitalMaterial(MaterialType.ARCHIVE_ID),
				setUpDigitalMaterial(MaterialType.BOOKS_ID)
		])
		collection4.save(flush: true)

		calendar.set(2014, 06, 06)
		Collection collection5 = setUpCollection([
				name         : 'Fifth',
				dateOfArrival: calendar.getTime()
		])
		setUpAnalogMaterialCollection(collection5, [
				setUpAnalogMaterial(MaterialType.PERIODICALS_ID)
		])
		setUpDigitalMaterialCollection(collection5, [
				setUpDigitalMaterial(MaterialType.PERIODICALS_ID),
				setUpDigitalMaterial(MaterialType.BOOKS_ID)
		])
		setUpLocations(collection5, [
				setUpLocation(Depot.FIFTH_FLOOR_ID)
		])
		collection5.save(flush: true)
	}

	static Collection setUpCollection(Map properties = [:]) {
		Collection collection = new Collection(
				name: 'Test collection',
				acquisitionId: '12345',
				content: 'Content',
				listsAvailable: 'Lists available',
				toBeDone: 'To be done',
				priority: Priority.HIGH,
				level: Priority.MEDIUM,
				owner: 'Owner',
				accrual: Accrual.ACCRUAL,
				dateOfArrival: new Date(),
				contactPerson: 'AAA',
				remarks: 'Remarks',
				originalPackageTransport: 'Original package transport',
				status: Status.get(Status.COLLECTION_LEVEL_READY_ID)
		)

		collection.properties << properties
		collection.save()

		return collection
	}

	static AnalogMaterial setUpAnalogMaterial(Long materialType) {
		MaterialType material = MaterialType.get(materialType)
		return new AnalogMaterial(
				materialType: material,
				size: 12,
				unit: material.inMeters ? AnalogUnit.METER : AnalogUnit.NUMBER
		)
	}

	static DigitalMaterial setUpDigitalMaterial(Long materialType) {
		return new DigitalMaterial(
				materialType: MaterialType.get(materialType)
		)
	}

	static Location setUpLocation(Long depot) {
		return new Location(
				cabinet: 'Cabinet',
				shelf: 'Shelf',
				depot: Depot.get(depot)
		)
	}

	static void setUpAnalogMaterialCollection(Collection collection, List<AnalogMaterial> analogMaterials) {
		AnalogMaterialCollection analogMaterialCollection = new AnalogMaterialCollection()

		analogMaterials.each { analogMaterialCollection.addToMaterials(it) }

		collection.analogMaterialCollection = analogMaterialCollection
		collection.save()
	}

	static void setUpDigitalMaterialCollection(Collection collection, List<DigitalMaterial> digitalMaterials) {
		DigitalMaterialCollection digitalMaterialCollection = new DigitalMaterialCollection(
				numberOfFiles: 123,
				totalSize: 456,
				unit: ByteUnit.GB
		)

		digitalMaterials.each { digitalMaterialCollection.addToMaterials(it) }

		collection.digitalMaterialCollection = digitalMaterialCollection
		collection.save()
	}

	static void setUpLocations(Collection collection, List<Location> locations) {
		locations.each { collection.addToLocations(it) }
		collection.save()
	}

	static void setUpBootStrapData() {
		[(Appraisal.YES_ID): 'yes',
		 (Appraisal.NO_ID) : 'no',
		 (Appraisal.NA_ID) : 'n.a'].
				each { Long id, String name ->
					new Appraisal(id: id, name: name).save()
				}

		[(Contract.YES_ID)          : 'yes',
		 (Contract.NA_ID)           : 'n.a (not applicable)',
		 (Contract.NOT_YET_THERE_ID): 'not yet there',
		 (Contract.UNKNOWN_ID)      : 'unknown'].
				each { Long id, String name ->
					new Contract(id: id, name: name).save()
				}

		[(Depot.RANGEERTERREIN_ID): 'Rangeerterrein',
		 (Depot.SORTEERRUIMTE_ID) : 'Sorteerruimte',
		 (Depot.THIRD_FLOOR_ID)   : '3rd floor',
		 (Depot.FOURTH_FLOOR_ID)  : '4th floor',
		 (Depot.FIFTH_FLOOR_ID)   : '5th floor',
		 (Depot.REGIONAL_DESK_ID) : 'Regional Desk',
		 (Depot.BG_DEPOT_ID)      : 'B&G depot',
		 (Depot.ELSEWHERE_ID)     : 'Elsewhere',
		 (Depot.ZERO_FLOOR_ID)    : '0th floor'].
				each { Long id, String name ->
					new Depot(id: id, name: name).save()
				}

		[(MaterialType.ARCHIVE_ID)      : [name: 'Archive', inMeters: true, inNumbers: false],
		 (MaterialType.BOOKS_ID)        : [name: 'Books', inMeters: true, inNumbers: true],
		 (MaterialType.PERIODICALS_ID)  : [name: 'Periodicals', inMeters: true, inNumbers: false],
		 (MaterialType.MOVING_IMAGES_ID): [name: 'Moving images', inMeters: false, inNumbers: true],
		 (MaterialType.EPHEMERAL_ID)    : [name: 'Ephemeral', inMeters: true, inNumbers: false],
		 (MaterialType.SOUND_ID)        : [name: 'Sound', inMeters: false, inNumbers: true],
		 (MaterialType.POSTERS_ID)      : [name: 'Posters', inMeters: false, inNumbers: true],
		 (MaterialType.DRAWINGS_ID)     : [name: 'Drawings', inMeters: false, inNumbers: true],
		 (MaterialType.PHOTOS_ID)       : [name: 'Photos', inMeters: false, inNumbers: true],
		 (MaterialType.OTHER_ID)        : [name: 'Other', inMeters: false, inNumbers: true]].
				each { Long id, Map materialType ->
					if (!MaterialType.get(id)) {
						new MaterialType([id: id] + materialType).save()
					}
				}

		[(Status.NOT_PROCESSED_ID)         : 'Not processed',
		 (Status.IN_PROCESS_ID)            : 'In process',
		 (Status.COLLECTION_LEVEL_READY_ID): 'Collection level ready',
		 (Status.PROCESSED_ID)             : 'Processed',
		 (Status.WONT_BE_PROCESSED_ID)     : 'Won\'t be processed'].
				each { Long id, String status ->
					new Status(id: id, status: status).save()
				}

		[Authority.ROLE_SUPER_ADMIN, Authority.ROLE_ADMIN, Authority.ROLE_USER].
				each { String role ->
					new Authority(authority: role).save()
				}
	}
}