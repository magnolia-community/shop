
[#include "/shop/paragraphs/macros/shoppingCartTable.ftl"]
[#-------------- ASSIGNS ---------------------]

[#assign shoppingCart = model.getShoppingCart()!]

[#if actionResult == "go-to-first-page"]
	<div class="text">
		${i18n.get("form.user.errorMessage.go-to-first-page", [cmsfn.createLink("website", model.view.firstPage)])}
	</div>
[#elseif actionResult == "success"]
    <div class="text success">
        <h1>${model.view.successTitle!i18n['form.default.successTitle']}</h1>
        <p>${model.view.successMessage!}</p>
    </div>
[#elseif actionResult == "session-expired"]
	<div class="text error">
		${i18n.get("form.user.errorMessage.session-expired", [cmsfn.createLink("website", model.view.firstPage)])}
	</div>
[#elseif actionResult == "failure"]
	<div class="text error">
		<ul>
			<li>${model.view.errorMessage}</li>
		</ul>
	</div>
[#else]
    [#if model.view.validationErrors?size > 0]
        <div class="text error">
            <h1>${model.view.errorTitle!i18n['form.default.errorTitle']}</h1>
            <ul>
                [#assign keys = model.view.validationErrors?keys]
                [#list keys as key]
                    <li>
                        <a href="#${key}_label">${model.view.validationErrors[key]}</a>
                    </li>
                [/#list]
            </ul>
        </div>
    [/#if]
    
    <div class="text">
        <h1>${content.formTitle!}</h1>
        <p>${content.formText!}</p>
    </div>
    	
    <div class="form-wrapper" >
        <form id="${content.formName?default("form0")}" method="post" action="" enctype="${def.parameters.formEnctype?default("multipart/form-data")}" >
            <div class="form-item-hidden">
				<input type="hidden" name="mgnlModelExecutionUUID" value="${content.@uuid}" />
				[#if model.formState?has_content]
					<input type="hidden" name="mgnlFormToken" value="${model.formState.token}" />
				[/#if]
            </div>
            
 
		    [#-- Rendering: Shopping Cart --]
		
		    [@shoppingCartTable shoppingCart=shoppingCart type=""/]
                
            [@cms.area name="fieldsets"/]
            
            [#if cmsfn.editMode]
                <div>[@cms.newBar contentNodeCollectionName="fieldsets"  newLabel="${i18n['form.fieldset.newLabel']}" paragraph="formGroupFields" /]</div>
            [/#if]
        </form>
    </div>
[/#if]