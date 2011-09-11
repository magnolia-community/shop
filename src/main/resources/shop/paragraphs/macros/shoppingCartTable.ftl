[#macro shoppingCartTable shoppingCart type]
	[#if !shoppingCart?has_content || shoppingCart.getCartItemsCount() == 0]
	    <p>${i18n['shoppingcart.empty']}</p>
	    [#else]
	    <table cellspacing="1" cellpadding="1" border="0" width="100%" class="shopping_cart">
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
	    				<td>
	    					<a href="${productLink}">${product.productTitle}</a>
							[#if product.options?has_content]
		    					[#assign keys = product.options?keys]
								[#list keys as key]
									<span class="option">[#assign option = mgnl.getContentByUUID("data", product.options[key].valueUUID)!]
									[#assign option = mgnl.i18n(option) /]
									[#assign optionSet = mgnl.i18n(option?parent)]
									<span class="label">${optionSet.title}:</span> <span class="value">${option.title}</span>[#if key_has_next], [/#if]</span>
								[/#list]
							[/#if]
	    				</td>
	    			[#else]
	    				<td>
	    					${product.productTitle}
							[#if product.options?has_content]
		    					[#assign keys = product.options?keys]
								[#list keys as key]
									<span class="option">[#assign option = mgnl.getContentByUUID("data", product.options[key].valueUUID)!]
									[#assign option = mgnl.i18n(option) /]
									[#assign optionSet = mgnl.i18n(option?parent)]
									<span class="label">${optionSet.title}:</span> <span class="value">${option.title}</span>[#if key_has_next], [/#if]</span>
								[/#list]
							[/#if]
						</td>
	    			[/#if]
	    			<td>${product.quantity}</td>
	    			<td>${product.unitPrice?string("0.00")}</td>
	    			<td>${product.itemTotal?string("0.00")}</td>
	    			[#if type=="cart"]
	    			  <td>
	    			  	<a class="product-add-more" href="${model.getCommandLink('add', product.productUUID, product_index)}"><img class="shopAddRemove" src="${ctx.contextPath}/.resources/images/add.gif" /></a> 
	    			  	<a class="product-substract" href="${model.getCommandLink('substract', product.productUUID, product_index)}"><img class="shopAddRemove" src="${ctx.contextPath}/.resources/images/remove.gif" /></a>
	    			  	<a class="product-removeall" href="${model.getCommandLink('removeall', product.productUUID, product_index)}"><img class="shopAddRemove" src="${ctx.contextPath}/.resources/images/removeAll.gif" /></a>
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