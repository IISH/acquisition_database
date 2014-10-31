package org.iish.acquisition.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * Utilities class for printing.
 */
class PrinterUtil {

	/**
	 * Prints a human-readable BigDecimal.
	 * @param value The BigDecimal in question.
	 * @return A human-readable BigDecimal.
	 */
	static String printBigDecimal(BigDecimal value) {
		if (value != null) {
			DecimalFormatSymbols symbols = new DecimalFormatSymbols()
			symbols.setDecimalSeparator((char) ',')

			DecimalFormat formatter = new DecimalFormat('0.#####', symbols)
			return formatter.format(value)
		}
		else {
			return null
		}
	}
}
