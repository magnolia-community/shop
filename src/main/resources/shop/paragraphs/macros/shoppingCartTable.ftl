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
	    		<tr>
	    			<td>${product.productTitle}</td>
	    			<td>${product.quantity}</td>
	    			<td>${product.unitPrice}</td>
	    			<td>${product.itemTotal}</td>
	    			[#if type=="cart"]
	    			  <td><a href="${model.getCommandLink('add', product.productUUID)}">+</a>|<a href="${model.getCommandLink('substract', product.productUUID)}">-</a></td>
	    			[/#if] 
	    		</tr>
	    	[/#list]
	    	<tr>
				<td></td>
				<td></td>
				<td></td>
				<td [#if type=="cart"]colspan="2"[/#if]>${i18n['vat']} ${shoppingCart.itemTaxTotal}</td>
			</tr>
			<tr>
				<td></td>
				<td></td>
				<td></td>
				<td [#if type=="cart"]colspan="2"[/#if]>${i18n['shoppingCart.total']} ${shoppingCart.grossTotalInclTax}</td>
			</tr>
	    </tbody> 
	    </table>
	[/#if]
[/#macro]