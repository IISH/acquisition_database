package org.iish.acquisition.util

import groovy.xml.MarkupBuilder
import org.iish.acquisition.command.CollectionSearchCommand
import org.iish.acquisition.search.CollectionSearch
import org.iish.acquisition.search.Pager

import java.text.SimpleDateFormat

import static java.lang.Integer.parseInt

/**
 * Utility tag libraries.
 */
class UtilsTagLib {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat('dd/MM/yyyy')

	/**
	 * Creates a pager to go to the previous and the next record.
	 * @attr collectionSearchCommand REQUIRED The collection search that was performed.
	 */
	def prevNextPager = { attrs ->
		MarkupBuilder builder = new MarkupBuilder(out)
		builder.doubleQuotes = true

		CollectionSearchCommand collectionSearchCommand = attrs.collectionSearchCommand
		if (!collectionSearchCommand.isASearch()) {
			collectionSearchCommand = CollectionSearchCommand.getDefaultCollectionSearchCommand(params)
		}

		CollectionSearch collectionSearch = collectionSearchCommand.getCollectionSearch()
		Pager pager = collectionSearch.getPagedResults(params.long('id'))

		String prevClass = 'previous disabled'
		String prevLink = '#'
		if (pager.previousId) {
			prevClass = 'previous'
			prevLink = g.createLink(controller: 'collection', action: 'edit', id: pager.previousId,
					params: request.getAttribute('queryParams'))
		}

		String nextClass = 'next disabled'
		String nextLink = '#'
		if (pager.nextId) {
			nextClass = 'next'
			nextLink = g.createLink(controller: 'collection', action: 'edit', id: pager.nextId,
					params: request.getAttribute('queryParams'))
		}

		builder.ul(class: 'pager hidden-print') {
			builder.li(class: prevClass) {
				builder.a(href: prevLink, "← ${g.message(code: 'default.paginate.prev')}")
			}
			builder.li(class: nextClass) {
				builder.a(href: nextLink, "${g.message(code: 'default.paginate.next')} →")
			}
		}
	}

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

		out << g.link(action: actionName, class: 'sort', params: params + [sort: attrs.field, order: order]) {
			g.message(code: attrs.messageCode) + "&nbsp;&nbsp;<span class=\"$imageClass\"></span>"
		}
	}

    /**
     * Creates a table with checkboxes.
     * @attr values REQUIRED The values.
     * @attr nrColumns REQUIRED The number of columns.
     * @attr name REQUIRED The name of the checkboxes.
     * @attr label The label of the checkbox.
     * @attr value REQUIRED The value of the checkbox.
     * @attr class The class of each checkbox.
     * @attr checked Is the checkbox checked?
     */
    def checkboxTable = { attrs ->
        MarkupBuilder builder = new MarkupBuilder(out)
        builder.doubleQuotes = true

        int nrColumns = parseInt(attrs.nrColumns)
        builder.table(class: 'table table-condensed table-striped checkbox-click') {
            builder.tbody {
                attrs.values.eachWithIndex { def value, int i ->
                    if (i % nrColumns == 0) {
                        builder.mkp.yieldUnescaped('<tr>')
                    }

                    builder.td {
                        Map checkboxProps = [type: 'checkbox', name: attrs.name]

                        if (attrs.value instanceof Closure<String>) {
                            checkboxProps.put('value', attrs.value.call(value))
                        }
                        else {
                            checkboxProps.put('value', attrs.value.toString())
                        }

                        def checkedValue = attrs.checked
                        if (checkedValue) {
                            boolean isChecked
                            if (checkedValue instanceof Closure<Boolean>) {
                                isChecked = checkedValue.call(value)
                            }
                            else {
                                isChecked = attrs.checked.equalsIgnoreCase('true')
                            }

                            if (isChecked) {
                                checkboxProps.put('checked', 'checked')
                            }
                        }

                        def classValue = attrs.class
                        if (classValue) {
                            if (classValue instanceof Closure<String>) {
                                checkboxProps.put('class', classValue.call(value))
                            }
                            else {
                                checkboxProps.put('class', classValue.toString())
                            }
                        }

                        builder.input(checkboxProps)

                        String label = value.toString()
                        def labelValue = attrs.label
                        if (labelValue) {
                            if (labelValue instanceof Closure<String>) {
                                label = labelValue.call(value)
                            }
                            else {
                                label = g.message(code: value."$labelValue")
                            }
                        }

                        builder.span(label)
                    }

                    if (i % nrColumns == (nrColumns - 1)) {
                        builder.mkp.yieldUnescaped('</tr>')
                    }
                }

                int nrOfColumnsWritten = attrs.values.size() % nrColumns
                if (nrOfColumnsWritten > 0) {
                    int nrOfColumnsLeft = nrColumns - nrOfColumnsWritten
                    for (int i = 0; i < nrOfColumnsLeft; i++) {
                        builder.td()
                    }
                    builder.mkp.yieldUnescaped('</tr>')
                }
            }
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
        } else {
            out << login
        }
    }

	/**
	 * Prints the file size in a human readable format.
	 * @attr size REQUIRED The file size in bytes.
	 * @attr unit Force print with the given unit.
	 */
	def fileSize = { attrs ->
		out << PrinterUtil.printFileSize(attrs.size, attrs.unit)
	}
}
