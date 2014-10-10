package org.iish.acquisition.domain

import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Holds the virus and file identification reports for an upload on the ingest depot.
 */
class IngestDepotReport {
	transient static GrailsApplication grailsApplication

	String virusReport
	String fileIdentificationReport

	static belongsTo = [
			ingestDepotUploadStatus: IngestDepotUploadStatus
	]

	static constraints = {
		virusReport nullable: true
		fileIdentificationReport nullable: true
		ingestDepotUploadStatus unique: true
	}

	static mapping = {
		table 'ingest_depot_reports'

		virusReport type: 'text', sqlType: 'mediumtext'
		fileIdentificationReport type: 'text', sqlType: 'mediumtext'
	}

	/**
	 * Returns whether a given ingest depot upload status has a virus report.
	 * @param ingestDepotUploadStatus The ingest depot upload status in question.
	 * @return True if a virus report was found in the database.
	 */
	static boolean hasVirusReport(IngestDepotUploadStatus ingestDepotUploadStatus) {
		return (withCriteria {
			eq('ingestDepotUploadStatus', ingestDepotUploadStatus)
			isNotNull('virusReport')
			projections {
				count()
			}
		}.first() == 1)
	}

	/**
	 * Returns whether a given ingest depot upload status has a file identification report.
	 * @param ingestDepotUploadStatus The ingest depot upload status in question.
	 * @return True if a file identification report was found in the database.
	 */
	static boolean hasFileIdentificationReport(IngestDepotUploadStatus ingestDepotUploadStatus) {
		return (withCriteria {
			eq('ingestDepotUploadStatus', ingestDepotUploadStatus)
			isNotNull('fileIdentificationReport')
			projections {
				count()
			}
		}.first() == 1)
	}

	/**
	 * Returns the content type of the virus report.
	 * @return The content type of the virus report.
	 */
	static String getVirusReportContentType() {
		return grailsApplication.config.ingestDepot.contentType.virusReport
	}

	/**
	 * Returns the content type of the file identification report.
	 * @return The content type of the file identification report.
	 */
	static String getFileIdentificationReportContentType() {
		return grailsApplication.config.ingestDepot.contentType.fileIdentificationReport
	}

	/**
	 * Returns the extension of the virus report.
	 * @return The extension of the virus report.
	 */
	static String getVirusReportExtension() {
		return grailsApplication.config.ingestDepot.extension.virusReport
	}

	/**
	 * Returns the extension of the file identification report.
	 * @return The extension of the file identification report.
	 */
	static String getFileIdentificationReportExtension() {
		return grailsApplication.config.ingestDepot.extension.fileIdentificationReport
	}

	@Override
	String toString() {
		return "${ingestDepotUploadStatus.name} (ingest depot report)"
	}
}
