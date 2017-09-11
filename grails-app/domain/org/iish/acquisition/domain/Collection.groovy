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
	boolean collectionLevelReady
	boolean deleted = false

	static belongsTo = [
			addedBy  : User,
			contract : Contract,
			appraisal: Appraisal,
			status   : Status
	]

	static hasOne = [
			digitalMaterialStatus    : DigitalMaterialStatus,
			analogMaterialCollection : AnalogMaterialCollection,
			digitalMaterialCollection: DigitalMaterialCollection,
			miscMaterialCollection   : MiscMaterialCollection
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
		toBeDone nullable: true
		priority nullable: true
		level nullable: true
		owner nullable: true, maxSize: 255
		contactPerson blank: false, maxSize: 7, validator: { val, obj ->
			if (!val || !val.matches('^[A-Za-z]{3}(/[A-Za-z]{3})?$')) {
				'collection.wrong.contactPerson.message'
			}
		}
		remarks nullable: true
		originalPackageTransport nullable: true

		addedBy nullable: true
		appraisal nullable: true

		digitalMaterialStatus nullable: true
		analogMaterialCollection nullable: true
		digitalMaterialCollection nullable: true
		miscMaterialCollection nullable: true, validator: { val, obj ->
			if (!val && !obj.analogMaterialCollection && !obj.digitalMaterialCollection) {
				'collection.no.material.collection.message'
			}
		}

		locations validator: { val, obj ->
			if (!val || val.isEmpty()) {
				'collection.no.location.message'
			}
		}
	}

	static mapping = {
		table 'collections'

		objectRepositoryPID column: 'object_repository_pid'

		locations cascade: 'all-delete-orphan', sort: 'depot'
		photos cascade: 'all-delete-orphan'

		digitalMaterialStatus fetch: 'join'
		analogMaterialCollection fetch: 'join'
		digitalMaterialCollection fetch: 'join'
		miscMaterialCollection fetch: 'join'
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

	/**
	 * Searches for a specific location of this collection with the given id.
	 * @param id The id of the location part of this collection.
	 * @return The location if found, otherwise null is returned.
	 */
	Location getLocationById(Long id) {
		locations?.find { it.id == id }
	}

    /**
     * Returns whether this collection is (or has been) a digital material collection.
     * @return Whether this collection is (or has been) a digital material collection.
     */
    boolean isDigital() {
        digitalMaterialCollection || objectRepositoryPID
    }

	/**
	 * Returns a unique digital identifier as id or in the format "[acquisitionTypeId][acquisitionId].[id]".
	 * @return A unique digital identifier.
	 */
	String getDigitalId() {
		if (acquisitionTypeId && acquisitionId) {
			return acquisitionTypeId.name + acquisitionId + '.' + id
		}
		return id
	}

    /**
	 * After each insert or update, check if digital material was entered.
	 * If so, make sure a PID is created and the digital material status can be tracked.
	 */
	protected void afterInsertOrUpdate() {
		if (digitalMaterialCollection && (!objectRepositoryPID || !digitalMaterialStatus)) {
			createPidIfNotExists()
			createDigitalMaterialStatusIfNotExists()

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
	 * If there is no digital material status created yet,
	 * a new digital material status is created for this collection.
	 */
	private void createDigitalMaterialStatusIfNotExists() {
		if (!digitalMaterialStatus) {
			DigitalMaterialStatusCode statusCode = DigitalMaterialStatusCode.get(DigitalMaterialStatusCode.FOLDER)
			digitalMaterialStatus = new DigitalMaterialStatus(
					statusCode: statusCode,
					statusSubCode: DigitalMaterialStatusSubCode.REQUESTED,
					timerStarted: new Date()
			)
		}
	}

	@Override
	String toString() {
		return name
	}
}
