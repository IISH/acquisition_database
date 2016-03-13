package org.iish.acquisition.service

import grails.plugin.mail.MailService
import org.iish.acquisition.command.RecipientsCommand
import org.iish.acquisition.util.EmailException

/**
 * Service responsible for sending emails.
 */
class EmailService {
	MailService mailService

	/**
	 * Sends an email with a link to the given collection to the configured recipient
	 * and the initiator of this request with the request to complement the given collection.
	 * @param recipientsCommand The recipients of this email.
	 * @param subj The subject of the email.
     * @param body The body of the email.
	 * @throws EmailException
	 */
	void sentComplementRequestEmail(RecipientsCommand recipientsCommand, String subj, String body)
            throws EmailException {
		try {
			mailService.sendMail {
				from 'Acquisition database <noreply@iisg.nl>'
				to recipientsCommand.getRecipientsArray()
				subject subj
				text body
			}
		}
		catch (Exception e) {
			throw new EmailException(e.message, e)
		}
	}
}
