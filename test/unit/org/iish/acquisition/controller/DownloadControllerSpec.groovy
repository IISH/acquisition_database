package org.iish.acquisition.controller

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.iish.acquisition.domain.Photo
import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

@TestFor(DownloadController)
@Mock(Photo)
class DownloadControllerSpec extends Specification {

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
}
