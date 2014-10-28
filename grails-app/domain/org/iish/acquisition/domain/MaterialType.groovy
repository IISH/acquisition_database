package org.iish.acquisition.domain

/**
 * The material types that a collection could contain.
 */
class MaterialType {
	static final long ARCHIVE_ID = 1L
	static final long BOOKS_ID = 2L
	static final long PERIODICALS_ID = 3L
	static final long MOVING_IMAGES_ID = 4L
	static final long EPHEMERA_ID = 5L
	static final long SOUND_ID = 6L
	static final long POSTERS_ID = 7L
	static final long DRAWINGS_ID = 8L
	static final long PHOTOS_ID = 9L
	static final long OTHER_ID = 10L

	String name
	boolean inMeters
	boolean inNumbers

	static hasMany = [
			analogMaterials : AnalogMaterial,
			digitalMaterials: DigitalMaterial
	]

	static constraints = {
		name blank: false, maxSize: 255, unique: true
	}

	static mapping = {
		table 'material_types'
		sort 'id'
		cache true
		name index: 'material_types_name_idx'
	}

	/**
	 * Returns a set of possible analog units for the given material type.
	 * @return A set of possible analog units.
	 */
	Set<AnalogUnit> getMatchingAnalogUnits() {
		Set<AnalogUnit> analogUnits = []
		if (inMeters) {
			analogUnits.add(AnalogUnit.METER)
		}
		if (inNumbers) {
			analogUnits.add(AnalogUnit.NUMBER)
		}
		return analogUnits
	}

	@Override
	String toString() {
		return name
	}
}
