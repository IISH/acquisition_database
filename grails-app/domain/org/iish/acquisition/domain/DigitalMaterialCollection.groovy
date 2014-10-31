package org.iish.acquisition.domain

import org.iish.acquisition.util.PrinterUtil

/**
 * The collection of digital material descriptions.
 */
class DigitalMaterialCollection {
	Integer numberOfFiles
	BigDecimal totalSize
	ByteUnit unit
	Integer numberOfDiskettes
	Integer numberOfOpticalDisks

	static belongsTo = [
			collection: Collection
	]

	static hasMany = [
			materials: DigitalMaterial
	]

	static constraints = {
		numberOfFiles nullable: true, min: 0
		totalSize nullable: true, min: BigDecimal.ZERO, scale: 5
		unit nullable: true
		numberOfDiskettes nullable: true, min: 0
		numberOfOpticalDisks nullable: true, min: 0
		collection unique: true
		materials validator: { val, obj ->
			if (!val || val.isEmpty()) {
				'collection.no.material.digital.collection.message'
			}
		}
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
	 * Returns whether all digital material collection details (except materials) were filled out.
	 * @return Whether all digital material collection details were filled out.
	 */
	boolean isFilledOut() {
		return (numberOfFiles || totalSize || unit || numberOfDiskettes || numberOfOpticalDisks)
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
