package org.iish.acquisition.domain

import grails.plugin.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Holds the status of the digital material located in the ingest depot.
 */
class DigitalMaterialStatus {
	static GrailsApplication grailsApplication

	Date startIngest
	boolean ingestDelayed = false
	boolean lastActionFailed = false

	static belongsTo = [
			collection: Collection,
			statusCode: DigitalMaterialStatusCode
	]

	static constraints = {
		collection unique: true
		startIngest nullable: true
	}

	static mapping = {
		table 'digital_material_statuses'
		collection fetch: 'join'
	}

	void beforeUpdate() {
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
	 * Returns whether the timer expired.
	 * @return Whether the timer expired.
	 */
	boolean isTimerExpired() {
		return (getTimerExpirationDate().compareTo(new Date()) < 0)
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
					return ((statusCode.id >= DigitalMaterialStatusCode.FOLDER_CREATED) &&
							SpringSecurityUtils.ifAllGranted(Authority.ROLE_OFFLOADER_1))
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
		return new Integer(grailsApplication.config.ingestDepot.timer.initial.minutes.toString())
	}

	/**
	 * Returns the extended timer duration in minutes.
	 * @return The extended timer duration in minutes.
	 */
	static int getTimerExtendedInMinutes() {
		return new Integer(grailsApplication.config.ingestDepot.timer.extended.minutes.toString())
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
		Collection.withCriteria {
			createAlias('digitalMaterialStatus', 'status')
			eq('status.statusCode.id', DigitalMaterialStatusCode.MATERIAL_UPLOADED)
		}
	}

	/**
	 * Returns a list of digital material collections ready for restore.
	 * @return A list of matching collections.
	 */
	static List<Collection> getReadyForRestore() {
		Collection.withCriteria {
			createAlias('digitalMaterialStatus', 'status')
			eq('status.statusCode.id', DigitalMaterialStatusCode.READY_FOR_RESTORE)
		}
	}

	/**
	 * Returns a list of digital material collections ready for ingest.
	 * @return A list of matching collections.
	 */
	static List<Collection> getReadyForIngest() {
		Collection.withCriteria {
			createAlias('digitalMaterialStatus', 'status')

			isNull('status.startIngest')
			eq('status.lastActionFailed', false)

			or {
				eq('status.statusCode.id', DigitalMaterialStatusCode.READY_FOR_PERMANENT_STORAGE)
				and {
					eq('status.ingestDelayed', false)
					lt('dateCreated', getLatestCreationDateExpired(false))
				}
				and {
					eq('status.ingestDelayed', true)
					lt('dateCreated', getLatestCreationDateExpired(true))
				}
			}
		}
	}

	/**
	 * Returns the latest creation date for which the timer has not yet expired.
	 * @param ingestDelayed Whether we should take into account extended timers.
	 * @param date Which date to take to calculate the timer expiration creation date, by default 'now'.
	 * @return The latest creation date for which the timer has not yet expired.
	 */
	static Date getLatestCreationDateExpired(boolean extended, Date date = new Date()) {
		Calendar calendar = Calendar.getInstance()
		calendar.setTime(date)

		if (extended) {
			calendar.add(Calendar.MINUTE, -getTimerExtendedInMinutes())
		}
		else {
			calendar.add(Calendar.MINUTE, -getTimerInitialInMinutes())
		}

		return calendar.getTime()
	}

	@Override
	String toString() {
		return statusCode
	}
}
