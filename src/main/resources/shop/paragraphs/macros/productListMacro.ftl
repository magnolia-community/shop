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
	[#setting number_format="0.##"]	
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
	                  	<img src="${stk.getAssetVariation(asset, 'teaser').link}"  />
	                  	</a>    
	                  
	                  [#else]
	                  <a  href="${itemLink!}">
	                  	<img src="${noPicLink}"  />
	                  	</a>    
	                  
	                  [/#if]
					  <p>${description1}</p>
					  
					  <p><em class="more"><a href="${itemLink!}">${i18n['link.readmore']} <span> ${productTitle}</span></a></em></p>
					  <p>${i18n.get('price.detail.text', [bean.price, bean.currency, bean.taxIncluded, bean.tax])}</p>
			    	  [@addForm product=product model=model/]
				</li>
			[/#list]
		</ul>
	[/#if]
[/#macro]