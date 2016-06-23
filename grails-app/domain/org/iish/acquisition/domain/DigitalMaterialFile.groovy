package org.iish.acquisition.domain

import org.iish.acquisition.util.PrinterUtil

/**
 * Represents an uploaded file from the ingest depot.
 */
class DigitalMaterialFile {
	String originalFilename
	Long size
	String contentType
	byte[] file

	static constraints = {
		originalFilename maxSize: 255
		contentType maxSize: 100
	}

	static mapping = {
		table 'digital_material_files'
		file sqlType: 'mediumblob'
	}

	/**
	 * Returns a DigitalMaterialFile object from the database, without the file data.
	 * @param id The id of the digital material file.
	 * @return DigitalMaterialFile object.
	 */
	static DigitalMaterialFile getFileMetaData(Long id) {
		return withCriteria {
			eq('id', id)
			projections {
				property('id')
				property('originalFilename')
				property('size')
				property('contentType')
			}
		}.collect {
			DigitalMaterialFile digitalMaterialFile = new DigitalMaterialFile(
					originalFilename: it[1],
					size: new Long(it[2].toString()),
					contentType: it[3]
			)
			digitalMaterialFile.setId(new Long(it[0].toString()))
			return digitalMaterialFile
		}.first()
	}

	/**
	 * Returns the file size in a human friendly readable way.
	 * @return The file size.
	 */
	String getReadableFileSize() {
		return PrinterUtil.printFileSize(size)
	}

	@Override
	String toString() {
		return originalFilename
	}
}
