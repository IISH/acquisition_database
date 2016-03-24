package org.iish.acquisition.util

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * Utilities class for printing human readable texts.
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

	/**
	 * Returns the file size in a human friendly readable way.
	 * @param size The file size in bytes.
	 * @param unit Force print with the given unit.
	 * @return The file size.
	 */
	static String printFileSize(long size, String unit = null) {
		if (size <= 0) {
			return '0 bytes'
		}

		String[] units = ['bytes', 'kB', 'MB', 'GB', 'TB']

		int digitGroups = (int) (Math.log10(size) / Math.log10(1024))
		if (unit) {
			int idx = units.findIndexOf { unit == it }
			if (idx >= 0) {
				digitGroups = idx
			}
		}

		BigDecimal computedSize = size / Math.pow(1024, digitGroups)
		return "${computedSize.setScale(2, RoundingMode.HALF_UP)} ${units[digitGroups]}"
	}
}
