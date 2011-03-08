[#if model.navigation.showVerticalNavigation]
    <div id="nav">
        <div id="nav-box">
            <h6>${i18n['structural.subnavigation']}</h6>
            [@renderNavigation navigation=model.productCategoryNavigation /]
        </div><!-- end nav-box -->
    </div><!-- end nav -->
[/#if]

[#macro renderNavigation navigation]
    [#if navigation.items?has_content]
        <ul>
        [#list navigation.items as item]
            [#if item.open && item.selected]
                [#assign cssClass = " class=\"open on\""]
            [#elseif item.open]
                [#assign cssClass = " class=\"open\""]
            [#elseif item.selected]
                [#assign cssClass = " class=\"on\""]
            [#else]
                [#assign cssClass = ""]
            [/#if]

            <li${cssClass}>
                [#if item.selected]
				    [#assign title]
				        <span><em>${i18n['nav.selected']}</em>${item.navigationTitle}</span>
				    [/#assign]
				    [#if content.@uuid != item.content.@uuid]
				        <a href="${item.href}">${title}</a>
				    [#else]
				        <strong>${title}</strong>
				    [/#if]
                [#else]
                    <a href="${item.href}">${item.navigationTitle}</a>
                [/#if]
                [#if item.open]
                    [@renderNavigation navigation=item /]
                [/#if]
            </li>
        [/#list]
        </ul>
    [/#if]
[/#macro]
