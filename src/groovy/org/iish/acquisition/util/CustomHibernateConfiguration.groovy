package org.iish.acquisition.util

import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsAnnotationConfiguration
import org.hibernate.MappingException
import org.hibernate.QueryException
import org.hibernate.dialect.function.SQLFunction
import org.hibernate.dialect.function.SQLFunctionTemplate
import org.hibernate.engine.spi.FilterDefinition
import org.hibernate.engine.spi.Mapping
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.type.StandardBasicTypes
import org.hibernate.type.Type

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

		addSqlFunction('match', new MatchSQLFunction())
		addSqlFunction('regexp', new SQLFunctionTemplate(StandardBasicTypes.INTEGER, '(?1 regexp ?2)'))
	}

	/**
	 * Implementation of the 'match against' function in MySQL.
	 */
	private final class MatchSQLFunction implements SQLFunction {
		@Override
		boolean hasArguments() {
			return true
		}

		@Override
		boolean hasParenthesesIfNoArguments() {
			return true
		}

		@Override
		Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
			return StandardBasicTypes.INTEGER
		}

		@Override
		String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory) throws QueryException {
			List<String> columns = arguments.subList(0, arguments.size() - 1) as List<String>
			String searchString = arguments.get(arguments.size() - 1)
            return "match (${columns.join(', ')}) against ($searchString in boolean mode)".toString()
		}
	}
}

