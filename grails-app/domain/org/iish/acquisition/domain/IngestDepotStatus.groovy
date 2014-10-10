package org.iish.acquisition.domain

/**
 * Holds the status of the digital material located in the ingest depot.
 */
class IngestDepotStatus extends AbstractIngestDepotStatus {
	IngestDepotStatusCode statusCode = IngestDepotStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION
	boolean manualSorProcessOnHold = false
	boolean manualStartSorProcess = false

	static belongsTo = [
			collection: Collection
	]

	static hasMany = [
			uploadStatuses: IngestDepotUploadStatus
	]

	static constraints = {
		collection unique: true
	}

	static mapping = {
		table 'ingest_depot_statuses'

		collection fetch: 'join'
		uploadStatuses cascade: 'all-delete-orphan', sort: 'id'
	}

	/**
	 * Returns all upload statuses of which the status is not yet successful.
	 * @return All upload statuses of which the status is not yet successful.
	 */
	List<IngestDepotUploadStatus> getAllUnsuccessfulUploadStatuses() {
		return uploadStatuses?.findAll {
			it.getStatusSubCode() != IngestDepotStatusCode.SUB_CODE_SUCCESS
		} as List<IngestDepotUploadStatus>
	}

	/**
	 * Returns whether all uploads (so far) finished successfully.
	 * @return True if all uploads so far finished successfully.
	 */
	boolean isSuccessfulUpload() {
		if (uploadStatuses?.size() > 0) {
			List<IngestDepotUploadStatus> allUnsuccessfulUploadStatuses = getAllUnsuccessfulUploadStatuses()
			return allUnsuccessfulUploadStatuses.isEmpty()
		}

		return false
	}

	/**
	 * Returns whether the user may set the SOR process.
	 * @return Whether the user may set the SOR process.
	 */
	boolean canManuallySetSorProcess() {
		return (isSuccessfulUpload() && (statusCode < IngestDepotStatusCode.CREATE_ISO) &&
				(statusCode.getSubCode() == IngestDepotStatusCode.SUB_CODE_SUCCESS))
	}

	@Override
	String toString() {
		return statusCode
	}
}
