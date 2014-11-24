package org.iish.acquisition.depot

/**
 * Provides actions on the ingest depot.
 */
interface IngestDepot {

	/**
	 * Sets the path of the current working directory.
	 * @param path The path in question.
	 */
	void setPath(String path)

	/**
	 * Returns the path of the current working directory.
	 * @return The path in question.
	 */
	String getPath()

	/**
	 * Returns the path of the current working directory, split per folder in the hierarchy.
	 * @return The folders in the hierarchy.
	 */
	String[] getPathAsArray()

	/**
	 * Lists the folders and files in the current working directory.
	 * @return The folders and files.
	 */
	List<IngestDepotFile> list()

	/**
	 * Removes the file or folder on the given path from the ingest depot.
	 * @param path The file or folder to delete.
	 */
	void remove(String path)

	/**
	 * Closes the connection to the ingest depot.
	 */
	void close()
}
