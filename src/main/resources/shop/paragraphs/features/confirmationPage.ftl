[#assign previousCart = model.previousShoppingCart /]
[#-------------- RENDERING PART --------------]




<div >
    <${def.headingLevel}>${i18n['confirmationPage.title']!}</${def.headingLevel}>
	[#if previousCart?has_content]
      <p>${i18n.get('confirmationPage.text', [previousCart.name])}</p>
    [/#if]
    
</div>



