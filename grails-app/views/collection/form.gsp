<%@ page import="org.iish.acquisition.domain.IngestDepotReport; org.iish.acquisition.domain.IngestDepotStatusCode; org.iish.acquisition.domain.Authority; org.iish.acquisition.domain.AnalogUnit" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="page.collection.${actionName}.label"/></title>
</head>

<body>
<g:hasErrors bean="${collection}">
    <div class="alert alert-danger" role="alert">
        <button type="button" class="close" data-dismiss="alert">
            <span aria-hidden="true">&times;</span>
            <span class="sr-only">Close</span>
        </button>

        <ul>
            <g:eachError bean="${collection}" var="error">
                <li><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </div>
</g:hasErrors>

<g:if test="${actionName == 'edit'}">
    <div class="row content-menu hidden-print">
        <div class="col-xs-20">
            <g:prevNextPager collectionSearchCommand="${collectionSearchCommand}"/>
        </div>

        <div class="col-xs-4">
            <g:link action="print" id="${params.id}" params="${request.getAttribute('queryParams')}"
                    class="btn btn-default btn-print">
                <span class="glyphicon glyphicon-print"></span>
                <g:message code="default.button.print.label"/>
            </g:link>
        </div>
    </div>
</g:if>

<form class="form-horizontal" role="form" method="post" action="#" enctype="multipart/form-data">

<div class="form-group">
    <label for="collection.name" class="col-xs-4 control-label">
        <g:message code="collection.name.label"/>
        <span class="required">*</span>
    </label>

    <div class="col-xs-13">
        <input type="text" id="collection.name" name="collection.name" class="form-control"
               value="${collection.name}"/>
    </div>

    <div class="col-xs-7">
        <div class="form-group form-group-nested">
            <label for="collection.acquisitionId" class="col-xs-4 control-label">
                <g:message code="collection.acquisitionId.label"/>
            </label>
            <g:select id="collection.acquisitionTypeId" name="collection.acquisitionTypeId"
                      class="form-control form-control-small"
                      from="${acquisitionTypes}" value="${collection.acquisitionTypeId?.id}" optionKey="id"
                      optionValue="name"/>
            <input type="text" id="collection.acquisitionId" name="collection.acquisitionId"
                   class="form-control form-control-medium" value="${collection.acquisitionId}"/>
        </div>
    </div>
</div>

<g:if test="${collection.addedBy || collection.objectRepositoryPID}">
    <div class="form-group">
        <g:if test="${collection.addedBy}">
            <label class="col-xs-4 control-label">
                <g:message code="collection.addedBy.label"/>
            </label>

            <div class="col-xs-13">
                <p class="form-control-static">${collection.addedBy}</p>
            </div>
        </g:if>
        <g:else>
            <div class="col-xs-17"></div>
        </g:else>

        <g:if test="${collection.objectRepositoryPID}">
            <label class="col-xs-1 control-label">
                <g:message code="collection.objectRepositoryPID.label"/>
            </label>

            <div class="col-xs-6">
                <p class="form-control-static">${collection.objectRepositoryPID}</p>
            </div>
        </g:if>
    </div>
</g:if>

<div class="form-group">
    <label class="col-xs-4 control-label">
        <g:message code="collection.location.label"/>
        <span class="required">*</span>
    </label>

    <div class="col-xs-20 elements">
        <div class="row control-label">
            <div class="col-xs-6">
                <label>
                    <g:message code="location.depot.label"/>
                </label>
            </div>

            <div class="col-xs-10">
                <label>
                    <g:message code="location.cabinet.label"/>
                </label>
            </div>

            <div class="col-xs-6">
                <label>
                    <g:message code="location.shelf.label"/>
                </label>
            </div>
        </div>

        <div class="removables">
            <g:each in="${collection.locations}" var="location" status="i">
                <div class="form-group removable">
                    <input type="hidden" id="collection.location[${i}].id" name="collection.location[${i}].id"
                           value="${location.id}"/>

                    <div class="col-xs-6">
                        <g:select id="collection.location[${i}].depot" name="collection.location[${i}].depot"
                                  class="form-control" from="${depots}" value="${location.depot?.id}" optionKey="id"
                                  optionValue="name" noSelection="${['null': '']}"/>
                    </div>

                    <div class="col-xs-10">
                        <input type="text" id="collection.location[${i}].cabinet"
                               name="collection.location[${i}].cabinet" class="form-control"
                               value="${location.cabinet}"/>
                    </div>

                    <div class="col-xs-6">
                        <input type="text" id="collection.location[${i}].shelf" name="collection.location[${i}].shelf"
                               class="form-control" value="${location.shelf}"/>
                    </div>

                    <div class="col-xs-2">
                        <button type="button" class="btn btn-link remove">
                            <span class="glyphicon glyphicon-remove"></span>
                        </button>
                    </div>
                </div>
            </g:each>
        </div>

        <div class="row">
            <div class="col-xs-24">
                <input type="hidden" class="next-number"
                       value="${collection.locations ? collection.locations.size() : 0}"/>
                <button type="button" class="btn btn-link add">
                    <span class="glyphicon glyphicon-plus"></span>
                    &nbsp;
                    <g:message code="default.add.label"
                               args="${[g.message(code: 'collection.location.label').toString().toLowerCase()]}"/>
                </button>
            </div>
        </div>

        <div class="form-group removable hidden">
            <input type="hidden" id="collection.location[].id" name="collection.location[].id" value=""/>

            <div class="col-xs-6">
                <g:select id="collection.location[].depot" name="collection.location[].depot"
                          class="form-control" from="${depots}" value="" optionKey="id"
                          optionValue="name" noSelection="${['null': '']}"/>
            </div>

            <div class="col-xs-10">
                <input type="text" id="collection.location[].cabinet"
                       name="collection.location[].cabinet" class="form-control"
                       value=""/>
            </div>

            <div class="col-xs-6">
                <input type="text" id="collection.location[].shelf" name="collection.location[].shelf"
                       class="form-control" value=""/>
            </div>

            <div class="col-xs-2">
                <button type="button" class="btn btn-link remove">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
            </div>
        </div>
    </div>
</div>

<div class="form-group form-group-nested">
    <label class="col-xs-4 control-label">
        <g:message code="collection.materialCollections.label"/>
        <span class="required">*</span>
    </label>

    <div class="col-xs-20">
        <div class="panel panel-default">
            <div class="panel-heading">
                <g:message code="collection.analogMaterialCollection.label"/>
            </div>

            <div class="panel-body form-control-listing">
                <g:if test="${true}">
                    <div class="row">
                </g:if>

                <g:each in="${materialTypes}" var="materialType" status="i">
                    <g:if test="${(i % 2 == 0) && (i > 0)}">
                        <div class="row">
                    </g:if>

                    <g:set var="materialMeter" value="${collection.analogMaterialCollection?.
                            getMaterialByTypeAndUnit(materialType, AnalogUnit.METER)}"/>
                    <g:set var="materialNumber" value="${collection.analogMaterialCollection?.
                            getMaterialByTypeAndUnit(materialType, AnalogUnit.NUMBER)}"/>

                    <div class="col-xs-5">
                        <div class="checkbox">
                            <label for="collection.analogMaterialCollection[${i}].materialType">
                                <input type="hidden" name="collection.analogMaterialCollection[${i}].materialTypeId"
                                       value="${materialType.id}"/>
                                <g:checkBox id="collection.analogMaterialCollection[${i}].materialType"
                                            name="collection.analogMaterialCollection[${i}].materialType"
                                            value="${materialType.id}"
                                            checked="${materialMeter?.isSelected || materialNumber?.isSelected}"/>
                                ${materialType.getNameAnalog()}
                            </label>
                        </div>
                    </div>

                    <div class="col-xs-7">
                        <div class="form-group">
                            <g:if test="${materialType.inMeters}">
                                <input type="text" id="collection.analogMaterialCollection[${i}].meterSize"
                                       name="collection.analogMaterialCollection[${i}].meterSize"
                                       class="form-control form-control-small"
                                       value="${materialMeter?.sizeToString()}"/>
                                <span class="control-label">${AnalogUnit.METER}</span>
                            </g:if>
                            <g:if test="${materialType.inMeters && materialType.inNumbers}">
                                /
                            </g:if>
                            <g:if test="${materialType.inNumbers}">
                                <input type="text" id="collection.analogMaterialCollection[${i}].numberSize"
                                       name="collection.analogMaterialCollection[${i}].numberSize"
                                       class="form-control form-control-small"
                                       value="${materialNumber?.sizeToString()}"/>
                                <span class="control-label">${AnalogUnit.NUMBER}</span>
                            </g:if>
                        </div>
                    </div>

                    <g:if test="${i % 2 == 1}">
                        </div>
                    </g:if>
                </g:each>

                <g:if test="${materialTypes.size() % 2 == 1}">
                    </div>
                </g:if>
            </div>
        </div>

        <div class="panel panel-default">
            <div class="panel-heading">
                <g:message code="collection.digitalMaterialCollection.label"/>
            </div>

            <div class="panel-body form-control-listing">
                <g:if test="${true}">
                    <div class="row">
                </g:if>

                <g:each in="${materialTypes}" var="materialType" status="i">
                    <g:set var="material"
                           value="${collection.digitalMaterialCollection?.getMaterialByType(materialType)}"/>

                    <g:if test="${(i % 2 == 0) && (i > 0)}">
                        <div class="row">
                    </g:if>

                    <div class="col-xs-6">
                        <div class="checkbox">
                            <label for="collection.digitalMaterialCollection[${i}].materialType">
                                <g:checkBox id="collection.digitalMaterialCollection[${i}].materialType"
                                            name="collection.digitalMaterialCollection[${i}].materialType"
                                            value="${materialType.id}"
                                            checked="${material}"/>
                                ${materialType.getNameDigital()}
                            </label>
                        </div>
                    </div>

                    <g:if test="${i == 1}">
                        <div class="col-xs-12">
                            <div class="form-group">
                                <label for="collection.digitalMaterialCollection.numberOfFiles"
                                       class="col-xs-10 control-label">
                                    <g:message code="digitalMaterialCollection.numberOfFiles.label"/>
                                </label>
                                <input type="text" id="collection.digitalMaterialCollection.numberOfFiles"
                                       name="collection.digitalMaterialCollection.numberOfFiles"
                                       class="form-control form-control-medium"
                                       value="${collection.digitalMaterialCollection?.numberOfFiles}"/>
                                <span class="control-label">
                                    <g:message code="digitalMaterialCollection.files.label"/>
                                </span>
                            </div>
                        </div>
                    </g:if>

                    <g:if test="${i == 3}">
                        <div class="col-xs-12">
                            <div class="form-group">
                                <label for="collection.digitalMaterialCollection.totalSize"
                                       class="col-xs-10 control-label">
                                    <g:message code="digitalMaterialCollection.totalSize.label"/>
                                </label>
                                <input type="text" id="collection.digitalMaterialCollection.totalSize"
                                       name="collection.digitalMaterialCollection.totalSize"
                                       class="form-control form-control-small"
                                       value="${collection.digitalMaterialCollection?.totalSizeToString()}"/>
                                <g:select id="collection.digitalMaterialCollection.unit"
                                          name="collection.digitalMaterialCollection.unit"
                                          class="form-control form-control-small" from="${byteUnits}"
                                          value="${collection.digitalMaterialCollection?.unit?.id}" optionKey="id"
                                          optionValue="name"/>
                            </div>
                        </div>
                    </g:if>

                    <g:if test="${i == 7}">
                        <div class="col-xs-12">
                            <div class="form-group">
                                <input type="text" id="collection.digitalMaterialCollection.numberOfDiskettes"
                                       name="collection.digitalMaterialCollection.numberOfDiskettes"
                                       class="col-xs-offset-10 form-control form-control-small"
                                       value="${collection.digitalMaterialCollection?.numberOfDiskettes}"/>
                                <span class="control-label">
                                    <g:message code="digitalMaterialCollection.numberOfDiskettes.lowercase.label"/>
                                </span>
                            </div>
                        </div>
                    </g:if>

                    <g:if test="${i == 9}">
                        <div class="col-xs-12">
                            <div class="form-group">
                                <input type="text" id="collection.digitalMaterialCollection.numberOfOpticalDisks"
                                       name="collection.digitalMaterialCollection.numberOfOpticalDisks"
                                       class="col-xs-offset-10 form-control form-control-small"
                                       value="${collection.digitalMaterialCollection?.numberOfOpticalDisks}"/>
                                <span class="control-label">
                                    <g:message code="digitalMaterialCollection.numberOfOpticalDisks.lowercase.label"/>
                                </span>
                            </div>
                        </div>
                    </g:if>

                    <g:if test="${i % 2 == 1}">
                        </div>
                    </g:if>
                </g:each>

                <g:if test="${materialTypes.size() % 2 == 1}">
                    </div>
                </g:if>
            </div>
        </div>
    </div>
</div>

<div class="form-group">
    <label for="collection.content" class="col-xs-4 control-label">
        <g:message code="collection.content.label"/>
        <span class="required">*</span>
    </label>

    <div class="col-xs-16">
        <textarea id="collection.content" name="collection.content" class="form-control"
                  rows="9">${collection.content}</textarea>
    </div>
</div>

<div class="form-group">
    <label for="collection.listsAvailable" class="col-xs-4 control-label">
        <g:message code="collection.listsAvailable.label"/>
        <span class="required">*</span>
    </label>

    <div class="col-xs-16">
        <input type="text" id="collection.listsAvailable" name="collection.listsAvailable" class="form-control"
               value="${collection.listsAvailable}"/>
    </div>
</div>

<div class="form-group">
    <label for="collection.toBeDone" class="col-xs-4 control-label">
        <g:message code="collection.toBeDone.label"/>
    </label>

    <div class="col-xs-16">
        <textarea id="collection.toBeDone" name="collection.toBeDone" class="form-control"
                  rows="9">${collection.toBeDone}</textarea>
    </div>

    <sec:ifAllGranted roles="${Authority.ROLE_ADMIN}">
        <div class="col-xs-4">
            <div class="form-group form-group-nested">
                <div class="col-xs-24">
                    <label for="collection.priority" class="control-label">
                        <g:message code="collection.priority.label"/>
                    </label>
                    <g:select id="collection.priority" name="collection.priority"
                              class="form-control" from="${priorities}" value="${collection.priority?.id}"
                              optionKey="id"
                              optionValue="name" noSelection="${['null': '']}"/>
                </div>
            </div>

            <div class="form-group form-group-nested">
                <div class="col-xs-24">
                    <label for="collection.level" class="control-label">
                        <g:message code="collection.level.label"/>
                    </label>
                    <g:select id="collection.level" name="collection.level"
                              class="form-control" from="${priorities}" value="${collection.level?.id}" optionKey="id"
                              optionValue="name" noSelection="${['null': '']}"/>
                </div>
            </div>
        </div>
    </sec:ifAllGranted>
    <sec:ifNotGranted roles="${Authority.ROLE_ADMIN}">
        <div class="col-xs-4">
            <g:if test="${collection.priority}">
                <div class="form-group form-group-nested">
                    <div class="col-xs-24">
                        <label for="collection.priority" class="control-label">
                            <g:message code="collection.priority.label"/>
                        </label>

                        <p class="form-control-static">
                            ${collection.priority}
                        </p>
                    </div>
                </div>
            </g:if>

            <g:if test="${collection.level}">
                <div class="form-group form-group-nested">
                    <div class="col-xs-24">
                        <label for="collection.level" class="control-label">
                            <g:message code="collection.level.label"/>
                        </label>

                        <p class="form-control-static">
                            ${collection.level}
                        </p>
                    </div>
                </div>
            </g:if>
        </div>
    </sec:ifNotGranted>
</div>

<div class="form-group">
    <label for="collection.owner" class="col-xs-4 control-label">
        <g:message code="collection.owner.label"/>
        <span class="required">*</span>
    </label>

    <div class="col-xs-8">
        <input type="text" id="collection.owner" name="collection.owner" class="form-control"
               value="${collection.owner}"/>
    </div>
</div>

<div class="form-group">
    <label for="collection.contract" class="col-xs-4 control-label">
        <g:message code="collection.contract.label"/>
    </label>

    <div class="col-xs-8">
        <g:select id="collection.contract" name="collection.contract"
                  class="form-control" from="${contracts}" value="${collection.contract?.id}" optionKey="id"
                  optionValue="name" noSelection="${['null': '']}"/>
    </div>
</div>

<div class="form-group">
    <label for="collection.accrual" class="col-xs-4 control-label">
        <g:message code="collection.accrual.label"/>
        <span class="required">*</span>
    </label>

    <div class="col-xs-8">
        <g:select id="collection.accrual" name="collection.accrual"
                  class="form-control" from="${accruals}" value="${collection.accrual?.id}" optionKey="id"
                  optionValue="name" noSelection="${['null': '']}"/>
    </div>
</div>

<div class="form-group">
    <label class="col-xs-4 control-label">
        <g:message for="collection.appraisal" code="collection.appraisal.label"/>
    </label>

    <div class="col-xs-8">
        <g:select id="collection.appraisal" name="collection.appraisal"
                  class="form-control" from="${appraisals}" value="${collection.appraisal?.id}" optionKey="id"
                  optionValue="name" noSelection="${['null': '']}"/>
    </div>
</div>

<div class="form-group">
    <label for="collection.dateOfArrival" class="col-xs-4 control-label">
        <g:message code="collection.dateOfArrival.label"/>
        <span class="required">*</span>
    </label>

    <div class="col-xs-8">
        <g:datePicker value="${(collection.dateOfArrival) ? collection.dateOfArrival : new Date()}"
                      id="collection.dateOfArrival" name="collection.dateOfArrival"/>
    </div>
</div>

<div class="form-group">
    <label for="collection.contactPerson" class="col-xs-4 control-label">
        <g:message code="collection.contactPerson.label"/>
        <span class="required">*</span>
    </label>

    <div class="col-xs-8">
        <input type="text" id="collection.contactPerson" name="collection.contactPerson" class="form-control"
               maxlength="7" value="${collection.contactPerson}"/>
    </div>

    <div class="col-xs-12 help-block">
        <g:message code="collection.contactPerson.help.message"/>
    </div>
</div>

<div class="form-group">
    <label for="collection.remarks" class="col-xs-4 control-label">
        <g:message code="collection.remarks.label"/>
    </label>

    <div class="col-xs-16">
        <textarea id="collection.remarks" name="collection.remarks" class="form-control"
                  rows="9">${collection.remarks}</textarea>
    </div>
</div>

<div class="form-group">
    <label for="collection.originalPackageTransport" class="col-xs-4 control-label">
        <g:message code="collection.originalPackageTransport.label"/>
    </label>

    <div class="col-xs-16">
        <textarea id="collection.originalPackageTransport" name="collection.originalPackageTransport"
                  class="form-control" rows="9">${collection.originalPackageTransport}</textarea>
    </div>
</div>

<g:if test="${actionName == 'edit'}">
    <div class="form-group">
        <label class="col-xs-4 control-label">
            <g:message code="collection.status.label"/>
        </label>

        <g:set var="halfSizeStatuses" value="${Math.ceil(statuses.size() / 2)}"/>

        <div class="col-xs-8">
            <g:each in="${statuses[0..<halfSizeStatuses]}" var="status">
                <div class="radio">
                    <label>
                        <g:radio id="collection.status.id" name="collection.status.id" value="${status.id}"
                                 checked="${collection.status?.id == status.id}"/>
                        ${status}
                    </label>
                </div>
            </g:each>
        </div>

        <div class="col-xs-8">
            <g:each in="${statuses[halfSizeStatuses..<statuses.size()]}" var="status">
                <div class="radio">
                    <label>
                        <g:radio id="collection.status.id" name="collection.status.id" value="${status.id}"
                                 checked="${collection.status?.id == status.id}"/>
                        ${status}
                    </label>
                </div>
            </g:each>
        </div>
    </div>

    <div class="form-group">
        <label class="col-xs-4 control-label">
            <g:message code="collection.collectionLevelReady.label"/>
        </label>

        <div class="col-xs-8">
            <div class="radio">
                <label>
                    <g:radio name="collection.collectionLevelReady" value="${true}"
                             checked="${collection.collectionLevelReady}"/>
                    <g:message code="default.boolean.true"/>
                </label>
            </div>
        </div>

        <div class="col-xs-8">
            <div class="radio">
                <label>
                    <g:radio name="collection.collectionLevelReady" value="${false}"
                             checked="${!collection.collectionLevelReady}"/>
                    <g:message code="default.boolean.false"/>
                </label>
            </div>
        </div>
    </div>
</g:if>

<g:if test="${uploadedPhotos.size() > 0}">
    <div class="form-group">
        <label class="col-xs-4 control-label">
            <g:message code="collection.uploadedPhotos.label"/>
        </label>

        <div class="col-xs-16">
            <input type="hidden" id="deletedPhotos" name="collection.deletedPhotos" value=""/>

            <ul class="list-unstyled form-control-static">
                <g:each in="${collection.photos}" var="uploadedPhoto">
                    <li>
                        <input type="hidden" name="collection.uploadedPhoto.id" value="${uploadedPhoto.id}"/>

                        <g:link controller="download" action="photo" id="${uploadedPhoto.id}">
                            ${uploadedPhoto.originalFilename}
                        </g:link>
                        (${uploadedPhoto.getReadableFileSize()})

                        <button type="button" class="btn btn-link btn-xs remove-image">
                            <span class="glyphicon glyphicon-remove"></span>
                        </button>
                    </li>
                </g:each>
            </ul>
        </div>
    </div>
</g:if>

<div class="form-group">
    <label class="col-xs-4 control-label">
        <g:message code="collection.uploadPhoto.label"/>
    </label>

    <div class="col-xs-20 elements">
        <div class="removables"></div>

        <div class="row">
            <div class="col-xs-24">
                <input type="hidden" class="next-number" value="0"/>
                <button type="button" class="btn btn-link add">
                    <span class="glyphicon glyphicon-plus"></span>
                    &nbsp;
                    <g:message code="default.add.label"
                               args="${[g.message(code: 'photo.label').toString().toLowerCase()]}"/>
                </button>
            </div>
        </div>

        <div class="form-group removable hidden">
            <div class="col-xs-14">
                <input type="file" id="collection.photo[]" name="collection.photo[]" class="form-control"/>
            </div>

            <div class="col-xs-2">
                <button type="button" class="btn btn-link remove">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
            </div>
        </div>
    </div>
</div>

<g:set var="ingestDepotStatus" value="${collection.ingestDepotStatus}"/>
<g:if test="${ingestDepotStatus}">
    <div class="form-group">
        <label class="col-xs-4 control-label">
            <g:message code="collection.ingestDepotStatus.label"/>
        </label>

        <div class="col-xs-20">
            <div class="form-control-static">
                <span class="<g:if test="${ingestDepotStatus.getStatusSubCode() ==
                        IngestDepotStatusCode.SUB_CODE_FAIL}">text-danger</g:if><g:else>text-success</g:else>">
                    ${ingestDepotStatus.getHumanReadableMessage()}</span>

                <ul>
                    <g:each in="${ingestDepotStatus.uploadStatuses}" var="uploadStatus">
                        <li>
                            <span class="<g:if test="${uploadStatus.getStatusSubCode() ==
                                    IngestDepotStatusCode.SUB_CODE_FAIL}">text-danger</g:if><g:else>text-success</g:else>">
                                ${uploadStatus.getHumanReadableMessage()}</span>

                            <g:if test="${IngestDepotReport.hasVirusReport(uploadStatus)}">
                                (<g:link controller="download" action="virusReport"
                                         id="${uploadStatus.id}"><g:message
                                        code="ingestDepotStatus.download.virusReport.label"/></g:link>)
                            </g:if>

                            <g:if test="${IngestDepotReport.hasFileIdentificationReport(uploadStatus)}">
                                (<g:link controller="download" action="fileIdentificationReport"
                                         id="${uploadStatus.id}"><g:message
                                        code="ingestDepotStatus.download.fileIdentificationReport.label"/></g:link>)
                            </g:if>
                        </li>
                    </g:each>
                </ul>
            </div>
        </div>

        <sec:ifAllGranted roles="${Authority.ROLE_SUPER_ADMIN}">
            <g:if test="${ingestDepotStatus.canManuallySetSorProcess()}">
                <div class="col-xs-8 col-xs-push-4">
                    <div class="checkbox">
                        <label>
                            <g:checkBox name="onHold" checked="${ingestDepotStatus.manualSorProcessOnHold}"/>
                            <g:message code="ingestDepotStatus.manual.onHold.label"/>
                        </label>
                    </div>
                </div>
            </g:if>

            <g:if test="${ingestDepotStatus.canManuallySetSorProcess()}">
                <div class="col-xs-8 col-xs-push-4">
                    <div class="checkbox">
                        <label>
                            <g:checkBox name="startProcess" checked="${ingestDepotStatus.manualStartSorProcess}"/>
                            <g:message code="ingestDepotStatus.manual.startProcess.label"/>
                        </label>
                    </div>
                </div>
            </g:if>
        </sec:ifAllGranted>
    </div>
</g:if>

<div class="form-group hidden-print">
    <div class="col-xs-22 text-right">
        <button type="submit" class="btn btn-default btn-save">
            <g:message code="default.button.save.label"/>
        </button>

        <g:link action="list" params="${request.getAttribute('queryParams')}" class="btn btn-default btn-cancel">
            <g:message code="default.button.cancel.label"/>
        </g:link>

        <g:if test="${actionName == 'edit'}">
            <g:link action="delete" id="${params.id}" params="${request.getAttribute('queryParams')}"
                    class="btn btn-default btn-delete">
                <g:message code="default.button.delete.label"/>
            </g:link>
        </g:if>
    </div>
</div>
</form>
</body>
</html>