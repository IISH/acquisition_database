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

	/**
	 * TODO: Dummy test as long as there is no fix for assigned ids in unit test.
	 */
	void dummyTest() {
		assert true
	}

	/*def setup() {
		[
		 (DigitalMaterialStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION): [status: '10', isSetByUser: false],
		 (DigitalMaterialStatusCode.FOLDER_CREATED)                 : [status: '30', isSetByUser: false],
		 (DigitalMaterialStatusCode.MATERIAL_UPLOADED)              : [status: '40', isSetByUser: true],
		 (DigitalMaterialStatusCode.READY_FOR_PERMANENT_STORAGE)    : [status: '100', isSetByUser: true],
		 (DigitalMaterialStatusCode.UPLOADING_TO_PERMANENT_STORAGE) : [status: '110', isSetByUser: false],
		 (DigitalMaterialStatusCode.MOVED_TO_PERMANENT_STORAGE)     : [status: '120', isSetByUser: false]
		].
				each { Long id, Map statusInfo ->
					if (!DigitalMaterialStatusCode.get(id)) {
						DigitalMaterialStatusCode digitalMaterialStatusCode = new DigitalMaterialStatusCode(statusInfo)
						digitalMaterialStatusCode.setId(id)
						digitalMaterialStatusCode.save()
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
		controller.status('10622/BULK00001', DigitalMaterialStatusCode.FOLDER_CREATED, false, null)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		collection.digitalMaterialStatus.statusCode.id == DigitalMaterialStatusCode.FOLDER_CREATED
		!collection.digitalMaterialStatus.lastActionFailed
	}

	void "test status 2"() {
		given:
		Collection collection = Collection.findByObjectRepositoryPID('10622/BULK00002')

		when:
		controller.status('10622/BULK00002', DigitalMaterialStatusCode.MATERIAL_UPLOADED, true, null)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		collection.digitalMaterialStatus.statusCode.id == DigitalMaterialStatusCode.MATERIAL_UPLOADED
		collection.digitalMaterialStatus.lastActionFailed
	}

	void "test invalid status 1"() {
		when:
		controller.status('10622/BULK00003', null, null, null)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_BAD_REQUEST
	}

	void "test invalid status 2"() {
		when:
		controller.status('10622/BULK00004', DigitalMaterialStatusCode.FOLDER_CREATED, false, null)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_NOT_FOUND
	}

	void "test invalid status 3"() {
		when:
		controller.status('10622/BULK00003', 999L, false, null)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_BAD_REQUEST
	}*/
}
