package org.iish.acquisition.command

import grails.validation.Validateable
import org.iish.acquisition.domain.User

/**
 * Command object which contains all email recipients.
 */
@Validateable
class RecipientsCommand {
	List<Long> recipients

	static constraints = {
		recipients validator: { val, obj ->
			if (!val || val.isEmpty()) {
				'email.missing.recipients.message'
			}
		}
	}

	/**
	 * Returns the list of users who are the recipients.
	 * @return The users.
	 */
	List<User> getUsers() {
		return User.findAllByMayReceiveEmailAndEmailIsNotNull(true).findAll {
			recipients?.contains(it.id)
		}
	}

	/**
	 * Returns the list of users who are the recipients as an array as accepted by the mail functionality.
	 * @return A list of strings for each recipient.
	 */
	String[] getRecipientsArray() {
		return getUsers().collect { "${it.toString()} <${it.email}>".toString() }.toArray() as String[]
	}
}
