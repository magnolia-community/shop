[#-- Assigns: Get Content --]
[#include "/shop/paragraphs/macros/addForm.ftl"]
[#setting number_format="0.##"]

<style type="text/css">
.photo-index .photo {
	background: url(../img/bgs/photo-corners.png) 0 100% no-repeat;
	width: 140px;
}
.photo-index .photo dt {
	min-height: 88px;
	height: auto;
}
.photo-index .photo dt img {
	width: 138px;
}
.photo-index dl {
	margin-right: 15px;
}
.photo-index dl.last {
	margin-right: 0;
}
.photo-index dd {
	font-size: 50%;
}
</style>
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
	<div class="teaser">
	[#if asset?has_content]        
      <a  href="${itemLink!}">
      	<img class="photo" src="${stk.getAssetVariation(asset, 'teaser').link}"  />
      </a>    
      
    [#else]
      <a  href="${itemLink!}">
      	<img src="${noPicLink}"  />
      </a>    
      
    [/#if]
	<p>${description1}</p>
	<p>${description2}</p>
	<p>${i18n.get('price.detail.text', [bean.price, bean.currency, bean.taxIncluded, bean.tax])}</p>
	<p>[@addForm product=product model=model/]</p>
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