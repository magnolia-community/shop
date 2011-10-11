[#-- Include: Global --]
[#assign cms=JspTaglibs["cms-taglib"]]

[#-------------- RENDERING PART --------------]

${model.resetShoppingCart()}

[#-- Rendering: Shopping Cart --]


<div >
    <${def.headingLevel}>${i18n['confirmationPage.title']!}</${def.headingLevel}>
	[#if ctx.cartId?has_content]
      <p>${i18n.get('confirmationPage.text', [ctx.cartId])}</p>
    [/#if]
    
</div>



