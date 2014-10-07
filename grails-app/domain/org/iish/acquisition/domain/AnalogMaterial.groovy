package org.iish.acquisition.domain

/**
 * Represents how much of a certain type of analog material belongs to a collection.
 */
class AnalogMaterial {
	Integer size
	AnalogUnit unit

	static belongsTo = [
			materialCollection: AnalogMaterialCollection,
			materialType      : MaterialType
	]

	static constraints = {
		size min: 0
	}

	static mapping = {
		table 'analog_materials'

		materialCollection fetch: 'join'
		materialType fetch: 'join'
	}

	@Override
	String toString() {
		return "$materialType: $size $unit"
	}
}
