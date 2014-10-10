package org.iish.acquisition.domain

import java.math.RoundingMode

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

	/**
	 * Returns Photo objects from the database, without the photo data.
	 * @param collection The collection to return the Photos of.
	 * @return Photo objects.
	 */
	static List<Photo> getPhotoMetaData(Collection collection) {
		return withCriteria {
			eq('collection', collection)
			projections {
				property('id')
				property('originalFilename')
				property('size')
				property('contentType')

			}
		}.collect {
			Photo photo = new Photo(originalFilename: it[1], size: new Long(it[2].toString()), contentType: it[3])
			photo.setId(new Long(it[0].toString()))
			return photo
		}
	}

	/**
	 * Returns the file size in a human friendly readable way.
	 * @return The file size.
	 */
	String getReadableFileSize() {
		if (!size) {
			return '0 bytes'
		}

		if (size / 1024 > 1) {
			if (size / 1048576 > 1) {
				return "${(size / 1048576).setScale(2, RoundingMode.HALF_UP)} MB"
			}

			return "${(size / 1024).setScale(2, RoundingMode.HALF_UP)} KB"
		}

		return "${size} bytes"
	}

	@Override
	String toString() {
		return originalFilename
	}
}
