/**
 * This file Copyright (c) 2010-2015 Magnolia International
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
package info.magnolia.module.shop.components;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.util.SelectorUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.shop.ShopConfiguration;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.module.shop.accessors.ShopAccessor;
import info.magnolia.module.shop.accessors.ShopProductAccessor;
import info.magnolia.module.shop.beans.ShoppingCart;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templatingkit.STKModule;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.module.templatingkit.templates.components.AbstractItemListModel;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shop paragraph model, used on productdetail and productlist paragraphs.
 */
public class ShopParagraphModel extends AbstractItemListModel<TemplateDefinition> {

    private static Logger log = LoggerFactory.getLogger(ShopParagraphModel.class);
    private Node siteRoot = null;
    protected static final String[] ALLOWED_IMAGE_TYPES = new String[] { "gif", "jpg", "jpeg", "png" };

    public ShopParagraphModel(Node content, TemplateDefinition definition, RenderingModel<?> parent, STKTemplatingFunctions stkFunctions, TemplatingFunctions templatingFunctions, STKModule stkModule) {
        super(content, definition, parent, stkFunctions, templatingFunctions);
        this.siteRoot = stkFunctions.siteRoot(content);
    }

    public ShoppingCart getShoppingCart() {
        return ShopUtil.getShoppingCart(ShopUtil.getShopName());
    }

    public ShoppingCart getPreviousShoppingCart() {
        return ShopUtil.getPreviousShoppingCart(ShopUtil.getShopName());
    }

    @Override
    public Node getSiteRoot() {
        return siteRoot;
    }

    /**
     * Gets product selected from the url, using selector.
     */
    public Node getProduct() {
        try {
            String productId = SelectorUtil.getSelector(2);
            if (StringUtils.isNotEmpty(productId)) {
                return new ShopProductAccessor(productId).getNode();
            }
        } catch (Exception e) {
            log.info("item not found", e);
        }
        return null;
    }

    public TemplateProductPriceBean getProductPriceBean(Node product) {
        try {
            ShopConfiguration shopConfiguration = new ShopAccessor(ShopUtil.getShopName()).getShopConfiguration();

            Node priceCategory = ShopUtil.getShopPriceCategory(shopConfiguration);

            Node currency = ShopUtil.getCurrencyByUUID(PropertyUtil.getString(priceCategory, "currencyUUID"));
            Node tax = getTaxByUUID(PropertyUtil.getString(product, "taxCategoryUUID"));

            TemplateProductPriceBean bean = new TemplateProductPriceBean();
            bean.setFormatting(PropertyUtil.getString(currency, "formatting"));
            bean.setPrice(getProductPriceByCategory(product, priceCategory.getIdentifier()));
            bean.setCurrency(PropertyUtil.getString(currency, "title"));
            boolean taxIncluded = PropertyUtil.getBoolean(priceCategory, "taxIncluded", false);
            if (taxIncluded) {
                bean.setTaxIncluded(ShopUtil.getMessages().get("tax.included"));
            } else {
                bean.setTaxIncluded(ShopUtil.getMessages().get("tax.no.included"));
            }

            bean.setTax(PropertyUtil.getString(tax, "tax"));
            return bean;
        } catch (Exception e) {
            return new TemplateProductPriceBean();
        }
    }

    public Collection<Node> getOptionSets(Node product) {
        ArrayList<Node> optionSets = null;
        try {
            optionSets = new ArrayList<Node>(templatingFunctions.children(product, "shopProductOptions"));
        } catch (RepositoryException e) {
            log.error("Cant get product options ", e);
            return null;
        }
        return ShopUtil.transformIntoI18nContentList(optionSets);
    }

    public Collection<Node> getOptions(Node option) {
        ArrayList<Node> options = null;
        try {
            options = new ArrayList<Node>(templatingFunctions.children(option, "shopProductOption"));
        } catch (RepositoryException e) {
            log.error("Cant get product options ", e);
            return null;
        }
        return ShopUtil.transformIntoI18nContentList(options);
    }

    public String getCurrencyTitle() {
        return ShopUtil.getCurrencyTitle();
    }

    public String getCurrencyFormatting() {
        return ShopUtil.getCurrencyFormatting();
    }

    public Node getTaxByUUID(String uuid) {
        try {
            return ShopUtil.wrapWithI18n(NodeUtil.getNodeByIdentifier(ShopRepositoryConstants.SHOPS, uuid));
        } catch (RepositoryException e) {
            log.error("Cant get tax category " + uuid, e);
        }
        return null;
    }

    protected Double getProductPriceByCategory(Node product, String priceCategoryUUID) throws ValueFormatException, RepositoryException {
        Node pricesNode = product.getNode("prices");
        if (pricesNode.hasNodes()) {
            for (NodeIterator iterator = pricesNode.getNodes(); iterator.hasNext();) {
                Node priceNode = (Node) iterator.next();
                if (!priceNode.isNodeType(MgnlNodeType.NT_METADATA)) {
                    Node price = ShopUtil.wrapWithI18n(priceNode);
                    if (price.hasProperty("priceCategoryUUID") && PropertyUtil.getString(price, "priceCategoryUUID").equals(priceCategoryUUID)) {
                        Property productPrice = PropertyUtil.getPropertyOrNull(price, "price");
                        if (productPrice != null) {
                            return productPrice.getDouble();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * returns the link to the shopping cart page based on template category-subcategory.
     */
    public String getShoppingCartLink() {
        try {
            Node shoppingCartPage = ShopUtil.getContentByTemplateCategorySubCategory(ShopUtil.getShopRoot(), "feature", "shopping-cart");
            if (shoppingCartPage == null) {
                shoppingCartPage = ShopUtil.getContentByTemplateCategorySubCategory(getSiteRoot(), "feature", "shopping-cart");
            }
            return templatingFunctions.link(shoppingCartPage);
        } catch (Exception e) {
            log.error("Cant get shopping cart page", e);
        }
        return "";
    }

    public String getProductDetailPageLink(Node product) {
        try {
            return ShopLinkUtil.getProductDetailPageLink(templatingFunctions, product, siteRoot);
        } catch (RepositoryException e) {
            return "";
        }
    }

    @Override
    protected void filter(List<Node> itemList) {
    }

    @Override
    protected int getMaxResults() {
        try {
            if (content.hasProperty("maxResults")) {
                return (int) content.getProperty("maxResults").getLong();
            }
            return Integer.MAX_VALUE;
        } catch (RepositoryException e) {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    protected void sort(List<Node> itemList) {
    }

    @Override
    protected List<Node> search() throws RepositoryException {

        List<Node> productList = new ArrayList<Node>();
        String productCategory = MgnlContext.getAggregationState().getMainContentNode().getIdentifier();
        if (StringUtils.isNotEmpty(productCategory)) {
            productList = ShopProductAccessor.getProductsByProductCategory(productCategory);
        }

        return productList;
    }

    @Override
    public Collection<Node> getItems() throws RepositoryException {
        List<Node> itemsList = search();

        // nothing found, return empty list
        if (itemsList == null) {
            return new ArrayList<Node>();
        }

        this.filter(itemsList);
        this.sort(itemsList);
        itemsList = stkFunctions.cutList(itemsList, getMaxResults());

        return itemsList;
    }

}
