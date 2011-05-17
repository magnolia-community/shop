[#-- Include: Global --]
[#include "/templating-kit/paragraphs/teasers/init.inc.ftl"]
[#include "/shop/paragraphs/macros/addForm.ftl"]
[#include "/shop/paragraphs/macros/productListMacro.ftl"]
[#include "/templating-kit/paragraphs/macros/pagination.ftl"]


[#assign category=model.category!]
[#assign pager=model.pager!]
[#assign productList = pager.pageItems!]

[#-- Rendering Part --]

<div class="${divClass}" ${divID} >
	[@cms.editBar /]
    <${headingLevel}>
    	[#if ctx.type?has_content && ctx.type = "offers"]
    		${i18n['productList.currentOffers']}
    	[#else]
    		${productList?size} ${i18n['productList.products.found']}
    	[/#if]
    </${headingLevel}>
    [#-- Macro: Pager --]
	[@pagination pager "top" /]
	[@productListMacro productList=productList/]
</div>

[#-- Macro: Pager --]
[@pagination pager "bottom" /]
