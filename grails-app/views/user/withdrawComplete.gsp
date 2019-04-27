%{--
  Created by IntelliJ IDEA.
  User: eunse
  Date: 2019-02-08
  Time: 오전 2:57
--}%

<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
    <meta name="layout" content="main_ekkor">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
</head>

<body>
<g:sidebar/>

<div id="create-user" class="content" role="main">
    <h3 class="content-header">회원 탈퇴 완료</h3>
    <div class="panel panel-default panel-margin-10">
        <div class="panel-body panel-body-content text-center">
            <p class="lead">지금까지 이용해 주셔서 감사합니다.</p>
            <g:link uri="/" class="btn btn-default">완료</g:link>
        </div>
    </div>
</div>
</body>
</html>