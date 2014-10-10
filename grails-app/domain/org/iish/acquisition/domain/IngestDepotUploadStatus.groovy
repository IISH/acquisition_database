package org.iish.acquisition.domain

/**
 * Holds the status of a specific upload in the ingest depot.
 */
class IngestDepotUploadStatus extends AbstractIngestDepotStatus {
	IngestDepotStatusCode statusCode = IngestDepotStatusCode.UPLOAD_FILES
	String name
	IngestDepotReport ingestDepotReport = new IngestDepotReport()

	static belongsTo = [
			ingestDepotStatus: IngestDepotStatus
	]

	static hasOne = [
			ingestDepotReport: IngestDepotReport
	]

	static constraints = {
		name blank: false, maxSize: 255
	}

	static mapping = {
		table 'ingest_depot_upload_statuses'
		ingestDepotStatus fetch: 'join'
		ingestDepotReport lazy: true
	}

	/**
	 * Returns a human readable status message.
	 * @return A translated status message.
	 */
	@Override
	String getHumanReadableMessage() {
		return statusCode.getMessage(messageSource, name)
	}

	@Override
	String toString() {
		return "$name (ingest depot upload status)"
	}
}
