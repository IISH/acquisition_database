<%@ page import="org.iish.acquisition.domain.User; org.iish.acquisition.domain.Authority" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="page.collection.admin.label"/></title>
</head>

<body>
<div class="row content-menu top hidden-print">
    <div class="col-xs-24">
        <ul class="nav nav-pills">
            <li role="presentation">
                <g:link action="read-write">
                    <g:message code="admin.readWrite.label"/>
                </g:link>
            </li>
            <li role="presentation" class="active">
                <g:link action="read-only">
                    <g:message code="admin.readOnly.label"/>
                </g:link>
            </li>
        </ul>
    </div>
</div>

<form role="form" method="post" action="${g.createLink(controller: 'admin', action: 'read-only')}" class="form-horizontal">
    <div class="row elements">
        <div class="row control-label">
            <div class="col-xs-7 col-xs-offset-1 text-center">
                <label>
                    <g:message code="admin.userLogin.label"/>
                </label>
            </div>
        </div>

        <div class="removables">
            <g:each in="${usersAndRoles}" var="userAndRole" status="i">
                <g:set var="user" value="${userAndRole.key as User}"/>
                <g:set var="roles" value="${userAndRole.value as String[]}"/>

                <div class="form-group removable">
                    <div class="col-xs-7 col-xs-offset-1">
                        <input type="text" id="user[${i}].login" name="user[${i}].login" class="form-control"
                               value="${user.login}"/>
                    </div>

                    <div class="col-xs-2 col-xs-offset-11">
                        <button type="button" class="btn btn-link remove">
                            <span class="glyphicon glyphicon-remove"></span>
                        </button>
                    </div>
                </div>
            </g:each>
        </div>

        <div class="row">
            <div class="col-xs-22 text-right">
                <input type="hidden" class="next-number" value="${usersAndRoles ? usersAndRoles.size() : 0}"/>

                <button type="button" class="btn btn-default add">
                    <span class="glyphicon glyphicon-plus"></span>
                    &nbsp;
                    <g:message code="default.add.label"
                               args="${[g.message(code: 'admin.user.label').toString().toLowerCase()]}"/>
                </button>

                <button type="submit" class="btn btn-default btn-save">
                    <g:message code="default.button.save.label"/>
                </button>
            </div>
        </div>

        <div class="form-group removable hidden">
            <div class="col-xs-7 col-xs-offset-1">
                <input type="text" id="user[].login" name="user[].login" class="form-control" value=""/>
            </div>

            <div class="col-xs-2 col-xs-offset-11">
                <button type="button" class="btn btn-link remove">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
            </div>
        </div>
    </div>
</form>
</body>
</html>