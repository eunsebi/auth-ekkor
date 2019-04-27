<html>

<head>
<meta name="layout" content="${gspLayout ?: 'main_with_banner'}"/>
<title><g:message code='springSecurity.denied.title' /></title>
</head>

<body>
<g:sidebar/>
<div class="body">
	<div class="errors"><g:message code='springSecurity.denied.message' /></div>
	<div><g:link href="/">[Go to Main]</g:link></div>
</div>
</body>

</html>
