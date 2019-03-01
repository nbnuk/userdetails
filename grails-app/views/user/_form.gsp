<%@ page import="au.org.ala.userdetails.User" %>

<div class="row">
    <div class="col-md-6">
        <div class="form-group fieldcontain ${hasErrors(bean: userInstance, field: 'firstName', 'error')} ">
            <label for="fi rstName">
                <g:message code="user.firstName.label" default="First Name"/>
            </label>
            <g:textField name="firstName" class="form-control" value="${userInstance?.firstName}"/>
        </div>

        <div class="form-group fieldcontain ${hasErrors(bean: userInstance, field: 'lastName', 'error')} ">
            <label for="lastName">
                <g:message code="user.lastName.label" default="Last Name"/>
            </label>
            <g:textField name="lastName" class="form-control" value="${userInstance?.lastName}"/>
        </div>

        <div class="form-group fieldcontain ${hasErrors(bean: userInstance, field: 'email', 'error')} ">
            <label for="email">
                <g:message code="user.email.label" default="Email"/>
            </label>
            <g:textField name="email" class="form-control" value="${userInstance?.email}"/>
        </div>

        <div class="form-group">
            <label for="organisation">Organisation</label>
            <input id="organisation" name="organisation" type="text" class="form-control" value="${props?.organisation}"/>
        </div>

        <div class="form-group">
            <label for="city">City</label>
            <input id="city" name="city" type="text" class="form-control" value="${props?.city}" />
        </div>

        <div class="form-group">
            <label for="state">County</label>
            <g:select id="state" name="state" class="form-control"
                      value="${props?.state}"
                      keys="${stateMap?.keySet()}"
                      from="${stateMap?.values()}"
            />
        </div>

        <div class="form-group">
            <label for="telephone">Telephone</label>
            <input id="telephone" name="telephone" type="text" class="form-control" value="${props?.telephone}" />
        </div>

        <div class="form-group">
            <label for="primaryUserType">Primary usage</label>
            <input id="primaryUserType" name="primaryUserType" type="text" class="form-control"
                   value="${props?.primaryUserType}"
                   data-provide="typeahead"
                   data-items="20"
                   data-source='["Amateur naturalist","Amateur photographer","Biodiversity Research","Biogeographer","Biologist","Botanist","Bush Regenerator","BushCare leader","Citizen scientist","Collection manager","Collection technician","Communications","Conservation Planner","Consultant","Data manager","Database Manager","Eco Tourism","Ecologist","Education","Education programs developer","Entomologist","Environmental Officer","Environmental Scientist","Farming","Field Researcher","Forester","Geochemist","GIS visualisation","Identification","IT specialist","Land manager","Land owner","Librarian","Mycologist ","Naturalist","Observer","Park Ranger","Pest control","Pest Identification","PhD Student","Policy developer","Predicting distribution","Researcher","Science communicator","Scientific Illustrator","Scientist","Student","Taxonomist","Teacher","Veterinary Pathologist","Volunteer","Volunteer Digitizer","Writer","Zoologist"]'
                   data-validation-engine="validate[required]"
            />
        </div>

        <div class="form-group">
            <label for="secondaryUserType">Secondary usage</label>
            <input id="secondaryUserType" name="secondaryUserType" type="text" class="form-control"
                   value="${props?.secondaryUserType}"
                   data-provide="typeahead" data-items="20"
                   data-source='["Amateur naturalist","Amateur photographer","Biodiversity Research","Biogeographer","Biologist","Botanist","Bush Regenerator","BushCare leader","Citizen scientist","Collection manager","Collection technician","Communications","Conservation Planner","Consultant","Data manager","Database Manager","Eco Tourism","Ecologist","Education","Education programs developer","Entomologist","Environmental Officer","Environmental Scientist","Farming","Field Researcher","Forester","Geochemist","GIS visualisation","Identification","IT specialist","Land manager","Land owner","Librarian","Mycologist ","Naturalist","Observer","Park Ranger","Pest control","Pest Identification","PhD Student","Policy developer","Predicting distribution","Researcher","Science communicator","Scientific Illustrator","Scientist","Student","Taxonomist","Teacher","Veterinary Pathologist","Volunteer","Volunteer Digitizer","Writer","Zoologist"]'
                   data-validation-engine="validate[required]"
            />
        </div>

</div>
<div class="col-md-6 well well-lg">
    <div class="fieldcontain ${hasErrors(bean: userInstance, field: 'activated', 'error')} ">
        <div class="checkbox">
            <label>
                <g:checkBox name="activated" value="${userInstance?.activated}"/> Activated
            </label>
        </div>
    </div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'locked', 'error')} ">
    <div class="checkbox">
        <label>
            <g:checkBox name="locked" value="${userInstance?.locked}"/> Locked
        </label>
    </div>
</div>

<hr/>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'userRoles', 'error')} ">
    <label for="userRoles">
        <g:message code="user.userRoles.label" default=" Roles"/>

    </label>

    <table class="table">
        <g:each in="${userInstance?.userRoles ?}" var="u">
            <tr>
                <td><g:link controller="userRole" action="list" params="[role:u?.encodeAsHTML()]">${u?.encodeAsHTML()}</g:link></td>
                <td><g:link controller="userRole"
                            action="deleteRole"
                            class="btn btn-warning btn-xs"
                            params="[userId:u.user.id,role:u.role.role]"
                            onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
                        ${message(code: 'default.button.delete.label', default: 'Delete')}
                    </g:link>
                </td>
            </tr>
        </g:each>
    </table>

    <g:link controller="userRole" action="create"
            class="btn btn-default"
                    params="['user.id': userInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'userRole.label', default: 'UserRole')])}</g:link>

</div>

</div>

</div>