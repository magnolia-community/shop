[#macro shoppingCartTable shoppingCart type]
	[#setting number_format="0.##"]
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
	    			<td>${product.unitPrice}</td>
	    			<td>${product.itemTotal}</td>
	    			[#if type=="cart"]
	    			  <td><a class="product-add" href="${model.getCommandLink('add', product.productUUID)}">+</a>|<a class="product-substract" href="${model.getCommandLink('substract', product.productUUID)}">-</a></td>
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
				<td [#if type=="cart"]colspan="2"[/#if]>${i18n['shoppingCart.total']} ${shoppingCart.grossTotalInclTax} ${model.currencyTitle}</td>
			</tr>
	    </tbody> 
	    </table>
	[/#if]
[/#macro]