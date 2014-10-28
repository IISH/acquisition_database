package org.iish.acquisition.converter

import org.grails.databinding.converters.ValueConverter

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException

/**
 * Converts a String into a BigDecimal.
 */
class BigDecimalValueConverter implements ValueConverter {

	@Override
	boolean canConvert(Object value) {
		return (value instanceof String)
	}

	/**
	 * Converts a String into a BigDecimal, using the Dutch decimal mark, which is a comma.
	 * @param value The String to convert.
	 * @return A BigDecimal.
	 */
	@Override
	Object convert(Object value) {
		try {
			// Use the German locale, as it is the closest matching predefined locale.
			DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.GERMAN)
			decimalFormat.setParseBigDecimal(true)
			return (BigDecimal) decimalFormat.parseObject(value.toString())
		}
		catch (ParseException pe) {
			return null
		}
	}

	@Override
	Class<?> getTargetType() {
		return BigDecimal
	}
}
