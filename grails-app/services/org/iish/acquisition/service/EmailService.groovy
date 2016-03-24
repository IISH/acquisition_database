package org.iish.acquisition.service

import grails.gsp.PageRenderer
import grails.plugin.mail.MailService
import grails.util.Holders
import org.apache.log4j.Logger
import org.iish.acquisition.command.RecipientsCommand
import org.iish.acquisition.domain.Authority
import org.iish.acquisition.domain.DigitalMaterialStatus
import org.iish.acquisition.domain.DigitalMaterialStatusSubCode
import org.iish.acquisition.util.EmailException

/**
 * Service responsible for sending emails.
 */
class EmailService {
	private static final Logger LOG = Logger.getLogger(EmailService.class)

	MailService mailService
	PageRenderer groovyPageRenderer

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

	/**
	 * Sends an email with information about a status change of the digital material.
	 * @param digitalMaterialStatus The digital material status.
	 */
	void sentStatusChangeEmail(DigitalMaterialStatus digitalMaterialStatus) {
		try {
			if (digitalMaterialStatus.statusSubCode == DigitalMaterialStatusSubCode.FINISHED ||
					digitalMaterialStatus.statusSubCode == DigitalMaterialStatusSubCode.FAILED) {
				Authority offloader1 = Authority.findByAuthority(Authority.ROLE_OFFLOADER_1)
				RecipientsCommand recipientsCommand = RecipientsCommand.getRecipientsByAuthority(offloader1)

				if (digitalMaterialStatus.statusSubCode == DigitalMaterialStatusSubCode.FAILED) {
					recipientsCommand.addAdditionalRecipient("JIRA", Holders.config.jira.email.address.toString())
					sentFailureEmail(digitalMaterialStatus, recipientsCommand)
				}
				else {
					sentFinishedEmail(digitalMaterialStatus, recipientsCommand)
				}
			}
		}
		catch (Exception e) {
			LOG.error("Could not sent status change email: ${e.message}", e)
		}
	}

	/**
	 * Sends an email about a successful event of the digital material.
	 * @param digitalMaterialStatus The digital material status.
	 * @param recipientsCommand The recipients of the email.
	 */
	private void sentFinishedEmail(DigitalMaterialStatus digitalMaterialStatus, RecipientsCommand recipientsCommand) {
		mailService.sendMail {
			from 'Acquisition database <noreply@iisg.nl>'
			to recipientsCommand.getRecipientsArray()
			subject "Finished: ${digitalMaterialStatus} for PID ${digitalMaterialStatus.collection.objectRepositoryPID}".toString()
			text groovyPageRenderer.render(view: '/email/finished', model: [digitalMaterialStatus: digitalMaterialStatus])
		}
	}

	/**
	 * Sends an email about a failed event of the digital material.
	 * @param digitalMaterialStatus The digital material status.
	 * @param recipientsCommand The recipients of the email.
	 */
	private void sentFailureEmail(DigitalMaterialStatus digitalMaterialStatus, RecipientsCommand recipientsCommand) {
		mailService.sendMail {
			from 'Acquisition database <noreply@iisg.nl>'
			to recipientsCommand.getRecipientsArray()
			subject "Failure: ${digitalMaterialStatus} for PID ${digitalMaterialStatus.collection.objectRepositoryPID}".toString()
			text groovyPageRenderer.render(view: '/email/failed', model: [digitalMaterialStatus: digitalMaterialStatus])
		}
	}
}
