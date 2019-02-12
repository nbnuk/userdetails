<%@ page import="au.org.ala.userdetails.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.skin.layout}">
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
    <meta name="breadcrumbParent" content="${createLink(controller:'admin', action:'index')},Administration" />
</head>

<body>

<div id="list-user" class="content scaffold-list" role="main">

    <div class="row">
        <div class="col-md-4">
            <h1><g:message code="default.list.label" args="[entityName]"/></h1>
        </div>
        <div class="col-md-8">
            <div class="pull-right">
                <g:form class="form-inline" action="list" controller="user" method="get">
                    <g:link class="btn btn-primary" action="create"><i class="fa fa-pencil"></i> <g:message code="default.new.label" args="[entityName]" /></g:link>
                    <div class="input-group">
                        <input type="text" class="form-control" name="q" value="${q?:''}" placeholder="Search for user"/>
                        <span class="input-group-btn">
                            <input type="submit" class="btn btn-default"/>
                        </span>
                    </div>
                </g:form>
            </div>
        </div>
        <div class="col-md-12">

    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table class="table table-bordered table-striped table-condensed">
        <thead>
        <tr>
            <g:sortableColumn property="id" title="${message(code: 'user.id.label', default: 'ID')}"/>

            <g:sortableColumn property="email" title="${message(code: 'user.email.label', default: 'Email')}"/>

            <g:sortableColumn property="firstName"
                              title="${message(code: 'user.firstName.label', default: 'First Name')}"/>

            <g:sortableColumn property="lastName"
                              title="${message(code: 'user.lastName.label', default: 'Last Name')}"/>

            <g:sortableColumn property="activated"
                              title="${message(code: 'user.activated.label', default: 'Activated')}"/>

            <g:sortableColumn property="locked" title="${message(code: 'user.locked.label', default: 'Locked')}"/>

            <g:sortableColumn property="created" title="${message(code: 'user.created.label', default: 'Created')}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${userInstanceList}" status="i" var="userInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show"
                            id="${userInstance.id}">${userInstance.id}</g:link></td>

                <td><g:link action="show"
                            id="${userInstance.id}">${fieldValue(bean: userInstance, field: "email")}</g:link></td>

                <td>${fieldValue(bean: userInstance, field: "firstName")}</td>

                <td>${fieldValue(bean: userInstance, field: "lastName")}</td>

                <td><g:formatBoolean boolean="${userInstance.activated}"/></td>

                <td><g:formatBoolean boolean="${userInstance.locked}"/></td>

                <td>${fieldValue(bean: userInstance, field: "created")}</td>

            </tr>
        </g:each>
        </tbody>
    </table>

    <g:if test="${!q}">
        <div class="pagination">
            <hf:paginate total="${userInstanceTotal}" params="[q:q]"/>
        </div>
    </g:if>
        </div>
    </div>
</div>
</body>
</html>
