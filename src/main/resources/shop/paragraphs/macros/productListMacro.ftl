[#-- Assigns: Macro for Item iteration --]
[#macro assignItemValues product]
    [#-- Assigns: Get Content from List Item--]
    [#assign asset = stk.getAsset(product, 'image')!]
	[#assign description1 = product.productDescription1!]
	[#assign description2 = product.productDescription2!]
	[#assign productTitle = product.title!]
	[#assign itemLink = model.getProductDetailPageLink(product)]
	[#assign bean = model.getProductPriceBean(product)]
    [#assign noPicLink = ctx.contextPath + "/docroot/shop/images/box.gif"]
   	
[/#macro]
[#macro productListMacro productList]
	[#if productList?size > 0]
		<ul>
			[#list productList as product]
				[#-- Macro: Item Assigns --]
	        	[@assignItemValues product=product/]
	        	[#-- Rendering: Item rendering --]
	        	<li>
			    	<h3><a  href="${itemLink!}">${productTitle}</a></h3>
					  <p>${description1}</p>
					  <div class="product-price-container">
					  	<div class="product-price">
					  		[#assign price=bean.price?number?string("0.00")]
					  		${i18n.get('price.detail.text', [price, bean.currency, bean.taxIncluded, bean.tax])}
					  	</div>
			    	  	<div class="product-add">
			    	  		[@addForm product=product model=model/]
			    	  	</div>
			    	  </div>
				</li>
			[/#list]
		</ul>
	[/#if]
[/#macro]