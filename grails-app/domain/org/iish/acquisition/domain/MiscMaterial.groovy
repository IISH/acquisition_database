package org.iish.acquisition.domain

/**
 * Represents how many items of a certain type of misc material belongs to a collection.
 */
class MiscMaterial {
	Integer size
	boolean isSelected = true

	// Transient value, only used to warn the user when he/she forgot to check the checkbox
	static transients = ['isSelected']

	static belongsTo = [
			materialCollection: MiscMaterialCollection,
			materialType      : MiscMaterialType
	]

	static constraints = {
		size nullable: true, min: 0, validator: { val, obj ->
			if (!obj.isSelected) {
				['collection.not.checked.material.message', obj.materialType.name]
			}
			else if (val == null) {
				['collection.no.size.material.message', obj.materialType.name]
			}
		}
	}

	static mapping = {
		table 'misc_materials'

		materialCollection fetch: 'join'
		materialType fetch: 'join'
	}

	@Override
	String toString() {
		return "${materialType.name}: ${size} ${AnalogUnit.NUMBER.toString()}"
	}
}
