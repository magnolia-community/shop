[#-- Include: Global --]
[#include "/templating-kit/paragraphs/teasers/init.inc.ftl"]
[#include "/shop/paragraphs/macros/addForm.ftl"]
[#include "/shop/paragraphs/macros/productListMacro.ftl"]
[#include "/templating-kit/paragraphs/macros/pagination.ftl"]


[#assign pager=model.pager!]
[#assign productList = pager.pageItems!]

[#if pager?exists]
	[#assign count=pager.count]
[#else]
	[#assign count=0]
[/#if]

[#-- Rendering Part --]

<div class="${divClass}" ${divID} >
	[@cms.editBar /]
		
	[#-- Macro: Pager --]
	[@pagination pager "top" /]
	
	<em>${count} ${(count>1)?string(i18n['productList.products.found'],i18n['productList.product.found'])}</em>
	
	[@productListMacro productList=productList/]
</div>

[#-- Macro: Pager --]
[@pagination pager "bottom" /]
