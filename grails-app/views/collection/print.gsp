<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="page.collection.print.label"/></title>
</head>

<body>
<div class="row content-menu top hidden-print">
    <div class="col-xs-24">
        <g:link action="edit" id="${params.id}" params="${request.getAttribute('queryParams')}"
                class="btn btn-default btn-back">
            &leftarrow; <g:message code="default.button.back.label"/>
        </g:link>
    </div>
</div>

<div id="print">
<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.name.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.name}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.acquisitionId.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.acquisitionTypeId?.toString()}
        ${collection.acquisitionId}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.addedBy.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.addedBy}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.objectRepositoryPID.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.objectRepositoryPID}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.location.label"/>
    </label>

    <div class="col-xs-20">
        <div class="row">
            <label class="col-xs-6 control-label">
                <g:message code="location.depot.label"/>
            </label>

            <label class="col-xs-10 control-label">
                <g:message code="location.cabinet.label"/>
            </label>

            <label class="col-xs-6 control-label">
                <g:message code="location.shelf.label"/>
            </label>
        </div>

        <div>
            <g:each in="${collection.locations}" var="location" status="i">
                <div class="row">
                    <div class="col-xs-6">
                        ${location.depot?.getName()}
                    </div>

                    <div class="col-xs-10">
                        ${location.cabinet}
                    </div>

                    <div class="col-xs-6">
                        ${location.shelf}
                    </div>
                </div>
            </g:each>
        </div>
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.analogMaterialCollection.label"/>
    </label>

    <div class="col-xs-20">
        <ul class="list-unstyled">
            <g:each in="${collection.analogMaterialCollection?.materials}" var="analogMaterial">
                <li>${analogMaterial.toString()}</li>
            </g:each>
        </ul>
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.digitalMaterialCollection.label"/>
    </label>

    <div class="col-xs-10">
        <ul class="list-unstyled">
            <g:each in="${collection.digitalMaterialCollection?.materials}" var="digitalMaterial">
                <li>${digitalMaterial.toString()}</li>
            </g:each>
        </ul>
    </div>

    <div class="col-xs-10">
        <ul class="list-unstyled">
            <g:if test="${collection.digitalMaterialCollection?.numberOfFiles}">
                <li>
                    <strong><g:message code="digitalMaterialCollection.numberOfFiles.label"/>:</strong>
                    ${collection.digitalMaterialCollection?.numberOfFiles}
                    <g:message code="digitalMaterialCollection.files.label"/>
                </li>
            </g:if>

            <g:if test="${collection.digitalMaterialCollection?.totalSize}">
                <li>
                    <strong><g:message code="digitalMaterialCollection.totalSize.label"/>:</strong>
                    ${collection.digitalMaterialCollection?.totalSizeToString()}
                    ${collection.digitalMaterialCollection?.unit?.name()}
                </li>
            </g:if>

            <g:if test="${collection.digitalMaterialCollection?.numberOfDiskettes}">
                <li>
                    ${collection.digitalMaterialCollection?.numberOfDiskettes}
                    <g:message code="digitalMaterialCollection.numberOfDiskettes.lowercase.label"/>
                </li>
            </g:if>

            <g:if test="${collection.digitalMaterialCollection?.numberOfOpticalDisks}">
                <li>
                    ${collection.digitalMaterialCollection?.numberOfOpticalDisks}
                    <g:message code="digitalMaterialCollection.numberOfOpticalDisks.lowercase.label"/>
                </li>
            </g:if>
        </ul>
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.content.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.content}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.listsAvailable.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.listsAvailable}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.toBeDone.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.toBeDone}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.priority.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.priority?.toString()}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.level.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.level?.toString()}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.owner.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.owner}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.contract.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.contract?.toString()}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.accrual.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.accrual?.toString()}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.appraisal.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.appraisal?.toString()}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.dateOfArrival.label"/>
    </label>

    <div class="col-xs-20">
        <g:formatDate date="${collection.dateOfArrival}"/>
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.contactPerson.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.contactPerson}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.remarks.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.remarks}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.originalPackageTransport.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.originalPackageTransport}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.status.label"/>
    </label>

    <div class="col-xs-20">
        ${collection.status?.getStatus()}
    </div>
</div>

<div class="row">
    <label class="col-xs-4 control-label">
        <g:message code="collection.collectionLevelReady.label"/>
    </label>

    <div class="col-xs-20">
        <g:if test="${collection.collectionLevelReady}">
            <g:message code="default.boolean.true"/>
        </g:if>
        <g:else>
            <g:message code="default.boolean.false"/>
        </g:else>
    </div>
</div>
</div>
</body>
</html>