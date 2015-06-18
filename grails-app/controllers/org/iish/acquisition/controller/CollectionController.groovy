package org.iish.acquisition.controller

import org.iish.acquisition.command.CollectionSearchCommand
import org.iish.acquisition.command.RecipientsCommand
import org.iish.acquisition.domain.*
import org.iish.acquisition.export.CollectionXlsExport
import org.iish.acquisition.search.CollectionSearch
import org.iish.acquisition.search.PagedResultList
import org.iish.acquisition.service.CollectionService
import org.iish.acquisition.service.EmailService
import org.iish.acquisition.util.EmailException
import org.springframework.context.MessageSource

/**
 * The controller for collection related actions.
 */
class CollectionController {
	CollectionService collectionService
	EmailService emailService
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
				acquisitionTypes       : AcquisitionType.values(),
				depots                 : Depot.list(),
				statuses               : Status.list(),
				digitalStatuses        : DigitalMaterialStatusCode.list(),
				materialTypes          : MaterialType.list(),
				miscMaterialTypes      : MiscMaterialType.list(),
				priorities             : Priority.values()
		]
	}

	/**
	 * Lists the collections returned by a timer started request.
	 */
	def status_1_9(CollectionSearchCommand collectionSearchCommand) {
		if (!params.int('max')) {
			params.max = 20
		}
		if (!params.int('offset')) {
			params.offset = 0
		}

		collectionSearchCommand = CollectionSearchCommand.getTimerNotPassedCollectionSearchCommand(params)

		CollectionSearch collectionSearch = collectionSearchCommand.getCollectionSearch()
		PagedResultList results = collectionSearch.
				getPaginatedResults(params.int('max'), params.int('offset'))

		render view: 'timer', model: [
				results                : results,
				collectionSearchCommand: collectionSearchCommand,
				acquisitionTypes       : AcquisitionType.values(),
				depots                 : Depot.list(),
				statuses               : Status.list(),
				digitalStatuses        : DigitalMaterialStatusCode.list(),
				materialTypes          : MaterialType.list(),
				miscMaterialTypes      : MiscMaterialType.list(),
				priorities             : Priority.values(),
				digitalMaterialStatus  : DigitalMaterialStatus.list()
		]
	}

	/**
	 * Lists the collections returned by a timer passed request.
	 */
	def status_10_12(CollectionSearchCommand collectionSearchCommand) {
		if (!params.int('max')) {
			params.max = 20
		}
		if (!params.int('offset')) {
			params.offset = 0
		}

		collectionSearchCommand = CollectionSearchCommand.getTimerPassedCollectionSearchCommand(params)

		CollectionSearch collectionSearch = collectionSearchCommand.getCollectionSearch()
		PagedResultList results = collectionSearch.
				getPaginatedResults(params.int('max'), params.int('offset'))

		render view: 'timer', model: [
				results                : results,
				collectionSearchCommand: collectionSearchCommand,
				acquisitionTypes       : AcquisitionType.values(),
				depots                 : Depot.list(),
				statuses               : Status.list(),
				digitalStatuses        : DigitalMaterialStatusCode.list(),
				materialTypes          : MaterialType.list(),
				miscMaterialTypes      : MiscMaterialType.list(),
				priorities             : Priority.values(),
				digitalMaterialStatus  : DigitalMaterialStatus.list()
		]
	}

	/**
	 * Search through the collections.
	 */
	def search() {
		render view: 'search', model: [
				acquisitionTypes : AcquisitionType.values(),
				depots           : Depot.list(),
				statuses         : Status.list(),
				digitalStatuses  : DigitalMaterialStatusCode.list(),
				materialTypes    : MaterialType.list(),
				miscMaterialTypes: MiscMaterialType.list(),
				priorities       : Priority.values(),
				booleanEntrySet  : [
						(Boolean.TRUE) : g.message(code: 'default.boolean.true'),
						(Boolean.FALSE): g.message(code: 'default.boolean.false')
				].entrySet()
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
				collection                : collection,
				digitalMaterialStatus     : collection.digitalMaterialStatus,
				acquisitionTypes          : AcquisitionType.values(),
				depots                    : Depot.list(),
				materialTypes             : MaterialType.list(),
				miscMaterialTypes         : MiscMaterialType.list(),
				byteUnits                 : ByteUnit.values(),
				priorities                : Priority.values(),
				contracts                 : Contract.list(),
				accruals                  : Accrual.values(),
				appraisals                : Appraisal.list(),
				statuses                  : Status.list(),
				digitalMaterialStatusCodes: DigitalMaterialStatusCode.list(),
				uploadedPhotos            : []
		]
	}

	/**
	 * Updating an existing collection.
	 * @param collection The collection to update.
	 */
	def edit(Collection collection, CollectionSearchCommand collectionSearchCommand) {
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
					collection                : collection,
					digitalMaterialStatus     : collection.digitalMaterialStatus,
					acquisitionTypes          : AcquisitionType.values(),
					depots                    : Depot.list(),
					materialTypes             : MaterialType.list(),
					miscMaterialTypes         : MiscMaterialType.list(),
					byteUnits                 : ByteUnit.values(),
					priorities                : Priority.values(),
					contracts                 : Contract.list(),
					accruals                  : Accrual.values(),
					appraisals                : Appraisal.list(),
					statuses                  : Status.list(),
					digitalMaterialStatusCodes: DigitalMaterialStatusCode.list(),
					uploadedPhotos            : Photo.getPhotoMetaData(collection),
					collectionSearchCommand   : collectionSearchCommand,
					recipients                : User.findAllByMayReceiveEmailAndEmailIsNotNull(true)
			]
		}
	}

	/**
	 * Print version of an existing collection.
	 * @param collection The collection to print.
	 */
	def print(Collection collection) {
		ifCollectionExists(collection, params.long('id')) {
			render view: 'print', model: [collection: collection]
		}
	}

	/**
	 * Sends a complementary request email for a collection.
	 * @param collection The collection to email.
	 * @param recipientsCommand The recipients to email to.
	 */
	def email(Collection collection, RecipientsCommand recipientsCommand) {
		ifCollectionExists(collection, params.long('id')) {
			if (!recipientsCommand.validate()) {
				flash.errors = recipientsCommand.errors.allErrors
				redirect action: 'edit', id: collection.id, params: request.getAttribute('queryParams')
				return
			}

			try {
				emailService.sentComplementRequestEmail(recipientsCommand, collection)

				flash.message = g.message(code: 'default.mail.success.message')
				redirect action: 'edit', id: collection.id, params: request.getAttribute('queryParams')
			}
			catch (EmailException e) {
				flash.status = 'error'
				flash.message = g.message(code: 'default.mail.fail.message', args: [e.getMessage()])
				redirect action: 'edit', id: collection.id, params: request.getAttribute('queryParams')
			}
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
}