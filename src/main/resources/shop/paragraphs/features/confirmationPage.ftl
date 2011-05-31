[#-- Include: Global --]
[#include "/templating-kit/paragraphs/teasers/init.inc.ftl"]


[#-------------- RENDERING PART --------------]

${model.resetShoppingCart()}

[#-- Rendering: Shopping Cart --]
<div class="${divClass}" ${divID} >
    <${headingLevel}>${i18n['confirmationPage.title']!}</${headingLevel}>
	[#if ctx.cartId?has_content]
      <p>${i18n.get('confirmationPage.text', [ctx.cartId])}</p>
    [/#if]
    
</div><!-- end ${divClass} -->



