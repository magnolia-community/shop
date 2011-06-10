[#-- Assigns: Get Content --]
[#include "/shop/paragraphs/macros/addForm.ftl"]
[#include "/templating-kit/paragraphs/teasers/init.inc.ftl"]

[#assign maxImages = content.maxImages!6]

[#assign product = model.product!]
[#assign category=model.category!]

[#if product?has_content]
	[#assign asset = stk.getAsset(product, 'image')!]
	[#assign description1 = product.productDescription1!]
	[#assign description2 = product.productDescription2!]
	[#assign productTitle = product.title!]
	[#assign bean = model.getProductPriceBean(product)]
	
	[#assign noPicLink = ctx.contextPath + "/docroot/shop/images/box.gif"]
	
	[#-- Rendering Part --]
	<h1>[#if category?has_content]<em>${category.title}</em>[/#if]
	    ${productTitle}
	</h1>
	<div class="${divClass}" ${divID}>
	<p>${description1}</p>
	<p>${description2}</p>
	<div class="product-price-container">
		<div class="product-price">
			[#assign price=bean.price?number?string("0.00")]
			${i18n.get('price.detail.text', [price, bean.currency])}
		</div>
		<div class="product-add">
	  		[@addForm product=product model=model/]
	  	</div>
  	</div>
</div>
	[#if model.images??]
    <div class="photo-index">
        [#list model.images as image]
            [#if image_index < maxImages]
                [#if image.link?has_content]
                    [#assign class][#if (image_index+1)%3==0]photo last[#else]photo[/#if][/#assign]

                    <dl class="${class}">
                        <dt ><img src="${stk.getAssetVariation(image, 'gallery-thumbnail').link}" alt="${image.title}"/></dt>
                        <dd class="zoom"><a href="${stk.getAssetVariation(image, 'gallery-zoom').link}" rel="showbox" title="${i18n['link.zoom.title']}">${i18n['link.zoom']}</a></dd>
                        	<dd class="caption">${i18n['image.zoom']}</dd>
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
        
    </div><!-- end photo-index -->

[/#if]
		
[/#if]