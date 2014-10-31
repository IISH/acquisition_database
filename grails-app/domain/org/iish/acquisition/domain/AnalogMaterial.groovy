package org.iish.acquisition.domain

import org.iish.acquisition.util.PrinterUtil

/**
 * Represents how much of a certain type of analog material belongs to a collection.
 */
class AnalogMaterial {
	BigDecimal size
	AnalogUnit unit
	boolean isSelected = true

	// Transient value, only used to warn the user when he/she forgot to check the checkbox
	static transients = ['isSelected']

	static belongsTo = [
			materialCollection: AnalogMaterialCollection,
			materialType      : MaterialType
	]

	static constraints = {
		size nullable: true, min: BigDecimal.ZERO, scale: 5, validator: { val, obj ->
			if (!obj.isSelected) {
				['collection.not.checked.material.message', obj.materialType.getNameAnalog()]
			}
			else if (val == null) {
				['collection.no.size.material.message', obj.materialType.getNameAnalog()]
			}
		}
	}

	static mapping = {
		table 'analog_materials'

		materialCollection fetch: 'join'
		materialType fetch: 'join'
	}

	/**
	 * Returns the size in a human-readable format.
	 * @return The size in a human-readable format.
	 */
	String sizeToString() {
		return PrinterUtil.printBigDecimal(size)
	}

	@Override
	String toString() {
		return "${materialType.getNameAnalog()}: ${sizeToString()} $unit"
	}
}
