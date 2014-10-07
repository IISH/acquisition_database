<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="page.collection.search.label"/></title>
</head>

<body>
<form class="form-horizontal" role="form" method="get" action="${g.createLink(action: 'list')}">
    <input type="hidden" name="search" value="1" />

    <div class="form-group">
        <label for="keyword" class="col-xs-4 col-xs-offset-2 control-label">
            <g:message code="search.keywords.label"/>
        </label>

        <div class="col-xs-15">
            <input type="text" id="keyword" name="keyword" class="form-control"/>
        </div>
    </div>

    <div class="form-group">
        <label for="collectionName" class="col-xs-4 col-xs-offset-2 control-label">
            <g:message code="search.collection.name.label"/>
        </label>

        <div class="col-xs-15">
            <input type="text" id="collectionName" name="collectionName" class="form-control"/>
        </div>
    </div>

    <div class="form-group">
        <label for="location" class="col-xs-4 col-xs-offset-2 control-label">
            <g:message code="search.locations.name.label"/>
        </label>

        <div class="col-xs-15">
            <g:select id="location" name="location" class="form-control" from="${depots}" optionKey="id"
                      optionValue="name" multiple="${true}" size="3"/>
        </div>
    </div>

    <div class="form-group">
        <label for="fromDate" class="col-xs-4 col-xs-offset-2 control-label">
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
        <label for="contactPerson" class="col-xs-4 col-xs-offset-2 control-label">
            <g:message code="search.contact.person.label"/>
        </label>

        <div class="col-xs-15">
            <input type="text" id="contactPerson" name="contactPerson" class="form-control"/>
        </div>
    </div>

    <div class="form-group">
        <label class="col-xs-4 col-xs-offset-2 control-label">
            <g:message code="search.status.label"/>
        </label>

        <div class="col-xs-15">
            <g:select id="status" name="status" class="form-control" from="${statuses}" optionKey="id"
                      optionValue="status" multiple="${true}" size="3"/>
        </div>
    </div>

    <div class="form-group">
        <label for="analog" class="col-xs-4 col-xs-offset-2 control-label">
            <g:message code="search.analog.label"/>
        </label>

        <div class="col-xs-15">
            <g:select id="analog" name="analog" class="form-control" from="${materialTypes}" optionKey="id"
                      optionValue="name" multiple="${true}" size="3"/>
        </div>
    </div>

    <div class="form-group">
        <label for="digital" class="col-xs-4 col-xs-offset-2 control-label">
            <g:message code="search.digital.label"/>
        </label>

        <div class="col-xs-15">
            <g:select id="digital" name="digital" class="form-control" from="${materialTypes}" optionKey="id"
                      optionValue="name" multiple="${true}" size="3"/>
        </div>
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