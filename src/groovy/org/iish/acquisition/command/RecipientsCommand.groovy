package org.iish.acquisition.command

import grails.validation.Validateable
import org.iish.acquisition.domain.Authority
import org.iish.acquisition.domain.User
import org.iish.acquisition.domain.UserAuthority

/**
 * Command object which contains all email recipients.
 */
@Validateable
class RecipientsCommand {
	List<Long> recipients

	private List<String> additional = []

	static constraints = {
		recipients validator: { val, obj ->
			if (!val || val.isEmpty()) {
				'email.missing.recipients.message'
			}
		}
	}

	/**
	 * Add additional recipients.
	 * @param name The name of the recipient.
	 * @param email The email address of the recipient.
	 */
	void addAdditionalRecipient(String name, String email) {
		additional.add("${name} <${email}>".toString())
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
		List<String> users = getUsers().collect { "${it.toString()} <${it.email}>".toString() }
		users.addAll(additional)
		return users as String[]
	}

	/**
	 * Create a command object with all email recipients with the given authority
	 * @param authority
	 * @return
	 */
	static RecipientsCommand getRecipientsByAuthority(Authority authority) {
		List<Long> recipients = UserAuthority.findAllByAuthority(authority).collect { it.userId }
		return new RecipientsCommand(recipients: recipients)
	}
}
