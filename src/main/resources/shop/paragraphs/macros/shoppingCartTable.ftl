[#macro shoppingCartTable shoppingCart type]
	[#if !shoppingCart?has_content || shoppingCart.getCartItemsCount() == 0]
	    <p>${i18n['shoppingcart.empty']}</p>
	[#else]
		[#assign currencyTitle = model.currencyTitle!]
		[#assign currencyFormatting = model.currencyFormatting]
	    <table cellspacing="1" cellpadding="1" border="0" width="100%" class="shopping_cart">
	    <thead>
	    	<th id="prod-name-col-width">${i18n['table.product']}</th>
	    	<th id="price">${i18n['table.quantity']}</th>
	    	<th id="price">${i18n['table.unitprice']}</th>
	    	<th id="price">${i18n['table.total']}</th>
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
	    			<td id="price">${product.quantity}</td>
	    			<td id="price">${product.unitPrice?string(currencyFormatting!)}</td>
	    			<td id="price">${product.itemTotal?string(currencyFormatting!)}</td>
	    			[#if type=="cart"]
	    			  <td>
	    			  	<a class="product-add-more" href="${model.getCommandLink('add', product.productUUID, product_index)}"><img class="shopAddRemove" src="${ctx.contextPath}/.resources/images/add.gif" /></a> 
	    			  	<a class="product-subtract" href="${model.getCommandLink('subtract', product.productUUID, product_index)}"><img class="shopAddRemove" src="${ctx.contextPath}/.resources/images/remove.gif" /></a>
	    			  	<a class="product-removeall" href="${model.getCommandLink('removeall', product.productUUID, product_index)}"><img class="shopAddRemove" src="${ctx.contextPath}/.resources/images/removeAll.gif" /></a>
	    			  </td>
	    			[/#if] 
	    		</tr>
	    	[/#list]
	    	<tr>
				<td></td>
				<td></td>
				<td>${i18n['shoppingcart.subtotal']}</td>
				<td id="price" > ${shoppingCart.grossTotalExclTax?string(currencyFormatting!)} ${currencyTitle!}</td>
				[#if type=="cart"]<td></td>[/#if]
			</tr>
	    	<tr>
				<td></td>
				<td></td>
				<td>${i18n['vat']}</td>
				<td id="price" > ${shoppingCart.itemTaxTotal?string("0.00")} ${currencyTitle!}</td>
				[#if type=="cart"]<td></td>[/#if]
			</tr>
			<tr>
				<td></td>
				<td></td>
				<td>${i18n['shoppingCart.total']}</td>
				<td id="price" > ${shoppingCart.grossTotalInclTax?string(currencyFormatting!)} ${currencyTitle}</td>
				[#if type=="cart"]<td></td>[/#if]
			</tr>
	    </tbody> 
	    </table>
	[/#if]
[/#macro]