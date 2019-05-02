<!doctype html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.skin.layout}"/>
    <meta name="section" content="home"/>
    <g:if test="${!alreadyRegistered && edit}">
        <g:set var="title">Edit your account</g:set>
        <meta name="breadcrumbParent" content="${g.createLink(controller: 'profile')},My Profile" />
    </g:if>
    <g:else>
        <g:set var="title">Create your account</g:set>
    </g:else>
    <title>${title}</title>
    <asset:stylesheet src="application.css" />
    <asset:stylesheet src="createAccount.css" />
</head>
<body>

<div class="row">
    <h1>${title}</h1>
    <g:if test="${inactiveUser}">
        <div class="row">
            <div class="col-sm-12">
                <div class="well">
                    <p class="text-danger">A user is already registered with the email address <strong>${params.email}</strong> however it is currently disabled.
                    </p>

                    <p>
                        If you think this is an error or you disabled your account please contact <a
                            href="mailto:${grailsApplication.config.supportEmail}">${grailsApplication.config.supportEmail}</a>.
                    </p>
                </div>
            </div>
        </div>
    </g:if>
    <g:elseif test="${alreadyRegistered}">
        <div class="row">
            <div class="col-sm-12">
                <div class="well">
                    <p class="text-danger">A user is already registered with the email address <strong>${params.email}</strong>.</p>

                    <p>
                        To login with this user name, <a
                            href="${grailsApplication.config.security.cas.loginUrl}">click here</a>.<br/>
                        To start the process of resetting your password, <g:link controller="registration"
                                                                                 action="forgottenPassword"
                                                                                 params="${[email: params.email]}">click here</g:link>.
                    </p>
                </div>
            </div>
        </div>
    </g:elseif>

    <div class="row">
        <div class="col-md-8 col-md-push-4">
            <div class="well">
                <g:if test="${!edit}">
                    <p>
                        To create your new account, fill in the fields opposite and click "Create".
                    </p>
                </g:if>
                <p>
                    In the Primary and Secondary Usage fields you can enter your own text to describe your
                    intended usage of the site. Examples include: "Amateur naturalist", "Photographer", "Ecologist".
                </p>
                <p>
                    For the Atlas' policy on the collection and use of personal information see our
                    <a href="${grailsApplication.config.privacyPolicy}">Privacy Policy</a>.
                </p>
                <p>
                    Your email address will be your NBN Atlas account login.
                    <g:if test="${!edit}">
                        An &quot;account activation&quot; link will be
                        emailed to the address provided. You need click this link, in order to complete the
                        registration process. Note, you may need to check you spam/junk mail folder, as activation emails
                        sometimes get caught by mail filters.
                    </g:if>
                </p>
            </div>
        </div>
        <div class="col-md-4 col-md-pull-8">
            <div>
            <g:form name="updateAccountForm" method="POST" action="${edit ? 'update' : 'register'}" controller="registration" useToken="true" onsubmit="updateAccountSubmit.disabled = true; return true;">
                <div class="form-group">
                    <label for="firstName">First name</label>
                    <input id="firstName" name="firstName" type="text" class="form-control" value="${user?.firstName}" data-validation-engine="validate[required]"/>
                </div>
                <div class="form-group">
                    <label for="lastName">Last name</label>
                    <input id="lastName" name="lastName" type="text" class="form-control" value="${user?.lastName}"  data-validation-engine="validate[required]"/>
                </div>
                <div class="form-group">
                    <label for="email">Email address</label>
                    <input id="email" name="email" type="text" class="form-control" value="${user?.email}"
                           data-validation-engine="validate[required,custom[email]]"
                           data-errormessage-value-missing="Email is required!"
                    />
                </div>

                <g:if test="${!edit}">
                    <div class="form-group">
                    <label for="password">Password</label>
                    <input id="password"
                           name="password"
                           class="form-control"
                           value=""
                           data-validation-engine="validate[required, minSize[8]]"
                           data-errormessage-value-missing="Password is required!"
                           type="password"
                    />
                    </div>
                    <div class="form-group">
                    <label for="reenteredPassword">Reentered password</label>
                    <input id="reenteredPassword"
                           name="reenteredPassword"
                           class="form-control"
                           value=""
                           data-validation-engine="validate[required, minSize[8]]"
                           data-errormessage-value-missing="Password is required!"
                           type="password"
                    />
                    </div>
                </g:if>
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
                    <g:select id="state" name="state"
                              class="form-control"
                              value="${props?.state}"
                              keys="${stateMap?.keySet()}"
                              from="${stateMap?.values()}"
                    />
                </div>
                <div class="form-group">
                    <label for="primaryUserType">Primary usage</label>
                    <input id="primaryUserType" name="primaryUserType" type="text" class="form-control usageAuto"
                           value="${props?.primaryUserType?:''}"
                           data-validation-engine="validate[required]"
                    />
                </div>
                <div class="form-group">
                    <label for="secondaryUserType">Secondary usage</label>
                    <input id="secondaryUserType" name="secondaryUserType" type="text"  class="form-control usageAuto"
                           value="${props?.secondaryUserType?:''}"
                           data-validation-engine="validate[required]"
                    />
                </div>
                <g:if test="${edit}">
                    <button id="updateAccountSubmit" class="btn btn-primary">Update account</button>
                    <button id="disableAccountSubmit" class="btn btn-danger">Disable account</button>
                </g:if>
                <g:else>
                    <button id="updateAccountSubmit" class="btn btn-primary">Create account</button>
                </g:else>
            </g:form>
            </div>
            <g:if test="${flash.invalidToken}">
                Please don't click the button twice.
            </g:if>
        </div>
   </div>
</div>
</body>
<asset:javascript src="createAccount.js" asset-defer="" />
<asset:script type="text/javascript">
    $(function(){

        //$('.typeahead').typeahead();
        var usageOptions = [
            "Amateur naturalist","Amateur photographer","Biodiversity Research","Biogeographer",
            "Biologist","Botanist","Bush Regenerator","BushCare leader","Citizen scientist","Collection manager",
            "Collection technician","Communications","Conservation Planner","Consultant","Data manager",
            "Database Manager","Eco Tourism","Ecologist","Education","Education programs developer","Entomologist",
            "Environmental Officer","Environmental Scientist","Farming","Field Researcher","Forester","Geochemist",
            "GIS visualisation","Identification","IT specialist","Land manager","Land owner","Librarian","Mycologist",
            "Naturalist","Observer","Park Ranger","Pest control","Pest Identification","PhD Student","Policy developer",
            "Predicting distribution","Researcher","Science communicator","Scientific Illustrator","Scientist",
            "Student","Taxonomist","Teacher","Veterinary Pathologist","Volunteer","Volunteer Digitizer","Writer",
            "Zoologist"
        ];

        $(".usageAuto").autocomplete(usageOptions, {});
        $('#updateAccountForm').validationEngine('attach', { scroll: false });
        $("#updateAccountSubmit").click(function(e) {

            $("#updateAccountSubmit").attr('disabled','disabled');

            var pm = $('#password').val() == $('#reenteredPassword').val();
            if(!pm){
                alert("The supplied passwords do not match!");
            }

            var valid = $('#updateAccountForm').validationEngine('validate');

            if (valid && pm) {
                $("form[name='updateAccountForm']").submit();
            } else {
                $('#updateAccountSubmit').removeAttr('disabled');
                e.preventDefault();
            }
        });

        $("#disableAccountSubmit").click(function(e) {

            $("#disableAccountSubmit").attr('disabled','disabled');

            var valid = confirm("${message(code: 'default.button.delete.user.confirm.message', default: "Are you sure want to disable your account? You won't be able to login again. You will have to contact support@ala.org.au in the future if you want to reactivate your account.")}");

            if (valid) {
                $('#updateAccountForm').validationEngine('detach');
                $("form[name='updateAccountForm']").attr('action','disableAccount');
                $("form[name='updateAccountForm']").submit();
            } else {
                $('#disableAccountSubmit').removeAttr('disabled');
                e.preventDefault();
            }
        });



    });
</asset:script>
</html>
