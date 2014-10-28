package org.iish.acquisition.domain

import org.iish.acquisition.util.PrinterUtil

/**
 * The collection of digital material descriptions.
 */
class DigitalMaterialCollection {
	Integer numberOfFiles
	BigDecimal totalSize
	ByteUnit unit

	static belongsTo = [
			collection: Collection
	]

	static hasMany = [
			materials: DigitalMaterial
	]

	static constraints = {
		numberOfFiles min: 0
		totalSize min: BigDecimal.ZERO, scale: 5, maxSize: 5
		collection unique: true
	}

	static mapping = {
		table 'digital_material_collections'
		materials cascade: 'all-delete-orphan', sort: 'materialType'
		collection fetch: 'join'
	}

	/**
	 * Searches the collection for a specific material type.
	 * @param materialType The material type to search for.
	 * @return The material if found, otherwise null is returned.
	 */
	DigitalMaterial getMaterialByType(MaterialType materialType) {
		materials?.find {
			it.materialType.id == materialType.id
		}
	}

	/**
	 * Returns the total size in a human-readable format.
	 * @return The total size in a human-readable format.
	 */
	String totalSizeToString() {
		return PrinterUtil.printBigDecimal(totalSize)
	}

	@Override
	String toString() {
		return "$collection (digital material collection)"
	}
}
