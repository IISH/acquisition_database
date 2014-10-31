package org.iish.acquisition.util

/**
 * Thrown when exception occurs when sending emails.
 */
class EmailException extends Exception {
	public EmailException(String message, Throwable cause) {
		super(message, cause)
	}
}
