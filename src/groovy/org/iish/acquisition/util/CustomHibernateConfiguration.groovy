package org.iish.acquisition.util

import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsAnnotationConfiguration
import org.hibernate.MappingException
import org.hibernate.dialect.function.SQLFunctionTemplate
import org.hibernate.engine.spi.FilterDefinition
import org.hibernate.type.StandardBasicTypes

/**
 * Extends the Hibernate configuration to define a soft delete filter on the collections in the database.
 */
class CustomHibernateConfiguration extends GrailsAnnotationConfiguration {

	/**
	 * Adds a filter definition and filter for the collection soft delete.
	 * And adds support for regular expressions in HQL (MySQL).
	 * @throws MappingException
	 */
	@Override
	protected void secondPassCompile() throws MappingException {
		super.secondPassCompile()

		addFilterDefinition(new FilterDefinition('softDeleteFilter', 'deleted = 0', [:]))
		getClassMapping('org.iish.acquisition.domain.Collection').
				addFilter('softDeleteFilter', 'deleted = 0', true, [:], [:])

		addSqlFunction('regexp', new SQLFunctionTemplate(StandardBasicTypes.INTEGER, '(?1 regexp ?2)'))
	}
}

