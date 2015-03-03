package org.iish.acquisition.controller

import org.iish.acquisition.domain.DigitalMaterialFile
import org.iish.acquisition.domain.Photo

import javax.servlet.http.HttpServletResponse

/**
 * Responsible for handling file downloads of files stored in the acquisition database.
 */
class DownloadController {

	/**
	 * Returns an uploaded photograph.
	 */
	def photo(Photo photo) {
		if (photo?.id) {
			response.setContentType(photo.contentType)
			response.setHeader('Content-disposition', "attachment;filename=\"${photo.originalFilename}\"")

			OutputStream outputStream = response.getOutputStream()
			outputStream.write(photo.photo)
			outputStream.flush()
		}
		else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND)
		}
	}

	/**
	 * Returns an uploaded manifest.
	 */
	def manifest(DigitalMaterialFile manifest) {
		if (manifest?.id) {
			response.setContentType(manifest.contentType)
			response.setHeader('Content-disposition', "attachment;filename=\"${manifest.originalFilename}\"")

			OutputStream outputStream = response.getOutputStream()
			outputStream.write(manifest.file)
			outputStream.flush()
		}
		else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND)
		}
	}
}
