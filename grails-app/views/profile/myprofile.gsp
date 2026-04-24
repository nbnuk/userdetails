<%@ page import="grails.util.Holders" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="${grailsApplication.config.skin.layout}"/>
        <meta name="section" content="home"/>
        <meta name="breadcrumb" content="My Profile"/>
        <title><g:message code="userdetails.my.profile"/> | ${grailsApplication.config.skin.orgNameLong}</title>
        <asset:stylesheet src="application.css"/>
    </head>

    <body>
        <!-- New style -->
        <div id="content">
            <div>
                <div class="row">
                    <div class="col-lg-12">
                        <h1 class="page-header-text"><g:message code="myprofile.hello" args="[user.firstName]" /></h1>
                    </div>
                </div>
                <p/>
                <div class="row">

                    <!-- Column 1 -->
                    <div class="col-lg-4">

                        <!-- User details -->
                        <div class="my-profile-floating-div">
                            <div class="image my-profile-icon">
                                <i class="glyphicon glyphicon-user"></i>
                            </div>
                            <div class="content my-profile-section-content">
                                <h4 id="update-your-details" class="my-profile-sub-header">
                                    <g:link controller="registration" action="editAccount">
                                        <g:message code="myprofile.update" />
                                    </g:link>
                                </h4>
                                <p class="my-profile-content-paragraph"><g:message code="myprofile.update.content" /></p>
                            </div>
                        </div>

                        <!-- Reset password -->
                        <div class="my-profile-floating-div">
                            <div class="image my-profile-icon">
                                <i class="glyphicon glyphicon-lock"></i>
                            </div>
                            <div class="content my-profile-section-content">
                                <h4 id="reset-your-password" class="my-profile-sub-header">
                                    <g:link controller="registration" action="forgottenPassword">
                                        <g:message code="userdetails.index.reset.password" />
                                    </g:link>
                                </h4>
                                <p class="my-profile-content-paragraph"><g:message code="userdetails.index.reset.password.content" /></p>
                            </div>
                        </div>

                        <g:if test="${isAdmin}">
                            <div class="my-profile-floating-div">
                                <div class="image my-profile-icon">
                                    <i class="glyphicon glyphicon-cog"></i>
                                </div>
                                <div class="content my-profile-section-content">
                                    <h4 id="admin-tools" class="my-profile-sub-header">
                                        <g:link controller="admin">
                                            <g:message code="myprofile.admin.tools" />
                                        </g:link>
                                    </h4>
                                    <p class="my-profile-content-paragraph"><g:message code="myprofile.admin.tools" /></p>
                                </div>
                            </div>
                        </g:if>
                    </div>

                    <!-- Column 2 -->
                    <div class="col-lg-4">


                        <!-- Species List -->
                        <div class="my-profile-floating-div">
                            <div class="image my-profile-icon">
                                <i class="glyphicon glyphicon-list"></i>
                            </div>
                            <div class="content my-profile-section-content">
                                <h4 id="species-lists" class="my-profile-sub-header">
                                    <a href="${grailsApplication.config.lists.url}">
                                        <g:message code="myprofile.uploaded.species.lists" />
                                    </a>
                                </h4>
                                <p class="my-profile-content-paragraph"><g:message code="myprofile.uploaded.species.lists.content" /></p>
                            </div>
                        </div>

                        <!-- Annotated records -->
                        <div class="my-profile-floating-div">
                            <div class="image my-profile-icon">
                                <i class="glyphicon glyphicon-pencil"></i>
                            </div>
                            <div class="content my-profile-section-content">
                                <h4 id="records-annotated" class="my-profile-sub-header" >
                                    <a href="${grailsApplication.config.biocache.search.url}%22${user.id}%22">
                                        <g:message code="myprofile.view.records.you.annotated" />
                                    </a>
                                </h4>
                                <p class="my-profile-content-paragraph"><g:message code="myprofile.view.records.you.annotated.message" /></p>
                            </div>
                        </div>

                    </div>

                    <!-- Column 3 -->
                    <div class="col-lg-4">

                        <!-- Manage alerts -->
                        <div class="my-profile-floating-div">
                            <div class="image my-profile-icon">
                                <i class="glyphicon glyphicon-envelope"></i>
                            </div>
                            <div class="content my-profile-section-content">
                                <h4 id="my-alerts" class="my-profile-sub-header">
                                    <a href="${grailsApplication.config.alerts.url}">
                                        <g:message code="myprofile.your.alerts" />
                                    </a>
                                </h4>
                                <p class="my-profile-content-paragraph"><g:message code="myprofile.your.alerts.content" /></p>
                            </div>
                        </div>

                    <!-- Manage Saved Searches -->
                    <div class="my-profile-floating-div">
                        <div class="image my-profile-icon">
                            <i class="fa fa-tags"></i>
                        </div>
                        <div class="content my-profile-section-content">
                            <h4 id="my-saved-searches" class="my-profile-sub-header">
                                <a href="${grailsApplication.config.alerts.url}/savedSearch/mySavedSearches">
                                    <g:message code="myprofile.your.saved.searches" default="Manage your saved searches"/>
                                </a>
                            </h4>
                            <p class="my-profile-content-paragraph"><g:message code="myprofile.your.saved.searches.content" default="View, edit, delete or create saved searches"/></p>
                        </div>
                    </div>


                    </div>
                </div>
            </div>

%{--            <div id="external-linkages" class="row">--}%
%{--    --}%
%{--                <g:if test="${Holders.config.getProperty('oauth.providers.inaturalist.enabled', Boolean, false)}">--}%
%{--                    <div class="col-lg-6">--}%
%{--                        <div class=" well well-small">--}%
%{--                            <div class="d-flex">--}%
%{--                                <div class="image">--}%
%{--                                    <img src="${grailsApplication.config.getProperty('logo.inaturalist')}">--}%
%{--                                </div>--}%
%{--                                <div class="content">--}%
%{--                                    <h4>${grailsApplication.config.getProperty('inaturalist.name')}</h4>--}%
%{--                                </div>--}%
%{--                            </div>--}%
%{--                        </div>--}%
%{--                    </div>--}%
%{--                </g:if>--}%
%{--            </div>--}%
        </div>
    </body>
</html>
