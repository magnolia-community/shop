/**
 * This file Copyright (c) 2010-2013 Magnolia International
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
package info.magnolia.module.shop.util;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.i18n.I18nContentSupportFactory;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.wrapper.HTMLEscapingNodeWrapper;
import info.magnolia.jcr.wrapper.I18nNodeWrapper;
import info.magnolia.module.shop.ShopConfiguration;
import info.magnolia.module.shop.accessors.ShopAccesor;
import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.beans.ShoppingCart;
import info.magnolia.module.templatingkit.templates.category.TemplateCategoryUtil;
import info.magnolia.repository.RepositoryConstants;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Paragraphs util class.
 *
 * @author tmiyar
 *
 */
public final class ShopUtil {

    private static Logger log = LoggerFactory.getLogger(ShopUtil.class);
    public static String ATTRIBUTE_SHOPNAME = "shopName";
    public static String ATTRIBUTE_SHOPPINGCART = "shoppingCart";
    public static String SHOP_TEMPLATE_NAME = "shopHome";
    public static String I18N_BASENAME = "info.magnolia.module.shop.messages";
    public static final BigDecimal HUNDRED = new BigDecimal("100");
    public static final BigDecimal ONE = new BigDecimal("1");
    public static final BigDecimal ZERO = new BigDecimal("0");

    private ShopUtil() {
    }

    /**
     * Gets the shop current node.
     */
    public static Node getShopRoot() {
        Node currContent = MgnlContext.getAggregationState().getMainContent().getJCRNode();
        try {
            while (currContent.getDepth() >= 0) {
                String subCategory = TemplateCategoryUtil.getTemplateSubCategory(currContent);
                if (subCategory != null && subCategory.equals("shopHome")) {
                    return currContent;
                } else {
                    currContent = currContent.getParent();
                }
            }
        } catch (PathNotFoundException ex) {
            log.error("Path not found!", ex);
        } catch (RepositoryException ex) {
            log.error("Repository exception", ex);
        }
        log.error("No template found with subcategory shopHome");
        return null;
    }

    public static Content getShopRootByShopName(String shopName) {

        Collection<Content> shops = QueryUtil.query(RepositoryConstants.WEBSITE, "select * from mgnl:content where currentShop='"
                + shopName + "'");
        if (!shops.isEmpty()) {
            return shops.iterator().next();
        }
        log.error("No shop found with name " + shopName);
        return null;
    }

    public static Messages getMessages() {
        Locale currentLocale = I18nContentSupportFactory.getI18nSupport().getLocale();
        final Messages msg = MessagesManager.getMessages(
                I18N_BASENAME, currentLocale);
        return msg;
    }

    public static void setShoppingCartInSession() {
        String shopName = getShopName();

        if (StringUtils.isNotEmpty(shopName)) {

            DefaultShoppingCartImpl cart = (DefaultShoppingCartImpl) getShoppingCart();
            ShopConfiguration shopConfiguration = null;
            try {
                shopConfiguration = new ShopAccesor(getShopName()).getShopConfiguration();
            } catch (Exception e) {
                log.error("cant get shop configuration for " + getShopName());
            }
            if (cart == null && shopConfiguration != null) {

                try {
                    cart = shopConfiguration.getCartClass();
                    MgnlContext.setAttribute(ATTRIBUTE_SHOPPINGCART, cart, Context.SESSION_SCOPE);
                } catch (Exception e) {
                    log.error("Error in shop " + getShopName(), e);
                }
            }
        }
    }

    public static String getShopName() {
        return (String) MgnlContext.getAttribute(ATTRIBUTE_SHOPNAME);
    }

    /**
     * Used in product dialog, for getting productCategories, productPrices...
     * and the storageNode is null.
     */
    public static String getShopNameFromPath() {
        String mgnlPath = MgnlContext.getParameter("mgnlPath");
        String shopName = "";
        if (StringUtils.isNotEmpty(mgnlPath)) {
            String[] pathSplit = StringUtils.split(mgnlPath, "/");
            if (pathSplit.length >= 2) {
                shopName = pathSplit[1];
            }
        }
        return shopName;
    }

    public static String getShopName(Content dataNode) {
        if (dataNode != null) {
            try {
                // shop name is name of node @ level 2
                // @TODO: Make sure it's a data node
                Content level2Node = dataNode.getAncestor(2);
                if (level2Node != null) {
                    return level2Node.getName();
                }
            } catch (PathNotFoundException ex) {
                log.error("Could not get level 2 node of node " + dataNode.getHandle(), ex);
            } catch (AccessDeniedException ex) {
                log.error("Could not get level 2 node of node " + dataNode.getHandle(), ex);
            } catch (RepositoryException ex) {
                log.error("Could not get level 2 node of node " + dataNode.getHandle(), ex);
            }
        }
        return null;
    }

    public static ShoppingCart getShoppingCart() {
        return (ShoppingCart) MgnlContext.getAttribute(ATTRIBUTE_SHOPPINGCART);
    }

    public static Collection<Node> transformIntoI18nContentList(Collection<Node> contentList) {
        Collection<Node> i18nProductList = new ArrayList<Node>();
        if (contentList != null) {
            for (Node content : contentList) {
                i18nProductList.add(ShopUtil.wrapWithI18n(content));
            }
        }
        return i18nProductList;
    }

    public static String getCurrencyTitle() {

        ShopConfiguration shopConfiguration;
        try {
            shopConfiguration = new ShopAccesor(getShopName()).getShopConfiguration();

            if (shopConfiguration != null) {
                Node priceCategory = getShopPriceCategory(shopConfiguration);
                Node currency = getCurrencyByUUID(PropertyUtil.getString(priceCategory,
                        "currencyUUID"));
                return PropertyUtil.getString(currency, "title");
            }
        } catch (Exception e) {
            //nothing
        }
        return "";
    }

    public static String getCurrencyFormatting() {

        ShopConfiguration shopConfiguration;
        try {
            shopConfiguration = new ShopAccesor(getShopName()).getShopConfiguration();

            if (shopConfiguration != null) {
                Node priceCategory = getShopPriceCategory(shopConfiguration);
                Node currency = getCurrencyByUUID(PropertyUtil.getString(priceCategory,
                        "currencyUUID"));
                return PropertyUtil.getString(currency, "formatting");
            }
        } catch (Exception e) {
            //nothing
        }
        return "";
    }

    public static Node getShopPriceCategory(ShopConfiguration shopConfiguration) {
        if (shopConfiguration != null) {
            try {
                return shopConfiguration.getPriceCategoryManager().getPriceCategoryInUse();
            } catch (Exception e) {
                log.error("Error in shop " + getShopName(), e);
            }
        }
        return null;
    }

    public static Node getCurrencyByUUID(String uuid) {
        try {
            return new I18nNodeWrapper(NodeUtil.getNodeByIdentifier("data", uuid));
        } catch (RepositoryException e) {
            log.error("Cant read the currency " + uuid, e);
        }
        return null;

    }

    public static String getPath(boolean removeEndToken, String... strings) {
        String path = "/";

        for (String string : strings) {
            path += string + "/";
        }
        if (removeEndToken) {
            path = StringUtils.chomp(path, "/");
        }
        return path;
    }

    public static String getPath(String... strings) {
        return getPath(true, strings);
    }

    public static Node getContentByTemplateCategorySubCategory(Node siteRoot, String category, String subCategory) {

        try {
            List<Node> nodes = TemplateCategoryUtil.getContentListByTemplateCategorySubCategory(siteRoot, category, subCategory);
            if (nodes.size() > 0) {
                return nodes.get(0);
            }
        } catch (RepositoryException e) {
            log.error("no template found with category=" + category + " and subcategory=" + subCategory);
        }

        return null;
    }

    public static Node wrapWithI18n(Node node) {
        if (node == null) {
            return null;
        }
        return NodeUtil.isWrappedWith(node, I18nNodeWrapper.class) ? node : new I18nNodeWrapper(node);
    }

    public static Node wrapWithHTML(Node node, boolean linebreaks) {
        if (node == null) {
            return null;
        }
        return NodeUtil.isWrappedWith(node, HTMLEscapingNodeWrapper.class) ? node : new HTMLEscapingNodeWrapper(node, linebreaks);
    }

    public static Collection<Content> getShippingOptions() {
        // There must be goods to ship, i.e. no "digital" goods etc., i.e. the
        // total weight of the shopping cart must be > 0
        DefaultShoppingCartImpl cart = (DefaultShoppingCartImpl) getShoppingCart();
        if (cart.getTotalWeight() > 0) {
            // There must be a shipping country set
            if (StringUtils.isNotBlank(cart.getShippingAddressCountry())) {
                // The country must exist in the shop definition
                String queryString = "/jcr:root/shops/" + ShopUtil.getShopName() + "/countries//element(*,shopCountry)[@name='"
                        + cart.getShippingAddressCountry() + "']";
                Collection<Content> matching = QueryUtil.query("data", queryString, "xpath", "shopCountry");
                if (matching.isEmpty()) {
                    log.debug("Shipping country \"" + cart.getShippingAddressCountry()
                            + "\" does not exist in the shop configuration");
                } else if (matching.size() > 1) {
                    log.error("More than one country with the name \"" + cart.getShippingAddressCountry()
                            + "\" exist in the shop configuration");
                } else {
                    Content shippingCountry = matching.iterator().next();
                    Content shippingOptionsNode = ContentUtil.getContent(shippingCountry, "shippingOptions");
                    if (shippingOptionsNode != null) {
                        Collection<NodeData> shippingOptionUUIDs = shippingOptionsNode.getNodeDataCollection();
                        // There must be at least 1 shipping option for this country
                        if (shippingOptionUUIDs.isEmpty()) {
                            log.debug("No shipping options selected for the country \"" + cart.getShippingAddressCountry() + "\"");
                        } else {
                            // At least one of these options must exist
                            queryString = "/jcr:root//*[";
                            for (NodeData nd : shippingOptionUUIDs) {
                                queryString += "@jcr:uuid = '" + nd.getString() + "' or ";
                            }
                            queryString = StringUtils.substringBeforeLast(queryString, " or ") + "]";
                            matching = QueryUtil.query("data", queryString, "xpath", "shopShippingOption");
                            if (matching.isEmpty()) {
                                log.error("None of the shipping options selected for country \"" + cart.getShippingAddressCountry()
                                        + "\" exists anymore.");
                            } else {
                                return matching;
                            }
                        }
                    } else {
                        log.debug("No shipping options selected in country " + cart.getShippingAddressCountry());
                    }
                }
            } else {
                log.debug("No shipping country set in cart.");
            }
        } else {
            log.debug("No goods to ship (total weight of cart is not > 0.");
        }
        return new ArrayList();
    }

    /**
     * Gets the shipping cost from the provided option and for the carts total
     * weight. If no matching price is found in the option, null is returned.
     *
     * @param shippingOption
     * @return shipping price for the weight of the cart or null
     */
    public static BigDecimal getShippingPriceForOptionBigDecimal(Content shippingOption, DefaultShoppingCartImpl cart) {
        if (shippingOption != null) {
            // loop over all prices and find the one with the closest max weight
            // TODO: Fix grid dialog to respect the "type" attribute. At the moment all values
            // are being stored as Strings. Therefore "order by @maxWeight ascending" will not
            // give us the desired result (hence the complicated selection of the correct price
            // below).
            String queryString = "/jcr:root//*[@jcr:uuid='" + shippingOption.getUUID()
                    + "']/prices/*[@price and @maxWeight] order by @maxWeight ascending";
            Collection<Content> matching = QueryUtil.query("data", queryString, "xpath", "mgnl:contentNode");
            if (matching.isEmpty()) {
                log.debug("No prices found in shipping option " + shippingOption.getHandle());
            } else {
                Content matchingPrice = null;
                NodeData priceND, maxWeightND;
                double maxWeight = 1000;
                for (Content priceNode : matching) {
                    priceND = priceNode.getNodeData("price");
                    maxWeightND = priceNode.getNodeData("maxWeight");
                    if (maxWeightND.isExist() && priceND.isExist() && maxWeightND.getDouble() > cart.getTotalWeight()) {
                        if (maxWeightND.getDouble() < maxWeight) {
                            // this one is closer to the cart weight
                            // TODO: Too complicated but necessary (see not above)
                            matchingPrice = priceNode;
                            maxWeight = maxWeightND.getDouble();
                        }
                    }
                }
                if (matchingPrice != null) {
                    return new BigDecimal(matchingPrice.getNodeData("price").getString());
                } else {
                    log.debug("No matching prices found in shipping option " + shippingOption.getHandle());
                }
            }
        }
        return null;
    }

    /**
     *
     * @param shippingOption
     * @return
     */
    public static double getShippingPriceForOption(Content shippingOption, DefaultShoppingCartImpl cart) {
        BigDecimal price = getShippingPriceForOptionBigDecimal(shippingOption, cart);
        if (price != null) {
            String taxCategoryUUID = NodeDataUtil.getString(shippingOption, "taxCategoryUUID");
            if (StringUtils.isNotBlank(taxCategoryUUID)) {
                Content taxCategory = ContentUtil.getContentByUUID("data", taxCategoryUUID);
                if (taxCategory != null) {
                    if (taxCategory.getNodeData("tax").isExist()) {
                        double rate = taxCategory.getNodeData("tax").getDouble();
                        BigDecimal rateBD = new BigDecimal("" + rate);
                        boolean taxIncluded = NodeDataUtil.getBoolean(shippingOption, "taxIncluded", true);

                        if (cart.getTaxFree() && !taxIncluded) {
                            // tax not included but has to be payed by seller
                            // -> charge it to buyer -> add it
                            price = getPriceIncludingTax(price, rateBD);
                        } else if (cart.getTaxIncluded() && !taxIncluded) {
                            // prices in cart are including tax, shipping price
                            // however is not -> add it
                            price = getPriceIncludingTax(price, rateBD);
                        } else if (!cart.getTaxIncluded() && taxIncluded) {
                            // prices in cart are excluding tax, shipping price
                            // however is including tax -> substract it
                            price = getPriceExcludingTax(price, rateBD);
                        }
                    }
                }
            }
            return price.doubleValue();
        } else {
            return 0;
        }
    }

    public static BigDecimal getPriceExcludingTax(BigDecimal priceIncludingTax, BigDecimal taxRate) {
        if (priceIncludingTax != null && priceIncludingTax.doubleValue() >= 0 && taxRate != null
                && taxRate.doubleValue() >= 0) {
            return priceIncludingTax.divide(HUNDRED.add(taxRate), 10, BigDecimal.ROUND_HALF_UP).multiply(HUNDRED);
        }
        return priceIncludingTax;
    }

    public static BigDecimal getPriceIncludingTax(BigDecimal priceExcludingTax, BigDecimal taxRate) {
        if (priceExcludingTax != null && priceExcludingTax.doubleValue() >= 0 && taxRate != null
                && taxRate.doubleValue() >= 0) {
            return priceExcludingTax.multiply(ONE.add(taxRate.divide(HUNDRED)));
        }
        return priceExcludingTax;
    }

    public static BigDecimal getTax(BigDecimal price, boolean taxIncluded, BigDecimal taxRate) {
        if (price != null && price.doubleValue() >= 0 && taxRate != null && taxRate.doubleValue() >= 0) {
            if (taxIncluded) {
                return price.divide(HUNDRED.add(taxRate), 10, BigDecimal.ROUND_HALF_UP).multiply(taxRate);
            } else {
                return price.multiply(taxRate.divide(HUNDRED));
            }
        }
        return new BigDecimal("0");
    }

}
