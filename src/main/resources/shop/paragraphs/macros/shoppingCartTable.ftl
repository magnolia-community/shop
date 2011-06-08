[#macro shoppingCartTable shoppingCart type]
	[#if !shoppingCart?has_content || shoppingCart.getCartItemsCount() == 0]
	    <p>${i18n['shoppingcart.empty']}</p>
	    [#else]
	    <table cellspacing="1" cellpadding="1" border="0" width="100%">
	    <thead>
	    	<th>${i18n['table.product']}</th>
	    	<th>${i18n['table.quantity']}</th>
	    	<th>${i18n['table.unitprice']}</th>
	    	<th>${i18n['table.total']}</th>
	    	[#if type=="cart"]
	          <th></th>	
	    	[/#if]
	    </thead>
		<tbody>
	    	[#list shoppingCart.getCartItems() as product]
	    		[#assign productLink = model.getProductDetailPageLink(product.product)!]
	    		<tr>
	    			[#if productLink?has_content]
	    				<td><a href="${productLink}">${product.productTitle}</a></td>
	    			[#else]
	    				<td>${product.productTitle}</td>
	    			[/#if]
	    			<td>${product.quantity}</td>
	    			<td>${product.unitPrice?string("0.00")}</td>
	    			<td>${product.itemTotal?string("0.00")}</td>
	    			[#if type=="cart"]
	    			  <td>
	    			  	<a class="product-add-more" href="${model.getCommandLink('add', product.productUUID)}"><img class="shopAddRemove" src="${ctx.contextPath}/.resources/images/add.gif" /></a> 
	    			  	<a class="product-substract" href="${model.getCommandLink('substract', product.productUUID)}"><img class="shopAddRemove" src="${ctx.contextPath}/.resources/images/remove.gif" /></a>
	    			  	<a class="product-removeall" href="${model.getCommandLink('removeall', product.productUUID)}"><img class="shopAddRemove" src="${ctx.contextPath}/.resources/images/removeAll.gif" /></a>
	    			  </td>
	    			[/#if] 
	    		</tr>
	    	[/#list]
	    	<tr>
				<td></td>
				<td></td>
				<td></td>
				<td [#if type=="cart"]colspan="2"[/#if]>${i18n['shoppingcart.subtotal']} ${shoppingCart.grossTotalExclTax?string("0.00")} ${model.currencyTitle}</td>
			</tr>
	    	<tr>
				<td></td>
				<td></td>
				<td></td>
				<td [#if type=="cart"]colspan="2"[/#if]>${i18n['vat']} ${shoppingCart.itemTaxTotal?string("0.00")} ${model.currencyTitle}</td>
			</tr>
			<tr>
				<td></td>
				<td></td>
				<td></td>
				<td [#if type=="cart"]colspan="2"[/#if]>${i18n['shoppingCart.total']} ${shoppingCart.grossTotalInclTax?string("0.00")} ${model.currencyTitle}</td>
			</tr>
	    </tbody> 
	    </table>
	[/#if]
[/#macro]