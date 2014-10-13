package org.iish.acquisition.controller

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.iish.acquisition.domain.IngestDepotReport
import org.iish.acquisition.domain.IngestDepotUploadStatus
import org.iish.acquisition.domain.Photo
import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

@TestFor(DownloadController)
@Mock([Photo, IngestDepotUploadStatus, IngestDepotReport])
class DownloadControllerSpec extends Specification {

	def setup() {
		grailsApplication.config.ingestDepot.contentType.virusReport = 'text/xml'
		grailsApplication.config.ingestDepot.extension.virusReport = 'xml'
		grailsApplication.config.ingestDepot.contentType.fileIdentificationReport = 'text/plain'
		grailsApplication.config.ingestDepot.extension.fileIdentificationReport = 'txt'
	}

	void "test photo 1 download"() {
		given:
		Photo photo = new Photo(
				originalFilename: 'First photo.jpg',
				size: 10L,
				contentType: 'image/jpeg',
				photo: 'first photo'.bytes)
		photo.setId(1L)

		when:
		controller.photo(photo)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		controller.response.getContentType() == 'image/jpeg'
		controller.response.getHeaderValue('Content-disposition') == 'attachment;filename="First photo.jpg"'
		controller.response.getContentAsByteArray() == 'first photo'.bytes
	}

	void "test photo 2 download"() {
		given:
		Photo photo = new Photo(
				originalFilename: 'Second photo.png',
				size: 20L,
				contentType: 'image/png',
				photo: 'second photo'.bytes)
		photo.setId(2L)

		when:
		controller.photo(photo)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		controller.response.getContentType() == 'image/png'
		controller.response.getHeaderValue('Content-disposition') == 'attachment;filename="Second photo.png"'
		controller.response.getContentAsByteArray() == 'second photo'.bytes
	}

	void "test no photo download"() {
		given:
		Photo photo = null

		when:
		controller.photo(photo)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_NOT_FOUND
	}

	void "test virus report 1 download"() {
		given:
		IngestDepotReport.grailsApplication = grailsApplication
		IngestDepotUploadStatus ingestDepotUploadStatus = new IngestDepotUploadStatus(
				name: 'Disk 1',
				ingestDepotReport: new IngestDepotReport(
						virusReport: 'a virus report',
						fileIdentificationReport: 'a file identification report'
				)
		)
		ingestDepotUploadStatus.setId(1L)

		when:
		controller.virusReport(ingestDepotUploadStatus)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		controller.response.getContentType() == 'text/xml'
		controller.response.getHeaderValue('Content-disposition') == 'attachment;filename="Disk 1 (virus report).xml"'
		controller.response.getContentAsString() == 'a virus report'
	}

	void "test virus report 2 download"() {
		given:
		IngestDepotReport.grailsApplication = grailsApplication
		IngestDepotUploadStatus ingestDepotUploadStatus = new IngestDepotUploadStatus(
				name: 'Disk 2',
				ingestDepotReport: new IngestDepotReport(
						virusReport: null,
						fileIdentificationReport: 'a file identification report'
				)
		)
		ingestDepotUploadStatus.setId(1L)

		when:
		controller.virusReport(ingestDepotUploadStatus)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_NOT_FOUND
	}

	void "test no virus report download"() {
		given:
		IngestDepotReport.grailsApplication = grailsApplication
		IngestDepotUploadStatus ingestDepotUploadStatus = null

		when:
		controller.virusReport(ingestDepotUploadStatus)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_NOT_FOUND
	}

	void "test file identification report 1 download"() {
		given:
		IngestDepotReport.grailsApplication = grailsApplication
		IngestDepotUploadStatus ingestDepotUploadStatus = new IngestDepotUploadStatus(
				name: 'Disk 1',
				ingestDepotReport: new IngestDepotReport(
						virusReport: 'a virus report',
						fileIdentificationReport: 'a file identification report'
				)
		)
		ingestDepotUploadStatus.setId(1L)

		when:
		controller.fileIdentificationReport(ingestDepotUploadStatus)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		controller.response.getContentType() == 'text/plain'
		controller.response.getHeaderValue('Content-disposition') ==
				'attachment;filename="Disk 1 (file identification report).txt"'
		controller.response.getContentAsString() == 'a file identification report'
	}

	void "test file identification report 2 download"() {
		given:
		IngestDepotReport.grailsApplication = grailsApplication
		IngestDepotUploadStatus ingestDepotUploadStatus = new IngestDepotUploadStatus(
				name: 'Disk 2',
				ingestDepotReport: new IngestDepotReport(
						virusReport: 'a virus report',
						fileIdentificationReport: null,
				)
		)
		ingestDepotUploadStatus.setId(1L)

		when:
		controller.fileIdentificationReport(ingestDepotUploadStatus)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_NOT_FOUND
	}

	void "test no file identification report download"() {
		given:
		IngestDepotReport.grailsApplication = grailsApplication
		IngestDepotUploadStatus ingestDepotUploadStatus = null

		when:
		controller.fileIdentificationReport(ingestDepotUploadStatus)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_NOT_FOUND
	}
}
