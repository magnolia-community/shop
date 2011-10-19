[#-- Include: Global --]
[#include "/templating-kit/paragraphs/teasers/init.inc.ftl"]
[#include "/shop/paragraphs/macros/addForm.ftl"]
[#include "/shop/paragraphs/macros/productListMacro.ftl"]
[#include "/templating-kit/paragraphs/macros/pagination.ftl"]


[#assign category=model.category!]
[#assign pager=model.pager!]
[#assign productList = pager.pageItems!]

[#if pager?exists]
	[#assign count=pager.count]
[#else]
	[#assign count=0]
[/#if]

[#-- Rendering Part --]
<div id="page-intro">
	<h1>
	[#if model.listType != "default"]
		<em>${count} ${(count>1)?string(i18n['productList.products.found'],i18n['productList.product.found'])}</em>
	[/#if]
		${model.title!}
	</h1>
</div>
<div class="${divClass}" ${divID} >
	[@cms.editBar /]
		
	[#-- Macro: Pager --]
	[@pagination pager "top" /]
	
	[@productListMacro productList=productList/]
</div>

[#-- Macro: Pager --]
[@pagination pager "bottom" /]
