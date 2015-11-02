<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'ec3.label', default: 'Ec3')}" />
    <title><g:message code="failure.list.label" /></title>
</head>
<body>
<a href="#list-ec3" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>
<div id="list-ec3" class="content scaffold-list" role="main">
    <g:if test="${flash.error}">
        <div class="errors" role="error"><g:message code="elf.error.instances.lookup" />
        <br>
        <g:message code="elf.contact.administrator" />
        </div>
    </g:if>

    <div class="pagination">
        <g:paginate total="${ec3Count ?: 0}" />
    </div>
</div>
</body>
</html>