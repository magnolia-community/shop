
[#-------------- INCLUDE AND ASSIGN PART --------------]

[#-- Include: Global --]
[#include "/templating-kit/components/teasers/init.inc.ftl"]

[#-- Assigns: Get and check Teaser Target --]
[#assign target = model.target!]
[#assign hasTarget = target?has_content]

[#-- Assigns: Macro assigning Values --]
[#macro assignValues]

    [#-- Assigns: Get Content --]
    [#assign product = model.product!]
    [#assign productJCRNode = model.getJCRNode(product)!]
    [#assign title = content.teaserTitle!product.title!product.@name]
    [#assign text = content.teaserAbstract!product.productDescription1!]
    [#assign kicker = target.kicker!]
    [#assign teaserLink = model.productDetailPageLink!]
    [#assign hasLinkList = content.hasLinkList!false]

    [#if !hideTeaserImage]
        [#assign imageLink = (model.image!).link!]
        [#if !(imageLink?has_content)]
		    [#assign asset = stkfn.getAsset(productJCRNode, 'image')!]
		    [#assign imageLink = stkfn.getAssetVariation(asset, 'teaser').link]
        [/#if]
        [#if !imageLink?has_content && divIDPrefix="teaser"]  [#-- TODO: This is a hack, solution with imageLink return object must be implemented --]
            [#assign divClass = "${divClass} mod"]
        [/#if]
    [/#if]

    [#-- Assigns: Is Content Available --]
    [#assign hasImageLink = imageLink?has_content]
    [#assign hasText = text?has_content]
    [#assign hasKicker = kicker?has_content]

    [#-- Assigns: Define alt for image tag --]
    [#if hasImageLink]
        [#assign imageAlt = title]
    [#else]
        [#assign imageAlt = "${i18n['image.resolveError']}" ]
    [/#if]
[/#macro]



[#-------------- RENDERING PART --------------]

[#-- Rendering: Teaser Internal Page --]
[#if hasTarget]
    [#-- Macro: Value Assigns --]
    [@assignValues /]

    <div class="${divClass}" ${divID}>

        <${headingLevel}>
            <a href="${teaserLink}">
                [#if hasKicker]<em>${kicker}</em>[/#if]
                ${title}
            </a>
        </${headingLevel}>
        [#if hasImageLink]<a href="${teaserLink}"><img src="${imageLink}" alt="${imageAlt}" /></a>[/#if]
        [#if hasText]<p>${text + " "}<em class="more"><a href="${teaserLink}">${i18n['link.readon']} <span>${title}</span></a></em></p>[/#if]

        [#-- Rendering the teaser's linkList --]
        [@cms.area name="linkList"/]
    </div><!-- end ${divClass} -->

[#else]
    [#if cmsfn.editMode]
        <div class="${divClass}" ${divID}>

            <${headingLevel}><a href="javascript:alert('${i18n['teaser.internal.resolveError']?js_string}')">${i18n['teaser.internal.resolveError']}</a></${headingLevel}>
        </div><!-- end ${divClass} -->
    [/#if]
[/#if]


