package org.iish.acquisition.controller

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import org.iish.acquisition.domain.Collection
import org.iish.acquisition.domain.IngestDepotStatus
import org.iish.acquisition.domain.IngestDepotStatusCode
import org.iish.acquisition.domain.IngestDepotUploadStatus
import spock.lang.Specification

import javax.servlet.http.HttpServletResponse

@TestFor(ServiceController)
@Mock([Collection, IngestDepotStatus, IngestDepotUploadStatus])
@TestMixin(DomainClassUnitTestMixin)
class ServiceControllerSpec extends Specification {

	def setup() {
		new Collection(
				objectRepositoryPID: '10622/BULK00001',
				ingestDepotStatus: new IngestDepotStatus(
						statusCode: IngestDepotStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION,
						uploadStatuses: []
				)
		).save(validate: false)

		new Collection(
				objectRepositoryPID: '10622/BULK00002',
				ingestDepotStatus: new IngestDepotStatus(
						statusCode: IngestDepotStatusCode.CREATE_FOLDER_SUCCESS,
						uploadStatuses: [
								new IngestDepotUploadStatus(
										name: 'Disk 1',
										statusCode: IngestDepotStatusCode.UPLOAD_FILES
								),
								new IngestDepotUploadStatus(
										name: 'Disk 2',
										statusCode: IngestDepotStatusCode.FILE_IDENTIFICATION
								)
						],
						manualSorProcessOnHold: true
				)
		).save(validate: false)

		new Collection(
				objectRepositoryPID: '10622/BULK00003',
				ingestDepotStatus: new IngestDepotStatus(
						statusCode: IngestDepotStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION,
						uploadStatuses: [],
						manualSorProcessOnHold: true,
						manualStartSorProcess: true
				)
		).save(validate: false)
	}

	void "test folders"() {
		when:
		controller.folders()

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		controller.response.getContentType().startsWith('text/json')
		controller.response.json.pids instanceof java.util.Collection

		// TODO: Cannot test nor mock the method call Collection.getWithoutFolder()
		/*controller.response.json.pids.size == 2
		controller.response.json.pids.contains('10622/BULK00001')
		controller.response.json.pids.contains('10622/BULK00003')*/
	}

	void "test manualActions 1"() {
		when:
		controller.manualActions('10622/BULK00002')

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		controller.response.getContentType().startsWith('text/json')
		controller.response.json.sorProcessOnHold instanceof Boolean
		controller.response.json.sorProcessOnHold
		controller.response.json.startSorProcess instanceof Boolean
		!controller.response.json.startSorProcess
	}

	void "test manualActions 2"() {
		when:
		controller.manualActions('10622/BULK00003')

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		controller.response.getContentType().startsWith('text/json')
		controller.response.json.sorProcessOnHold instanceof Boolean
		controller.response.json.sorProcessOnHold
		controller.response.json.startSorProcess instanceof Boolean
		controller.response.json.startSorProcess
	}

	void "test manualActions with invalid PID"() {
		when:
		controller.manualActions('10622/BULK00000')

		then:
		controller.response.getStatus() == HttpServletResponse.SC_NOT_FOUND
	}

	void "test manualActions without PID"() {
		when:
		controller.manualActions(null)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_NOT_FOUND
	}

	void "test status 1"() {
		given:
		Collection collection = Collection.findByObjectRepositoryPID('10622/BULK00001')

		when:
		controller.status('10622/BULK00001', 2, 0, null)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		collection.ingestDepotStatus.statusCode == IngestDepotStatusCode.getByCodeAndSubcode(2, 0)
	}

	void "test status 2"() {
		given:
		Collection collection = Collection.findByObjectRepositoryPID('10622/BULK00002')
		Set<IngestDepotUploadStatus> uploadStatuses = collection.ingestDepotStatus.uploadStatuses

		when:
		controller.status('10622/BULK00002', 4, 1, 'Disk 1')

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		collection.ingestDepotStatus.statusCode == IngestDepotStatusCode.CREATE_FOLDER_SUCCESS
		uploadStatuses.find {
			(it.name == 'Disk 1') && (it.statusCode == IngestDepotStatusCode.getByCodeAndSubcode(4, 1))
		}
	}

	void "test status 3"() {
		given:
		Collection collection = Collection.findByObjectRepositoryPID('10622/BULK00002')
		Set<IngestDepotUploadStatus> uploadStatuses = collection.ingestDepotStatus.uploadStatuses

		when:
		controller.status('10622/BULK00002', 3, 1, 'Disk 3')

		then:
		controller.response.getStatus() == HttpServletResponse.SC_OK
		collection.ingestDepotStatus.statusCode == IngestDepotStatusCode.CREATE_FOLDER_SUCCESS
		uploadStatuses.find {
			(it.name == 'Disk 3') && (it.statusCode == IngestDepotStatusCode.getByCodeAndSubcode(3, 1))
		}
	}

	void "test invalid status"() {
		when:
		controller.status('10622/BULK00002', null, null, null)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_BAD_REQUEST
	}

	void "test virusReport"() {
		given:
		Collection collection = Collection.findByObjectRepositoryPID('10622/BULK00001')
		Set<IngestDepotUploadStatus> uploadStatuses = collection.ingestDepotStatus.uploadStatuses

		when:
		controller.virusReport('10622/BULK00001', 'Disk 1', 'This is a virus report')

		then:
		uploadStatuses.find {
			(it.name == 'Disk 1') && (it.ingestDepotReport.virusReport == 'This is a virus report')
		}
	}

	void "test invalid virusReport"() {
		when:
		controller.virusReport('10622/BULK00001', null, null)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_BAD_REQUEST
	}

	void "test fileIdentificationReport"() {
		given:
		Collection collection = Collection.findByObjectRepositoryPID('10622/BULK00002')
		Set<IngestDepotUploadStatus> uploadStatuses = collection.ingestDepotStatus.uploadStatuses

		when:
		controller.fileIdentificationReport('10622/BULK00002', 'Disk 1', 'This is a file identification report')

		then:
		uploadStatuses.find {
			(it.name == 'Disk 1') &&
					(it.ingestDepotReport.fileIdentificationReport == 'This is a file identification report')
		}
	}

	void "test invalid fileIdentificationReport"() {
		when:
		controller.virusReport('10622/BULK00002', null, null)

		then:
		controller.response.getStatus() == HttpServletResponse.SC_BAD_REQUEST
	}
}
