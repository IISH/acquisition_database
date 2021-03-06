package org.iish.acquisition.controller

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.iish.acquisition.depot.IngestDepot
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
	def list(String path) {
		withPathInIngestDepot(path) { IngestDepot ingestDepot ->
			render view: 'list', model: [
					path       : ingestDepot.getPath(),
					pathAsArray: ingestDepot.getPathAsArray(),
					files      : ingestDepot.list()
			]
		}
	}

	/**
	 * Deletes a set of given files and/or folders on the ingest depot.
	 */
	def delete(String path) {
		withPathInIngestDepot(path) { IngestDepot ingestDepot ->
			List<String> toDelete = params.list('file')
			toDelete.each { ingestDepot.remove(it) }

			redirect action: 'list', params: [path: path]
		}
	}

	/**
	 * Simple wrapper method for establishing a connection to the given path on the ingest depot.
	 * @param path The path on the ingest depot.
	 * @param body What to do request from the ingest depot.
	 */
	private def withPathInIngestDepot(String path, Closure body) {

        Boolean secure = new Boolean(grailsApplication.config.ingestDepot.ftp.secure.toString())
        Boolean enterLocalPassiveMode = new Boolean(grailsApplication.config.ingestDepot.ftp.enterLocalPassiveMode.toString())
        String host = grailsApplication.config.ingestDepot.ftp.host
        Integer port = new Integer(grailsApplication.config.ingestDepot.ftp.port.toString())
        String username = grailsApplication.config.ingestDepot.ftp.username
        String password = grailsApplication.config.ingestDepot.ftp.password
        path = path ?: '/'

        try {
			IngestDepot ingestDepot = new IngestDepotImpl(host, port, username, password, secure, enterLocalPassiveMode, path)
			body(ingestDepot)
			ingestDepot.close()
		}
		catch (IOException ioe) {
			flash.status = 'error'
			flash.message = g.message(code: 'default.depot.fail.message', args: [ioe.getMessage()])
			redirect controller: 'collection', action: 'list'
		}
	}
}
