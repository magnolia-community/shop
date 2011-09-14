[#-- Assigns: Macro for Item iteration --]
[#macro assignItemValues product]
    [#-- Assigns: Get Content from List Item--]
    [#assign asset = stk.getAsset(product, 'image')!]
	[#assign description1 = product.productDescription1!]
	[#assign description2 = product.productDescription2!]
	[#assign productTitle = product.title!]
	[#assign itemLink = model.getProductDetailPageLink(product)]
	[#assign bean = model.getProductPriceBean(product)]
	[#assign optionSets = model.getOptionSets(product)]
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
	        		[#-- Link to open product edit dialog, dialog only used from website--]
	        		[#if mgnl.editMode]
	        		<a  target="_blank" href="${ctx.contextPath}/.magnolia/dialogs/shopProduct.html?mgnlPath=${(product?parent).@handle}&mgnlNode=${product.@name}&mgnlRepository=data&mgnlLocale=${state.locale.language}&mgnlRichE=false&mgnlRichEPaste=" >
	        		${i18n['edit.product']}</a>
	        		[/#if]
	        		
			    	<h3><a  href="${itemLink!}">${productTitle}</a></h3>
					  [#if asset?has_content]        
	                  <a  href="${itemLink!}">
	                  	<img src="${stk.getAssetVariation(asset, 'teaser').link}"  />
	                  </a>    
	                  
	                  [#else]
	                  <a  href="${itemLink!}">
	                  	<img src="${noPicLink}"  />
	                  	</a>    
	                  
	                  [/#if]
					  <p>${description1} <em class="more"><a href="${itemLink!}">${i18n['link.readmore']} <span> ${productTitle}</span></a></em></p>
					  <div class="product-price-container">
					  	<div class="product-price">
					  		[#assign price=bean.price!0?number?string("0.00")]
					  		${i18n.get('price.detail.text', [price, bean.currency])}
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