[#-- Include: Global --]
[#include "/templating-kit/paragraphs/teasers/init.inc.ftl"]

[#-------------- ASSIGNS ---------------------]

[#assign shoppingCart = model.getShoppingCart()!]
[#-------------- RENDERING PART --------------]


[#-- Rendering: Shopping Cart --]
<div class="${divClass}" ${divID} >
    <${headingLevel}>${i18n['shoppingcart.title']}</${headingLevel}>

    [#if !shoppingCart?has_content || shoppingCart.getCartItemsCount() == 0]
    <p>${i18n['shoppingcart.empty']}</p>
    [#else]
    <ul>
    	[#list shoppingCart.getCartItems() as product]
    		<li>
    			${product.quantity}x ${product.productTitle}
    		</li>
    	[/#list]
    	
    </ul>
    <p>${i18n['shoppingCart.total']} ${shoppingCart.grossTotal} ${i18n['shoppingCart.ext.vat']}</p>
    [/#if]
    [#if shoppingCart?has_content && shoppingCart.getCartItemsCount() > 0]
    <p>
    <em class="more"><a href="${model.getShoppingCartLink()}" >${i18n['shoppingCart.view']}</a></em>
    </p>
    [/#if]
</div><!-- end ${divClass} -->


