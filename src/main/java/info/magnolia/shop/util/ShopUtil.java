/**
 * This file Copyright (c) 2013-2015 Magnolia International
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
package info.magnolia.shop.util;

import ch.fastforward.magnolia.ocm.atomictypeconverter.MgnlAtomicTypeConverterProvider;
import ch.fastforward.magnolia.ocm.ext.MgnlConfigMapperImpl;
import ch.fastforward.magnolia.ocm.ext.MgnlObjectConverterImpl;
import info.magnolia.cms.i18n.I18nContentSupportFactory;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SystemContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.wrapper.HTMLEscapingNodeWrapper;
import info.magnolia.jcr.wrapper.I18nNodeWrapper;
import info.magnolia.shop.ShopConfiguration;
import info.magnolia.shop.ShopNodeTypes;
import info.magnolia.shop.ShopRepositoryConstants;
import info.magnolia.shop.accessors.ShopAccessor;
import info.magnolia.shop.beans.*;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.atomictypeconverter.impl.DefaultAtomicTypeConverterProvider;
import org.apache.jackrabbit.ocm.manager.cache.impl.RequestObjectCacheImpl;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.manager.objectconverter.impl.ProxyManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Paragraphs util class.
 */
public final class ShopUtil {

    private static final Logger log = LoggerFactory.getLogger(ShopUtil.class);
    public static String ATTRIBUTE_SHOPNAME = "shopName";
    public static String ATTRIBUTE_SHOPPINGCART = "shoppingCart";
    public static String ATTRIBUTE_PREVIOUS_SHOPPINGCART = "lastShoppingCart";
    public static String SHOP_TEMPLATE_NAME = "shopHome";
    public static String I18N_BASENAME = "info.magnolia.shop.messages";
    public static final BigDecimal HUNDRED = new BigDecimal("100");
    public static final BigDecimal ONE = new BigDecimal("1");
    public static final BigDecimal ZERO = new BigDecimal("0");

    private ShopUtil() {
    }

    /**
     * Gets the shop current node.
     *
     * @return shop home page
     */
    public static Node getShopRoot() {
        Node currContent = MgnlContext.getAggregationState().getMainContentNode();
        // TODO: Fix 4 MTE
//        try {
//            while (currContent.getDepth() >= 0) {
//                String subCategory = TemplateCategoryUtil.getTemplateSubCategory(currContent);
//                if (subCategory != null && subCategory.equals("shopHome")) {
//                    return currContent;
//                } else {
//                    currContent = currContent.getParent();
//                }
//            }
//        } catch (PathNotFoundException ex) {
//            log.error("Path not found!", ex);
//        } catch (RepositoryException ex) {
//            log.error("Repository exception", ex);
//        }
//        log.error("No template found with subcategory shopHome");
        return null;
    }

    public static Node getShopRootByShopName(String shopName) {

        NodeIterator shops = null;
        try {
            shops = QueryUtil.search(RepositoryConstants.WEBSITE, "select * from mgnl:content where currentShop='" + shopName + "'", "sql", "mgnl:content");
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        if (shops != null && shops.hasNext()) {
            return shops.nextNode();
        }
        log.error("No shop found with name " + shopName);
        return null;
    }

    public static Messages getMessages() {
        Locale currentLocale = I18nContentSupportFactory.getI18nSupport().getLocale();
        final Messages msg = MessagesManager.getMessages(I18N_BASENAME, currentLocale);
        return msg;
    }

    /**
     * @deprecated Use {@link #setShoppingCartInSession(String shopName)}
     */
    @Deprecated
    public static void setShoppingCartInSession() {
        String shopName = getShopName();
        setShoppingCartInSession(shopName);
    }

    public static void setShoppingCartInSession(String shopName) {
        if (StringUtils.isNotEmpty(shopName)) {
            DefaultShoppingCartImpl cart = (DefaultShoppingCartImpl) getShoppingCart(shopName);
            ShopConfiguration shopConfiguration = null;
            try {
                shopConfiguration = new ShopAccessor(shopName).getShopConfiguration();
            } catch (Exception e) {
                log.error("cant get shop configuration for " + shopName);
            }
            if (cart == null && shopConfiguration != null) {

                try {
                    cart = shopConfiguration.getCartClass();
                    MgnlContext.setAttribute(shopName + "_" + ATTRIBUTE_SHOPPINGCART, cart, Context.SESSION_SCOPE);
                } catch (Exception e) {
                    log.error("Error in shop " + shopName, e);
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

    public static String getShopName(Node dataNode) {
        if (dataNode != null) {
            try {
                // shop name is name of node @ level 2
                // @TODO: Make sure it's a data node
                Node level2Node = (Node) dataNode.getAncestor(2);
                if (level2Node != null) {
                    return level2Node.getName();
                }
            } catch (PathNotFoundException ex) {
                log.error("Could not get level 2 node of node " + dataNode, ex);
            } catch (AccessDeniedException ex) {
                log.error("Could not get level 2 node of node " + dataNode, ex);
            } catch (RepositoryException ex) {
                log.error("Could not get level 2 node of node " + dataNode, ex);
            }
        }
        return null;
    }

    /**
     * @return
     * @deprecated Use {@link #getShoppingCart(String shopName)}
     */
    @Deprecated
    public static ShoppingCart getShoppingCart() {
        return (ShoppingCart) MgnlContext.getAttribute(ATTRIBUTE_SHOPPINGCART);
    }

    /**
     * @param shopName
     * @return Returns the current users shopping cart for the named shop.
     */
    public static ShoppingCart getShoppingCart(String shopName) {
        if (StringUtils.isBlank(shopName)) {
            return null;
        }
        return (ShoppingCart) MgnlContext.getAttribute(shopName + "_" + ATTRIBUTE_SHOPPINGCART);
    }

    /**
     * @param shopName
     * @return Returns the last (i.e. previous) shopping cart. The shopping cart gets reset in the {@link info.magnolia.shop.processors.SaveAndConfirmFormProcessor}. So this is useful for all features
     * occurring after the cart was saved (e.g. sending confirmation mails, displaying a confirmation page...)
     */
    public static ShoppingCart getPreviousShoppingCart(String shopName) {
        if (StringUtils.isBlank(shopName)) {
            return null;
        }
        return (ShoppingCart) MgnlContext.getAttribute(shopName + "_" + ATTRIBUTE_PREVIOUS_SHOPPINGCART);
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
            shopConfiguration = new ShopAccessor(getShopName()).getShopConfiguration();

            if (shopConfiguration != null) {
                Node priceCategory = getShopPriceCategory(shopConfiguration);
                Node currency = getCurrencyByUUID(PropertyUtil.getString(priceCategory, "currencyUUID"));
                return PropertyUtil.getString(currency, "title");
            }
        } catch (Exception e) {
            // nothing
        }
        return "";
    }

    public static String getCurrencyFormatting() {

        ShopConfiguration shopConfiguration;
        try {
            shopConfiguration = new ShopAccessor(getShopName()).getShopConfiguration();

            if (shopConfiguration != null) {
                Node priceCategory = getShopPriceCategory(shopConfiguration);
                Node currency = getCurrencyByUUID(PropertyUtil.getString(priceCategory, "currencyUUID"));
                return PropertyUtil.getString(currency, "formatting");
            }
        } catch (Exception e) {
            // nothing
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
            return new I18nNodeWrapper(NodeUtil.getNodeByIdentifier(ShopRepositoryConstants.SHOPS, uuid));
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

        // TODO: Fix 4 MTE
//        try {
//            List<Node> nodes = TemplateCategoryUtil.getContentListByTemplateCategorySubCategory(siteRoot, category, subCategory);
//            if (nodes.size() > 0) {
//                return nodes.get(0);
//            }
//        } catch (RepositoryException e) {
//            log.error("no template found with category=" + category + " and subcategory=" + subCategory);
//        }

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

    public static NodeIterator getShippingOptions() throws RepositoryException {
        // There must be goods to ship, i.e. no "digital" goods etc., i.e. the
        // total weight of the shopping cart must be > 0
        DefaultShoppingCartImpl cart = (DefaultShoppingCartImpl) getShoppingCart(getShopName());
        if (cart.getTotalWeight() > 0) {
            // There must be a shipping country set
            if (StringUtils.isNotBlank(cart.getShippingAddressCountry())) {
                // The country must exist in the shop definition
                String queryString = "/jcr:root/shops/" + ShopUtil.getShopName() + "/countries//element(*,shopCountry)[@name='" + cart.getShippingAddressCountry() + "']";
                NodeIterator matching = QueryUtil.search("data", queryString, javax.jcr.query.Query.XPATH, "shopCountry");
                if (!matching.hasNext()) {
                    log.debug("Shipping country \"" + cart.getShippingAddressCountry() + "\" does not exist in the shop configuration");
                } else if (matching.getSize() > 1) {
                    log.error("More than one country with the name \"" + cart.getShippingAddressCountry() + "\" exist in the shop configuration");
                } else {
                    Node shippingCountry = matching.nextNode();
                    Node shippingOptionsNode = shippingCountry.getNode("shippingOptions");
                    if (shippingOptionsNode != null) {
                        PropertyIterator shippingOptionUUIDs = shippingOptionsNode.getProperties();
                        // There must be at least 1 shipping option for this country
                        if (!shippingOptionUUIDs.hasNext()) {
                            log.debug("No shipping options selected for the country \"" + cart.getShippingAddressCountry() + "\"");
                        } else {
                            // At least one of these options must exist
                            queryString = "/jcr:root//*[";
                            while (shippingOptionUUIDs.hasNext()) {
                                Property nd = shippingOptionUUIDs.nextProperty();
                                queryString += "@jcr:uuid = '" + nd.getString() + "' or ";
                            }
                            queryString = StringUtils.substringBeforeLast(queryString, " or ") + "]";
                            matching = QueryUtil.search("data", queryString, javax.jcr.query.Query.XPATH, "shopShippingOption");
                            if (!matching.hasNext()) {
                                log.error("None of the shipping options selected for country \"" + cart.getShippingAddressCountry() + "\" exists anymore.");
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
        return null;
    }

    /**
     * Gets the shipping cost from the provided option and for the carts total
     * weight. If no matching price is found in the option, null is returned.
     *
     * @param shippingOption
     * @return shipping price for the weight of the cart or null
     */
    public static BigDecimal getShippingPriceForOptionBigDecimal(Node shippingOption, DefaultShoppingCartImpl cart) {
        if (shippingOption != null) {
            try {
                // loop over all prices and find the one with the closest max weight
                // TODO: Fix grid dialog to respect the "type" attribute. At the moment all values
                // are being stored as Strings. Therefore "order by @maxWeight ascending" will not
                // give us the desired result (hence the complicated selection of the correct price
                // below).
                String queryString = "/jcr:root//*[@jcr:uuid='" + shippingOption.getIdentifier() + "']/prices/*[@price and @maxWeight] order by @maxWeight ascending";
                NodeIterator matching = QueryUtil.search("data", queryString, javax.jcr.query.Query.XPATH, "mgnl:contentNode");
                if (!matching.hasNext()) {
                    log.debug("No prices found in shipping option " + shippingOption.getPath());
                } else {
                    Node matchingPrice = null;
                    Property maxWeightND;
                    double maxWeight = 1000;
                    while (matching.hasNext()) {
                        Node priceNode = matching.nextNode();
                        maxWeightND = priceNode.getProperty("maxWeight");
                        if (priceNode.hasProperty("maxWeight") && priceNode.hasProperty("price") && maxWeightND.getDouble() > cart.getTotalWeight()) {
                            if (maxWeightND.getDouble() < maxWeight) {
                                // this one is closer to the cart weight
                                // TODO: Too complicated but necessary (see not above)
                                matchingPrice = priceNode;
                                maxWeight = maxWeightND.getDouble();
                            }
                        }
                    }
                    if (matchingPrice != null) {
                        return new BigDecimal(matchingPrice.getProperty("price").getString());
                    } else {
                        log.debug("No matching prices found in shipping option " + shippingOption.getPath());
                    }
                }
            } catch (RepositoryException e) {
                log.debug(e.getMessage(), e);
                return null;
            }
        }
        return null;
    }

    /**
     * @param shippingOption
     * @return
     */
    public static double getShippingPriceForOption(Node shippingOption, DefaultShoppingCartImpl cart) {
        try {
            BigDecimal price = getShippingPriceForOptionBigDecimal(shippingOption, cart);
            if (price != null) {
                String taxCategoryUUID = PropertyUtil.getString(shippingOption, "taxCategoryUUID");
                if (StringUtils.isNotBlank(taxCategoryUUID)) {
                    Node taxCategory = NodeUtil.getNodeByIdentifier("data", taxCategoryUUID);
                    if (taxCategory != null) {
                        if (taxCategory.hasProperty("tax")) {
                            double rate = taxCategory.getProperty("tax").getDouble();
                            BigDecimal rateBD = new BigDecimal("" + rate);
                            boolean taxIncluded = PropertyUtil.getBoolean(shippingOption, "taxIncluded", true);

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
        } catch (RepositoryException e) {
            log.debug(e.getMessage(), e);
            return 0;
        }
    }

    public static BigDecimal getPriceExcludingTax(BigDecimal priceIncludingTax, BigDecimal taxRate) {
        if (priceIncludingTax != null && priceIncludingTax.doubleValue() >= 0 && taxRate != null && taxRate.doubleValue() >= 0) {
            return priceIncludingTax.divide(HUNDRED.add(taxRate), 10, BigDecimal.ROUND_HALF_UP).multiply(HUNDRED);
        }
        return priceIncludingTax;
    }

    public static BigDecimal getPriceIncludingTax(BigDecimal priceExcludingTax, BigDecimal taxRate) {
        if (priceExcludingTax != null && priceExcludingTax.doubleValue() >= 0 && taxRate != null && taxRate.doubleValue() >= 0) {
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

    public static HashMap<String, CartItemOption> getOptionsConfiguration() {
        Iterator<String> keysIter = MgnlContext.getParameters().keySet().iterator();
        HashMap<String, CartItemOption> options = new HashMap<String, CartItemOption>();
        String currKey;
        String optionUUID;
        Node optionNode = null;
        Node optionSetNode;
        CartItemOption cio;
        while (keysIter.hasNext()) {
            currKey = keysIter.next();
            if (currKey.startsWith("option_")) {
                optionUUID = MgnlContext.getParameter(currKey);
                try {
                    optionNode = NodeUtil.getNodeByIdentifier(ShopRepositoryConstants.SHOP_PRODUCTS, optionUUID);
                } catch (RepositoryException ex) {
                    log.error("could not get current option", ex);
                }
                if (optionNode != null) {
                    try {
                        optionNode = ShopUtil.wrapWithI18n(optionNode);
                        optionSetNode = optionNode.getParent();
                        cio = new CartItemOption();
                        cio.setOptionSetUUID(optionSetNode.getIdentifier());
                        cio.setTitle(PropertyUtil.getString(optionSetNode, "title"));
                        cio.setValueTitle(PropertyUtil.getString(optionNode, "title"));
                        cio.setValueName(optionNode.getName());
                        cio.setValueUUID(optionNode.getIdentifier());
                        options.put(currKey, cio);
                    } catch (PathNotFoundException ex) {
                        log.error("could not get parent of " + NodeUtil.getPathIfPossible(optionNode), ex);
                    } catch (AccessDeniedException ex) {
                        log.error("could not get parent of " + NodeUtil.getPathIfPossible(optionNode), ex);
                    } catch (RepositoryException ex) {
                        log.error("could not get parent of " + NodeUtil.getPathIfPossible(optionNode), ex);
                    }
                }
            }
        }
        return options;
    }

    public static void addToCart() {
        String shopName = getShopName();
        addToCart(shopName);
    }

    public static void addToCart(String shopName) {
        String quantityString = MgnlContext.getParameter("quantity");
        int quantity = 1;
        try {
            quantity = new Integer(quantityString);
            if (quantity <= 0) {
                quantity = 1;
            }
        } catch (NumberFormatException nfe) {
            log.info("quantity = 0, will be set to 1");
        }
        HashMap<String, CartItemOption> options = ShopUtil.getOptionsConfiguration();
        String product = MgnlContext.getParameter("product");
        if (StringUtils.isBlank(product)) {
            log.error("Cannot add item to cart because no \"product\" parameter was found in the request");
        } else {
            ShoppingCart cart = ShopUtil.getShoppingCart(shopName);
            int success = cart.addToShoppingCart(product, quantity, options);
            if (success <= 0) {
                log.error("Cannot add item to cart because no product for " + product + " could be found");
            }
        }
    }


    public static void updateItemQuantity(String shopName, int quantity, String itemName) {
        ShoppingCart cart = getShoppingCart(shopName);
        cart.updateItemByName(itemName, quantity);
    }

    /**
     * @param productUUID
     * @param command
     * @deprecated Deprecated since v.2.3.0. Use {@link #updateItemQuantity(String shopName, int quantity, String itemName)}
     */
    @Deprecated
    public static void updateItemQuantity(String productUUID, String command) {
        String shopName = getShopName();
        updateItemQuantity(productUUID, command, shopName);
    }

    /**
     *
     * @param productUUID
     * @param command
     * @deprecated Use {@link #updateItemQuantity(String productUUID, String command, String shopName)}
     */
    @Deprecated
    public static void updateItemQuantity(String productUUID, String command, String shopName) {
        ShoppingCart shoppingCart = ShopUtil.getShoppingCart(shopName);
        int indexOfProductInCart = -1;
        // first try to determine the item by looking for an "item" parameter (index of item)
        if (MgnlContext.getParameter("item") != null) {
            try {
                indexOfProductInCart = (new Integer(MgnlContext.getParameter("item"))).intValue();
            } catch (NumberFormatException nfe) {
                // log error?
            }
        }
        // if no item index was provided, try to get the item by its product uuid.
        if (indexOfProductInCart < 0) {
            indexOfProductInCart = ((DefaultShoppingCartImpl) shoppingCart).indexOfProduct(productUUID);
        }
        if (indexOfProductInCart >= 0 && indexOfProductInCart < shoppingCart.getCartItemsCount()) {
            ShoppingCartItem shoppingCartItem = shoppingCart.getCartItems().get(indexOfProductInCart);
            int quantity = shoppingCartItem.getQuantity();

            if (command.equals("add")) {
                //
//                shoppingCartItem.setQuantity(++quantity);
                shoppingCart.updateItemByName(shoppingCartItem.getName(), ++quantity);
            } else if (command.equals("subtract") || command.equals("removeall")) {
                if (quantity <= 1 || command.equals("removeall")) {
                    shoppingCart.getCartItems().remove(indexOfProductInCart);
                } else {
//                    shoppingCartItem.setQuantity(--quantity);
                    shoppingCart.updateItemByName(shoppingCartItem.getName(), --quantity);
                } // quantity <=1

            } // else command
        } // else product on cart
    }

    public static void resetShoppingCart(String shopName) {
        // move old cart
        // @TODO: Should we check if the order has been completed before we move it?
        ShoppingCart lastCart = (ShoppingCart) MgnlContext.getAttribute(shopName + "_" + ATTRIBUTE_SHOPPINGCART);
        if (lastCart != null) {
            MgnlContext.setAttribute(shopName + "_" + ATTRIBUTE_PREVIOUS_SHOPPINGCART, lastCart, Context.SESSION_SCOPE);
        }
        // clear old cart
        MgnlContext.removeAttribute(shopName + "_" + ATTRIBUTE_SHOPPINGCART, Context.SESSION_SCOPE);
        // initialize new cart
        ShopUtil.setShoppingCartInSession(shopName);
    }

    public static Collection<Node> xpathQuery(String workspace, String xpath, String returnType, boolean wrapWithI18n) {
        List<Node> result = new ArrayList<Node>();
        try {
            NodeIterator ni = QueryUtil.search(workspace, xpath, javax.jcr.query.Query.XPATH, returnType);
            while (ni.hasNext()) {
                if (wrapWithI18n) {
                    result.add(wrapWithI18n(ni.nextNode()));
                } else {
                    result.add(ni.nextNode());
                }
            }
        } catch (RepositoryException e) {
            log.info(e.getMessage(), e);
        }
        return result;
    }

    public static ProductPrice getProductPriceBean(Node product) {
        return getProductPriceBean(product, ShopUtil.getShopName());
    }

    public static ProductPrice getProductPriceBean(Node product, String shopName) {
        try {
            ShopConfiguration shopConfiguration = new ShopAccessor(shopName).getShopConfiguration();

            Node priceCategory = getShopPriceCategory(shopConfiguration);

            Node currency = getCurrencyByUUID(PropertyUtil.getString(priceCategory, "currencyUUID"));
            Node tax = getTaxByUUID(PropertyUtil.getString(product, "taxCategoryUUID"));

            ProductPrice bean = new ProductPrice();
            bean.setFormatting(PropertyUtil.getString(currency, "formatting"));
            bean.setPrice(getProductPriceByCategory(product, priceCategory.getIdentifier()));
            bean.setCurrency(PropertyUtil.getString(currency, "title"));
            boolean taxIncluded = PropertyUtil.getBoolean(priceCategory, "taxIncluded", false);
            if (taxIncluded) {
                bean.setTaxIncluded(getMessages().get("tax.included"));
            } else {
                bean.setTaxIncluded(getMessages().get("tax.no.included"));
            }

            bean.setTax(PropertyUtil.getString(tax, "tax"));
            return bean;
        } catch (Exception e) {
            return new ProductPrice();
        }
    }

    public static Node getTaxByUUID(String uuid) {
        try {
            return wrapWithI18n(NodeUtil.getNodeByIdentifier(ShopRepositoryConstants.SHOPS, uuid));
        } catch (RepositoryException e) {
            log.error("Cant get tax category " + uuid, e);
        }
        return null;
    }

    public static Double getProductPriceByCategory(Node product, String priceCategoryUUID) throws ValueFormatException, RepositoryException {
        // TODO: wouldn't that be better with a query?
        Node pricesNode = product.getNode("prices");
        if (pricesNode.hasNodes()) {
            for (NodeIterator iterator = pricesNode.getNodes(); iterator.hasNext();) {
                Node priceNode = (Node) iterator.next();
                if (!priceNode.isNodeType("mgnl:metaData")) {
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
    
    public static Double roundUpTo2Decimal(Double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public static int getMaxQuantityPerOrder(String productUUID) {
        Node product = null;
        try {
            product = NodeUtil.getNodeByIdentifier(ShopRepositoryConstants.SHOP_PRODUCTS, productUUID);
        } catch (Exception e) {
            log.error("Could not get product with uuid " + productUUID, e);
        }
        if (product == null) {
            return 0;
        }
        // check the maxQuantityPerOrder
        Long maxQuantityPerOrder = null;
        try {
            if (product.hasProperty(LimitedProduct.MAX_QUANTITYPER_ORDER_PROPERTY)) {
                maxQuantityPerOrder = PropertyUtil.getLong(product, LimitedProduct.MAX_QUANTITYPER_ORDER_PROPERTY);
            }
        } catch (RepositoryException e) {
            log.error("Could not get " + LimitedProduct.MAX_QUANTITYPER_ORDER_PROPERTY + " from product " + productUUID, e);
        }
        if (maxQuantityPerOrder != null) {
            return maxQuantityPerOrder.intValue();
        }
        return 0;
    }

    public static Collection<ShoppingCart> getOrdersByCustomerNumber(String shopName, String customerNumber) throws RepositoryException {
        if (StringUtils.isBlank(shopName) || StringUtils.isBlank(customerNumber)) {
            return null;
        }
        Mapper mapper = new MgnlConfigMapperImpl();
        RequestObjectCacheImpl requestObjectCache = new RequestObjectCacheImpl();
        DefaultAtomicTypeConverterProvider converterProvider = new MgnlAtomicTypeConverterProvider();
        MgnlObjectConverterImpl oc = new MgnlObjectConverterImpl(mapper, converterProvider, new ProxyManagerImpl(), requestObjectCache);

        ObjectContentManager ocm = new ObjectContentManagerImpl(Components.getComponent(SystemContext.class).getJCRSession("shoppingCarts"), mapper);
        ((ObjectContentManagerImpl) ocm).setObjectConverter(oc);
        ((ObjectContentManagerImpl) ocm).setRequestObjectCache(requestObjectCache);

        String query = "/jcr:root/" + shopName + "//element(*," + ShopNodeTypes.SHOP_CART + ")[@customerNumber='"+ customerNumber +"']";
        return ocm.getObjects(query, "xpath");
    }
}
