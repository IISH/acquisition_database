package org.iish.acquisition.domain

import org.springframework.context.MessageSource

/**
 * An abstract class, for every domain class that has an IngestDepotStatusCode.
 */
abstract class AbstractIngestDepotStatus {
	transient MessageSource messageSource

	IngestDepotStatusCode statusCode

	/**
	 * Returns a human readable status message.
	 * @return A translated status message.
	 */
	String getHumanReadableMessage() {
		return getStatusCode().getMessage(messageSource)
	}

	/**
	 * Get the sub code of the status.
	 * @return The sub code of the status.
	 */
	int getStatusSubCode() {
		return getStatusCode().subCode
	}
}
