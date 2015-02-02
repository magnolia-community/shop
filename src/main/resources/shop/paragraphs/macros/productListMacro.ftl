[#-- Assigns: Macro for Item iteration --]
[#macro assignItemValues product]
    [#-- Assigns: Get Content from List Item--]
    [#assign asset = stkfn.getAsset(cmsfn.asJCRNode(product), 'image')!]
	[#assign description1 = product.productDescription1!]
	[#assign description2 = product.productDescription2!]
	[#assign productTitle = product.title!]
	[#assign itemLink = model.getProductDetailPageLink(cmsfn.asJCRNode(product))]
	[#assign bean = model.getProductPriceBean(cmsfn.asJCRNode(product))]
	[#assign optionSets = model.getOptionSets(cmsfn.asJCRNode(product))]
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
					  [#if asset?has_content]        
	                  <a  href="${itemLink!}">
	                  	<img src="${stkfn.getAssetVariation(asset, 'teaser').link}"  />
	                  </a>    
	                  
	                  [#else]
	                  <a  href="${itemLink!}">
	                  	<img src="${noPicLink}"  />
	                  	</a>    
	                  
	                  [/#if]
					  <p>${description1}</p>
					  <p><em class="more"><a href="${itemLink!}">${i18n['link.readmore']} <span> ${productTitle}</span></a></em></p>
					  <div class="product-price-container">
					  	<div class="product-price">
					  		[#assign price=bean.price!]
					  		[#if price?has_content && bean.currency?has_content]
						  		${i18n.get('price.detail.text', [bean.currency, price])}
						  	[/#if]
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