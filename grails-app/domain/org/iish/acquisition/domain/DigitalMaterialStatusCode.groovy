package org.iish.acquisition.domain

/**
 * The status code a digital material process may have.
 */
class DigitalMaterialStatusCode {
	public static final long NEW_DIGITAL_MATERIAL_COLLECTION = 10L

	public static final long FOLDER_CREATION_RUNNING = 20L
	public static final long FOLDER_CREATED = 30L

	public static final long MATERIAL_UPLOADED = 40L

	public static final long BACKUP_RUNNING = 50L
	public static final long BACKUP_FINISHED = 60L

	public static final long READY_FOR_RESTORE = 70L
	public static final long RESTORE_RUNNING = 80L
	public static final long RESTORE_FINISHED = 90L

	public static final long READY_FOR_PERMANENT_STORAGE = 100L
	public static final long UPLOADING_TO_PERMANENT_STORAGE = 110L
	public static final long MOVED_TO_PERMANENT_STORAGE = 120L

	String status
	boolean isSetByUser

	static hasMany = [
		digitalMaterialStatuses: DigitalMaterialStatus
	]

	static constraints = {
		status blank: false, maxSize: 255, unique: true
	}

	static mapping = {
		table 'digital_material_status_codes'
		sort 'id'
		cache true
		id generator: 'assigned'
	}

	@Override
	String toString() {
		return status
	}
}