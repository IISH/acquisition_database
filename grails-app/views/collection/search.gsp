<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="page.collection.search.label"/></title>
</head>

<body>
<form class="form-horizontal" role="form" method="get" action="${g.createLink(action: 'list')}">
<input type="hidden" name="search" value="1"/>

<div class="form-group">
    <label for="keyword" class="col-xs-6 control-label">
        <g:message code="search.keywords.label"/>
    </label>

    <div class="col-xs-15">
        <input type="text" id="keyword" name="keyword" class="form-control"/>
    </div>
</div>

<div class="form-group">
    <label for="collectionName" class="col-xs-6 control-label">
        <g:message code="search.collection.name.label"/>
    </label>

    <div class="col-xs-15">
        <input type="text" id="collectionName" name="collectionName" class="form-control"/>
    </div>
</div>

<div class="form-group">
    <label for="acquisitionId" class="col-xs-6 control-label">
        <g:message code="search.acquisition.id.label"/>
    </label>

    <div class="col-xs-5">
        <g:select id="acquisitionTypeId" name="acquisitionTypeId" class="form-control"
                  from="${acquisitionTypes}" optionKey="id" optionValue="name"/>
    </div>

    <div class="col-xs-10">
        <input type="text" id="acquisitionId" name="acquisitionId" class="form-control"/>
    </div>
</div>

<div class="form-group">
    <label for="location" class="col-xs-6 control-label">
        <g:message code="search.locations.name.label"/>
    </label>

    <div class="col-xs-15">
        <g:select id="location" name="location" class="form-control" from="${depots}" optionKey="id"
                  optionValue="name" multiple="${true}" size="3"/>
        <span class="help-block">
            <g:message code="default.select.help.message"/>
        </span>
    </div>
</div>

<div class="form-group">
    <label for="cabinet" class="col-xs-6 control-label">
        <g:message code="search.cabinet.label"/>
    </label>

    <div class="col-xs-15">
        <input type="text" id="cabinet" name="cabinet" class="form-control"/>
    </div>
</div>

<div class="form-group">
    <label for="fromDate" class="col-xs-6 control-label">
        <g:message code="search.date.from.label"/>
    </label>

    <div class="col-xs-7">
        <g:datePicker id="fromDate" name="fromDate"/>
    </div>

    <label for="toDate" class="col-xs-1 control-label">
        <g:message code="search.date.to.label"/>
    </label>

    <div class="col-xs-7">
        <g:datePicker id="toDate" name="toDate"/>
    </div>
</div>

<div class="form-group">
    <label for="contactPerson" class="col-xs-6 control-label">
        <g:message code="search.contact.person.label"/>
    </label>

    <div class="col-xs-15">
        <input type="text" id="contactPerson" name="contactPerson" class="form-control"/>
    </div>
</div>

<div class="form-group">
    <label for="status" class="col-xs-6 control-label">
        <g:message code="search.status.label"/>
    </label>

    <div class="col-xs-15">
        <g:select id="status" name="status" class="form-control" from="${statuses}" optionKey="id"
                  optionValue="status" multiple="${true}" size="3"/>
        <span class="help-block">
            <g:message code="default.select.help.message"/>
        </span>
    </div>
</div>

<div class="form-group">
    <label for="collectionLevelReady" class="col-xs-6 control-label">
        <g:message code="search.collectionLevelReady.label"/>
    </label>

    <div class="col-xs-15">
        <g:select id="collectionLevelReady" name="collectionLevelReady" class="form-control"
                  from="${booleanEntrySet}" optionKey="key" optionValue="value" noSelection="${['null': '']}"/>
    </div>
</div>

<div class="form-group">
    <label for="status" class="col-xs-6 control-label">
        <g:message code="search.status.digital.label"/>
    </label>

    <div class="col-xs-15">
        <g:select id="statusDigital" name="statusDigital" class="form-control" from="${digitalStatuses}" optionKey="id"
                  optionValue="status" multiple="${true}" size="3"/>
        <span class="help-block">
            <g:message code="default.select.help.message"/>
        </span>
    </div>
</div>

<div class="form-group">
    <label class="col-xs-6 control-label">
        <g:message code="search.priority.label"/>
    </label>

    <g:each in="${priorities}" var="priority">
        <div class="col-xs-5">
            <div class="checkbox">
                <label>
                    <input type="checkbox" name="priority" value="${priority.id}"/>
                    ${priority.toString()}
                </label>
            </div>
        </div>
    </g:each>
</div>

<div class="form-group">
    <label class="col-xs-6 control-label">
        <g:message code="search.level.label"/>
    </label>

    <g:each in="${priorities}" var="level">
        <div class="col-xs-5">
            <div class="checkbox">
                <label>
                    <input type="checkbox" name="level" value="${level.id}"/>
                    ${level.toString()}
                </label>
            </div>
        </div>
    </g:each>
</div>

<div class="form-group">
    <label for="analog" class="col-xs-6 control-label">
        <g:message code="search.analog.label"/>
    </label>

    <div class="col-xs-15">
        <g:select id="analog" name="analog" class="form-control" from="${materialTypes}" optionKey="id"
                  optionValue="nameAnalog" multiple="${true}" size="3"/>
        <span class="help-block">
            <g:message code="default.select.help.message"/>
        </span>
    </div>
</div>

<div class="form-group">
    <label for="digital" class="col-xs-6 control-label">
        <g:message code="search.digital.label"/>
    </label>

    <div class="col-xs-15">
        <g:select id="digital" name="digital" class="form-control" from="${materialTypes}" optionKey="id"
                  optionValue="nameDigital" multiple="${true}" size="3"/>
        <span class="help-block">
            <g:message code="default.select.help.message"/>
        </span>
    </div>
</div>

<div class="form-group">
    <label class="col-xs-6 control-label">
        <g:message code="search.misc.label"/>
    </label>

    <g:each in="${miscMaterialTypes}" var="materialType">
        <div class="col-xs-7">
            <div class="checkbox">
                <label>
                    <input type="checkbox" name="misc" value="${materialType.id}"/>
                    ${materialType.toString()}
                </label>
            </div>
        </div>
    </g:each>
</div>

<div class="form-group">
    <div class="col-xs-21 text-right">
        <button type="submit" class="btn btn-default btn-search">
            <g:message code="default.button.search.label"/>
        </button>

        <g:link action="list" params="${request.getAttribute('queryParams')}" class="btn btn-default btn-cancel">
            <g:message code="default.button.cancel.label"/>
        </g:link>
    </div>
</div>
</form>
</body>
</html>