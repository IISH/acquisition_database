package org.iish.acquisition.domain

import grails.util.Holders
import grails.plugin.springsecurity.SpringSecurityUtils

/**
 * Holds the status of the digital material located in the ingest depot.
 */
class DigitalMaterialStatus {
	Date startIngest
	boolean ingestDelayed = false
	boolean lastActionFailed = false
	Date lastStatusChange = new Date()
	String message

	static belongsTo = [
			collection : Collection,
			statusCode : DigitalMaterialStatusCode,

			manifestCsv: DigitalMaterialFile,
			manifestXml: DigitalMaterialFile
	]

	static constraints = {
		collection unique: true
		startIngest nullable: true
		message nullable: true, blank: true

		manifestCsv nullable: true
		manifestXml nullable: true
	}

	static mapping = {
		table 'digital_material_statuses'
		collection fetch: 'join'
	}

	void beforeUpdate() {
		lastStatusChange = new Date()

		if (statusCode.id == DigitalMaterialStatusCode.UPLOADING_TO_PERMANENT_STORAGE) {
			startIngest = new Date()
		}
	}

	/**
	 * Returns the expiration date/time of the timer.
	 * After this date/time, the digital material on the ingest depot will automatically be moved to the SOR.
	 * @return The expiration date/time of the timer.
	 */
	Date getTimerExpirationDate() {
		Calendar calendar = Calendar.getInstance()
		calendar.setTime(collection.dateCreated)

		if (ingestDelayed) {
			calendar.add(Calendar.MINUTE, getTimerExtendedInMinutes())
		}
		else {
			calendar.add(Calendar.MINUTE, getTimerInitialInMinutes())
		}

		return calendar.getTime()
	}

	/**
	 * Returns whether the automatic ingest procedure can still be delayed. (Timer will be extended)
	 * @return Whether the automatic ingest procedure can still be delayed
	 */
	boolean canDelayIngest() {
		return (!ingestDelayed &&
				(statusCode.id < DigitalMaterialStatusCode.READY_FOR_PERMANENT_STORAGE) &&
				SpringSecurityUtils.ifAllGranted(Authority.ROLE_OFFLOADER_3))
	}

	/**
	 * Returns whether the status code can be changed to the given status code by the user.
	 * @param newStatusCode The new status code.
	 * @return Whether the user may change the status code to the new given status code.
	 */
	boolean canChangeTo(DigitalMaterialStatusCode newStatusCode) {
		if (newStatusCode.id == statusCode.id) {
			return true
		}

		if (!lastActionFailed && newStatusCode.isSetByUser && (newStatusCode.id > statusCode.id)) {
			switch (newStatusCode.id) {
				case DigitalMaterialStatusCode.MATERIAL_UPLOADED:
					return ((statusCode.id >= DigitalMaterialStatusCode.FOLDER_CREATED)
							&& SpringSecurityUtils.ifAllGranted(Authority.ROLE_OFFLOADER_1))
				case DigitalMaterialStatusCode.READY_FOR_PERMANENT_STORAGE:
				case DigitalMaterialStatusCode.READY_FOR_RESTORE:
					return ((statusCode.id >= DigitalMaterialStatusCode.BACKUP_FINISHED) &&
							SpringSecurityUtils.ifAllGranted(Authority.ROLE_OFFLOADER_2))
			}
		}

		return false
	}

	/**
	 * Returns the initial timer duration in minutes.
	 * @return The initial timer duration in minutes.
	 */
	static int getTimerInitialInMinutes() {
		return new Integer(Holders.config.ingestDepot.timer.initial.minutes.toString())
	}

	/**
	 * Returns the extended timer duration in minutes.
	 * @return The extended timer duration in minutes.
	 */
	static int getTimerExtendedInMinutes() {
		return new Integer(Holders.config.ingestDepot.timer.extended.minutes.toString())
	}

	/**
	 * Returns a list of digital material collections without a folder on the ingest depot.
	 * @return A list of matching collections.
	 */
	static List<Collection> getWithoutFolder() {
		Collection.withCriteria {
			createAlias('digitalMaterialStatus', 'status')
			eq('status.statusCode.id', DigitalMaterialStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION)
		}
	}

	/**
	 * Returns a list of digital material collections ready for backup.
	 * @return A list of matching collections.
	 */
	static List<Collection> getReadyForBackup() {
		List<Collection> readyForBackup = Collection.withCriteria {
			createAlias('digitalMaterialStatus', 'status')
			eq('status.statusCode.id', DigitalMaterialStatusCode.MATERIAL_UPLOADED)
		}

		return getIngestNotStartedOrNotEligible(readyForBackup)
	}

	/**
	 * Returns a list of digital material collections ready for restore.
	 * @return A list of matching collections.
	 */
	static List<Collection> getReadyForRestore() {
		List<Collection> readyForRestore = Collection.withCriteria {
			createAlias('digitalMaterialStatus', 'status')
			eq('status.statusCode.id', DigitalMaterialStatusCode.READY_FOR_RESTORE)
		}

		return getIngestNotStartedOrNotEligible(readyForRestore)
	}

	/**
	 * Returns a list of digital material collections ready for ingest.
	 * @return A list of matching collections.
	 */
	static List<Collection> getReadyForIngest() {
		Collection.withCriteria {
			createAlias('digitalMaterialStatus', 'status')

			and {
				isNull('status.startIngest')
				eq('status.lastActionFailed', false)

				or {
					eq('status.statusCode.id', DigitalMaterialStatusCode.READY_FOR_PERMANENT_STORAGE)
					and {
						eq('status.ingestDelayed', false)
						lt('dateCreated', getLatestCreationDateInitialExpired())
					}
					and {
						eq('status.ingestDelayed', true)
						lt('dateCreated', getLatestCreationDateExtendedExpired())
					}
				}
			}
		}
	}

	/**
	 * Returns the latest creation date for which the initial timer has not yet expired.
	 * @param date Which date to take to calculate the timer expiration creation date, by default 'now'.
	 * @return The latest creation date for which the initial timer has not yet expired.
	 */
	static Date getLatestCreationDateInitialExpired(Date date = new Date()) {
		return getLatestCreationDateExpired(getTimerInitialInMinutes(), date)
	}

	/**
	 * Returns the latest creation date for which the extended timer has not yet expired.
	 * @param date Which date to take to calculate the timer expiration creation date, by default 'now'.
	 * @return The latest creation date for which the extended timer has not yet expired.
	 */
	static Date getLatestCreationDateExtendedExpired(Date date = new Date()) {
		return getLatestCreationDateExpired(getTimerExtendedInMinutes(), date)
	}

	/**
	 * Returns the latest creation date for which the timer has not yet expired.
	 * @param minutes The number of minutes on the timer.
	 * @param date Which date to take to calculate the timer expiration creation date, by default 'now'.
	 * @return The latest creation date for which the timer has not yet expired.
	 */
	private static Date getLatestCreationDateExpired(Integer minutes, Date date = new Date()) {
		Calendar calendar = Calendar.getInstance()
		calendar.setTime(date)
		calendar.add(Calendar.MINUTE, -minutes)

		return calendar.getTime()
	}

	/**
	 * Removes from the given list of digital material collections
	 * those that have started ingest or are eligible for ingest.
	 * @param collections The list of collections to be filtered.
	 * @return The filtered list of collections.
	 */
	private static List<Collection> getIngestNotStartedOrNotEligible(List<Collection> collections) {
		List<Collection> ingestStarted = Collection.withCriteria {
			createAlias('digitalMaterialStatus', 'status')
			isNotNull('status.startIngest')
		}
		List<Collection> readyForIngest = getReadyForIngest()

		return collections - ingestStarted - readyForIngest
	}

	@Override
	String toString() {
		return statusCode
	}
}
