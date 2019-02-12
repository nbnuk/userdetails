<!doctype html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.skin.layout}"/>
    <meta name="section" content="home"/>
    <title>User Administration | ${grailsApplication.config.skin.orgNameLong}</title>
    <asset:stylesheet src="application.css" />
</head>
<body>
    <div class="row">
        <div class="col-md-12" id="page-body" role="main">
            <h1>User Administration</h1>
            <ul class="userdetails-menu">
                <li><g:link class="btn btn-default" controller="user" action="list">Find a user</g:link></li>
                <li><g:link class="btn btn-default" controller="admin" action="resetPasswordForUser">Reset user password</g:link></li>
                <li><g:link class="btn btn-default" controller="role" action="list">Roles</g:link></li>
                <li><g:link class="btn btn-default" controller="authorisedSystem" action="list">Authorised systems</g:link></li>
                <li><g:link class="btn btn-default" controller="admin" action="bulkUploadUsers">Bulk create user accounts</g:link></li>
                <li><g:link class="btn btn-default" controller="admin" action="exportUsers">Export users to CSV file</g:link></li>
                <li><g:link class="btn btn-default" controller="alaAdmin" action="index">Atlas admin page</g:link></li>
            </ul>
        </div>
    </div>
</body>
</html>
