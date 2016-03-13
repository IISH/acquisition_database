package org.iish.acquisition.service

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.grails.databinding.DataBinder
import org.grails.databinding.SimpleMapDataBindingSource
import org.iish.acquisition.converter.BigDecimalValueConverter
import org.iish.acquisition.domain.*
import org.springframework.web.multipart.commons.CommonsMultipartFile

/**
 * A service responsible for processing collections.
 */
@Transactional
class CollectionService {
	DataBinder grailsWebDataBinder
	SpringSecurityService springSecurityService
	BigDecimalValueConverter bigDecimalConverter

	/**
	 * Updates the given collection with the given data from the user.
	 * @param collection The collection to update.
	 * @param params The data as filled out by the user.
	 * @return Whether the changes were successfully persisted to the database.
	 */
	boolean updateCollection(Collection collection, GrailsParameterMap params) {
		GrailsParameterMap collectionParams = params.collection as GrailsParameterMap

		grailsWebDataBinder.bind(collection, collectionParams as SimpleMapDataBindingSource, [
				'name', 'acquisitionId', 'content', 'listsAvailable', 'toBeDone', 'owner', 'dateOfArrival',
				'contactPerson', 'remarks', 'originalPackageTransport', 'contract', 'appraisal', 'status',
				'collectionLevelReady'], [])

		// Enums are not (yet) supported by the data binder
		collection.acquisitionTypeId = AcquisitionType.getById(collectionParams.int('acquisitionTypeId'))
		collection.accrual = Accrual.getById(collectionParams.int('accrual'))

		if (SpringSecurityUtils.ifAllGranted(Authority.ROLE_ADMIN)) {
			collection.priority = Priority.getById(collectionParams.int('priority'))
			collection.level = Priority.getById(collectionParams.int('level'))
		}

		// If this is a new record, also record who created the record
		if (!collection.id) {
			collection.addedBy = (User) springSecurityService.getCurrentUser()
		}

		processDigitalMaterialStatus(collection, collectionParams)
		updateLocations(collection, collectionParams)

		deletePhotos(collection, collectionParams)
		processUploadedPhotos(collection, collectionParams)

		updateAnalogMaterialCollection(collection, collectionParams)
		updateDigitalMaterialCollection(collection, collectionParams)
		updateMiscMaterialCollection(collection, collectionParams)

		return collection.save()
	}

	/**
	 * Updates the locations of a given collection.
	 * @param collection The collection to update.
	 * @param params The data as filled out by the user.
	 */
	private void updateLocations(Collection collection, GrailsParameterMap params) {
		Set<Location> locationsToRemove = collection.locations ? new HashSet<>(collection.locations) : []
		Set<Location> locationsToAdd = []

		int i = 0
		while (params["location[$i]"]) {
			GrailsParameterMap locationData = params["location[$i]"] as GrailsParameterMap
			i++

			Location location = collection.getLocationById(locationData.long('id'))
			locationsToRemove.remove(location)
			if (!location) {
				location = new Location()
			}

			grailsWebDataBinder.bind(location, locationData as SimpleMapDataBindingSource, ['depot', 'cabinet'], [])

			if (location.isEmpty()) {
				locationsToRemove.add(location)
			}
			else {
				locationsToAdd.add(location)
			}
		}

		locationsToRemove.each { collection.removeFromLocations(it) }
		locationsToAdd.each { collection.addToLocations(it) }
	}

	/**
	 * Processes the uploaded photos.
	 * @param collection The collection to update.
	 * @param params The data as filled out by the user.
	 */
	private void processUploadedPhotos(Collection collection, GrailsParameterMap params) {
		int i = 0
		while (params["photo[$i]"]) {
			CommonsMultipartFile photoData = (CommonsMultipartFile) params["photo[$i]"]
			i++

			if (photoData.size > 0) {
				Photo photo = new Photo(
						originalFilename: photoData.originalFilename,
						size: photoData.size,
						contentType: photoData.contentType,
						photo: photoData.bytes
				)
				collection.addToPhotos(photo)
			}
		}
	}

	/**
	 * Removes selected uploaded photos.
	 * @param collection The collection to update.
	 * @param params The data as filled out by the user.
	 */
	private void deletePhotos(Collection collection, GrailsParameterMap params) {
		params.deletedPhotos?.toString()?.split(';')?.each { it ->
			if (it.isLong()) {
				Photo photo = Photo.load(it.toLong())
				collection.removeFromPhotos(photo)
			}
		}
	}

	/**
	 * Updates the analog material collection of a given collection.
	 * @param collection The collection to update.
	 * @param params The data as filled out by the user.
	 */
	private void updateAnalogMaterialCollection(Collection collection, GrailsParameterMap params) {
		AnalogMaterialCollection materialCollection = collection.analogMaterialCollection ?:
				new AnalogMaterialCollection()
		Set<AnalogMaterial> materials = (materialCollection.materials) ?
				new HashSet<AnalogMaterial>(materialCollection.materials) : []

		int i = 0
		while (params["analogMaterialCollection[$i]"]) {
			GrailsParameterMap materialData = params["analogMaterialCollection[${i++}]"] as GrailsParameterMap
			boolean selected = materialData.materialType?.isLong()
			String enteredMeters = (materialData.meterSize) ? materialData.meterSize.trim() : ''
			String enteredNumbers = (materialData.numberSize) ? materialData.numberSize.trim() : ''

			// See if we can find the material type
			MaterialType materialType = null
			if (materialData.materialTypeId?.isLong()) {
				Long materialTypeId = materialData.materialTypeId.toLong()
				materialType = MaterialType.get(materialTypeId)
			}

			// Only continue if the checkbox of the material item was checked or a number was filled out
			if (materialType && (selected || !enteredMeters.isEmpty() || !enteredNumbers.isEmpty())) {
				// If the material can be added in meters, update the analog material with the given size in meters
				if (materialType.inMeters) {
					AnalogMaterial meterMaterial = updateAnalogMaterial(
							materialCollection, materialType, AnalogUnit.METER, enteredMeters, selected)
					materials.remove(meterMaterial)
				}

				// If the material can be added in numbers, update the analog material with the given size in numbers
				if (materialType.inNumbers) {
					AnalogMaterial numberMaterial = updateAnalogMaterial(
							materialCollection, materialType, AnalogUnit.NUMBER, enteredNumbers, selected)
					materials.remove(numberMaterial)
				}
			}
		}

		// Remove all material items that were previously checked and stored, but are not checked anymore
		materialCollection.materials?.removeAll(materials)

		// We only store a material collection if the collection has at least one material item
		if (materialCollection.materials?.size() > 0) {
			collection.analogMaterialCollection = materialCollection
		}
		else {
			collection.analogMaterialCollection = null
			materialCollection.delete()
		}
	}

	/**
	 * Searches the analog collection for the analog material with the given type and unit and updates it.
	 * @param materialCollection The analog material collection to update.
	 * @param materialType The material type to search for.
	 * @param unit The unit to search for.
	 * @param size The new size to update the collection with.
	 * @param isSelected Whether the material type checkbox was selected by the user.
	 * @return The updated material data.
	 */
	private AnalogMaterial updateAnalogMaterial(AnalogMaterialCollection materialCollection, MaterialType materialType,
	                                            AnalogUnit unit, String size, boolean isSelected) {
		AnalogMaterial material = materialCollection.getMaterialByTypeAndUnit(materialType, unit)
		if (!material) {
			material = new AnalogMaterial(materialType: materialType, unit: unit)
		}

		material.size = (BigDecimal) bigDecimalConverter.convert(size)
		material.isSelected = isSelected
		materialCollection.addToMaterials(material)

		return material
	}

	/**
	 * Updates the digital material collection of a given collection.
	 * @param collection The collection to update.
	 * @param params The data as filled out by the user.
	 */
	private void updateDigitalMaterialCollection(Collection collection, GrailsParameterMap params) {
		DigitalMaterialCollection materialCollection = collection.digitalMaterialCollection ?:
				new DigitalMaterialCollection()
		Set<DigitalMaterial> materials = (materialCollection.materials) ?
				new HashSet<DigitalMaterial>(materialCollection.materials) : []

		int i = 0
		while (params["digitalMaterialCollection[$i]"]) {
			GrailsParameterMap materialData = params["digitalMaterialCollection[${i++}]"] as GrailsParameterMap

			// Only continue if the checkbox of the material item was checked and a valid long value is sent
			if (materialData.materialType?.isLong()) {
				Long materialTypeId = materialData.materialType.toLong()
				MaterialType materialType = MaterialType.get(materialTypeId)

				DigitalMaterial material = updateDigitalMaterial(materialCollection, materialType)
				materials.remove(material)
			}
		}

		// Remove all material items that were previously checked and stored, but are not checked anymore
		materialCollection.materials?.removeAll(materials)

		// Save digital material collection data
		GrailsParameterMap materialCollectionData = params["digitalMaterialCollection"] as GrailsParameterMap
		Integer unitId = materialCollectionData.int('unit')

		materialCollection.setEnteredNumberOfFiles(materialCollectionData.numberOfFiles?.toString())
		materialCollection.setEnteredTotalSize(materialCollectionData.totalSize?.toString(), bigDecimalConverter)
		materialCollection.unit = (materialCollection.totalSize) ? ByteUnit.getById(unitId) : null

		// We only store a material collection if the collection has at least one material item or filled out data
		if ((materialCollection.materials?.size() > 0) || materialCollection.isFilledOut()) {
			collection.digitalMaterialCollection = materialCollection
		}
		else {
			collection.digitalMaterialCollection = null
			materialCollection.delete()
		}
	}

	/**
	 * Searches the digital collection for the digital material with the given type and updates it.
	 * @param materialCollection The digital material collection to update.
	 * @param materialType The material type to search for.
	 * @return The updated material data.
	 */
	private DigitalMaterial updateDigitalMaterial(DigitalMaterialCollection materialCollection,
	                                              MaterialType materialType) {
		DigitalMaterial material = materialCollection.getMaterialByType(materialType)
		if (!material) {
			material = new DigitalMaterial(materialType: materialType)
		}

		materialCollection.addToMaterials(material)

		return material
	}

	/**
	 * Updates the misc material collection of a given collection.
	 * @param collection The collection to update.
	 * @param params The data as filled out by the user.
	 */
	private void updateMiscMaterialCollection(Collection collection, GrailsParameterMap params) {
		MiscMaterialCollection materialCollection = collection.miscMaterialCollection ?: new MiscMaterialCollection()
		Set<MiscMaterial> materials = (materialCollection.materials) ?
				new HashSet<MiscMaterial>(materialCollection.materials) : []

		int i = 0
		while (params["miscMaterialCollection[$i]"]) {
			GrailsParameterMap materialData = params["miscMaterialCollection[${i++}]"] as GrailsParameterMap
			boolean selected = materialData.materialType?.isLong()
			Integer size = materialData.int('size')

			// See if we can find the material type
			MiscMaterialType materialType = null
			if (materialData.materialTypeId?.isLong()) {
				Long materialTypeId = materialData.materialTypeId.toLong()
				materialType = MiscMaterialType.get(materialTypeId)
			}

			// Only continue if the checkbox of the material item was checked or a number was filled out
			if (materialType && (selected || size)) {
				// Update the misc material with the given size in numbers
				MiscMaterial material = updateMiscMaterial(materialCollection, materialType, size, selected)
				materials.remove(material)
			}
		}

		// Remove all material items that were previously checked and stored, but are not checked anymore
		materialCollection.materials?.removeAll(materials)

		// We only store a material collection if the collection has at least one material item
		if (materialCollection.materials?.size() > 0) {
			collection.miscMaterialCollection = materialCollection
		}
		else {
			collection.miscMaterialCollection = null
			materialCollection.delete()
		}
	}

	/**
	 * Searches the misc collection for the misc material with the given type and updates it.
	 * @param materialCollection The misc material collection to update.
	 * @param materialType The material type to search for.
	 * @param size The new size to update the collection with.
	 * @param isSelected Whether the material type checkbox was selected by the user.
	 * @return The updated material data.
	 */
	private MiscMaterial updateMiscMaterial(MiscMaterialCollection materialCollection, MiscMaterialType materialType,
	                                        Integer size, boolean isSelected) {
		MiscMaterial material = materialCollection.getMaterialByType(materialType)
		if (!material) {
			material = new MiscMaterial(materialType: materialType)
		}

		material.size = size
		material.isSelected = isSelected
		materialCollection.addToMaterials(material)

		return material
	}

	/**
	 * Processes any changes made to the digital material status.
	 * @param collection The collection to update.
	 * @param params The data as filled out by the user.
	 */
	private void processDigitalMaterialStatus(Collection collection, GrailsParameterMap params) {
		DigitalMaterialStatus digitalMaterialStatus = collection.digitalMaterialStatus
		GrailsParameterMap digitalMaterialParams = params.digitalMaterialStatus as GrailsParameterMap

		if (digitalMaterialStatus && digitalMaterialParams) {
			Long newStatusCodeId = digitalMaterialParams.long('statusCode.id')
			DigitalMaterialStatusCode newStatusCode = DigitalMaterialStatusCode.get(newStatusCodeId)
			if (newStatusCode && digitalMaterialStatus.canChangeTo(newStatusCode)) {
				digitalMaterialStatus.statusCode = newStatusCode
				digitalMaterialStatus.statusSubCode = DigitalMaterialStatusSubCode.REQUESTED
			}

			if (digitalMaterialStatus.canDelayIngest() && digitalMaterialParams.delayIngest) {
				digitalMaterialStatus.ingestDelayed = true
			}

			digitalMaterialStatus.save()
		}
	}
}
