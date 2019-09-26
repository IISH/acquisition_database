<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="springSecurity.login.title"/></title>
</head>

<body>
<form id='loginForm' class="center-block text-center" action='${postUrl}' method='POST' autocomplete="off" role="form">
	<h1><g:message code="springSecurity.login.header"/></h1>

    <div class="form-group">
        <label for="username">
            <g:message code="springSecurity.login.username.label"/>
        </label>

        <input type='text' name='j_username' id='username' class="form-control text-center"/>
    </div>

    <div class="form-group">
        <label for="password">
            <g:message code="springSecurity.login.password.label"/>
        </label>

        <input type='password' name='j_password' id='password' class="form-control text-center"/>
    </div>

    <button type="submit" class="btn btn-default btn-login">
        <g:message code="springSecurity.login.button"/>
    </button>

	<div class="">
		<br />You can login with your Jira/Confluence account.<br />
		If you have forgotten your password you can request a new one via <a href="https://confluence.socialhistoryservices.org/login.action?os_destination=%2F" target="_blank">Confluence Lost Password</a>
	</div>
</form>

<script type='text/javascript'>
    <!--
    (function () {
        document.forms['loginForm'].elements['j_username'].focus();
    })();
    // -->
</script>
</body>
</html>
