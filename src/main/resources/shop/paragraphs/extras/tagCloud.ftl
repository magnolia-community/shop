
[#-------------- INCLUDE AND ASSIGN PART --------------]

[#-- Assigns: General --]
[#include "/templating-kit/components/teasers/init.inc.ftl"]

[#-- Assigns: Get and check tags --]
[#assign categories = cmsfn.asContentMapList(model.tagCloud!)]
[#assign has_categories = categories?has_content]
[#assign catCloudTitle = content.catCloudTitle!]

[#-- Assigns: Macro for Item iteration --]
[#macro assignItemValues item]
    [#-- Assigns: Get Content from List Item--]
    [#assign itemName = item.@name]
    [#assign itemDisplayName = item.displayName!itemName]
    [#assign itemLevel = item.level]
    [#assign itemLink = model.getProductListLink(itemName, item.displayName!)]
    [#assign itemNumberOfItems = model.getNumberOfItemsTaggedWith(item.@uuid)]
[/#macro]

[#-------------- RENDERING PART --------------]

[#-- Rendering: Category Cloud --]

<div class="${divClass}" ${divID}>
    [#if has_categories]
        [#if catCloudTitle?has_content]<${def.headingLevel!"h3"}>${catCloudTitle}</${def.headingLevel!"h3"}>[/#if]
        <ul>
            [#list categories as item]
                [#-- Macro: Item Assigns --]
                [@assignItemValues item=item /]
                [#if itemNumberOfItems > 0]
                <li>
                    <a href="${itemLink}" >${itemDisplayName} [${itemNumberOfItems}]</a>
                </li>
                [/#if]
            [/#list]
        </ul>
    [/#if]
</div>




