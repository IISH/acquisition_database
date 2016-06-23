package org.iish.acquisition.domain

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder as LCH

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
	static final long OTHER_UNKNOWN_ID = 10L

	transient MessageSource messageSource

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

	/**
	 * Returns the analog name of this material type.
	 * @return The analog name.
	 */
	String getNameAnalog() {
		return messageSource.getMessage("materialType.name.${id}.analog".toString(), new Object[0], LCH.locale)
	}

	/**
	 * Returns the digital name of this material type.
	 * @return The digital name.
	 */
	String getNameDigital() {
		return messageSource.getMessage("materialType.name.${id}.digital".toString(), new Object[0], LCH.locale)
	}

	/**
	 * Returns the total number of unique material types,
	 * if you take the meters and numbers separately.
	 * @param materialTypes The material types.
	 * @return The total number.
	 */
	static int getTotalNumberOfUniqueTypes(List<MaterialType> materialTypes) {
		List<Integer> meters = materialTypes.collect { it.inMeters ? 1 : 0 }
		List<Integer> numbers = materialTypes.collect { it.inNumbers ? 1 : 0 }
		return (meters + numbers).sum() as Integer ?: 0
	}

	@Override
	String toString() {
		return name
	}
}
