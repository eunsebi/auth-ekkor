%{--
  Created by IntelliJ IDEA.
  User: eunse
  Date: 2019-02-07
  Time: 오후 10:49
--}%

<%@ page contentType="text/html;charset=utf-8" %>
<html>
<head>
    <meta name="layout" content="main_with_banner"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><g:message code="default.create.label" args="[entityName]" /></title>
</head>

<body>
<g:sidebar/>

%{--<g:banner type="MAIN" />--}%

<div id="index" class="content scaffold-list clearfix" role="main">
    <iframe width="1300" height="900" src="https://ekkor.xyz/pay/home/payMain.do?email=${pay}" name="test" id="test" frameborder="0" scrolling="auto" align="middle">
        이 브라우저는 iframe을 지원하지 않습니다.</iframe>
</div>
<content tag="script">
    <script>
        $(function () {
            $('.timeago').timeago();
        });
    </script>
</content>
</body>
</html>