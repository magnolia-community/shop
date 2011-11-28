[#assign cms=JspTaglibs["cms-taglib"]]
[#-- Include: Global --]
[#-------------- ASSIGNS ---------------------]

[#assign shoppingCart = model.getShoppingCart()!]
[#assign currencyTitle = model.currencyTitle!]
[#assign currencyFormatting = model.currencyFormatting!"00.00"]
[#-------------- RENDERING PART --------------]

[#-- Rendering: Shopping Cart --]
[#if shoppingCart?has_content && shoppingCart.getCartItemsCount() > 0]
	<li ${mgnl.editMode?string("style=\"float: none; display: block;\"","")}>
        [#if mgnl.editMode]
            <div style="width: 103px">[@cms.editBar /]</div>
        [/#if]
		<div class="${divClass!}" ${divID!} >
		    <a href="${model.shoppingCartLink!}" class="cart_link">(${shoppingCart.cartItemsCount}) ${content.linkTitle!i18n['shoppingCart.view']}</a>
		</div><!-- end ${divClass!} -->
	</li>
[#else]
	<li ${mgnl.editMode?string("style=\"float: none; display: block;\"","")}>
        [#if mgnl.editMode]
            <div style="width: 103px">[@cms.editBar /]</div>
        [/#if]
		<div class="empty_cart">(0) ${content.linkTitle!i18n['shoppingCart.view']}</div>
	</li>
[/#if]
