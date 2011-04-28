[#-- Include: Global --]
[#include "/templating-kit/paragraphs/teasers/init.inc.ftl"]


[#-------------- RENDERING PART --------------]


[#-- Rendering: Shopping Cart --]
<div class="${divClass}" ${divID} >
    <${headingLevel}>${i18n['confirmationPage.title']!}</${headingLevel}>

    <p>${i18n.get('confirmationPage.text', [ctx.cartId])}</p>
    
</div><!-- end ${divClass} -->



