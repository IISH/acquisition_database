package org.iish.acquisition.service

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.grails.databinding.DataBinder
import org.grails.databinding.SimpleMapDataBindingSource
import org.iish.acquisition.domain.*
import org.springframework.web.multipart.commons.CommonsMultipartFile

/**
 * A service responsible for processing collections.
 */
@Transactional
class CollectionService {
	DataBinder grailsWebDataBinder
	SpringSecurityService springSecurityService

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
				'contactPerson', 'remarks', 'originalPackageTransport', 'contract', 'appraisal', 'status'], [])

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

		updateLocations(collection, collectionParams)
		processUploadedPhotos(collection, collectionParams)
		updateAnalogMaterialCollection(collection, collectionParams)
		updateDigitalMaterialCollection(collection, collectionParams)

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

			grailsWebDataBinder.bind(location, locationData as SimpleMapDataBindingSource,
					['depot', 'cabinet', 'shelf'], [])

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

			Photo photo = new Photo(
					originalFilename: photoData.originalFilename,
					size: photoData.size,
					contentType: photoData.contentType,
					photo: photoData.bytes
			)
			collection.addToPhotos(photo)
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
			GrailsParameterMap materialData = params["analogMaterialCollection[$i]"] as GrailsParameterMap
			i++

			// Only continue if the checkbox of the material item was checked and a valid long value is sent
			if (materialData.materialType?.isLong()) {
				MaterialType materialType = MaterialType.get(materialData.materialType.toLong())

				// If the material can be added in meters, update the analog material with the given size in meters
				AnalogMaterial meterMaterial = null
				if (materialType?.inMeters) {
					meterMaterial = updateAnalogMaterial(materialCollection, materialType, AnalogUnit.METER,
							materialData.int('meterSize'))
					materials.remove(meterMaterial)
				}

				// If the material can be added in numbers, update the analog material with the given size in numbers
				AnalogMaterial numberMaterial = null
				if (materialType?.inNumbers) {
					numberMaterial = updateAnalogMaterial(materialCollection, materialType, AnalogUnit.NUMBER,
							materialData.int('numberSize'))
					materials.remove(numberMaterial)
				}

				// If both meters and numbers are allowed and one of them has no size, remove that one
				if (materialType?.inMeters && materialType?.inNumbers) {
					if (!meterMaterial.size && numberMaterial.size) {
						materialCollection.removeFromMaterials(meterMaterial)
					}
					if (meterMaterial.size && !numberMaterial.size) {
						materialCollection.removeFromMaterials(numberMaterial)
					}
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
	 * @return The updated material data.
	 */
	private AnalogMaterial updateAnalogMaterial(AnalogMaterialCollection materialCollection, MaterialType materialType,
			AnalogUnit unit, Integer size) {
		AnalogMaterial material = materialCollection.getMaterialByTypeAndUnit(materialType, unit)
		if (!material) {
			material = new AnalogMaterial(materialType: materialType, unit: unit)
		}

		material.size = size
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
			GrailsParameterMap materialData = params["digitalMaterialCollection[$i]"] as GrailsParameterMap
			i++

			// Only continue if the checkbox of the material item was checked and a valid long value is sent
			if (materialData.materialType?.isLong()) {
				MaterialType materialType = MaterialType.get(materialData.materialType.toLong())

				DigitalMaterial material = updateDigitalMaterial(materialCollection, materialType)
				materials.remove(material)
			}
		}

		// Remove all material items that were previously checked and stored, but are not checked anymore
		materialCollection.materials?.removeAll(materials)

		// We only store a material collection if the collection has at least one material item
		if (materialCollection.materials?.size() > 0) {
			materialCollection.numberOfFiles = params.digitalMaterialCollection.int('numberOfFiles')
			materialCollection.totalSize = params.digitalMaterialCollection.int('totalSize')
			materialCollection.unit = ByteUnit.getById(params.digitalMaterialCollection.int('unit'))

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
}
