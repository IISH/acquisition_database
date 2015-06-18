<%@ page import="org.iish.acquisition.domain.Priority; org.iish.acquisition.domain.AcquisitionType; org.iish.acquisition.domain.DigitalMaterialStatus" %>
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
            <g:link controller="collection" action="export" params="${params}" class="btn btn-default btn-export">
                <g:message code="results.export.excel.label"/>
            </g:link>
        </div>
    </g:if>
</div>

<div id="search-info">
    <strong><g:message code="results.timer_total.records.label" args="${[results.getTotalCount()]}"/></strong>
</div>

<table class="table <g:if test="${results.size() > 0}">table-condensed table-striped table-hover table-click</g:if>">
    <thead>
    <tr>
        <th><g:sortLink field="name" messageCode="results.name.label"/></th>
	    <th><g:sortLink field="timer_deadline" messageCode="results.timer_deadline.label"/></th>
	    <th><g:sortLink field="status" messageCode="search.status.label"/></th>
        <th><g:sortLink field="analog_material" messageCode="results.analog.material.label"/></th>
        <th><g:sortLink field="digital_material" messageCode="results.digital.material.label"/></th>
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
            <td>
	            <span class="badge">${collection.id}</span> ${collection.name}
            </td>
	        <td>
		        <g:formatDate date="${collection.digitalMaterialStatus.getTimerExpirationDate()}"
		                      formatName="default.datetime.format"/>
	        </td>
	        <td>
		        ${collection.digitalMaterialStatus.statusCode.id/10} - ${collection.digitalMaterialStatus.message}
	        </td>
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
            <g:link controller="collection" action="export" params="${params}" class="btn btn-default btn-export">
                <g:message code="results.export.excel.label"/>
            </g:link>
        </div>
    </g:if>
</div>
</body>
</html>