package org.iish.acquisition.domain

/**
 * Represents how much of a certain type of digital material belongs to a collection.
 */
class DigitalMaterial {

	static belongsTo = [
			materialCollection: DigitalMaterialCollection,
			materialType      : MaterialType
	]

	static constraints = {
	}

	static mapping = {
		table 'digital_materials'

		materialCollection fetch: 'join'
		materialType fetch: 'join'
	}

	@Override
	String toString() {
		return materialType.getNameDigital()
	}
}
