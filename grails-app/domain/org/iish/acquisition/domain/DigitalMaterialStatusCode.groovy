package org.iish.acquisition.domain

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder as LCH

/**
 * The status code a digital material process may have.
 */
class DigitalMaterialStatusCode {
	public static final long FOLDER = 10L
	public static final long BACKUP = 20L
	public static final long RESTORE = 30L
	public static final long STAGINGAREA = 40L
	public static final long SOR = 50L
	public static final long CLEANUP = 60L
	public static final long AIP = 70L

	String status
	boolean isSetByUser
	boolean confirmRequired

	transient MessageSource messageSource

	static belongsTo = [
			dependsOn     : DigitalMaterialStatusCode,
			needsAuthority: Authority
	]

	static hasMany = [
			isDependentFor         : DigitalMaterialStatusCode,
			digitalMaterialStatuses: DigitalMaterialStatus
	]

	static constraints = {
		id bindable: true
		status blank: false, maxSize: 255, unique: true
		dependsOn nullable: true
		needsAuthority nullable: true
	}

	static mapping = {
		table 'digital_material_status_codes'
		sort 'id'
		cache true

		id generator: 'assigned'
		dependsOn fetch: 'join'
		needsAuthority fetch: 'join'
	}

	@Override
	String toString() {
		return messageSource.getMessage("digitalMaterialStatusCode.status.${id}", new Object[0], status, LCH.locale)
	}
}