package org.iish.acquisition.depot

import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPSClient
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Provides actions on the ingest depot using FTP.
 */
class IngestDepotImpl implements IngestDepot {
	private FTPClient client
	private String path = '/'

	/**
	 * Constructor call for FTP access to the ingest depot.
	 * @param grailsApplication Required for configuration properties.
	 */
	IngestDepotImpl(GrailsApplication grailsApplication) {
		Boolean secure = new Boolean(grailsApplication.config.ingestDepot.ftp.secure.toString())
        Boolean isImplicit = grailsApplication.config.ingestDepot.ftp.isImplicit
        Boolean enterLocalPassiveMode = grailsApplication.config.ingestDepot.ftp.enterLocalPassiveMode
        String host = grailsApplication.config.ingestDepot.ftp.host
        Integer port = new Integer(grailsApplication.config.ingestDepot.ftp.port.toString())
        String username = grailsApplication.config.ingestDepot.ftp.username
        String password = grailsApplication.config.ingestDepot.ftp.password


		client = secure ? new FTPSClient(isImplicit) : new FTPClient()
		client.connect(host, port)
		client.login(username, password)
        if (enterLocalPassiveMode) client.enterLocalPassiveMode();
	}

	/**
	 * Constructor call for FTP access to the ingest depot.
	 * @param grailsApplication Required for configuration properties.
	 * @param path The path of the current working directory on the ingest depot.
	 */
	IngestDepotImpl(GrailsApplication grailsApplication, String path) {
		this(grailsApplication)
		setPath(path)
	}

	/**
	 * Sets the path of the current working directory.
	 * @param path The path in question.
	 */
	@Override
	void setPath(String path) {
		client.changeWorkingDirectory(getValidPath(path))
		this.path = client.printWorkingDirectory()
	}

	/**
	 * Returns the path of the current working directory.
	 * @return The path in question.
	 */
	@Override
	String getPath() {
		return path
	}

	/**
	 * Returns the path of the current working directory, split per folder in the hierarchy.
	 * @return The folders in the hierarchy.
	 */
	@Override
	String[] getPathAsArray() {
		return path.split('/').findAll { isValidName(it) }
	}

	/**
	 * Lists the folders and files in the current working directory.
	 * @return The folders and files.
	 */
	@Override
	List<IngestDepotFile> list() {
		return client.listFiles().findAll { FTPFile file ->
			return ((file.isDirectory() || file.isFile()) && isValidName(file.getName()))
		}.collect { FTPFile file ->
			IngestDepotFile ingestDepotFile = new IngestDepotFile()
			ingestDepotFile.path = getValidPath(path + '/' + file.getName())
			ingestDepotFile.name = file.getName()
			ingestDepotFile.size = file.getSize()
			ingestDepotFile.isDirectory = file.isDirectory()

			return ingestDepotFile
		}.sort { IngestDepotFile file1, IngestDepotFile file2 ->
			if (file1.isDirectory() && file2.isDirectory()) {
				return file1.getName().compareTo(file2.getName())
			}
			else {
				return file1.isDirectory() ? -1 : 1
			}
		}
	}

	/**
	 * Removes the file or folder on the given path from the ingest depot.
	 * @param path The file or folder to delete.
	 */
	@Override
	void remove(String path) {
		path = getValidPath(path)
		FTPFile[] files = client.listFiles(path).findAll { isValidName(it.getName()) }

		files.each { FTPFile file ->
			if (file.isDirectory()) {
				String newPath = "$path/${file.getName()}"
				remove(newPath)
			}
			else {
				client.deleteFile(path)
			}
		}

		client.removeDirectory(path)
	}

	/**
	 * Closes the connection to the ingest depot.
	 */
	@Override
	void close() {
		client.logout()
		client.disconnect()
	}

	/**
	 * Returns a valid version of the given path. (Without empty folder names, '.' and '..')
	 * @param path The path in question.
	 * @return The valid version of the given path.
	 */
	private static String getValidPath(String path) {
		return '/' + path.split('/').findAll { isValidName(it) }.collect { it.trim() }.join('/')
	}

	/**
	 * Returns whether the given folder name is valid. (Not empty , '.' or '..')
	 * @param name The name of the folder in question.
	 * @return Whether the name is valid.
	 */
	private static boolean isValidName(String name) {
		return (!name.isAllWhitespace() && !name.equals('.') && !name.equals('..'))
	}
}
