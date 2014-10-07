package org.iish.acquisition.domain

/**
 * The collection of analog material descriptions.
 */
class AnalogMaterialCollection {

	static belongsTo = [
			collection: Collection
	]

	static hasMany = [
			materials: AnalogMaterial
	]

	static constraints = {
		collection unique: true
	}

	static mapping = {
		table 'analog_material_collections'
		materials cascade: 'all-delete-orphan', sort: 'materialType'
		collection fetch: 'join'
	}

	/**
	 * Searches the collection for a specific material type of the given unit.
	 * @param materialType The material type to search for.
	 * @param unit The unit type to search for.
	 * @return The material if found, otherwise null is returned.
	 */
	AnalogMaterial getMaterialByTypeAndUnit(MaterialType materialType, AnalogUnit unit) {
		materials?.find {
			(it.materialType.id == materialType.id) && (it.unit == unit)
		}
	}

	/**
	 * Searches the collection for a specific material type of any unit at all.
	 * @param materialType The material type to search for.
	 * @return The set of materials found, otherwise null is returned.
	 */
	Set<AnalogMaterial> getMaterialsByType(MaterialType materialType) {
		materials?.findAll {
			it.materialType.id == materialType.id
		}
	}

	@Override
	String toString() {
		return "$collection (analog material collection)"
	}
}
