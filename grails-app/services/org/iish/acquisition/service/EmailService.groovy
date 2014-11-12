package org.iish.acquisition.service

import grails.plugin.mail.MailService
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.iish.acquisition.command.RecipientsCommand
import org.iish.acquisition.domain.Collection
import org.iish.acquisition.util.EmailException
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder as LCH

/**
 * Service responsible for sending emails.
 */
class EmailService {
	MailService mailService
	MessageSource messageSource
	LinkGenerator grailsLinkGenerator

	/**
	 * Sends an email with a link to the given collection to the configured recipient
	 * and the initiator of this request with the request to complement the given collection.
	 * @param recipientsCommand The recipients of this email.
	 * @param collection The collection to complement.
	 * @throws EmailException
	 */
	void sentComplementRequestEmail(RecipientsCommand recipientsCommand, Collection collection) throws EmailException {
		try {
			mailService.sendMail {
				from 'Acquisition database <noreply@iisg.nl>'
				to recipientsCommand.getRecipientsArray()
				subject messageSource.getMessage('email.complement.request.subject', new Object[0], LCH.locale)
				text grailsLinkGenerator.
						link(controller: 'collection', action: 'edit', id: collection.id, absolute: true)
			}
		}
		catch (Exception e) {
			throw new EmailException(e.message, e)
		}
	}
}
