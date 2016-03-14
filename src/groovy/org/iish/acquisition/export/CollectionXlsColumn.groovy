package org.iish.acquisition.export

/**
 * Columns that can be exported in a XLS document.
 */
enum CollectionXlsColumn {
    ID('id', '#'),
    NAME('name', 'collection.name.label'),
    ACQUISITION_ID('acquisition_id', 'collection.acquisitionId.label'),
    ADDED_BY('added_by', 'collection.addedBy.label'),
    PID('pid', 'collection.objectRepositoryPID.label'),
    LOCATION('location', 'collection.location.label'),
    ANALOG_MATERIAL('analog_material', 'collection.analogMaterialCollection.extended.label'),
    DIGITAL_MATERIAL('digital_material', 'collection.digitalMaterialCollection.extended.label'),
    MISC_MATERIAL('misc_material', 'collection.miscMaterialCollection.extended.label'),
    CONTENT('content', 'collection.content.label'),
    LISTS_AVAILABLE('lists_available', 'collection.listsAvailable.label'),
    TO_BE_DONE('to_be_done', 'collection.toBeDone.label'),
    PRIORITY('priority', 'collection.priority.label'),
    LEVEL('level', 'collection.level.label'),
    OWNER('owner', 'collection.owner.label'),
    CONTRACT('contract', 'collection.contract.label'),
    ACCRUAL('accrual', 'collection.accrual.label'),
    APPRAISAL('appraisal', 'collection.appraisal.label'),
    DATE_OF_ARRIVAL('date_of_arrival', 'collection.dateOfArrival.label'),
    CONTACT_PERSON('contact_person', 'collection.contactPerson.label'),
    REMARKS('remarks', 'collection.remarks.label'),
    ORIGINAL_PACKAGE_TRANSPORT('original_package_transport', 'collection.originalPackageTransport.label'),
    STATUS('status', 'collection.status.label'),
    COLLECTION_LEVEL_READY('collection_level_ready', 'collection.collectionLevelReady.label')

    static final Map<String, CollectionXlsColumn> ALL_COLUMNS = [:] as HashMap

    final String name
    final String languageCode

    private CollectionXlsColumn(String name, String languageCode) {
        this.name = name
        this.languageCode = languageCode
    }

    static {
        values().each { CollectionXlsColumn column ->
            ALL_COLUMNS.put(column.name, column)
        }
    }
}