package org.iish.acquisition.controller

import org.iish.acquisition.domain.IngestDepotReport
import org.iish.acquisition.domain.IngestDepotUploadStatus
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
	 * Returns a virus report.
	 */
	def virusReport(IngestDepotUploadStatus ingestDepotUploadStatus) {
		String virusReport = ingestDepotUploadStatus?.ingestDepotReport?.virusReport
		if (virusReport) {
			String name = "${ingestDepotUploadStatus.name} (virus report)." +
					"${IngestDepotReport.getVirusReportExtension()}"

			response.setContentType(IngestDepotReport.getVirusReportContentType())
			response.setHeader('Content-disposition', "attachment;filename=\"${name}\"")

			OutputStream outputStream = response.getOutputStream()
			PrintWriter printWriter = new PrintWriter(outputStream)
			printWriter.print(virusReport)
			printWriter.close()
			outputStream.flush()
		}
		else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND)
		}
	}

	/**
	 * Returns a file identification report.
	 */
	def fileIdentificationReport(IngestDepotUploadStatus ingestDepotUploadStatus) {
		String fileIdentificationReport = ingestDepotUploadStatus?.ingestDepotReport?.fileIdentificationReport
		if (fileIdentificationReport) {
			String name = "${ingestDepotUploadStatus.name} (file identification report)." +
					"${IngestDepotReport.getFileIdentificationReportExtension()}"

			response.setContentType(IngestDepotReport.getFileIdentificationReportContentType())
			response.setHeader('Content-disposition', "attachment;filename=\"${name}\"")

			OutputStream outputStream = response.getOutputStream()
			PrintWriter printWriter = new PrintWriter(outputStream)
			printWriter.print(fileIdentificationReport)
			printWriter.close()
			outputStream.flush()
		}
		else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND)
		}
	}
}
