<%@ page import="grails.plugin.springsecurity.SpringSecurityUtils; org.iish.acquisition.domain.Authority" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="page.collection.depot.label"/></title>
</head>

<body>
<form role="form" method="post"
      action="${g.createLink(controller: 'depot', action: 'delete', params: [path: params.path])}">
    <div class="row content-menu top hidden-print">
        <g:set var="isOffloader2" value="${SpringSecurityUtils.ifAnyGranted(Authority.ROLE_OFFLOADER_2)}"/>

        <g:if test="${isOffloader2}">
            <div class="col-xs-5">
                <button type="submit" class="btn btn-default btn-delete">
                    <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                    <g:message code="ingestDepot.delete.button"/>
                </button>
            </div>
        </g:if>

        <div class="${isOffloader2 ? 'col-xs-19' : 'col-xs-24'}">
            <ol class="breadcrumb">
                <g:set var="parentPath" value=""/>

                <li class="${path == '/' ? 'active' : ''}">
                    <g:if test="${path == '/'}">
                        <g:message code="ingestDepot.root.name"/>
                    </g:if>
                    <g:else>
                        <g:link params="${[path: '/']}">
                            <g:message code="ingestDepot.root.name"/>
                        </g:link>
                    </g:else>
                </li>

                <g:each in="${pathAsArray}" var="directory">
                    <g:set var="parentPath" value="${parentPath + '/' + directory}"/>

                    <li class="${(pathAsArray.last() == directory) ? 'active' : ''}">
                        <g:if test="${pathAsArray.last() == directory}">
                            ${directory}
                        </g:if>
                        <g:else>
                            <g:link params="${[path: parentPath]}">
                                ${directory}
                            </g:link>
                        </g:else>
                    </li>
                </g:each>
            </ol>
        </div>
    </div>

    <table class="table <g:if test="${files.size() > 0}">table-condensed table-striped table-hover</g:if>">
        <thead>
        <tr>
            <g:if test="${isOffloader2}">
                <th class="column-checkbox"><input type="checkbox" class="checkAll"/></th>
            </g:if>
	        <th><g:message code="ingestDepot.name.label"/></th>
	        <th><g:message code="ingestDepot.date.label"/></th>
            <th><g:message code="ingestDepot.size.label"/></th>
        </tr>
        </thead>
        <tbody>
        <g:if test="${files.size() == 0}">
            <tr>
                <td colspan="${isOffloader2 ? '3' : '2'}" class="text-center">
                    <em><g:message code="default.no.files.message"/></em>
                </td>
            </tr>
        </g:if>
        <g:each in="${files}" var="file">
            <tr>
                <g:if test="${isOffloader2}">
                    <td class="column-checkbox">
                        <input type="checkbox" name="file" value="${file.getPath()}"/>
                    </td>
                </g:if>
                <td>
                    <g:if test="${file.isDirectory()}">
                        <span class="glyphicon glyphicon-folder-close" aria-hidden="true"></span>
                        <g:link controller="depot" action="list" params="${[path: file.getPath()]}">
                            ${file.getName()}
                        </g:link>
                    </g:if>
                    <g:else>
                        <span class="glyphicon glyphicon-file" aria-hidden="true"></span>
                        ${file.getName()}
                    </g:else>
                </td>
	            <td>
		            <g:formatDate formatName="default.datetime.format" date="${file.getDate().time}" />
	            </td>
                <td>
                    <g:if test="${!file.isDirectory()}">
                        ${file.getReadableFileSize()}
                    </g:if>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
</form>
</body>
</html>