
<%@ page import="xyz.ekkor.Banner" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="admin">
		<g:set var="entityName" value="${message(code: 'banner.label', default: 'Banner')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-banner" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-banner" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list banner">
			
				<g:if test="${banner?.target}">
				<li class="fieldcontain">
					<span id="target-label" class="property-label"><g:message code="banner.target.label" default="Target" /></span>
					
						<span class="property-value" aria-labelledby="target-label"><g:fieldValue bean="${banner}" field="target"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${banner?.clickCount}">
				<li class="fieldcontain">
					<span id="clickCount-label" class="property-label"><g:message code="banner.clickCount.label" default="Click Count" /></span>
					
						<span class="property-value" aria-labelledby="clickCount-label"><g:fieldValue bean="${banner}" field="clickCount"/></span>
					
				</li>
				</g:if>

				<g:if test="${banner?.ipCount}">
					<li class="fieldcontain">
						<span id="ipCount-label" class="property-label"><g:message code="banner.ipCount.label" default="Click per IP" /></span>

						<span class="property-value" aria-labelledby="ipCount-label"><g:fieldValue bean="${banner}" field="ipCount"/></span>

					</li>
				</g:if>
			
				<g:if test="${banner?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="banner.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${banner?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${banner?.image}">
				<li class="fieldcontain">
					<span id="image-label" class="property-label"><g:message code="banner.image.label" default="Image" /></span>
					
						<span class="property-value" aria-labelledby="image-label"><g:fieldValue bean="${banner}" field="image"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${banner?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="banner.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${banner?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${banner?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="banner.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${banner}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${banner?.type}">
				<li class="fieldcontain">
					<span id="type-label" class="property-label"><g:message code="banner.type.label" default="Type" /></span>
					
						<span class="property-value" aria-labelledby="type-label"><g:fieldValue bean="${banner}" field="type"/></span>
					
				</li>
				</g:if>

				<g:if test="${banner?.contentType}">
					<li class="fieldcontain">
						<span id="contentType-label" class="property-label"><g:message code="banner.contentType.label" default="Content Type" /></span>

						<span class="property-value" aria-labelledby="contentType-label"><g:fieldValue bean="${banner}" field="contentType"/></span>

					</li>
				</g:if>
			
				<g:if test="${banner?.url}">
				<li class="fieldcontain">
					<span id="url-label" class="property-label"><g:message code="banner.url.label" default="Url" /></span>
					
						<span class="property-value" aria-labelledby="url-label"><g:fieldValue bean="${banner}" field="url"/></span>
					
				</li>
				</g:if>

				<g:if test="${banner?.tagDesktop}">
					<li class="fieldcontain">
						<span id="tagDesktop-label" class="property-label"><g:message code="banner.tagDesktop.label" default="tagDesktop" /></span>

						<span class="property-value" aria-labelledby="tagDesktop-label"><g:textArea name="tagDesktop" value="${banner?.tagDesktop}" readonly="true"/></span>

					</li>
				</g:if>

				<g:if test="${banner?.tagMobile}">
					<li class="fieldcontain">
						<span id="tagMobile-label" class="property-label"><g:message code="banner.tagMobile.label" default="tagMobile" /></span>

						<span class="property-value" aria-labelledby="tagMobile-label"><g:textArea name="tagMobile" value="${banner?.tagMobile}" readonly="true"/></span>

					</li>
				</g:if>
			
				<g:if test="${banner?.visible}">
				<li class="fieldcontain">
					<span id="visible-label" class="property-label"><g:message code="banner.visible.label" default="Visible" /></span>
					
						<span class="property-value" aria-labelledby="visible-label"><g:formatBoolean boolean="${banner?.visible}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:banner, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${banner}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					<g:link class="save" action="export" resource="${banner}"><g:message code="default.button.export.label" default="통계 다운로드" /></g:link>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
