/**
 * This file Copyright (c) 2010-2011 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.module.shop.paragraphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.cms.util.SelectorUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.dms.beans.Document;
import info.magnolia.module.shop.ShopConfiguration;
import info.magnolia.module.shop.accessors.ShopAccesor;
import info.magnolia.module.shop.beans.ShoppingCart;
import info.magnolia.module.shop.search.AbstractProductListType;
import info.magnolia.module.shop.search.ProductListTypeCategory;
import info.magnolia.module.shop.util.CustomDataUtil;
import info.magnolia.module.shop.util.ShopProductPager;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;
import info.magnolia.module.templatingkit.STKModule;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.module.templatingkit.navigation.LinkImpl;
import info.magnolia.module.templatingkit.templates.STKTemplateModel;
import info.magnolia.module.templatingkit.templates.components.ImageGalleryParagraphModel;
import info.magnolia.module.templatingkit.util.STKUtil;

/**
 * Shop paragraph model, used on productdetail and productlist paragraphs.
 * 
 * @author tmiyar
 * 
 */
public class ShopParagraphModel extends ImageGalleryParagraphModel {

    private static Logger log = LoggerFactory
    .getLogger(ShopParagraphModel.class);
    private Content siteRoot = null;
    private RenderingModel parent = null;
    protected AbstractProductListType productListType = null;
    
    public ShopParagraphModel(Node content, TemplateDefinition definition,
            RenderingModel parent, STKTemplatingFunctions stkFunctions,
            TemplatingFunctions templatingFunctions, STKModule stkModule) {
        super(content, definition, parent, stkFunctions, templatingFunctions, stkModule);
        this.parent = parent;
        if (parent instanceof STKTemplateModel) {
            this.siteRoot = ContentUtil.asContent(((STKTemplateModel) parent).getSiteRoot());
        }
    }

    public ShoppingCart getShoppingCart() {
        return ShopUtil.getShoppingCart();
    }

    public Node getSiteRoot() {
        return siteRoot.getJCRNode();
    }

    @Override
    public String execute() {
        init();
        return "";
    }

    protected void init() {
            productListType = new ProductListTypeCategory(templatingFunctions, 
                    siteRoot, ContentUtil.asContent(content));
    }



    public ShopProductPager getPager() {
        return productListType.getPager();
    }

    
    /**
     * Gets product selected from the url, using selector.
     * 
     */
    public Content getProduct() {

            Content product;
            try {
                String productId = SelectorUtil.getSelector(2);
                if(StringUtils.isNotEmpty(productId)){
                    product = CustomDataUtil.getProductNode(productId);
                    return new I18nContentWrapper(product);
                }
            } catch (Exception e) {
                //item not found
            }
     
        return null;
    }

    public TemplateProductPriceBean getProductPriceBean(Content product) {
        ShopConfiguration shopConfiguration;
        try {
            shopConfiguration = new ShopAccesor(ShopUtil.getShopName()).getShopConfiguration();
        
            Content priceCategory = ShopUtil.getShopPriceCategory(shopConfiguration);
            
            Content currency = ShopUtil.getCurrencyByUUID(NodeDataUtil.getString(
                    priceCategory, "currencyUUID"));
            Content tax = getTaxByUUID(NodeDataUtil.getString(product,
                    "taxCategoryUUID"));
    
            TemplateProductPriceBean bean = new TemplateProductPriceBean();
            bean.setFormatting(NodeDataUtil.getString(currency, "formatting"));
            bean.setPrice(getProductPriceByCategory(product, priceCategory
                    .getUUID()));
            bean.setCurrency(NodeDataUtil.getString(currency, "title"));
            boolean taxIncluded = NodeDataUtil.getBoolean(priceCategory,
                    "taxIncluded", false);
            if (taxIncluded) {
                bean.setTaxIncluded(ShopUtil.getMessages().get("tax.included"));
            } else {
                bean.setTaxIncluded(ShopUtil.getMessages().get("tax.no.included"));
            }
            
            bean.setTax(NodeDataUtil.getString(tax, "tax"));
            return bean;
        } catch (Exception e) {
            return new TemplateProductPriceBean();
        }
    }

    public Collection<Content> getOptionSets(Content product) {
        ArrayList<Content> optionSets = new ArrayList<Content>(product.getChildren(new ItemType("shopProductOptions")));
        return ShopUtil.transformIntoI18nContentList(optionSets);
    }

    public Collection<Content> getOptions(Content option) {
        ArrayList<Content> options = new ArrayList<Content>(option.getChildren(new ItemType("shopProductOption")));
        return ShopUtil.transformIntoI18nContentList(options);
    }
    
    public String getCurrencyTitle() {
        return ShopUtil.getCurrencyTitle();
    }
    
    public String getCurrencyFormatting() {
        return ShopUtil.getCurrencyFormatting();
    }

    public Content getTaxByUUID(String uuid) {
        return new I18nContentWrapper(ContentUtil
                .getContentByUUID("data", uuid));
    }

    protected Double getProductPriceByCategory(Content product,
            String priceCategoryUUID) {
        Content pricesNode = ContentUtil.getContent(product, "prices");
        if (pricesNode.hasChildren()) {
            for (Iterator<Content> iterator = pricesNode.getChildren().iterator(); iterator
                    .hasNext();) {
                Content price = new I18nContentWrapper((Content) iterator
                        .next());
                if (NodeDataUtil.getString(price, "priceCategoryUUID").equals(
                        priceCategoryUUID)) {
                    return price.getNodeData("price").getDouble();
                }
            }
        }
        return null;
    }

    /**
     * returns the link to the shopping cart page based on template
     * category-subcategory.
     */
    public String getShoppingCartLink() {
        Content shoppingCartPage;
        try {
            shoppingCartPage = STKUtil.getContentByTemplateCategorySubCategory(
                    siteRoot, "feature", "shopping-cart");
            return new LinkImpl(shoppingCartPage.getJCRNode(), templatingFunctions).getHref();
        } catch (RepositoryException e) {
            log.error("Cant get shopping cart page", e);
        }
        return "";
    }

    /**
     * get images folder items.
     */
    @Override
    protected List<String> getKeys() {
        Content product = getProduct();
        Content dmsFolder = STKUtil.getReferencedContent(product,
                "imageDmsUUID", "dms");
        if (dmsFolder == null) {
            return new ArrayList<String>();
        }
        List<String> keys = new ArrayList<String>();
        try {
            dmsFolder = dmsFolder.getParent();

            Collection<Content> children = dmsFolder.getChildren(ItemType.CONTENTNODE);

            for (Iterator<Content> iterator = children.iterator(); iterator.hasNext();) {
                Content imageNode = iterator.next();
                if (showImage(new Document(imageNode))) {
                    keys.add(imageNode.getUUID());
                }
            }

        } catch (Exception e) {

        }
        return keys;
    }

    public String getProductDetailPageLink(Content product) {
        try {
            return ShopLinkUtil.getProductDetailPageLink(templatingFunctions, product, MgnlContext.getAggregationState().getMainContent(), siteRoot);
        } catch (RepositoryException e) {
            return "";
        }
    }

}
