package org.iish.acquisition.service

import grails.plugin.mail.MailService
import grails.plugin.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.iish.acquisition.domain.Collection
import org.iish.acquisition.domain.User
import org.iish.acquisition.util.EmailException
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder as LCH

/**
 * Service responsible for sending emails.
 */
class EmailService {
	MailService mailService
	MessageSource messageSource
	GrailsApplication grailsApplication
	LinkGenerator grailsLinkGenerator
	SpringSecurityService springSecurityService

	/**
	 * Sends an email with a link to the given collection to the configured recipient
	 * and the initiator of this request with the request to complement the given collection.
	 * @param collection The collection to complement.
	 * @throws EmailException
	 */
	void sentComplementRequestEmail(Collection collection) throws EmailException {
		try {
			String recipientAlwaysLogin = grailsApplication.config.email.complement.request.recipient

			User recipientAlways = User.findByLogin(recipientAlwaysLogin)
			User recipientInitiator = (User) springSecurityService.getCurrentUser()

			mailService.sendMail {
				from 'Acquisition database <noreply@iisg.nl>'
				to "${recipientAlways.toString()} <${recipientAlways.email}>",
						"${recipientInitiator.toString()} <${recipientInitiator.email}>"
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
