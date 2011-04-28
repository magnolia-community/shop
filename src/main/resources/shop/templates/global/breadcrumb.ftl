<div id="breadcrumb">
    <h2>${i18n['nav.selected']}</h2>
    <ol>
	    [#list model.breadcrumb as item]
	        [#if item_has_next]
	            <li><a href="${item.href!model.getCategoryLink(item)}">${item.navigationTitle!item.title!item.name}</a></li>
	        [#else]
	            <li><strong>${item.navigationTitle!item.title}</strong></li>
	        [/#if]
	    [/#list]
    </ol>
</div><!-- end breadcrumb -->