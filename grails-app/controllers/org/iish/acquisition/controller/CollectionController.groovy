package org.iish.acquisition.controller

import org.iish.acquisition.command.CollectionSearchCommand
import org.iish.acquisition.domain.*
import org.iish.acquisition.export.CollectionXlsExport
import org.iish.acquisition.search.CollectionSearch
import org.iish.acquisition.search.PagedResultList
import org.iish.acquisition.service.CollectionService
import org.springframework.context.MessageSource

/**
 * The controller for collection related actions.
 */
class CollectionController {
	CollectionService collectionService
	MessageSource messageSource

	static defaultAction = 'list'

	/**
	 * Lists the collections returned by a search request.
	 */
	def list(CollectionSearchCommand collectionSearchCommand) {
		if (!params.int('max')) {
			params.max = 20
		}
		if (!params.int('offset')) {
			params.offset = 0
		}
		if (!collectionSearchCommand.isASearch()) {
			collectionSearchCommand = CollectionSearchCommand.getDefaultCollectionSearchCommand(params)
		}

		CollectionSearch collectionSearch = collectionSearchCommand.getCollectionSearch()
		PagedResultList results = collectionSearch.
				getPaginatedResults(params.int('max'), params.int('offset'))

		render view: 'list', model: [
				results                : results,
				collectionSearchCommand: collectionSearchCommand,
				depots                 : Depot.list(),
				statuses               : Status.list(),
				materialTypes          : MaterialType.list()
		]
	}

	/**
	 * Search through the collections.
	 */
	def search() {
		render view: 'search', model: [
				depots       : Depot.list(),
				statuses     : Status.list(),
				materialTypes: MaterialType.list()
		]
	}

	/**
	 * Creates an Excel (xls) export for all collections returned by a search request.
	 */
	def export(CollectionSearchCommand collectionSearchCommand) {
		if (!collectionSearchCommand.isASearch()) {
			collectionSearchCommand = CollectionSearchCommand.getDefaultCollectionSearchCommand(params)
		}

		CollectionSearch collectionSearch = collectionSearchCommand.getCollectionSearch()
		CollectionXlsExport collectionXlsExport = new CollectionXlsExport(collectionSearch, messageSource)
		collectionXlsExport.build()

		response.setContentType(collectionXlsExport.getContentType())
		response.setHeader('Content-disposition', "attachment;filename=${collectionXlsExport.getFileName()}")
		collectionXlsExport.writeToStream(response.getOutputStream())
	}

	/**
	 * The registration of a new collection.
	 */
	def create() {
		Collection collection = new Collection(status: Status.get(Status.NOT_PROCESSED_ID))

		if (request.post && collectionService.updateCollection(collection, params)) {
			flash.message = g.message(
					code: 'default.created.message',
					args: [g.message(code: 'collection.label').toString().toLowerCase(), collection]
			)
			redirect action: 'edit', id: collection.id, params: request.getAttribute('queryParams')
			return
		}

		render view: 'form', model: [
				collection      : collection,
				acquisitionTypes: AcquisitionType.values(),
				depots          : Depot.list(),
				materialTypes   : MaterialType.list(),
				byteUnits       : ByteUnit.values(),
				priorities      : Priority.values(),
				contracts       : Contract.list(),
				accruals        : Accrual.values(),
				appraisals      : Appraisal.list(),
				statuses        : Status.list()
		]
	}

	/**
	 * Updating an existing collection.
	 * @param collection The collection to update.
	 */
	def edit(Collection collection) {
		ifCollectionExists(collection, params.long('id')) {
			if (request.post && collectionService.updateCollection(collection, params)) {
				flash.message = g.message(
						code: 'default.updated.message',
						args: [g.message(code: 'collection.label').toString().toLowerCase(), collection]
				)
				redirect action: 'list', params: request.getAttribute('queryParams')
				return
			}

			render view: 'form', model: [
					collection      : collection,
					acquisitionTypes: AcquisitionType.values(),
					depots          : Depot.list(),
					materialTypes   : MaterialType.list(),
					byteUnits       : ByteUnit.values(),
					priorities      : Priority.values(),
					contracts       : Contract.list(),
					accruals        : Accrual.values(),
					appraisals      : Appraisal.list(),
					statuses        : Status.list()
			]
		}
	}

	/**
	 * Deleting a collection.
	 * @param collection The collection to delete.
	 */
	def delete(Collection collection) {
		ifCollectionExists(collection, params.long('id')) {
			collection.deleted = true

			if (collection.save(flush: true)) {
				flash.message = g.message(code: 'default.deleted.message',
						args: [g.message(code: 'collection.label').toString().toLowerCase(), collection])
				redirect action: 'list', params: request.getAttribute('queryParams')
			}
			else {
				flash.status = 'error'
				flash.message = g.message(code: 'default.not.deleted.message',
						args: [g.message(code: 'collection.label').toString().toLowerCase(), collection])
				redirect action: 'edit', id: params.id, params: request.getAttribute('queryParams')
			}
		}
	}

	/**
	 * The default procedure to start an action which requires a collection.
	 * Checks whether the id is given, whether the collection actually exists and is not deleted.
	 * @param collection The collection in question.
	 * @param id The given id of the collection.
	 * @param body To run when the collection is found.
	 */
	private void ifCollectionExists(Collection collection, Long id, Closure body) {
		boolean runBody = true

		if (!id) {
			runBody = false
			flash.status = 'error'
			flash.message = g.message(code: 'default.no.id.message')
			redirect action: 'list', params: request.getAttribute('queryParams')
		}

		if (!collection || collection.isDeleted()) {
			runBody = false
			flash.status = 'error'
			flash.message = g.message(
					code: 'default.not.found.message',
					args: [g.message(code: 'collection.label'), id]
			)
			redirect action: 'list', params: request.getAttribute('queryParams')
		}

		if (runBody) {
			body()
		}
	}

	// TODO: For testing purposes
	def test() {
		(1..100).each { Integer count ->
			Collection col = new Collection(name: 'Test collection ' + count, acquisitionId: '12345',
					content: 'Content',
					listsAvailable: 'Lists available', toBeDone: 'To be done', priority: Priority.HIGH,
					level: Priority.MEDIUM, owner: 'Owner', accrual: Accrual.ACCRUAL, dateOfArrival: new Date(),
					contactPerson: 'CPS', remarks: 'remarks', originalPackageTransport: 'original',
					status: Status.get(Status.COLLECTION_LEVEL_READY_ID), addedBy: User.get(1L))

			col.save()

			AnalogMaterial analogMaterial1 = new AnalogMaterial(materialType: MaterialType.get(MaterialType
					.ARCHIVE_ID),
					size: 12, unit: AnalogUnit.METER)
			AnalogMaterial analogMaterial2 = new AnalogMaterial(
					materialType: MaterialType.get(MaterialType.PERIODICALS_ID),
					size: 34, unit: AnalogUnit.METER)
			AnalogMaterial analogMaterial3 = new AnalogMaterial(
					materialType: MaterialType.get(MaterialType.POSTERS_ID),
					size: 5, unit: AnalogUnit.NUMBER)

			DigitalMaterial digitalMaterial1 = new DigitalMaterial(
					materialType: MaterialType.get(MaterialType.BOOKS_ID))
			DigitalMaterial digitalMaterial2 = new DigitalMaterial(
					materialType: MaterialType.
							get(MaterialType.MOVING_IMAGES_ID))
			DigitalMaterial digitalMaterial3 = new DigitalMaterial(
					materialType: MaterialType.get(MaterialType.DRAWINGS_ID))

			AnalogMaterialCollection analogMaterialCollection = new AnalogMaterialCollection()
					.addToMaterials(analogMaterial1)
					.addToMaterials(analogMaterial2)
					.addToMaterials(analogMaterial3)

			DigitalMaterialCollection digitalMaterialCollection = new DigitalMaterialCollection(numberOfFiles: 123,
					totalSize: 456, unit: ByteUnit.GB)
					.addToMaterials(digitalMaterial1)
					.addToMaterials(digitalMaterial2)
					.addToMaterials(digitalMaterial3)

			Location location1 = new Location(cabinet: 'Cabinet', shelf: 'Shelf',
					depot: Depot.get(Depot.RANGEERTERREIN_ID))
			Location location2 = new Location(cabinet: 'dsadsa', shelf: 'sdsdad',
					depot: Depot.get(Depot.SORTEERRUIMTE_ID))

			col.analogMaterialCollection = analogMaterialCollection
			col.digitalMaterialCollection = digitalMaterialCollection

			col.save()

			col.addToLocations(location1)
			col.addToLocations(location2)

			col.save(flush: true)
		}

		render text: 'Done!'
	}
}