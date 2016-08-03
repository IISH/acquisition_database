package org.iish.acquisition.domain

import grails.util.Holders
import grails.plugin.springsecurity.SpringSecurityUtils
import org.iish.acquisition.service.EmailService

/**
 * Holds the status of the digital material located in the ingest depot.
 */
class DigitalMaterialStatus {
	EmailService emailService

	Date timerStarted
	Date startIngest
	boolean ingestDelayed = false
	Date lastStatusChange = new Date()
	String message
	DigitalMaterialStatusSubCode statusSubCode

	static transients = ['emailService']

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
		timerStarted index: 'digital_material_statuses_timer_started_idx'
	}

	void beforeUpdate() {
		lastStatusChange = new Date()

		if (!startIngest && (statusCode.id == DigitalMaterialStatusCode.STAGINGAREA)) {
			startIngest = new Date()
		}

		if (isDirty('statusCode') || isDirty('statusSubCode')) {
			runAsync {
				emailService.sentStatusChangeEmail(this)
			}
		}
	}

	/**
	 * Returns the expiration date/time of the timer.
	 * After this date/time, the digital material on the ingest depot will automatically be moved to the SOR.
	 * @return The expiration date/time of the timer.
	 */
	Date getTimerExpirationDate() {
		Calendar calendar = Calendar.getInstance()
		calendar.setTime(timerStarted)

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
				(statusCode.id < DigitalMaterialStatusCode.STAGINGAREA) &&
				SpringSecurityUtils.ifAllGranted(Authority.ROLE_OFFLOADER_3))
	}

	/**
	 * Returns whether the status code can be changed to the given status code by the user.
	 * @param newStatusCode The new status code.
	 * @return Whether the user may change the status code to the new given status code.
	 */
	boolean canChangeTo(DigitalMaterialStatusCode newStatusCode) {
		// If there is already something requested or running, no other action allowed
		boolean isRequested = (statusSubCode == DigitalMaterialStatusSubCode.REQUESTED)
		boolean isRunning = (statusSubCode == DigitalMaterialStatusSubCode.RUNNING)
		if (isRequested || isRunning) {
			return false
		}

		// Users may retry if an action failed
		boolean isSameStatusCode = (newStatusCode.id == statusCode.id)
		boolean lastActionFailed = (statusSubCode == DigitalMaterialStatusSubCode.FAILED)
		if (isSameStatusCode && lastActionFailed) {
			return true
		}

		// If the action depend on a previous action, confirm the dependency has been successful
		boolean fulfillsDependency = (newStatusCode.dependsOn?.id < statusCode.id)
		if (newStatusCode.dependsOn?.id == statusCode.id) {
			fulfillsDependency = (statusSubCode == DigitalMaterialStatusSubCode.FINISHED)
		}

		// If the action requires authorization, check if the current user is granted to perform the action
		String neededAuthority = newStatusCode.needsAuthority?.authority
		boolean isGranted = (!neededAuthority || SpringSecurityUtils.ifAllGranted(neededAuthority))

		// Determine whether a new status request is allowed
		return (newStatusCode.isSetByUser && fulfillsDependency && isGranted)
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
			eq('status.statusCode.id', DigitalMaterialStatusCode.FOLDER)
			eq('status.statusSubCode', DigitalMaterialStatusSubCode.REQUESTED)
		}
	}

	/**
	 * Returns a list of digital material collections ready for backup.
	 * @return A list of matching collections.
	 */
	static List<Collection> getReadyForBackup() {
		List<Collection> readyForBackup = Collection.withCriteria {
			createAlias('digitalMaterialStatus', 'status')
			eq('status.statusCode.id', DigitalMaterialStatusCode.BACKUP)
			eq('status.statusSubCode', DigitalMaterialStatusSubCode.REQUESTED)
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
			eq('status.statusCode.id', DigitalMaterialStatusCode.RESTORE)
			eq('status.statusSubCode', DigitalMaterialStatusSubCode.REQUESTED)
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

			// Return in list:
			// a) When status is manually set to 'stagingarea'
			// b) OR when ingest is not delayed AND 'not delayed' date has expired AND last action was completed AND ingest not started
			// c) OR when ingest is delayed AND 'delayed' date has expired AND last action was completed AND ingest not started
			// Remark: If previous action has failed, the collection will never show in 'stagingarea' until you manually set it on 'stagingarea'

			or {
				and {
					eq('status.statusCode.id', DigitalMaterialStatusCode.STAGINGAREA)
					eq('status.statusSubCode', DigitalMaterialStatusSubCode.REQUESTED)
				}

				and {
					eq('status.ingestDelayed', false)
					lt('status.timerStarted', getLatestCreationDateInitialExpired())
					eq('status.statusSubCode', DigitalMaterialStatusSubCode.FINISHED)
					isNull('status.startIngest') // Date when ingest started
				}

				and {
					eq('status.ingestDelayed', true)
					lt('status.timerStarted', getLatestCreationDateExtendedExpired())
					eq('status.statusSubCode', DigitalMaterialStatusSubCode.FINISHED)
					isNull('status.startIngest') // Date when ingest started
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
		List<Collection> readyForIngest = getReadyForIngest()
		List<Collection> ingestStarted = Collection.withCriteria {
			createAlias('digitalMaterialStatus', 'status')
			isNotNull('status.startIngest')
		}

		return collections - readyForIngest - ingestStarted
	}

	@Override
	String toString() {
		return statusCode
	}
}
