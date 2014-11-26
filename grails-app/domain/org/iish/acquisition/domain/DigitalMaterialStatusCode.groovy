package org.iish.acquisition.domain

/**
 * The status code a digital material process may have.
 */
class DigitalMaterialStatusCode {
	public static final long NEW_DIGITAL_MATERIAL_COLLECTION = 1L
	public static final long FOLDER_CREATED = 2L
	public static final long MATERIAL_UPLOADED = 3L
	public static final long BACKUP_RUNNING = 4L
	public static final long BACKUP_FINISHED = 5L
	public static final long READY_FOR_PERMANENT_STORAGE = 6L
	public static final long UPLOADING_TO_PERMANENT_STORAGE = 7L
	public static final long MOVED_TO_PERMANENT_STORAGE = 8L

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
	}

	@Override
	String toString() {
		return status
	}
}