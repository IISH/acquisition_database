package org.iish.acquisition.domain

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

/**
 * The statuses a process for a collection running on the ingest depot can have.
 */
enum IngestDepotStatusCode {
	NEW_DIGITAL_MATERIAL_COLLECTION(1, SUB_CODE_BUSY),
	CREATE_FOLDER(2, SUB_CODE_BUSY),
	CREATE_FOLDER_SUCCESS(2, SUB_CODE_SUCCESS),
	CREATE_FOLDER_FAIL(2, SUB_CODE_FAIL),
	UPLOAD_FILES(3, SUB_CODE_BUSY),
	UPLOAD_FILES_SUCCESS(3, SUB_CODE_SUCCESS),
	UPLOAD_FILES_FAIL(3, SUB_CODE_FAIL),
	FILE_IDENTIFICATION(4, SUB_CODE_BUSY),
	FILE_IDENTIFICATION_SUCCESS(4, SUB_CODE_SUCCESS),
	FILE_IDENTIFICATION_FAIL(4, SUB_CODE_FAIL),
	CREATE_ISO(5, SUB_CODE_BUSY),
	CREATE_ISO_SUCCESS(5, SUB_CODE_SUCCESS),
	CREATE_ISO_FAIL(5, SUB_CODE_FAIL),
	UPLOAD_SOR(6, SUB_CODE_BUSY),
	UPLOAD_SOR_SUCCESS(6, SUB_CODE_SUCCESS),
	UPLOAD_SOR_FAIL(6, SUB_CODE_FAIL)

	static final int SUB_CODE_BUSY = 0
	static final int SUB_CODE_SUCCESS = 1
	static final int SUB_CODE_FAIL = 2

	String id
	int code
	int subCode

	IngestDepotStatusCode(int code, int subCode) {
		this.id = "$code.$subCode"
		this.code = code
		this.subCode = subCode
	}

	/**
	 * Will attempt to resolve a message for the current status.
	 * @param messageSource The messageSource which will attempt to resolve messages.
	 * @param uploadName The name of the upload process, if it exists.
	 * @return The translated message, if found.
	 */
	String getMessage(MessageSource messageSource, String uploadName = '') {
		String name = "${name().toLowerCase().replace('_', '.')}.message"
		messageSource.getMessage(name, [uploadName] as String[], name, LocaleContextHolder.getLocale())
	}

	/**
	 * Returns whether this status is meant for the upload process, rather than the main process.
	 * @return True when this status is meant for the upload process.
	 */
	boolean isStatusForUploadProcess() {
		return ((code == UPLOAD_FILES.code) || (code == FILE_IDENTIFICATION.code))
	}

	/**
	 * Returns an ingest depot status code by its code and sub-code.
	 * @param code The code in question.
	 * @param subCode The sub-code in question.
	 * @return The matching ingest depot status code, if found.
	 */
	static IngestDepotStatusCode getByCodeAndSubcode(int code, int subCode) {
		values().find { (it.code == code) && (it.subCode == subCode) }
	}

	@Override
	String toString() {
		return id
	}
}