[#-- Include: Global --]
[#include "/templating-kit/components/teasers/init.inc.ftl"]
[#-------------- ASSIGNS ---------------------]

[#assign shoppingCart = model.getShoppingCart()!]
[#assign currencyTitle = model.currencyTitle!]
[#assign currencyFormatting = model.currencyFormatting!"00.00"]
[#-------------- RENDERING PART --------------]

[#-- Rendering: Shopping Cart --]
<div class="${divClass}" ${divID} >
	    <${headingLevel}>${i18n['shoppingcart.title']}</${headingLevel}>
	
	    [#if !shoppingCart?has_content || shoppingCart.getCartItemsCount() == 0]
	    	<p>${i18n['shoppingcart.empty']}</p>
	    [#else]
		    <ul class="shopping_cart">
		    	[#list shoppingCart.getCartItems() as product]
		    		<li>
		    			${product.quantity}x ${product.productTitle} [#if product.options?has_content]
		    				[#assign keys = product.options?keys]
							[#list keys as key]
								<span class="option">[#assign option = cmsfn.getContentByUUID("data", product.options[key].valueUUID)!]
								[#assign option = cmsfn.i18n(option) /]
								[#assign optionSet = cmsfn.i18n(option?parent)]
								<span class="label">${optionSet.title}:</span> <span class="value">${option.title}</span>[#if key_has_next], [/#if]</span>
							[/#list][/#if]${product.itemTotal?string(currencyFormatting)} ${currencyTitle}
		    		</li>
		    	[/#list]
		    	
		    </ul>
		    <p>${i18n['shoppingCart.total']} ${shoppingCart.grossTotal?string(currencyFormatting)} ${currencyTitle} ${i18n['shoppingCart.ext.vat']}</p>
	    [/#if]
	    [#if shoppingCart?has_content && shoppingCart.getCartItemsCount() > 0]
		    <p>
		    <em class="more"><a href="${model.shoppingCartLink!}" >${i18n['shoppingCart.view']}</a></em>
		    </p>
	    [/#if]
</div><!-- end ${divClass} -->


