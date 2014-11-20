package org.iish.acquisition.depot

import org.iish.acquisition.util.PrinterUtil

/**
 * Represents a file on the ingest depot.
 */
class IngestDepotFile {
	String path
	String name
	long size
	boolean isDirectory

	/**
	 * Returns the path in which this file is located.
	 * @return The path in which this file is located.
	 */
	String getPath() {
		return path
	}

	/**
	 * Sets the path in which this file is located.
	 * @param path The path in which this file is located.
	 */
	void setPath(String path) {
		this.path = path
	}

	/**
	 * Returns the name of the file or directory in question.
	 * @return The name of the file or directory in question.
	 */
	String getName() {
		return name
	}

	/**
	 * Sets the name of the file or directory in question.
	 * @param name The name of the file or directory in question.
	 */
	void setName(String name) {
		this.name = name
	}

	/**
	 * Returns the file size (in bytes) of the file in question.
	 * @return The file size (in bytes) of the file in question.
	 */
	long getSize() {
		return size
	}

	/**
	 * Sets the file size (in bytes) of the file in question.
	 * @param size The file size (in bytes) of the file in question.
	 */
	void setSize(long size) {
		this.size = size
	}

	/**
	 * Returns the file size (of the file in question in a human readable format.
	 * @return The file size (of the file in question in a human readable format.
	 */
	String getReadableFileSize() {
		return PrinterUtil.printFileSize(size)
	}

	/**
	 * Returns whether this item represents a directory, rather than a file.
	 * @return Whether this item represents a directory, rather than a file.
	 */
	boolean isDirectory() {
		return isDirectory
	}

	/**
	 * Sets whether this item represents a directory, rather than a file.
	 * @param isDirectory Whether this item represents a directory, rather than a file.
	 */
	void setIsDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory
	}
}
