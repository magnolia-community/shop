[#-- Assigns: Get Content --]
[#include "/shop/paragraphs/macros/addForm.ftl"]
[#include "/templating-kit/paragraphs/teasers/init.inc.ftl"]

[#assign maxImages = content.maxImages!6]

[#assign product = model.product!]

[#if product?has_content]
	[#assign asset = stk.getAsset(product, 'image')!]
	[#assign description1 = product.productDescription1!]
	[#assign description2 = product.productDescription2!]
	[#assign productTitle = product.title!]
	[#assign bean = model.getProductPriceBean(product)]
	[#assign optionSets = model.getOptionSets(product)]
	[#assign images = model.images!]
	
	[#assign noPicLink = ctx.contextPath + "/docroot/shop/images/box.gif"]
	[#-- Link to open product edit dialog, dialog only used from website--]
	[#if mgnl.editMode]
	<a  target="_blank" href="${ctx.contextPath}/.magnolia/dialogs/shopProduct.html?mgnlPath=${(product?parent).@handle}&mgnlNode=${product.@name}&mgnlRepository=data&mgnlLocale=${state.locale.language}&mgnlRichE=false&mgnlRichEPaste=" >
	${i18n['edit.product']}</a>
	[/#if]
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
	                        <dt ><img src="${stk.getAssetVariation(image, 'gallery-thumbnail').link}" alt="${image.title}"/></dt>
	                        <dd class="zoom"><a href="${stk.getAssetVariation(image, 'gallery-zoom').link}" rel="showbox" title="${i18n['link.zoom.title']}">${i18n['link.zoom']}</a></dd>
	                        	
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
                <dt ><img src="${stk.getAssetVariation(asset, 'gallery-thumbnail').link}" /></dt>
                <dd class="zoom"><a href="${stk.getAssetVariation(asset, 'gallery-zoom').link}" rel="showbox" title="${i18n['link.zoom.title']}">${i18n['link.zoom']}</a></dd>
                <dd class="caption">&nbsp;</dd>
            </dl>
            
        [/#if]
    </div><!-- end photo-index -->


		
[/#if]