package org.iish.acquisition.util

import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsAnnotationConfiguration
import org.hibernate.MappingException
import org.hibernate.engine.spi.FilterDefinition

/**
 * Extends the Hibernate configuration to define a soft delete filter on the collections in the database.
 */
class CustomHibernateConfiguration extends GrailsAnnotationConfiguration {

	/**
	 * Adds a filter definition and filter for the collection soft delete.
	 * @throws MappingException
	 */
	@Override
	protected void secondPassCompile() throws MappingException {
		super.secondPassCompile()

		addFilterDefinition(new FilterDefinition('softDeleteFilter', 'deleted = 0', [:]))
		getClassMapping('org.iish.acquisition.domain.Collection').
				addFilter('softDeleteFilter', 'deleted = 0', true, [:], [:])
	}
}

