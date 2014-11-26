package org.iish.acquisition.controller

import grails.converters.JSON
import org.iish.acquisition.domain.Collection
import org.iish.acquisition.domain.DigitalMaterialStatus
import org.iish.acquisition.domain.DigitalMaterialStatusCode

import javax.servlet.http.HttpServletResponse

/**
 * Web service for communication with the processes running on the ingest depot.
 */
class ServiceController {
	static allowedMethods = [folders    : 'GET',
	                         startBackup: 'GET',
	                         startIngest: 'GET',
	                         status     : 'POST']

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
	 * Updates the status of the digital material running for the given PID on the ingest depot.
	 */
	def status(String pid, Long status, Boolean failure) {
		doWithPid(pid) { Collection collection ->
			DigitalMaterialStatus digitalMaterialStatus = collection.digitalMaterialStatus
			DigitalMaterialStatusCode statusCode = DigitalMaterialStatusCode.get(status)

			if (digitalMaterialStatus && statusCode) {
				digitalMaterialStatus.statusCode = statusCode
				digitalMaterialStatus.lastActionFailed = failure
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
