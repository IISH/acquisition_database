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
	 * @return The file size.
	 */
	static String printFileSize(long size) {
		if (!size) {
			return '0 bytes'
		}

		if (size / 1024 > 1) {
			if (size / (1024 * 1024) > 1) {
				if (size / (1024 * 1024 * 1024) > 1) {
					if (size / (1024 * 1024 * 1024 * 1024) > 1) {
						return "${(size / (1024 * 1024 * 1024 * 1024)).setScale(2, RoundingMode.HALF_UP)} TB"
					}

					return "${(size / (1024 * 1024 * 1024)).setScale(2, RoundingMode.HALF_UP)} GB"
				}

				return "${(size / (1024 * 1024)).setScale(2, RoundingMode.HALF_UP)} MB"
			}

			return "${(size / 1024).setScale(2, RoundingMode.HALF_UP)} KB"
		}

		return "$size bytes"
	}
}
