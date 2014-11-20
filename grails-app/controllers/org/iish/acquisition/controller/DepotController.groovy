package org.iish.acquisition.controller

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.iish.acquisition.depot.IngestDepot
import org.iish.acquisition.depot.IngestDepotFile
import org.iish.acquisition.depot.IngestDepotImpl

/**
 * The controller for accessing files on the ingest depot.
 */
class DepotController {
	GrailsApplication grailsApplication

	static defaultAction = 'list'

	/**
	 * Lists the folders and files on the ingest depot.
	 */
	def list() {
		String enteredPath = params.containsKey('path') ? params.path : '/'
		IngestDepot ingestDepot = new IngestDepotImpl(grailsApplication, enteredPath)

		String path = ingestDepot.getPath()
		String[] pathAsArray = ingestDepot.getPathAsArray()
		List<IngestDepotFile> files = ingestDepot.list()

		ingestDepot.close()

		render view: 'list', model: [path: path, pathAsArray: pathAsArray, files: files]
	}

	/**
	 * Deletes a set of given files and/or folders on the ingest depot.
	 */
	def delete() {
		String enteredPath = params.containsKey('path') ? params.path : '/'
		IngestDepot ingestDepot = new IngestDepotImpl(grailsApplication, enteredPath)

		List<String> toDelete = params.list('file')
		toDelete.each { ingestDepot.remove(it) }

		ingestDepot.close()

		redirect action: 'list', params: [path: enteredPath]
	}
}
