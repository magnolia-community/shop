[#-- Include: Global --]
[#include "/templating-kit/paragraphs/teasers/init.inc.ftl"]
[#setting number_format="0.##"]

[#-------------- ASSIGNS ---------------------]

[#assign shoppingCart = model.getShoppingCart()!]
[#assign currencyTitle = model.currencyTitle!]
[#-------------- RENDERING PART --------------]


[#-- Rendering: Shopping Cart --]
<div class="${divClass}" ${divID} >
	<div class="shopping-cart-extras">
	    <${headingLevel}>${i18n['shoppingcart.title']}</${headingLevel}>
	
	    [#if !shoppingCart?has_content || shoppingCart.getCartItemsCount() == 0]
	    	<p>${i18n['shoppingcart.empty']}</p>
	    [#else]
		    <ul>
		    	[#list shoppingCart.getCartItems() as product]
		    		<li>
		    			${product.quantity}x ${product.productTitle} ${currencyTitle}
		    		</li>
		    	[/#list]
		    	
		    </ul>
		    <p>${i18n['shoppingCart.total']} ${shoppingCart.grossTotal} ${currencyTitle} ${i18n['shoppingCart.ext.vat']}</p>
	    [/#if]
	    [#if shoppingCart?has_content && shoppingCart.getCartItemsCount() > 0]
		    <p>
		    <em class="more"><a href="${model.getShoppingCartLink()}" >${i18n['shoppingCart.view']}</a></em>
		    </p>
	    [/#if]
</div><!-- end ${divClass} -->


