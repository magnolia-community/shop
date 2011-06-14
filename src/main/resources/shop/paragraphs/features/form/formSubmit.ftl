[#assign cms=JspTaglibs["cms-taglib"]]

[#assign backButtonText=content.backButtonText!]

<div id="shop-button-navigation">
[@cms.editBar /]
	<div id="shop-button-navigation-next">
    	<input type="submit" value="${content.buttonText!"Submit"?html}" />
    </div>
	[#if backButtonText?has_content]
		<div id="shop-button-navigation-previous">
    		<input id="back-button" type="button" onclick="history.go(-1);return false;" value="${backButtonText?html}" />
    	</div>
    [/#if]	
    
    
</div>
