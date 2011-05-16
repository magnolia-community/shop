
[#-------------- INCLUDE AND ASSIGN PART --------------]

[#-- Assigns: General --]
[#assign cms=JspTaglibs["cms-taglib"]]
[#include "/templating-kit/paragraphs/teasers/init.inc.ftl"]

[#assign link = model.productListLink]
[#-------------- RENDERING PART --------------]

[#-- Rendering: Product Search --]

<div class="${divClass}" ${divID}>
    [#-- Macro: Edit Bar --]
    [@editBar /]
    <h6>${i18n['productSearch.accesibility.title']}</h6>
    <form action="${link}" >
        <label for="searchbar">${i18n['productSearch.title']}</label>
        <input id="searchbar" name="queryProductsStr" type="text" value="" />
        <input class="button" type="submit" value="${i18n['productSearch.buttonTitle']}" />
    </form>
</div>


[#-------------- ADDITIONAL MACROS --------------]

[#-- Macro: Edit Bar --]
[#macro editBar]
   [@cms.editBar editLabel="" moveLabel="" /]
[/#macro]

