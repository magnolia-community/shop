/**
 * This file Copyright (c) 2003-2011 Magnolia International
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

import info.magnolia.cms.security.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.jcr.PathNotFoundException;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.dms.beans.Document;
import info.magnolia.module.shop.ShopConfiguration;
import info.magnolia.module.shop.accessors.ShopAccesor;
import info.magnolia.module.shop.beans.CartItemOption;
import info.magnolia.module.shop.beans.ShoppingCart;
import info.magnolia.module.shop.search.AbstractProductListType;
import info.magnolia.module.shop.search.DefaultProductListType;
import info.magnolia.module.shop.search.ProductListTypeCategory;
import info.magnolia.module.shop.search.ProductListTypeSearch;
import info.magnolia.module.shop.search.ProductListTypeTag;
import info.magnolia.module.shop.util.CustomDataUtil;
import info.magnolia.module.shop.util.ShopProductPager;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopLinkUtil.ParamType;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;
import info.magnolia.module.templatingkit.navigation.LinkImpl;
import info.magnolia.module.templatingkit.paragraphs.ImageGalleryParagraphModel;
import info.magnolia.module.templatingkit.templates.STKTemplateModel;
import info.magnolia.module.templatingkit.util.STKUtil;
import java.util.HashMap;

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
    private AbstractProductListType productListType = null;

    public ShopParagraphModel(Content content, RenderableDefinition definition,
            RenderingModel parent) {
        super(content, definition, parent);
        this.parent = parent;
        if (parent instanceof STKTemplateModel) {
            this.siteRoot = ((STKTemplateModel) parent).getSiteRoot();
        }

    }

    public ShoppingCart getShoppingCart() {
        return ShopUtil.getShoppingCart();
    }

    protected Content getSiteRoot() {
        return siteRoot;
    }

    @Override
    public String execute() {
        init();
        String command = MgnlContext.getParameter("command");

        if (StringUtils.isNotEmpty(command)) {
            if (StringUtils.equals(command, "addToCart")) {
                addToCart();
                return "";
            }
        }

        return "";
    }

    private void init() {
        if (ShopLinkUtil.isParamOfType(ParamType.CATEGORY)) {
            productListType = new ProductListTypeCategory(siteRoot, content);

        } else if (ShopLinkUtil.isParamOfType(ParamType.SEARCH)) {
            productListType = new ProductListTypeSearch(siteRoot, content);

        } else if (ShopLinkUtil.isParamOfType(ParamType.TAG)) {
            productListType = new ProductListTypeTag(siteRoot, content);
        } else {
            productListType = new DefaultProductListType(siteRoot, content);
        }

    }

    public void resetShoppingCart() {
        // initialize new cart
        MgnlContext.getWebContext().getRequest().getSession().removeAttribute(
                "shoppingCart");
        ShopUtil.setShoppingCartInSession();
    }

    private void addToCart() {
        String quantityString = MgnlContext.getParameter("quantity");
        int quantity = 1;
        try {
            quantity = (new Integer(quantityString)).intValue();
            if (quantity <= 0) {
                quantity = 1;
            }
        } catch (NumberFormatException nfe) {
            // TODO: log error? qunatity will be set to 1
        }
        // get all options
        Iterator keysIter = MgnlContext.getParameters().keySet().iterator();
        HashMap<String,CartItemOption> options = new HashMap();
        String currKey, optionSetUUID, optionUUID;
        Content optionNode, optionSetNode;
        CartItemOption cio;
        while (keysIter.hasNext()) {
            currKey = (String) keysIter.next();
            if (currKey.startsWith("option_")) {
                optionSetUUID = StringUtils.substringAfter(currKey, "option_");
                optionUUID = MgnlContext.getParameter(currKey);
                optionNode = ContentUtil.getContentByUUID("data", optionUUID);
                if (optionNode != null) {
                    try {
                        optionNode = new I18nContentWrapper(optionNode);
                        optionSetNode = optionNode.getParent();
                        cio = new CartItemOption();
                        cio.setOptionSetUUID(optionSetNode.getUUID());
                        cio.setTitle(NodeDataUtil.getString(optionSetNode, "title"));
                        cio.setValueTitle(NodeDataUtil.getString(optionNode, "title"));
                        cio.setValueName(optionNode.getName());
                        cio.setValueUUID(optionNode.getUUID());
                        options.put(currKey, cio);
                    } catch (PathNotFoundException ex) {
                        log.error("could not get parent of " + optionNode.getHandle(), ex);
                    } catch (AccessDeniedException ex) {
                        log.error("could not get parent of " + optionNode.getHandle(), ex);
                    } catch (RepositoryException ex) {
                        log.error("could not get parent of " + optionNode.getHandle(), ex);
                    }
                }
            }
        }
        String product = MgnlContext.getParameter("product");
        if (StringUtils.isBlank(product)) {
            log
                    .error("Cannot add item to cart because no \"product\" parameter was found in the request");
        } else {
            ShoppingCart cart = getShoppingCart();
            int success = cart.addToShoppingCart(product, quantity, options);
            if (success <= 0) {
                log.error("Cannot add item to cart because no product for "
                        + product + " could be found");

            }
        }
    }

    public ShopProductPager getPager() {
        return productListType.getPager();
    }

    /**
     * Gets the category selected from the url, using selector.
     * 
     */
    public Content getCategory() {
        String productCategoryUUID = ShopLinkUtil.getSelectedCategoryUUID();
        if (StringUtils.isNotEmpty(productCategoryUUID)) {
            Content node = ContentUtil.getContentByUUID("data",
                    productCategoryUUID);
            if (!node.isNodeType("product")) {
                return new I18nContentWrapper(node);
            }
        }
        return null;
    }

    /**
     * Gets product selected from the url, using selector.
     * 
     */
    public Content getProduct() {

        String productName = ShopLinkUtil.getParamValue(ParamType.PRODUCT);

        if (StringUtils.isNotEmpty(productName)) {
            Content product;
            try {
                product = CustomDataUtil.getProductNode(productName);
                return new I18nContentWrapper(product);
            } catch (Exception e) {
                //item not found
            }
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

    public Collection getOptionSets(Content product) {
        ArrayList optionSets = new ArrayList(product.getChildren(new ItemType("shopProductOptions")));
        return ShopUtil.transformIntoI18nContentList(optionSets);
    }

    public Collection getOptions(Content option) {
        ArrayList options = new ArrayList(option.getChildren(new ItemType("shopProductOption")));
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
            for (Iterator iterator = pricesNode.getChildren().iterator(); iterator
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
                    siteRoot, "feature", "shopShoppingCart");
            return new LinkImpl(shoppingCartPage).getHref();
        } catch (RepositoryException e) {
            log.error("Cant get shopping cart page", e);
        }
        return "";
    }

    /**
     * get images folder items.
     */
    protected List<String> getKeys() {
        Content product = getProduct();
        Content dmsFolder = STKUtil.getReferencedContent(product,
                "imageDmsUUID", "dms");
        if (dmsFolder == null) {
            return new ArrayList();
        }
        List<String> keys = new ArrayList();
        try {
            dmsFolder = dmsFolder.getParent();

            Collection children = dmsFolder.getChildren(ItemType.CONTENTNODE);

            for (Iterator iterator = children.iterator(); iterator.hasNext();) {
                Content imageNode = (Content) iterator.next();
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
            return ShopLinkUtil.getProductDetailPageLink(product, siteRoot);
        } catch (RepositoryException e) {
            return "";
        }
    }
    
    public String getTitle() {
        return productListType.getTitle();
    }

}
