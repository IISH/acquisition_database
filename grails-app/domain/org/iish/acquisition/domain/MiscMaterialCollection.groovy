package org.iish.acquisition.domain

/**
 * The collection of material descriptions that do not fit in either the analog or the digital material collections.
 */
class MiscMaterialCollection {

	static belongsTo = [
			collection: Collection
	]

	static hasMany = [
			materials: MiscMaterial
	]

	static constraints = {
		collection unique: true
	}

	static mapping = {
		table 'misc_material_collections'
		materials cascade: 'all-delete-orphan', sort: 'materialType'
		collection fetch: 'join'
	}

	/**
	 * Searches the collection for a specific material type.
	 * @param miscMaterialType The material type to search for.
	 * @return The material found, otherwise null is returned.
	 */
	MiscMaterial getMaterialByType(MiscMaterialType materialType) {
		materials?.find {
			it.materialType.id == materialType.id
		}
	}

	@Override
	String toString() {
		return "$collection (misc material collection)"
	}
}
