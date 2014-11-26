package org.iish.acquisition.domain

import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * Specifies one of the authorities given to the specified user.
 */
class UserAuthority implements Serializable {
	User user
	Authority authority

	static constraints = {
		authority validator: { Authority r, UserAuthority ur ->
			if (ur.user == null) {
				return
			}

			boolean existing = false
			withNewSession {
				existing = exists(ur.user.id, r.id)
			}

			if (existing) {
				return 'userRole.exists'
			}
		}
	}

	static mapping = {
		table 'users_authorities'
		id composite: ['authority', 'user']
	}

	/**
	 * Returns a UserAuthority for the given id of the user and id of the authority.
	 * @param userId The id of the user in question.
	 * @param authorityId The id of the authority in question.
	 * @return A UserAuthority, if found.
	 */
	static UserAuthority get(long userId, long authorityId) {
		where {
			(user == User.load(userId)) && (authority == Authority.load(authorityId))
		}.get()
	}

	/**
	 * Returns whether a UserAuthority for the given id of the user and id of the authority exists.
	 * @param userId The id of the user in question.
	 * @param authorityId The id of the authority in question.
	 * @return Whether a UserAuthority was found.
	 */
	static boolean exists(long userId, long authorityId) {
		where {
			(user == User.load(userId)) && (authority == Authority.load(authorityId))
		}.count() > 0
	}

	/**
	 * Grants the user an authority.
	 * @param user The user in question.
	 * @param authority The authority in question.
	 * @param flush Whether to flush the Hibernate session after save.
	 * @return The created UserAuthority instance.
	 */
	static UserAuthority create(User user, Authority authority, boolean flush = false) {
		def instance = new UserAuthority(user: user, authority: authority)
		instance.save(flush: flush, insert: true)
		instance
	}

	/**
	 * Removes an authority from a user.
	 * @param user The user in question.
	 * @param authority The authority in question.
	 * @param flush Whether to flush the Hibernate session after save.
	 */
	static void remove(User u, Authority r, boolean flush = false) {
		if (u == null || r == null) {
			return
		}

		findByUserAndAuthority(u, r).delete()

		if (flush) {
			withSession {
				it.flush()
			}
		}
	}

	/**
	 * Removes all authorities from a user.
	 * @param user The user in question.
	 * @param flush Whether to flush the Hibernate session after save.
	 * @return Whether the action was successful.
	 */
	static void removeAll(User u, boolean flush = false) {
		if (u == null) {
			return
		}

		findAllByUser(u).each { it.delete() }

		if (flush) {
			withSession {
				it.flush()
			}
		}
	}

	/**
	 * Removes a authority from all users with that authority.
	 * @param flush Whether to flush the Hibernate session after save.
	 * @return Whether the action was successful.
	 */
	static void removeAll(Authority r, boolean flush = false) {
		if (r == null) {
			return
		}

		where {
			authority == Authority.load(r.id)
		}.deleteAll()

		if (flush) {
			withSession {
				it.flush()
			}
		}
	}

	@Override
	boolean equals(other) {
		if (!(other instanceof UserAuthority)) {
			return false
		}

		return (other.user?.id == user?.id) && (other.authority?.id == authority?.id)
	}

	@Override
	int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder()

		if (user) {
			builder.append(user.id)
		}
		if (authority) {
			builder.append(authority.id)
		}

		return builder.toHashCode()
	}

	@Override
	String toString() {
		return "$user ($authority)"
	}
}
