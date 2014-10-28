<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="page.collection.results.label"/></title>
</head>

<body>
<div class="row">
    <div class="col-xs-20">
        <g:paginate action="list" total="${results.getTotalCount()}" params="${params}"/>
    </div>

    <g:if test="${results.getTotalCount() > 0}">
        <div class="col-xs-4 text-right">
            <g:link controller="collection" action="export" params="${params}" class="btn btn-default btn-export">
                <g:message code="results.export.excel.label"/>
            </g:link>
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
                    <g:message code="default.boolean.true" />
                </g:if>
                <g:else>
                    <g:message code="default.boolean.false" />
                </g:else>
            </dd>
        </g:if>
        <g:if test="${collectionSearchCommand.analog?.size() > 0}">
            <dt><g:message code="search.analog.label"/></dt>
            <dd>${materialTypes.findAll { collectionSearchCommand.analog.contains(it.id) }.join(', ')}</dd>
        </g:if>
        <g:if test="${collectionSearchCommand.digital?.size() > 0}">
            <dt><g:message code="search.digital.label"/></dt>
            <dd>${materialTypes.findAll { collectionSearchCommand.digital.contains(it.id) }.join(', ')}</dd>
        </g:if>
    </dl>
</div>

<table class="table <g:if test="${results.size() > 0}">table-condensed table-striped table-hover table-click</g:if>">
    <thead>
    <tr>
        <th><g:sortLink field="name" messageCode="results.name.label"/></th>
        <th><g:sortLink field="analog_material" messageCode="results.analog.material.label"/></th>
        <th><g:sortLink field="digital_material" messageCode="results.digital.material.label"/></th>
        <th><g:sortLink field="date" messageCode="results.date.label"/></th>
        <th><g:sortLink field="location" messageCode="results.location.label"/></th>
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
            <td>${collection.name}</td>
            <td>
                <ul class="list-group">
                    <g:each in="${collection.analogMaterialCollection?.materials}" var="material">
                        <li class="list-group-item">${material}</li>
                    </g:each>
                </ul>
            </td>
            <td>
                <ul class="list-group">
                    <g:each in="${collection.digitalMaterialCollection?.materials}" var="material">
                        <li class="list-group-item">${material}</li>
                    </g:each>
                </ul>
            </td>
            <td><g:formatDate date="${collection.dateOfArrival}"/></td>
            <td>
                <ul class="list-group">
                    <g:each in="${collection.locations}" var="location">
                        <li class="list-group-item">${location}</li>
                    </g:each>
                </ul>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>

<div class="row">
    <div class="col-xs-20">
        <g:paginate action="list" total="${results.getTotalCount()}" params="${params}"/>
    </div>

    <g:if test="${results.getTotalCount() > 0}">
        <div class="col-xs-4 text-right">
            <g:link controller="collection" action="export" params="${params}" class="btn btn-default btn-export">
                <g:message code="results.export.excel.label"/>
            </g:link>
        </div>
    </g:if>
</div>
</body>
</html>