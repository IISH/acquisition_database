package org.iish.acquisition.util

import java.sql.Types
import org.hibernate.dialect.MySQL5InnoDBDialect

/**
 * Override to make sure Hibernate uses tinyint for the boolean data type in MySQL.
 */
class CustomMySQL5InnoDBDialect extends MySQL5InnoDBDialect {
	CustomMySQL5InnoDBDialect() {
		super()
		registerColumnType(Types.BOOLEAN, 'tinyint(1)')
	}
}
