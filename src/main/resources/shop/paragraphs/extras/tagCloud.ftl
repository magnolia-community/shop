
[#-------------- INCLUDE AND ASSIGN PART --------------]

[#-- Assigns: General --]
[#assign cms=JspTaglibs["cms-taglib"]]
[#include "/templating-kit/paragraphs/teasers/init.inc.ftl"]

[#-- Assigns: Get and check tags --]
[#assign categories = model.tagCloud!]
[#assign has_categories = categories?has_content]
[#assign catCloudTitle = ""]

[#-- Assigns: Macro for Item iteration --]
[#macro assignItemValues item]
    [#-- Assigns: Get Content from List Item--]
    [#assign itemName = item.@name]
    [#assign itemDisplayName = item.displayName!itemName]
    [#assign itemLevel = item.level]
    [#assign itemLink = model.getProductListLink(itemName)]
[/#macro]

[#-------------- RENDERING PART --------------]

[#-- Rendering: Category Cloud --]

<div class="${divClass}" ${divID}>
    [#-- Macro: Edit Bar --]
    [@editBar /]
    [#if has_categories]
        [#if catCloudTitle?has_content]<${def.headingLevel!"h3"}>${catCloudTitle}</${def.headingLevel!"h3"}>[/#if]
        <ul>
            [#list categories as item]
                [#-- Macro: Item Assigns --]
                [@assignItemValues item=item /]
                <li >
                    <a href="${itemLink}" >${itemDisplayName} [${model.getNumberOfItemsCategorizedWith(item.@uuid)}]</a>
                </li>
            [/#list]
        </ul>
    [/#if]
</div>


[#-------------- ADDITIONAL MACROS --------------]

[#-- Macro: Edit Bar --]
[#macro editBar]
   [@cms.editBar editLabel="${i18n['catCloud.editLabel']}" moveLabel="" /]
[/#macro]

