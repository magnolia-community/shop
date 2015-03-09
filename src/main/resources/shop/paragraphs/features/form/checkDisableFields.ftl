[#assign formStateValsMap = model.formStateValues!]

<script type="text/javascript">

var map = {};

[#if formStateValsMap?has_content]
    [#assign keys = formStateValsMap?keys]
    [#list keys as key]
        [#if key?matches('^billing.*')]
            map["${key?replace('^billing', 'shipping', 'r')}"] = "${formStateValsMap[key]}";
        [/#if]
    [/#list]
[/#if]

var jq=jQuery.noConflict();

function toggleStatus() {
    if (jq('#${content.controlName!}').is(':checked')) {
        jq('#main input[type=text]').parent().hide('slow');
        jq('#main textarea').parent().hide('slow');
        for (var key in map) {
            if (document.body.contains(document.getElementById(key))) {
                document.getElementById(key).value = map[key];
            }
        }
    } else {
    	jq('#main input[type=text]').parent().show('slow');
    	jq('#main textarea').parent().show('slow');
        for (var key in map) {
            if (document.body.contains(document.getElementById(key))) {
                document.getElementById(key).value = "";
            }
        }
    }   
}

jq(document).ready(function() {

	toggleStatus();
});

</script>
<div ${model.style!}>
    
    [#if content.title?has_content]
        <label for="${content.controlName!''}">
            <span>
            [#if !model.isValid()]
                <em>${i18n['form.error.field']}</em>
            [/#if]
            ${content.title!}
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

