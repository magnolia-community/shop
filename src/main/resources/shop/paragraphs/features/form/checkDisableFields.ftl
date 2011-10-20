[#assign cms=JspTaglibs["cms-taglib"]]

<script type="text/javascript">
var jq=jQuery.noConflict();

function toggleStatus() {
    if (jq('#${content.controlName!}').is(':checked')) {
        jq('#main input[type=text]').parent().hide('slow');
        jq('#main textarea').parent().hide('slow');
        
    } else {
    	jq('#main input[type=text]').parent().show('slow');
    	jq('#main textarea').parent().show('slow');
    }   
}

jq(document).ready(function() {

	toggleStatus();
});

</script>
<div ${model.style!}>
    [@cms.editBar /]
    
    [#if content.title?has_content]
        <label for="${content.controlName!''}">
            <span>
            [#if !model.isValid()]
                <em>${i18n['form.error.field']}</em>
            [/#if]
            ${mgnl.encode(content).title!}
            [#if content.mandatory!false]
                <dfn title="required">${model.requiredSymbol!}</dfn>
            [/#if]
            </span>
        </label>
    [/#if]
    
    <fieldset >
		[#if content.legend?has_content]
		        <legend>${content.legend}</legend>
		[/#if]
		
        [#if model.value == content.controlValue]
            [#assign checked="checked=\"checked\""]
        [/#if]
        <div class="form-item">
			<input type="checkbox" id="${content.controlName!''}" name="${content.controlName!''}" value="${content.controlValue!}" ${checked!} onchange="javascript:toggleStatus()" />
	        <label for="${content.controlName!''}">${content.controlLabel!}</label>
        </div>
	    
		
    </fieldset>

</div>

