package org.iish.acquisition.controller

import grails.converters.JSON
import org.iish.acquisition.domain.Collection
import org.iish.acquisition.domain.DigitalMaterialFile
import org.iish.acquisition.domain.DigitalMaterialStatus
import org.iish.acquisition.domain.DigitalMaterialStatusCode
import org.iish.acquisition.domain.DigitalMaterialStatusSubCode
import org.iish.acquisition.service.EmailService
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletResponse

/**
 * Web service for communication with the processes running on the ingest depot.
 */
class ServiceController {
	static allowedMethods = [folders     : 'GET',
	                         startBackup : 'GET',
	                         startIngest : 'GET',
	                         startRestore: 'GET',
	                         status      : 'POST',
	                         manifest    : 'POST']

	/**
	 * Returns all of the PIDs without a folder on the ingest depot.
	 */
	def folders() {
		endWithResponse {
			[pids: DigitalMaterialStatus.getWithoutFolder()*.objectRepositoryPID]
		}
	}

	/**
	 * Returns all of the PIDs for which a backup may start.
	 */
	def startBackup() {
		endWithResponse {
			[pids: DigitalMaterialStatus.getReadyForBackup()*.objectRepositoryPID]
		}
	}

	/**
	 * Returns all of the PIDs for which an ingest may start.
	 */
	def startIngest() {
		endWithResponse {
			[pids: DigitalMaterialStatus.getReadyForIngest()*.objectRepositoryPID]
		}
	}

	/**
	 * Returns all of the PIDs for which a restore may start.
	 */
	def startRestore() {
		endWithResponse {
			[pids: DigitalMaterialStatus.getReadyForRestore()*.objectRepositoryPID]
		}
	}

	/**
	 * Updates the status of the digital material running for the given PID on the ingest depot.
	 */
	def status(String pid, Long status, Integer subStatus, String message) {
		doWithPid(pid) { Collection collection ->
			DigitalMaterialStatus digitalMaterialStatus = collection.digitalMaterialStatus
			DigitalMaterialStatusCode statusCode = DigitalMaterialStatusCode.get(status)
			DigitalMaterialStatusSubCode statusSubCode = DigitalMaterialStatusSubCode.getById(subStatus)

			if (digitalMaterialStatus && statusCode && statusSubCode) {
				digitalMaterialStatus.statusCode = statusCode
				digitalMaterialStatus.statusSubCode = statusSubCode
				digitalMaterialStatus.message = message
				digitalMaterialStatus.save(flush: true)

				render 'OK'
				return
			}

			response.sendError(HttpServletResponse.SC_BAD_REQUEST)
		}
	}

	/**
	 * Uploads a manifest of the digital material for the given PID on the ingest depot.
	 */
	def manifest(String pid) {
		doWithPid(pid) { Collection collection ->
			MultipartFile manifestCsv = (MultipartFile) params['manifest_csv']
			MultipartFile manifestXml = (MultipartFile) params['manifest_xml']
			DigitalMaterialStatus digitalMaterialStatus = collection.digitalMaterialStatus

			boolean manifestCsvExists = manifestCsv && !manifestCsv.isEmpty()
			boolean manifestXmlExists = manifestXml && !manifestXml.isEmpty()

			// There should be a digital material status and at least one type of manifest
			if (digitalMaterialStatus && (manifestCsvExists || manifestXmlExists)) {
				// If there is a CSV manifest
				if (manifestCsvExists) {
					digitalMaterialStatus.manifestCsv?.delete()

					DigitalMaterialFile csvFile = new DigitalMaterialFile(
							originalFilename: manifestCsv.getOriginalFilename(),
							contentType: 'text/csv',
							size: manifestCsv.getSize(),
							file: manifestCsv.getBytes()
					)
					csvFile.save()

					digitalMaterialStatus.setManifestCsv(csvFile)
				}

				// If there is an XML manifest
				if (manifestXmlExists) {
					digitalMaterialStatus.manifestXml?.delete()

					DigitalMaterialFile xmlFile = new DigitalMaterialFile(
							originalFilename: manifestXml.getOriginalFilename(),
							contentType: 'text/xml',
							size: manifestXml.getSize(),
							file: manifestXml.getBytes()
					)
					xmlFile.save()

					digitalMaterialStatus.setManifestXml(xmlFile)
				}

				digitalMaterialStatus.save(flush: true)

				render 'OK'
				return
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
}
