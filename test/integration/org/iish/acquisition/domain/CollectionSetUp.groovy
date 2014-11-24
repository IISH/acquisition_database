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
				setUpDigitalMaterial(MaterialType.EPHEMERA_ID)
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
				setUpDigitalMaterial(MaterialType.EPHEMERA_ID),
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
		setUpLocations(collection3, [
				setUpLocation(Depot.ELSEWHERE_ID)
		])
		setUpDigitalMaterialCollection(collection3, [
				setUpDigitalMaterial(MaterialType.MOVING_IMAGES_ID),
				setUpDigitalMaterial(MaterialType.PERIODICALS_ID),
				setUpDigitalMaterial(MaterialType.OTHER_UNKNOWN_ID)
		])
		collection3.save(flush: true)

		calendar.set(2014, 04, 04)
		Collection collection4 = setUpCollection([
				name         : 'Fourth',
				remarks      : 'this for the search in very specific keywords',
				dateOfArrival: calendar.getTime(),
				status       : Status.get(Status.IN_PROCESS_ID)
		])
		setUpLocations(collection4, [
				setUpLocation(Depot.ELSEWHERE_ID)
		])
		setUpAnalogMaterialCollection(collection4, [
				setUpAnalogMaterial(MaterialType.OTHER_UNKNOWN_ID)
		])
		setUpDigitalMaterialCollection(collection4, [
				setUpDigitalMaterial(MaterialType.ARCHIVE_ID),
				setUpDigitalMaterial(MaterialType.BOOKS_ID)
		])
		collection4.save(flush: true)

		calendar.set(2014, 06, 06)
		Collection collection5 = setUpCollection([
				name         : 'Fifth',
				remarks      : 'keywords',
				dateOfArrival: calendar.getTime(),
				status       : Status.get(Status.IN_PROCESS_ID)
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
				status: Status.get(Status.WONT_BE_PROCESSED_ID)
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

	static void cleanUpCollections() {
		Collection.list().each { it.delete(flush: true) }
	}
}