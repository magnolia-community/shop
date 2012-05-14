
[#-------------- INCLUDE AND ASSIGN PART --------------]

[#-- Assigns: General --]
[#include "/templating-kit/components/teasers/init.inc.ftl"]

[#assign link = model.productListLink]
[#-------------- RENDERING PART --------------]

[#-- Rendering: Product Search --]

<div class="${divClass}" ${divID}>
    [#-- Macro: Edit Bar --]
    <h6>${i18n['productSearch.accesibility.title']}</h6>
    <form action="${link}" >
        <input id="searchbar" name="queryProductsStr" type="text" value="" />
        <input class="button" type="submit" value="${i18n['productSearch.title']}" />
    </form>
</div>




