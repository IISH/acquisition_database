package org.iish.acquisition.domain

/**
 * The collection of digital material descriptions.
 */
class DigitalMaterialCollection {
	Integer numberOfFiles
	Integer totalSize
	ByteUnit unit

	static belongsTo = [
			collection: Collection
	]

	static hasMany = [
			materials: DigitalMaterial
	]

	static constraints = {
		numberOfFiles min: 0
		totalSize min: 0
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

	@Override
	String toString() {
		return "$collection (digital material collection)"
	}
}
