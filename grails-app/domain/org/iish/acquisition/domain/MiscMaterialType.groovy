package org.iish.acquisition.domain

/**
 * The mic material types that a collection could contain.
 */
class MiscMaterialType {
	static final long DISKETTES_ID = 1L
	static final long DVDS_CDS_ID = 2L

	String name

	static hasMany = [
			miscMaterials : MiscMaterial
	]

	static constraints = {
		name blank: false, maxSize: 255, unique: true
	}

	static mapping = {
		table 'misc_material_types'
		sort 'id'
		cache true
		name index: 'misc_material_types_name_idx'
	}

	@Override
	String toString() {
		return name
	}
}
