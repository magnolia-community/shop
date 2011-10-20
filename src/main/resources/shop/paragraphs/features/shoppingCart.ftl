[#-- Include: Global --]
[#include "/templating-kit/paragraphs/teasers/init.inc.ftl"]
[#include "/shop/paragraphs/macros/shoppingCartTable.ftl"]

[#-------------- ASSIGNS ---------------------]

[#assign shoppingCart = model.getShoppingCart()!]
[#-------------- RENDERING PART --------------]


[#-- Rendering: Shopping Cart --]
<div >
    <${headingLevel}>${i18n['shoppingcart.title']}</${headingLevel}>

    [@shoppingCartTable shoppingCart=shoppingCart type="cart"/]
    [#if shoppingCart?has_content && shoppingCart.getCartItemsCount() > 0]
	    <div class="form-wrapper">
			<form method="get" enctype="multipart/form-data" action="${model.getCheckoutFormLink()}">
			<div class="button-wrapper"> 
			<input type="submit" value="${i18n['checkout']}">
			</div>
		</div>
	[/#if]
    
</div><!-- end ${divClass} -->



