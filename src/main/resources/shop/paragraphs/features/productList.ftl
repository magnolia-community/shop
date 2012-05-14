[#-- Include: Global --]
[#include "/templating-kit/components/teasers/init.inc.ftl"]
[#include "/shop/paragraphs/macros/addForm.ftl"]
[#include "/shop/paragraphs/macros/productListMacro.ftl"]
[#include "/templating-kit/components/macros/pagination.ftl"]


[#assign pager=model.pager!]
[#assign productList = pager.pageItems!]

[#if pager?exists]
	[#assign count=pager.count]
[#else]
	[#assign count=0]
[/#if]

[#-- Rendering Part --]

<div class="${divClass}" ${divID} >
		
	[#-- Macro: Pager --]
	[@pagination pager "top" /]
	
	<em>${count} ${(count>1)?string(i18n['productList.products.found'],i18n['productList.product.found'])}</em>
	
	[@productListMacro productList=productList/]
</div>

[#-- Macro: Pager --]
[@pagination pager "bottom" /]
