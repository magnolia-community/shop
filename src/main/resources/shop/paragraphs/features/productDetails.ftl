[#-- Assigns: Get Content --]
[#include "/shop/paragraphs/macros/addForm.ftl"]
[#include "/templating-kit/components/teasers/init.inc.ftl"]

[#assign maxImages = content.maxImages!6]

[#assign product = cmsfn.asContentMap(model.product)!]

[#if product?has_content]
	[#assign asset = stkfn.getAsset(product, 'image')!]
	[#assign description1 = product.productDescription1!]
	[#assign description2 = product.productDescription2!]
	[#assign productTitle = product.title!]
	[#assign bean = model.getProductPriceBean(cmsfn.asJCRNode(product))]
	[#assign optionSets = model.getOptionSets(cmsfn.asJCRNode(product))]
	[#assign images = model.images!]
	
	[#assign noPicLink = ctx.contextPath + "/docroot/shop/images/box.gif"]
	[#-- Rendering Part --]
	<h1>
	    ${productTitle}
	</h1>
	<div class="${divClass}" ${divID}>
	<p>${description1}</p>
	<p>${description2}</p>
	<div class="product-price-container">
		<div class="product-price">
			[#assign price=bean.price]
			${i18n.get('price.detail.text', [bean.currency, price])}
		</div>
		<div class="product-add">
	  		[@addForm product=product model=model/]
	  	</div>
  	</div>
</div>
    <div class="photo-index">
	    [#if images?has_content]
	        [#list images as image]
	            [#if image_index < maxImages]
	                [#if image.link?has_content]
	                    [#assign class][#if (image_index+1)%3==0]photo last[#else]photo[/#if][/#assign]
	
	                    <dl class="${class}">
	                        <dt ><img src="${stkfn.getAssetVariation(image, 'gallery-thumbnail').link}" alt="${image.title}"/></dt>
	                        <dd class="zoom"><a href="${stkfn.getAssetVariation(image, 'gallery-zoom').link}" rel="showbox" title="${i18n['link.zoom.title']}">${i18n['link.zoom']}</a></dd>
	                        	
	                            <dd class="caption">${image.caption!"&nbsp;"}</dd>
	                        
	                        [#if image.copyright?has_content]
	                            <dd class="copyright">${image.copyright}</dd>
	                        [/#if]
	                        [#if image.description?has_content]
	                            <dd class="longdesc">${image.description}</dd>
	                        [/#if]
	                    </dl>
	                [#else]
	
	                [/#if]
	                [/#if]
	        [/#list]
	    [#elseif asset?has_content]
	    	<dl class="photo">
                <dt ><img src="${stkfn.getAssetVariation(asset, 'gallery-thumbnail').link}" /></dt>
                <dd class="zoom"><a href="${stkfn.getAssetVariation(asset, 'gallery-zoom').link}" rel="showbox" title="${i18n['link.zoom.title']}">${i18n['link.zoom']}</a></dd>
                <dd class="caption">&nbsp;</dd>
            </dl>
            
        [/#if]
    </div><!-- end photo-index -->


		
[/#if]