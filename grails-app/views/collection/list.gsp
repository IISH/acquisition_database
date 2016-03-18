<%@ page import="org.iish.acquisition.export.CollectionXlsColumn; org.iish.acquisition.domain.Priority; org.iish.acquisition.domain.AcquisitionType" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="page.collection.results.label"/></title>
</head>

<body>
<div class="row content-menu top hidden-print">
    <div class="col-xs-20">
        <g:paginate action="list" total="${results.getTotalCount()}" params="${params}"/>
    </div>

    <g:if test="${results.getTotalCount() > 0}">
        <div class="col-xs-4 text-right">
            <button data-toggle="modal" data-target="#exportModal" class="btn btn-default">
                <g:message code="results.export.excel.label"/>
            </button>
        </div>
    </g:if>
</div>

<div id="search-info">
    <strong><g:message code="results.total.records.label" args="${[results.getTotalCount()]}"/></strong>

    <dl class="dl-horizontal">
        <g:if test="${collectionSearchCommand.keyword}">
            <dt><g:message code="search.keywords.label"/></dt>
            <dd>${collectionSearchCommand.keyword}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.acquisitionTypeId && collectionSearchCommand.acquisitionId}">
            <dt><g:message code="search.acquisition.id.label"/></dt>
            <dd>${acquisitionTypes.find {
                collectionSearchCommand.acquisitionTypeId == it.id
            }} ${collectionSearchCommand.acquisitionId}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.collectionName}">
            <dt><g:message code="search.collection.name.label"/></dt>
            <dd>${collectionSearchCommand.collectionName}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.location?.size() > 0}">
            <dt><g:message code="search.locations.name.label"/></dt>
            <dd>${depots.findAll { collectionSearchCommand.location.contains(it.id) }.join(', ')}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.cabinet}">
            <dt><g:message code="search.cabinet.label"/></dt>
            <dd>${collectionSearchCommand.cabinet}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.fromDate && collectionSearchCommand.toDate}">
            <dt><g:message code="search.date.from.label"/></dt>
            <dd>
                <g:formatDate date="${collectionSearchCommand.fromDate}"/>
                <g:message code="search.date.to.label"/>
                <g:formatDate date="${collectionSearchCommand.toDate}"/>
            </dd>
        </g:if>
        <g:if test="${collectionSearchCommand.contactPerson}">
            <dt><g:message code="search.contact.person.label"/></dt>
            <dd>${collectionSearchCommand.contactPerson}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.status?.size() > 0}">
            <dt><g:message code="search.status.label"/></dt>
            <dd>${statuses.findAll { collectionSearchCommand.status.contains(it.id) }.join(', ')}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.collectionLevelReady}">
            <dt><g:message code="search.collectionLevelReady.label"/></dt>
            <dd>
                <g:if test="${collectionSearchCommand.collectionLevelReady}">
                    <g:message code="default.boolean.true"/>
                </g:if>
                <g:else>
                    <g:message code="default.boolean.false"/>
                </g:else>
            </dd>
        </g:if>
        <g:if test="${collectionSearchCommand.statusDigital?.size() > 0}">
            <dt><g:message code="search.status.digital.label"/></dt>
            <dd>${digitalStatuses.findAll { collectionSearchCommand.statusDigital.contains(it.id) }.join(', ')}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.subStatusDigital?.size() > 0}">
            <dt><g:message code="search.sub.status.digital.label"/></dt>
            <dd>${digitalSubStatuses.findAll { collectionSearchCommand.subStatusDigital.contains(it.id) }.join(', ')}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.priority?.size() > 0}">
            <dt><g:message code="search.priority.label"/></dt>
            <dd>${priorities.findAll { collectionSearchCommand.priority.contains(it.id) }.join(', ')}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.level?.size() > 0}">
            <dt><g:message code="search.level.label"/></dt>
            <dd>${priorities.findAll { collectionSearchCommand.level.contains(it.id) }.join(', ')}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.analog?.size() > 0}">
            <dt><g:message code="search.analog.label"/></dt>
            <dd>${materialTypes.findAll { collectionSearchCommand.analog.contains(it.id) }*.getNameAnalog().
                    join(', ')}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.digital?.size() > 0}">
            <dt><g:message code="search.digital.label"/></dt>
            <dd>${materialTypes.findAll { collectionSearchCommand.digital.contains(it.id) }*.getNameDigital().
                    join(', ')}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.misc?.size() > 0}">
            <dt><g:message code="search.misc.label"/></dt>
            <dd>${miscMaterialTypes.findAll { collectionSearchCommand.misc.contains(it.id) }*.name.join(', ')}</dd>
        </g:if>
    </dl>
</div>

<table class="table <g:if test="${results.size() > 0}">table-condensed table-striped table-hover table-click</g:if>">
    <thead>
    <tr>
        <g:if test="${collectionSearchCommand.columns.contains('name')}">
            <th><g:sortLink field="name" messageCode="results.name.label"/></th>
        </g:if>
        <g:if test="${collectionSearchCommand.columns.contains('timer_deadline')}">
            <th><g:sortLink field="timer_deadline" messageCode="results.timerDeadline.label"/></th>
        </g:if>
        <g:if test="${collectionSearchCommand.columns.contains('digital_status')}">
            <th><g:sortLink field="status" messageCode="results.digitalStatus.label"/></th>
        </g:if>
        <g:if test="${collectionSearchCommand.columns.contains('analog_material')}">
            <th><g:sortLink field="analog_material" messageCode="results.analog.material.label"/></th>
        </g:if>
        <g:if test="${collectionSearchCommand.columns.contains('digital_material')}">
            <th><g:sortLink field="digital_material" messageCode="results.digital.material.label"/></th>
        </g:if>
        <g:if test="${collectionSearchCommand.columns.contains('date')}">
            <th><g:sortLink field="date" messageCode="results.date.label"/></th>
        </g:if>
        <g:if test="${collectionSearchCommand.columns.contains('location')}">
            <th><g:sortLink field="location" messageCode="results.location.label"/></th>
        </g:if>
        <th class="visible-print"><g:message code="results.cabinet.label"/></th>
    </tr>
    </thead>
    <tbody>
    <g:if test="${results.size() == 0}">
        <tr>
            <td colspan="5" class="text-center">
                <em><g:message code="default.no.results.message"/></em>
            </td>
        </tr>
    </g:if>
    <g:each in="${results}" var="collection">
        <tr>
            <td class="hidden table-click-link">
                <g:createLink params="${params}" controller="collection" action="edit" id="${collection.id}"/>
            </td>
            <g:if test="${collectionSearchCommand.columns.contains('name')}">
                <td><span class="badge">${collection.id}</span> ${collection.name}</td>
            </g:if>
            <g:if test="${collectionSearchCommand.columns.contains('timer_deadline')}">
                <td>
                    <g:formatDate date="${collection.digitalMaterialStatus.getTimerExpirationDate()}"
                                  formatName="default.datetime.format"/>
                </td>
            </g:if>
            <g:if test="${collectionSearchCommand.columns.contains('digital_status')}">
                <td>
                    ${collection.digitalMaterialStatus.statusCode.id / 10} - ${collection.digitalMaterialStatus.message}
                </td>
            </g:if>
            <g:if test="${collectionSearchCommand.columns.contains('analog_material')}">
                <td>
                    <ul class="list-group">
                        <g:each in="${collection.analogMaterialCollection?.materials}" var="material">
                            <li class="list-group-item">${material}</li>
                        </g:each>
                    </ul>
                </td>
            </g:if>
            <g:if test="${collectionSearchCommand.columns.contains('digital_material')}">
                <td>
                    <ul class="list-group">
                        <g:each in="${collection.digitalMaterialCollection?.materials}" var="material">
                            <li class="list-group-item">${material}</li>
                        </g:each>

                        <g:if test="${collection.digitalMaterialCollection}">
                            <li class="list-group-item">
                                <g:message code="digitalMaterialCollection.numberOfFiles.export.label"/>:
                                ${collection.digitalMaterialCollection.numberOfFilesToString()}
                            </li>
                            <li class="list-group-item">
                                <g:message code="digitalMaterialCollection.totalSize.label"/>:
                                ${collection.digitalMaterialCollection.totalSizeToStringWithUnit()}
                            </li>
                        </g:if>
                    </ul>
                </td>
            </g:if>
            <g:if test="${collectionSearchCommand.columns.contains('date')}">
                <td><g:formatDate date="${collection.dateOfArrival}"/></td>
            </g:if>
            <g:if test="${collectionSearchCommand.columns.contains('location')}">
                <td>
                    <ul class="list-group">
                        <g:each in="${collection.locations}" var="location">
                            <li class="list-group-item">${location}</li>
                        </g:each>
                    </ul>
                </td>
            </g:if>
            <td class="visible-print">
                <ul class="list-group">
                    <g:each in="${collection.locations}" var="location">
                        <li class="list-group-item">${location.cabinet}</li>
                    </g:each>
                </ul>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>

<div class="row content-menu bottom hidden-print">
    <div class="col-xs-20">
        <g:paginate action="list" total="${results.getTotalCount()}" params="${params}"/>
    </div>

    <g:if test="${results.getTotalCount() > 0}">
        <div class="col-xs-4 text-right">
            <button data-toggle="modal" data-target="#exportModal" class="btn btn-default">
                <g:message code="results.export.excel.label"/>
            </button>
        </div>
    </g:if>
</div>

<div id="exportModal" class="modal fade hidden-print" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content modal-lg">
            <form role="form" method="get" action="${g.createLink(controller: 'collection', action: 'export')}">
                <g:each in="${request.getAttribute('queryParams')}" var="param">
                    <g:if test="${!['export', 'max', 'offset', 'columns'].contains(param.key)}">
                        <g:each in="${param.value}" var="val">
                            <input type="hidden" name="${param.key}" value="${val}"/>
                        </g:each>
                    </g:if>
                </g:each>

                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span>
                        <span class="sr-only">
                            <g:message code="default.close.label"/>
                        </span>
                    </button>

                    <h4 class="modal-title">
                        <g:message code="results.export.excel.label"/>
                    </h4>
                </div>

                <div class="modal-body">
                    <g:checkboxTable values="${CollectionXlsColumn.values()}" nrColumns="3"
                                     name="exportColumns" label="languageCode" value="name"
                                     checked="${{ CollectionXlsColumn.DEFAULT_COLUMNS.contains(it) }}"
                                     class="${{ CollectionXlsColumn.DEFAULT_COLUMNS.contains(it) ? 'default' : '' }}"/>
                </div>

                <div class="modal-footer">
                    <div class="btn-group btn-group-sm pull-left" data-toggle="buttons">
                        <label class="btn btn-default">
                            <input type="radio" class="all" autocomplete="off"/>
                            <g:message code="results.export.all.columns.label"/>
                        </label>
                        <label class="btn btn-default active">
                            <input type="radio" class="default" autocomplete="off" checked="checked"/>
                            <g:message code="results.export.default.columns.label"/>
                        </label>
                    </div>

                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        <g:message code="default.close.label"/>
                    </button>
                    <button type="submit" class="btn btn-primary">
                        <g:message code="results.export.excel.label"/>
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>