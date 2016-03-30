<%@ page import="org.iish.acquisition.domain.User; org.iish.acquisition.domain.Authority" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="page.collection.admin.label"/></title>
</head>

<body>
<form role="form" method="post" action="${g.createLink(controller: 'admin', action: 'index')}" class="form-horizontal">
    <div class="row elements">
        <div class="row control-label">
            <div class="col-xs-7 col-xs-offset-1 text-center">
                <label>
                    <g:message code="admin.userLogin.label"/>
                </label>
            </div>

            <div class="col-xs-2 text-center">
                <label>
                    <g:message code="admin.admin.label"/>
                </label>
            </div>

            <div class="col-xs-2 col-xs-offset-1 text-center">
                <label>
                    <g:message code="admin.noOffloader.label"/>
                </label>
            </div>

            <div class="col-xs-2 text-center">
                <label>
                    <g:message code="admin.offloader1.label"/>
                </label>
            </div>

            <div class="col-xs-2 text-center">
                <label>
                    <g:message code="admin.offloader2.label"/>
                </label>
            </div>

            <div class="col-xs-2 text-center">
                <label>
                    <g:message code="admin.offloader3.label"/>
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

                    <div class="col-xs-2">
                        <div class="text-center">
                            <g:checkBox id="user[${i}].user" name="user[${i}].admin" value="admin"
                                        checked="${roles.contains(Authority.ROLE_ADMIN)}"/>
                        </div>
                    </div>

                    <div class="col-xs-2 col-xs-offset-1">
                        <div class="text-center">
                            <g:radio id="user[${i}].offloader" name="user[${i}].offloader" value="no"
                                     checked="${!roles.contains(Authority.ROLE_OFFLOADER_1) &&
                                             !roles.contains(Authority.ROLE_OFFLOADER_2) &&
                                             !roles.contains(Authority.ROLE_OFFLOADER_3)}"/>
                        </div>
                    </div>

                    <div class="col-xs-2">
                        <div class="text-center">
                            <g:radio id="user[${i}].offloader" name="user[${i}].offloader" value="1"
                                     checked="${roles.contains(Authority.ROLE_OFFLOADER_1)}"/>
                        </div>
                    </div>

                    <div class="col-xs-2">
                        <div class="text-center">
                            <g:radio id="user[${i}].offloader" name="user[${i}].offloader" value="2"
                                     checked="${roles.contains(Authority.ROLE_OFFLOADER_2)}"/>
                        </div>
                    </div>

                    <div class="col-xs-2">
                        <div class="text-center">
                            <g:radio id="user[${i}].offloader" name="user[${i}].offloader" value="3"
                                     checked="${roles.contains(Authority.ROLE_OFFLOADER_3)}"/>
                        </div>
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
            <div class="col-xs-22 text-right">
                <input type="hidden" class="next-number"
                       value="${usersAndRoles ? usersAndRoles.size() : 0}"/>

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

            <div class="col-xs-2">
                <div class="text-center">
                    <g:checkBox id="user[].user" name="user[].admin" value="admin" checked="${false}"/>
                </div>
            </div>

            <div class="col-xs-2 col-xs-offset-1">
                <div class="text-center">
                    <g:radio id="user[].offloader" name="user[].offloader" value="no" checked="${true}"/>
                </div>
            </div>

            <div class="col-xs-2">
                <div class="text-center">
                    <g:radio id="user[].offloader" name="user[].offloader" value="1" checked="${false}"/>
                </div>
            </div>

            <div class="col-xs-2">
                <div class="text-center">
                    <g:radio id="user[].offloader" name="user[].offloader" value="2" checked="${false}"/>
                </div>
            </div>

            <div class="col-xs-2">
                <div class="text-center">
                    <g:radio id="user[].offloader" name="user[].offloader" value="3" checked="${false}"/>
                </div>
            </div>

            <div class="col-xs-2">
                <button type="button" class="btn btn-link remove">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
            </div>
        </div>
    </div>
</form>
</body>
</html>