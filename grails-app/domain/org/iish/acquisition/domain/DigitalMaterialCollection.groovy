package org.iish.acquisition.domain

import org.iish.acquisition.converter.BigDecimalValueConverter
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
		totalSize min: BigDecimal.ZERO, scale: 5
		unit nullable: true
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

	void afterInsert() {
		collection.afterInsertOrUpdate()
	}

	void beforeUpdate() {
		collection.afterInsertOrUpdate()
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
	 * No empty or 0 as value, unless '000' was entered.
	 * @param enteredNoOfFiles The value as entered by the user.
	 */
	void setEnteredNumberOfFiles(String enteredNoOfFiles) {
		if (enteredNoOfFiles?.equals('000')) {
			numberOfFiles = 0
		}
		else {
			Integer noOfFiles = enteredNoOfFiles?.isInteger() ? enteredNoOfFiles.toInteger() : null
			numberOfFiles = (noOfFiles && noOfFiles > 0) ? noOfFiles : null
		}
	}

	/**
	 * No empty or 0 as value, unless '000' was entered.
	 * @param enteredTotalSize The value as entered by the user.
	 * @param bigDecimalConverter The converter from String to BigDecimal.
	 */
	void setEnteredTotalSize(String enteredTotalSize, BigDecimalValueConverter bigDecimalConverter) {
		if (enteredTotalSize?.equals('000')) {
			totalSize = 0
		}
		else {
			BigDecimal totalSize = (BigDecimal) bigDecimalConverter.convert(enteredTotalSize)
			this.totalSize = (totalSize && totalSize.compareTo(BigDecimal.ZERO) > 0) ? totalSize : null
		}
	}

	/**
	 * Returns whether all digital material collection details (except materials) were filled out.
	 * @return Whether all digital material collection details were filled out.
	 */
	boolean isFilledOut() {
		return (numberOfFiles || totalSize || unit)
	}

	/**
	 * Returns the number of files in a human-readable format.
	 * @return The number of files in a human-readable format.
	 */
	String numberOfFilesToString() {
		String nrOfFiles = ''
		if (numberOfFiles == 0) {
			nrOfFiles = '000'
		}
		else if (numberOfFiles) {
			nrOfFiles = numberOfFiles.toString()
		}

		return nrOfFiles
	}

	/**
	 * Returns the total size in a human-readable format.
	 * @return The total size in a human-readable format.
	 */
	String totalSizeToString() {
		return (totalSize == 0) ? '000' : PrinterUtil.printBigDecimal(totalSize)
	}

	/**
	 * Returns the total size in a human-readable format with the unit.
	 * @return The total size in a human-readable format with the unit.
	 */
	String totalSizeToStringWithUnit() {
		if (unit) {
			return "${totalSizeToString()} ${unit.toString()}"
		}
		else {
			return totalSizeToString()
		}
	}

	@Override
	String toString() {
		return "$collection (digital material collection)"
	}
}
