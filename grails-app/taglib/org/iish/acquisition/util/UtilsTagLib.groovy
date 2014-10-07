package org.iish.acquisition.util

import groovy.xml.MarkupBuilder

import java.text.SimpleDateFormat

/**
 * Utility tag libraries.
 */
class UtilsTagLib {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat('dd/MM/yyyy')

	/**
	 * Creates a date picker.
	 * @attr value The selected value.
	 * @attr id The id of the date picker.
	 * @attr name The name of the date picker.
	 */
	def datePicker = { attrs ->
		String dateText = ''
		if (attrs.value && (attrs.value instanceof Date)) {
			dateText = DATE_FORMAT.format(attrs.value)
		}

		MarkupBuilder builder = new MarkupBuilder(out)
		builder.doubleQuotes = true

		builder.div(class: 'input-group date', 'data-date': dateText, 'data-date-format': 'dd/mm/yyyy') {
			builder.input(type: 'text', class: 'form-control', value: dateText, id: attrs.id, name: attrs.name)
			builder.span(class: 'input-group-addon') {
				builder.i(class: 'glyphicon glyphicon-calendar', '')
			}
		}
	}

	/**
	 * Creates a link for sorting.
	 * @attr field REQUIRED The name of the field for sorting.
	 * @attr messageCode REQUIRED The message code to use for the label of the link.
	 */
	def sortLink = { attrs ->
		String order = 'asc'
		if (params.sort?.equalsIgnoreCase(attrs.field) && params.order?.equalsIgnoreCase('asc')) {
			order = 'desc'
		}

		String imageClass = 'glyphicon glyphicon-sort'
		if (params.sort?.equalsIgnoreCase(attrs.field) && params.order?.equalsIgnoreCase('asc')) {
			imageClass = 'glyphicon glyphicon-sort-by-attributes'
		}
		else if (params.sort?.equalsIgnoreCase(attrs.field) && params.order?.equalsIgnoreCase('desc')) {
			imageClass = 'glyphicon glyphicon-sort-by-attributes-alt'
		}

		out << g.link(class: 'sort', params: params + [sort: attrs.field, order: order]) {
			g.message(code: attrs.messageCode) + "&nbsp;&nbsp;<span class=\"$imageClass\"></span>"
		}
	}

	/**
	 * Prints the name of the logged in user.
	 */
	def loggedInUserName = {
		String login = sec.loggedInUserInfo(field: 'username')
		String firstName = sec.loggedInUserInfo(field: 'firstName')
		String lastName = sec.loggedInUserInfo(field: 'lastName')

		if (firstName && lastName) {
			out << "$firstName $lastName"
		}
		else {
			out << login
		}
	}
}
