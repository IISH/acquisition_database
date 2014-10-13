package org.iish.acquisition.domain

import org.apache.commons.lang.StringUtils

import java.text.DecimalFormat

/**
 * The collections of the acquisition database.
 */
class Collection {
	String name
	AcquisitionType acquisitionTypeId = AcquisitionType.COLLECTION
	String acquisitionId
	String objectRepositoryPID
	Date dateCreated
	String content
	String listsAvailable
	String toBeDone
	Priority priority
	Priority level
	String owner
	Accrual accrual
	Date dateOfArrival
	String contactPerson
	String remarks
	String originalPackageTransport
	boolean deleted = false

	static belongsTo = [
			addedBy  : User,
			contract : Contract,
			appraisal: Appraisal,
			status   : Status
	]

	static hasOne = [
			ingestDepotStatus        : IngestDepotStatus,
			analogMaterialCollection : AnalogMaterialCollection,
			digitalMaterialCollection: DigitalMaterialCollection
	]

	static hasMany = [
			locations: Location,
			photos   : Photo
	]

	static constraints = {
		name blank: false, maxSize: 1000
		acquisitionId nullable: true, maxSize: 10
		objectRepositoryPID nullable: true, maxSize: 15, unique: true
		content blank: false
		listsAvailable blank: false
		toBeDone nullable: true, maxSize: 255
		priority nullable: true
		level nullable: true
		owner blank: false, maxSize: 255
		contactPerson blank: false, minSize: 3, maxSize: 3
		remarks nullable: true
		originalPackageTransport nullable: true

		addedBy nullable: true
		contract nullable: true
		appraisal nullable: true

		ingestDepotStatus nullable: true
		analogMaterialCollection nullable: true
		digitalMaterialCollection nullable: true, validator: { val, obj ->
			if (!val && !obj.analogMaterialCollection) {
				'collection.no.material.collection.message'
			}
		}
	}

	static mapping = {
		table 'collections'

		objectRepositoryPID column: 'object_repository_pid'

		locations cascade: 'all-delete-orphan', sort: 'depot'
		photos cascade: 'all-delete-orphan'

		ingestDepotStatus fetch: 'join'
		analogMaterialCollection fetch: 'join'
		digitalMaterialCollection fetch: 'join'
		addedBy fetch: 'join'
		contract fetch: 'join'
		accrual fetch: 'join'
		appraisal fetch: 'join'
		status fetch: 'join'

		content type: 'text', sqlType: 'text'
		toBeDone type: 'text', sqlType: 'text'
		remarks type: 'text', sqlType: 'text'
		originalPackageTransport type: 'text', sqlType: 'text'

		dateOfArrival sqlType: 'date', index: 'collections_date_of_arrival_idx'
		deleted index: 'collections_deleted_idx'
	}

	void afterInsert() {
		afterInsertOrUpdate()
	}

	void afterUpdate() {
		afterInsertOrUpdate()
	}

	/**
	 * Searches for a specific location of this collection with the given id.
	 * @param id The id of the location part of this collection.
	 * @return The location if found, otherwise null is returned.
	 */
	Location getLocationById(Long id) {
		locations?.find { it.id == id }
	}

	/**
	 * Returns a list of digital material collections without a folder on the ingest depot server.
	 * @return A list of matching collections.
	 */
	static List<Collection> getWithoutFolder() {
		where {
			ingestDepotStatus.statusCode == IngestDepotStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION
		}.list()
	}

	/**
	 * After each insert or update, check if digital material was entered.
	 * If so, make sure a PID is created and the ingest depot status can be tracked.
	 */
	private void afterInsertOrUpdate() {
		if (digitalMaterialCollection && (!objectRepositoryPID || !ingestDepotStatus)) {
			createPidIfNotExists()
			createIngestDepotStatusIfNotExists()

			save()
		}
	}

	/**
	 * If there is no PID created yet, a new PID is created for this collection.
	 */
	private void createPidIfNotExists() {
		// The PID is based on the id of the record, so it has to be saved before PID can be created
		if (!objectRepositoryPID && id) {
			String pattern = StringUtils.repeat('0', 5)
			DecimalFormat formatter = new DecimalFormat(pattern)
			objectRepositoryPID = '10622/BULK' + formatter.format(id)
		}
	}

	/**
	 * If there is no ingest depot status created yet, a new ingest depot status is created for this collection.
	 */
	private void createIngestDepotStatusIfNotExists() {
		if (!ingestDepotStatus) {
			ingestDepotStatus = new IngestDepotStatus()
		}
	}

	@Override
	String toString() {
		return name
	}
}
