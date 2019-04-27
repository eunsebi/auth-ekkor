%{--
  Created by IntelliJ IDEA.
  User: eunse
  Date: 2019-02-07
  Time: 오후 11:31
--}%

<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
    <title><g:message code="default.create.label" args="[entityName]" /></title>
</head>
<body>
<g:sidebar/>

<div id="create-user" class="content" role="main">
    <h3 class="content-header">회원가입</h3>
    <div class="panel panel-default panel-margin-10">
        <div class="panel-body panel-body-content text-center">
            <p class="lead"><strong>${email}</strong> 로 요청하신 계정정보를 보냈습니다.</p>
            <p>해당 이메일을 확인 하시고, 비밀번호 변경이 필요하신 경우 해당 이메일을 통해 변경 가능합니다..</p>
            <p><strong>※ 서비스에 따라 스팸으로 분류 되있을 수도 있습니다. 스팸함도 꼭 확인해 주시기 바랍니다.</strong></p>

            <g:link controller="login" action="auth" class="btn btn-primary">로그인</g:link>

        </div>
    </div>
</div>
</body>
</html>
