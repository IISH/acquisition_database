package org.iish.acquisition.controller

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import org.iish.acquisition.domain.Collection
import org.iish.acquisition.domain.DigitalMaterialStatus
import org.iish.acquisition.domain.DigitalMaterialStatusCode
import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

@TestFor(ServiceController)
@Mock([Collection, DigitalMaterialStatus, DigitalMaterialStatusCode])
@TestMixin(DomainClassUnitTestMixin)
class ServiceControllerSpec extends Specification {

	def setup() {
		[
		 (DigitalMaterialStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION): [status: '1', isSetByUser: false],
		 (DigitalMaterialStatusCode.FOLDER_CREATED)                 : [status: '2', isSetByUser: false],
		 (DigitalMaterialStatusCode.MATERIAL_UPLOADED)              : [status: '3', isSetByUser: true],
		 (DigitalMaterialStatusCode.READY_FOR_PERMANENT_STORAGE)    : [status: '4', isSetByUser: true],
		 (DigitalMaterialStatusCode.UPLOADING_TO_PERMANENT_STORAGE) : [status: '5', isSetByUser: false],
		 (DigitalMaterialStatusCode.MOVED_TO_PERMANENT_STORAGE)     : [status: '6', isSetByUser: false]
		].
				each { Long id, Map statusInfo ->
					if (!DigitalMaterialStatusCode.get(id)) {
						new DigitalMaterialStatusCode([id: id] + statusInfo).save()
					}
				}

		new Collection(
				objectRepositoryPID: '10622/BULK00001',
				digitalMaterialStatus: new DigitalMaterialStatus(
						statusCode: DigitalMaterialStatusCode.
								get(DigitalMaterialStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION),
						lastActionFailed: false
				)
		).save(validate: false)

		new Collection(
				objectRepositoryPID: '10622/BULK00002',
				digitalMaterialStatus: new DigitalMaterialStatus(
						statusCode: DigitalMaterialStatusCode.
								get(DigitalMaterialStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION),
						lastActionFailed: false
				)
		).save(validate: false)

		new Collection(
				objectRepositoryPID: '10622/BULK00003',
				digitalMaterialStatus: new DigitalMaterialStatus(
						statusCode: DigitalMaterialStatusCode.
								get(DigitalMaterialStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION),
						lastActionFailed: false
				)
		).save(validate: false)
	}

	void "test status 1"() {
		given:
		Collection collection = Collection.findByObjectRepositoryPID('10622/BULK00001')

		when:
		controller.status('10622/BULK00001', DigitalMaterialStatusCode.FOLDER_CREATED, false)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		collection.digitalMaterialStatus.statusCode.id == DigitalMaterialStatusCode.FOLDER_CREATED
		!collection.digitalMaterialStatus.lastActionFailed
	}

	void "test status 2"() {
		given:
		Collection collection = Collection.findByObjectRepositoryPID('10622/BULK00002')

		when:
		controller.status('10622/BULK00002', DigitalMaterialStatusCode.MATERIAL_UPLOADED, true)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		collection.digitalMaterialStatus.statusCode.id == DigitalMaterialStatusCode.MATERIAL_UPLOADED
		collection.digitalMaterialStatus.lastActionFailed
	}

	void "test invalid status 1"() {
		when:
		controller.status('10622/BULK00003', null, null)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_BAD_REQUEST
	}

	void "test invalid status 2"() {
		when:
		controller.status('10622/BULK00004', DigitalMaterialStatusCode.FOLDER_CREATED, false)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_NOT_FOUND
	}

	void "test invalid status 3"() {
		when:
		controller.status('10622/BULK00003', 10L, false)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_BAD_REQUEST
	}
}
