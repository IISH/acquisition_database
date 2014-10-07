package org.iish.acquisition.domain

/**
 * Represents an uploaded photo of the collection as delivered.
 */
class Photo {
	String originalFilename
	Long size
	String contentType
	byte[] photo

	static belongsTo = [
			collection: Collection
	]

	static constraints = {
		originalFilename maxSize: 255
		contentType maxSize: 50
	}

	static mapping = {
		table 'photos'
		photo sqlType: 'mediumblob'
	}

	@Override
	String toString() {
		return originalFilename
	}
}
