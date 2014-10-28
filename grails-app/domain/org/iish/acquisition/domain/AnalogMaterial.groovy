package org.iish.acquisition.domain

import org.iish.acquisition.util.PrinterUtil

/**
 * Represents how much of a certain type of analog material belongs to a collection.
 */
class AnalogMaterial {
	BigDecimal size
	AnalogUnit unit

	static belongsTo = [
			materialCollection: AnalogMaterialCollection,
			materialType      : MaterialType
	]

	static constraints = {
		size min: BigDecimal.ZERO, scale: 5, maxSize: 5
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
		return "$materialType: ${sizeToString()} $unit"
	}
}
