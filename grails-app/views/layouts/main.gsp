<%@ page import="org.iish.acquisition.domain.Authority; org.iish.acquisition.domain.Status; org.iish.acquisition.domain.DigitalMaterialStatusCode" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <title><g:message code="application.title.label"/> - <g:layoutTitle default=""/></title>
    <asset:stylesheet src="style.less"/>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <asset:javascript src="html5shiv-printshiv.js"/>
    <asset:javascript src="respond.js"/>
    <![endif]-->

    <g:layoutHead/>
</head>

<body>
<div id="main" class="container">
    <div class="page-header hidden-print clearfix">
        <a class="header-link pull-left" href="${grailsApplication.config.grails.serverURL}">
            <h1><g:message code="application.title.label"/></h1>
        </a>

        <sec:ifLoggedIn>
            <div class="user-info pull-right text-right">
                <div>
                    <strong>
                        <g:message code="springSecurity.loggedin.as.label"/>:
                    </strong>
                    <g:loggedInUserName/>
                </div>

                <div>
                    <sec:ifAnyGranted roles="${Authority.ROLE_ADMIN}">
                        <g:link controller="admin" action="index"><g:message
                                code="page.collection.admin.label"/></g:link>
                        |
                    </sec:ifAnyGranted>

                    <g:link controller="logout">
                        <g:message code="springSecurity.loggedin.logout.label"/>
                    </g:link>
                </div>
            </div>
        </sec:ifLoggedIn>
    </div>

    <div class="row">
        <sec:ifLoggedIn>
            <div class="col-xs-4 hidden-print">
                <nav class="navbar navbar-default" role="navigation">
                    <ul class="nav nav-pills nav-stacked">
                        <li <g:if test="${controllerName == 'collection' && actionName ==
                                'create'}">class="active"</g:if>>
                            <g:link controller="collection" action="create">
                                <g:message code="page.collection.create.label"/>
                            </g:link>
                        </li>
                        <li <g:if test="${controllerName == 'collection' && actionName ==
                                'list'}">class="active"</g:if>>
                            <g:link controller="collection" action="list"
                                    params="${[status: [Status.NOT_PROCESSED_ID, Status.IN_PROCESS_ID], search: 1]}">
                                <g:message code="page.collection.all.label"/>
                            </g:link>
                        </li>
                        <li <g:if test="${controllerName == 'collection' && actionName ==
                                'search'}">class="active"</g:if>>
                            <g:link controller="collection" action="search">
                                <g:message code="page.collection.search.label"/>
                            </g:link>
                        </li>
                        <li <g:if test="${controllerName == 'depot' && actionName ==
                                'list'}">class="active"</g:if>>
                            <g:link controller="depot" action="list">
                                <g:message code="page.collection.depot.label"/>
                            </g:link>
                        </li>
	                    <li <g:if test="${controllerName == 'collection' && actionName ==
			                    'timer_started'}">class="active"</g:if>>
		                    <g:link controller="collection" action="timer_started">
			                    <g:message code="page.collection.timer_started.label"/>
		                    </g:link>
	                    </li>
	                    <li <g:if test="${controllerName == 'collection' && actionName ==
			                    'timer_passed'}">class="active"</g:if>>
		                    <g:link controller="collection" action="timer_passed">
			                    <g:message code="page.collection.timer_passed.label"/>
		                    </g:link>
	                    </li>
                    </ul>
                </nav>
            </div>
        </sec:ifLoggedIn>

        <div class="<sec:ifLoggedIn>col-xs-20</sec:ifLoggedIn><sec:ifNotLoggedIn>col-xs-24</sec:ifNotLoggedIn> print-col">
            <div class="panel panel-default">
                <div id="container" class="panel-body">
                    <g:if test="${flash.message}">
                        <g:set var="alertClass" value="alert-success"/>
                        <g:if test="${flash.status?.equals('error') || params.login_error}">
                            <g:set var="alertClass" value="alert-danger"/>
                        </g:if>

                        <div class="alert ${alertClass}" role="alert">
                            <button type="button" class="close" data-dismiss="alert">
                                <span aria-hidden="true">&times;</span>
                                <span class="sr-only">Close</span>
                            </button>
                            ${flash.message}
                        </div>
                    </g:if>

                    <g:if test="${flash.errors}">
                        <div class="alert alert-danger" role="alert">
                            <button type="button" class="close" data-dismiss="alert">
                                <span aria-hidden="true">&times;</span>
                                <span class="sr-only">Close</span>
                            </button>

                            <ul>
                                <g:each in="${flash.errors}" var="error">
                                    <li><g:message error="${error}"/></li>
                                </g:each>
                            </ul>
                        </div>
                    </g:if>

                    <g:layoutBody/>
                </div>
            </div>
        </div>
    </div>
</div>
<asset:javascript src="application.js"/>
</body>
</html>