package org.iish.acquisition.controller

import grails.converters.JSON
import org.iish.acquisition.domain.Collection
import org.iish.acquisition.domain.IngestDepotStatus
import org.iish.acquisition.domain.IngestDepotStatusCode
import org.iish.acquisition.domain.IngestDepotUploadStatus

import javax.servlet.http.HttpServletResponse

/**
 * Web service for communication with the processes running on the ingest depot server.
 */
class ServiceController {
	static allowedMethods = [folders                 : 'GET',
	                         manualActions           : 'GET',
	                         status                  : 'POST',
	                         virusReport             : 'POST',
	                         fileIdentificationReport: 'POST']

	/**
	 * Returns all of the PIDs without a folder on the ingest depot server.
	 */
	def folders() {
		endWithResponse {
			[pids: Collection.getWithoutFolder()*.objectRepositoryPID]
		}
	}

	/**
	 * Returns all of the manual actions by the user on the process running for the given PID on the ingest server.
	 */
	def manualActions(String pid) {
		doWithPid(pid) { Collection collection ->
			endWithResponse {
				IngestDepotStatus ingestDepotStatus = collection.ingestDepotStatus

				[sorProcessOnHold: ingestDepotStatus.manualSorProcessOnHold,
				 startSorProcess : ingestDepotStatus.manualStartSorProcess]
			}
		}
	}

	/**
	 * Updates the status of the process running for the given PID on the ingest server.
	 */
	def status(String pid, Integer statusCode, Integer statusSubCode, String uploadName) {
		doWithPid(pid) { Collection collection ->
			IngestDepotStatusCode ingestDepotStatusCode = IngestDepotStatusCode.
					getByCodeAndSubcode(statusCode, statusSubCode)

			if (ingestDepotStatusCode) {
				if (ingestDepotStatusCode.isStatusForUploadProcess()) {
					if (uploadName) {
						IngestDepotUploadStatus ingestDepotStatus = findOrCreateUploadStatus(collection, uploadName)
						ingestDepotStatus.statusCode = ingestDepotStatusCode

						if (ingestDepotStatus.save(flush: true)) {
							render ''
							return
						}
					}
				}
				else {
					IngestDepotStatus ingestDepotStatus = collection.ingestDepotStatus
					ingestDepotStatus.statusCode = ingestDepotStatusCode

					if (ingestDepotStatus.save(flush: true)) {
						render ''
						return
					}
				}
			}

			response.sendError(HttpServletResponse.SC_BAD_REQUEST)
		}
	}

	/**
	 * Uploads the generated virus report to the acquisition database.
	 */
	def virusReport(String pid, String uploadName, String virusReport) {
		doWithPid(pid) { Collection collection ->
			if (uploadName && virusReport) {
				IngestDepotUploadStatus ingestDepotUploadStatus = findOrCreateUploadStatus(collection, uploadName)
				ingestDepotUploadStatus.ingestDepotReport.virusReport = virusReport

				if (ingestDepotUploadStatus.save(flush: true)) {
					render ''
					return
				}
			}

			response.sendError(HttpServletResponse.SC_BAD_REQUEST)
		}
	}

	/**
	 * Uploads the generated file identification report to the acquisition database.
	 */
	def fileIdentificationReport(String pid, String uploadName, String fileIdentificationReport) {
		doWithPid(pid) { Collection collection ->
			if (uploadName && fileIdentificationReport) {
				IngestDepotUploadStatus ingestDepotUploadStatus = findOrCreateUploadStatus(collection, uploadName)
				ingestDepotUploadStatus.ingestDepotReport.fileIdentificationReport = fileIdentificationReport

				if (ingestDepotUploadStatus.save(flush: true)) {
					render ''
					return
				}
			}

			response.sendError(HttpServletResponse.SC_BAD_REQUEST)
		}
	}

	/**
	 * Simple wrapper that returns the map in JSON format.
	 * @param body The body that should return a map to be returned in JSON format.
	 */
	private def endWithResponse(Closure body) {
		Map response = body()
		render(text: response as JSON, contentType: 'text/json', encoding: 'UTF-8')
	}

	/**
	 * Simple wrapper that checks whether we know about a collection with the given PID.
	 * @param pid The PID given.
	 * @param body The body that should run with the corresponding collection, if found.
	 */
	private def doWithPid(String pid, Closure body) {
		Collection collection = Collection.findByObjectRepositoryPID(pid?.trim())
		if (pid && collection) {
			body(collection)
		}
		else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND)
		}
	}

	/**
	 * Will find out if we already know about the given upload process.
	 * If not create the record, and return the record.
	 * @param collection The collection to which the upload process belongs.
	 * @param uploadName The name of the upload process.
	 * @return The status record for the given ingest depot upload process.
	 */
	private static IngestDepotUploadStatus findOrCreateUploadStatus(Collection collection, String uploadName) {
		IngestDepotStatus ingestDepotStatus = collection.ingestDepotStatus
		IngestDepotUploadStatus ingestDepotUploadStatus = IngestDepotUploadStatus.
				findByIngestDepotStatusAndName(ingestDepotStatus, uploadName.trim())

		if (!ingestDepotUploadStatus) {
			ingestDepotUploadStatus =
					new IngestDepotUploadStatus(ingestDepotStatus: ingestDepotStatus, name: uploadName.trim())
		}

		return ingestDepotUploadStatus
	}
}
