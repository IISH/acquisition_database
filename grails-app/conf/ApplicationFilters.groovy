import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.codehaus.groovy.grails.web.util.WebUtils
import org.hibernate.SessionFactory

import javax.servlet.http.HttpServletRequest

/**
 * All application filters, procedures that have to run before or after certain requests.
 */
class ApplicationFilters {
	SessionFactory sessionFactory

	/**
	 * All application filters.
	 */
	def filters = {
		all(controller: '*', action: '*') {
			before = {
				parseQueryStringParams(request)
				enableSoftDeleteFilter()
			}
		}
	}

	/**
	 * Parses the query (GET) string only and creates a parameter map out of it.
	 * @param request The request which contains the query string.
	 */
	void parseQueryStringParams(HttpServletRequest request) {
		Map<String, Object> queryParamsMap = WebUtils.fromQueryString(request.getQueryString() ?: '')
		request.setAttribute('queryParams', new GrailsParameterMap(queryParamsMap, request))
	}

	/**
	 * Enables the soft delete filter, which automatically adds a filter to each query on collections.
	 */
	void enableSoftDeleteFilter() {
		sessionFactory.currentSession.enableFilter('softDeleteFilter')
	}
}
