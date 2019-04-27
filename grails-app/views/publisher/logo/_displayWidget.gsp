<g:if test="${bean.logo}">
    <g:img style="width: 80px; length: 80px" uri="data:img/png;base64,${bean.logo?.encodeBase64()}"/>
</g:if>