/**
 * This file Copyright (c) 2003-2009 Magnolia International
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
package info.magnolia.module.shop;

import info.magnolia.module.shop.beans.ShoppingCart;
import info.magnolia.module.shop.beans.DefaultShoppingCart;
import info.magnolia.module.shop.beans.ShoppingCartItem;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.PageMVCHandler;
import info.magnolia.module.ocm.OCMModule;
import info.magnolia.module.ocm.ext.MgnlConfigMapperImpl;
import info.magnolia.module.ocm.ext.MgnlObjectConverterImpl;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
 * PageMVCHandler subclass which offers the common shop tasks like adding a
 * product to the cart, deleting a cart item or saving the cart.
 * 
 * @author will
 */
public class ShopPage extends PageMVCHandler {

    private static Logger log = LoggerFactory.getLogger(ShopPage.class);
    // tells if a confirmation mail will be sent to the customer when the cart
    // is beign saved
    private Boolean sendConfirmation = Boolean.FALSE;
    // shopKey is the name of the shop configuration node under
    // config:/modules/shop/config/shops
    private String shopKey = null;
    private String product = null;
    private String cartSessionVariable = null;
    private Map errorMessages;
    private String targetPage;
    private String errorPage;

    public ShopPage(String name, HttpServletRequest request, HttpServletResponse response) {
        super(name, request, response);
        errorMessages = new HashMap();
    }

    /**
     * Adds the product who's UUID is stored in the "product" query parameter to
     * the cart. If the product already exists in the cart the quantity will be
     * increased. The query parameters needed are:
     * <ul>
     * <li><b>product</b>: UUID of the product to be added</li>
     * <li><b>shopKey</b>: name of the shop configuration node</li>
     * <li><b>quantity</b>: integer value quantity</li>
     * </ul>
     * When adding the product to the cart, the products title, description and
     * price (for the language and price category set in the cart) is copied into
     * the cart item so changes to the products will not have an influence on the
     * cart.
     *
     * @return
     * @todo For shops with configurable products a method should be added which
     *       will not increase the quantity of a product already in the cart but
     *       rather add an other cart item for the product (e.g. same t-shirt,
     *       once size XS and pink, once size L and color orange.
     * @todo Do we need to support non-integer quantities?
     */
    public String addToCart() {

        // 1. get the shop configuration
        Content shopConfigNode = getShopConfigurationNode();
        if (shopConfigNode == null) {
            log.error("No shop configuration could be found (shopKey: " + shopKey + ")");
            getErrorMessages().put("add.to.cart", "no.shop.config");
            return VIEW_ERROR;
        }

        // 2. shopping cart - or create one if it does not exist
        ShoppingCart cart = getShoppingCart(shopConfigNode);
        if (cart == null) {
            log.error("Shopping cart could not be found!");
            getErrorMessages().put("add.to.cart", "cart.not.found");
            return VIEW_ERROR;
        }

        // 3. get product and quantity
        String quantityString = request.getParameter("quantity");
        int quantity = 1;
        try {
            quantity = (new Integer(quantityString)).intValue();
            if (quantity <= 0) {
                quantity = 1;
            }
        } catch (NumberFormatException nfe) {
            // TODO: log error? qunatity will be set to 1
        }
        if (StringUtils.isBlank(product)) {
            log.error("Cannot add item to cart because no \"product\" parameter was found in the request");
            getErrorMessages().put("add.to.cart", "no.product.provided");
            return VIEW_ERROR;
        } else {
            log.debug("Cart class: " + cart.getClass().getName());
            int success = cart.addToShoppingCart(product, quantity);
            if (success <= 0) {
                log.error("Cannot add item to cart because no product for " + product + " could be found");
                getErrorMessages().put("add.to.cart", "no.product.found");
                return VIEW_ERROR;
            }
        }
        return VIEW_SUCCESS;
    }

    /**
     * Removes the product whos UUID is stored in the "product" query parameter
     * from the cart. The query parameters are:
     * <ul>
     * <li><b>product</b>: UUID of the product to be added</li>
     * <li><b>shopKey</b>: name of the shop configuration node</li>
     * </ul>
     *
     * @return
     * @todo If the cart concept is changed to allow more than one cart item for
     *       the same product (needed for configurable products) we need an other
     *       way to identify the cart item. The cart item index might work. But is
     *       it "safe"? One could also introduce UUIDs for the cart items even
     *       before they are stored as nodes...
     */
    public String removeFromCart() {
        // 1. get the shop configuration
        Content shopConfigNode = getShopConfigurationNode();
        if (shopConfigNode == null) {
            log.error("No shop configuration could be found (shopKey: " + shopKey + ")");
            getErrorMessages().put("remove.from.cart", "no.shop.config");
            return VIEW_ERROR;
        }

        // 2. shopping cart - or create one if it does not exist
        ShoppingCart cart = getShoppingCart(shopConfigNode);
        if (cart == null) {
            log.error("Shopping cart could not be found!");
            getErrorMessages().put("remove.from.cart", "cart.not.found");
            return VIEW_ERROR;
        }

        // 3. remove item for product uuid
        if (StringUtils.isBlank(product)) {
            log.error("Cannot remove item from cart because no \"product\" parameter was found in the request");
            getErrorMessages().put("remove.from.cart", "no.product.provided");
            return VIEW_ERROR;
        } else {
            cart.removeFromShoppingCart(product);
        }
        return VIEW_SUCCESS;
    }

    /**
     * Cycles through the cart items and looks for a query parameter called
     * "quantity_<i>[productUUID]</i>" where productUUID is the UUID of the
     * product of the current cart item. If such a query parameter exists, the
     * items quantity will be updated with it.<br />
     * The query parameters are:
     * <ul>
     * <li><b>shopKey</b>: name of the shop configuration node</li>
     * <li><b>qunatity_<i>[productUUID]</i></b>: quantity to be set in the cart
     * item containing the product with UUID productUUID</li>
     * </ul>
     *
     * @return
     * @todo If the cart concept is changed to allow more than one cart item for
     *       the same product (needed for configurable products) we need an other
     *       way to identify the cart item. The cart item index might work. But is
     *       it "safe"? One could also introduce UUIDs for the cart items even
     *       before they are stored as nodes...
     */
    public String updateCartItemsQuantityByProductUUIDs() {
        // 1. get the shop configuration
        Content shopConfigNode = getShopConfigurationNode();
        if (shopConfigNode == null) {
            log.error("No shop configuration could be found (shopKey: " + shopKey + ")");
            getErrorMessages().put("remove.from.cart", "no.shop.config");
            return VIEW_ERROR;
        }

        // 2. shopping cart - or create one if it does not exist
        ShoppingCart cart = getShoppingCart(shopConfigNode);
        if (cart == null) {
            log.error("Shopping cart could not be found!");
            getErrorMessages().put("remove.from.cart", "cart.not.found");
            return VIEW_ERROR;
        }

        // 3. loop throught the items and look for a quntity parameter
        Iterator cartItems = cart.getCartItems().iterator();
        ShoppingCartItem currCartItem;
        String quantityParam;
        while (cartItems.hasNext()) {
            currCartItem = (ShoppingCartItem) cartItems.next();
            quantityParam = request.getParameter("quantity_" + currCartItem.getProductUUID());
            if (StringUtils.isNotBlank(quantityParam)) {
                Integer quantity = new Integer(quantityParam);
                if (quantity != null && quantity.intValue() > 0) {
                    currCartItem.setQuantity(quantity.intValue());
                } else {
                    log.error("Could not update quantity. " + quantityParam + " is not an Integer.");
                }
            }
        }
        return VIEW_SUCCESS;
    }

    /**
     * Updates parts of the shopping cart. Only the parts of the cart are updated
     * for which an "update key" is in the query parameters. This allows you to
     * update the cart in several steps. E.g. you could have the order address on
     * one page, the billing on the next and the shipping on a third. If you
     * update the shipping address and there is no query parameter for the zip,
     * the zip in the shipping address will be cleared.<br />
     * Supported update keys are:
     * <ul>
     * <li>orderAddress</li>
     * <li>billingAddress</li>
     * <li>shippingAddress</li>
     * <li>gtc</li>
     * </ul>
     *
     * @return
     */
    public String updateCart() {
        // 1. get the shop configuration
        Content shopConfigNode = getShopConfigurationNode();
        if (shopConfigNode == null) {
            log.error("No shop configuration could be found (shopKey: " + shopKey + ")");
            getErrorMessages().put("remove.from.cart", "no.shop.config");
            return VIEW_ERROR;
        }

        // 2. shopping cart - or create one if it does not exist
        DefaultShoppingCart cart = (DefaultShoppingCart) getShoppingCart(shopConfigNode);
        if (cart == null) {
            log.error("Shopping cart could not be found!");
            getErrorMessages().put("remove.from.cart", "cart.not.found");
            return VIEW_ERROR;
        }

        // 3. update the parts which need updating
        String success = VIEW_SUCCESS;
        List updateKeys = Arrays.asList(request.getParameterValues("update"));
        log.debug("Update keys: " + updateKeys);
        if (updateKeys.contains("orderAddress")) {
            log.debug("Updating order address in cart...");
            // update shipping address
            String orderSuccess = updateOrderAddress();
            if (orderSuccess.equals(VIEW_SUCCESS)) {
                success = orderSuccess;
            }
        }
        if (updateKeys.contains("billingAddress")) {
            log.debug("Updating billing address in cart...");
            // update billing address
            String billingSuccess = updateBillingAddress();
            if (billingSuccess.equals(VIEW_SUCCESS)) {
                success = billingSuccess;
            }
        }
        if (updateKeys.contains("shippingAddress")) {
            log.debug("Updating shipping address in cart...");
            // update shipping address
            String shippingSuccess = updateShippingAddress();
            if (shippingSuccess.equals(VIEW_SUCCESS)) {
                success = shippingSuccess;
            }
        }
        if (updateKeys.contains("gtc")) {
            log.debug("Updating gtc in cart...");
            // update the gtc flag
            String gtcString = request.getParameter("acceptedGTC");
            if (StringUtils.isNotBlank(gtcString)) {
                Boolean b = new Boolean(gtcString);
                if (b != null && b.booleanValue()) {
                    cart.setAcceptedGTC(b);
                } else {
                    cart.setAcceptedGTC(false);
                }
            } else {
                cart.setAcceptedGTC(false);
            }
        }
        return success;
    }

    public String updateOrderAddress() {
        // 1. get the shop configuration
        Content shopConfigNode = getShopConfigurationNode();
        if (shopConfigNode == null) {
            log.error("No shop configuration could be found (shopKey: " + shopKey + ")");
            getErrorMessages().put("remove.from.cart", "no.shop.config");
            return VIEW_ERROR;
        }

        // 2. shopping cart - or create one if it does not exist
        DefaultShoppingCart cart = (DefaultShoppingCart) getShoppingCart(shopConfigNode);
        if (cart == null) {
            log.error("Shopping cart could not be found!");
            getErrorMessages().put("remove.from.cart", "cart.not.found");
            return VIEW_ERROR;
        }

        // 3. update the fields
        cart.setOrderAddressCompany(request.getParameter("orderAddressCompany"));
        cart.setOrderAddressCompany2(request.getParameter("orderAddressCompany2"));
        cart.setOrderAddressFirstname(request.getParameter("orderAddressFirstname"));
        cart.setOrderAddressLastname(request.getParameter("orderAddressLastname"));
        cart.setOrderAddressSex(request.getParameter("orderAddressSex"));
        cart.setOrderAddressTitle(request.getParameter("orderAddressTitle"));
        cart.setOrderAddressStreet(request.getParameter("orderAddressStreet"));
        cart.setOrderAddressStreet2(request.getParameter("orderAddressStreet2"));
        cart.setOrderAddressZip(request.getParameter("orderAddressZip"));
        cart.setOrderAddressCity(request.getParameter("orderAddressCity"));
        cart.setOrderAddressState(request.getParameter("orderAddressState"));
        cart.setOrderAddressCountry(request.getParameter("orderAddressCountry"));
        cart.setOrderAddressPhone(request.getParameter("orderAddressPhone"));
        cart.setOrderAddressMobile(request.getParameter("orderAddressMobile"));
        cart.setOrderAddressMail(request.getParameter("orderAddressMail"));

        return VIEW_SUCCESS;
    }

    public String updateBillingAddress() {
        // 1. get the shop configuration
        Content shopConfigNode = getShopConfigurationNode();
        if (shopConfigNode == null) {
            log.error("No shop configuration could be found (shopKey: " + shopKey + ")");
            getErrorMessages().put("remove.from.cart", "no.shop.config");
            return VIEW_ERROR;
        }

        // 2. shopping cart - or create one if it does not exist
        DefaultShoppingCart cart = (DefaultShoppingCart) getShoppingCart(shopConfigNode);
        if (cart == null) {
            log.error("Shopping cart could not be found!");
            getErrorMessages().put("remove.from.cart", "cart.not.found");
            return VIEW_ERROR;
        }

        // 3. update the fields
        cart.setBillingAddressCompany(request.getParameter("billingAddressCompany"));
        cart.setBillingAddressCompany2(request.getParameter("billingAddressCompany2"));
        cart.setBillingAddressFirstname(request.getParameter("billingAddressFirstname"));
        cart.setBillingAddressLastname(request.getParameter("billingAddressLastname"));
        cart.setBillingAddressSex(request.getParameter("billingAddressSex"));
        cart.setBillingAddressTitle(request.getParameter("billingAddressTitle"));
        cart.setBillingAddressStreet(request.getParameter("billingAddressStreet"));
        cart.setBillingAddressStreet2(request.getParameter("billingAddressStreet2"));
        cart.setBillingAddressZip(request.getParameter("billingAddressZip"));
        cart.setBillingAddressCity(request.getParameter("billingAddressCity"));
        cart.setBillingAddressState(request.getParameter("billingAddressState"));
        cart.setBillingAddressCountry(request.getParameter("billingAddressCountry"));
        cart.setBillingAddressPhone(request.getParameter("billingAddressPhone"));
        cart.setBillingAddressMobile(request.getParameter("billingAddressMobile"));
        cart.setBillingAddressMail(request.getParameter("billingAddressMail"));

        return VIEW_SUCCESS;
    }

    public String updateShippingAddress() {
        // 1. get the shop configuration
        Content shopConfigNode = getShopConfigurationNode();
        if (shopConfigNode == null) {
            log.error("No shop configuration could be found (shopKey: " + shopKey + ")");
            getErrorMessages().put("remove.from.cart", "no.shop.config");
            return VIEW_ERROR;
        }

        // 2. shopping cart - or create one if it does not exist
        DefaultShoppingCart cart = (DefaultShoppingCart) getShoppingCart(shopConfigNode);
        if (cart == null) {
            log.error("Shopping cart could not be found!");
            getErrorMessages().put("remove.from.cart", "cart.not.found");
            return VIEW_ERROR;
        }

        // 3. update the fields
        cart.setShippingAddressCompany(request.getParameter("shippingAddressCompany"));
        cart.setShippingAddressCompany2(request.getParameter("shippingAddressCompany2"));
        cart.setShippingAddressFirstname(request.getParameter("shippingAddressFirstname"));
        cart.setShippingAddressLastname(request.getParameter("shippingAddressLastname"));
        cart.setShippingAddressSex(request.getParameter("shippingAddressSex"));
        cart.setShippingAddressTitle(request.getParameter("shippingAddressTitle"));
        cart.setShippingAddressStreet(request.getParameter("shippingAddressStreet"));
        cart.setShippingAddressStreet2(request.getParameter("shippingAddressStreet2"));
        cart.setShippingAddressZip(request.getParameter("shippingAddressZip"));
        cart.setShippingAddressCity(request.getParameter("shippingAddressCity"));
        cart.setShippingAddressState(request.getParameter("shippingAddressState"));
        cart.setShippingAddressCountry(request.getParameter("shippingAddressCountry"));
        cart.setShippingAddressPhone(request.getParameter("shippingAddressPhone"));
        cart.setShippingAddressMobile(request.getParameter("shippingAddressMobile"));
        cart.setShippingAddressMail(request.getParameter("shippingAddressMail"));

        return VIEW_SUCCESS;
    }

    /**
     *
     * @return
     */
    public String saveAndConfirmOrder() {
        // 0. update cart if necessary
        String view = updateCart();
        if (!view.equals(VIEW_SUCCESS)) {
            return view;
        }

        // 1. get the shop configuration
        Content shopConfigNode = getShopConfigurationNode();
        if (shopConfigNode == null) {
            log.error("No shop configuration could be found (shopKey: " + shopKey + ")");
            getErrorMessages().put("remove.from.cart", "no.shop.config");
            return VIEW_ERROR;
        }
        setCartSessionVariable(NodeDataUtil.getString(shopConfigNode, "cartSessionVariable", ShopModule.DEFAULT_CART_SESSION_VARIABLE_NAME));

        // 2. shopping cart - or create one if it does not exist
        DefaultShoppingCart cart = (DefaultShoppingCart) getShoppingCart(shopConfigNode);
        if (cart == null) {
            log.error("Shopping cart could not be found!");
            getErrorMessages().put("remove.from.cart", "cart.not.found");
            return VIEW_ERROR;
        }

        HttpSession session = request.getSession();
        cart.setOrderDate(new Date());
        cart.setUserIP(request.getRemoteAddr() + ":" + request.getRemotePort());

        // NEW: Save via OCM
        Mapper mapper = new MgnlConfigMapperImpl();
        RequestObjectCacheImpl requestObjectCache = new RequestObjectCacheImpl();
        DefaultAtomicTypeConverterProvider converterProvider = new DefaultAtomicTypeConverterProvider();
        MgnlObjectConverterImpl oc = new MgnlObjectConverterImpl(mapper, converterProvider, new ProxyManagerImpl(), requestObjectCache);
        ObjectContentManager ocm = new ObjectContentManagerImpl(MgnlContext.getHierarchyManager("data").getWorkspace().getSession(), mapper);
        ((ObjectContentManagerImpl) ocm).setObjectConverter(oc);
        ((ObjectContentManagerImpl) ocm).setRequestObjectCache(requestObjectCache);

        if (StringUtils.isBlank(cart.getUuid())) {
            // Cart has not been saved before (this would most likely be the standard case)
            // Set the parent path according to the shop configuration
            cart.setParentPath(shopConfigNode.getNodeData("shopDataRootPath").getString() + "/" + ShopModule.CARTS_FOLDER_NAME);
            ocm.insert(cart);
            ocm.save();
            // @TODO: did ocm set the uuid and path? If not: How can we do that efficiently?
            log.debug("UUID of newly inserted shopping cart: " + cart.getUuid());
            log.debug("Path of newly inserted shopping cart: " + cart.getPath());
        }


        if (shopConfigNode != null) {
            String fromEmail = NodeDataUtil.getString(shopConfigNode, "shopMailsFromAddress");
            String adminMailAddress = NodeDataUtil.getString(shopConfigNode, "shopAdminMailAddress");
            String confirmationMailTemplate = NodeDataUtil.getString(shopConfigNode, "confirmationMailTemplate");
            String i18nBasename = NodeDataUtil.getString(shopConfigNode, "i18nBasename");
            // If requested, send a confirmation mail and to the shop admin as bcc.
            // Otherwise just send it to the shop administrator.
            if (sendConfirmation.booleanValue()) {
                String toEmail = cart.getBillingAddressMail();
                String toName = cart.getBillingAddressFirstname() + " " + cart.getBillingAddressLastname();
                if (StringUtils.isNotBlank(toEmail)) {
                    try {
                        sendConfirmationMail(cart, fromEmail, "shop.mail.from", toEmail, toName, adminMailAddress,
                                confirmationMailTemplate, i18nBasename);
                    } catch (MessagingException ex) {
                        log.error("Could not send confirmation mail!", ex);
                    } catch (Exception ex) {
                        log.error("Could not send confirmation mail!", ex);
                    }
                }
                if (StringUtils.isNotBlank(adminMailAddress)) {
                    try {
                        sendConfirmationMail(cart, fromEmail, "shop.mail.from", adminMailAddress, null, null,
                                confirmationMailTemplate, i18nBasename);
                    } catch (MessagingException ex) {
                        log.error("Could not send confirmation mail!", ex);
                    } catch (Exception ex) {
                        log.error("Could not send confirmation mail!", ex);
                    }
                }
            } else {
                try {
                    sendConfirmationMail(cart, fromEmail, "shop.mail.from", adminMailAddress, null, null,
                            confirmationMailTemplate, i18nBasename);
                } catch (MessagingException ex) {
                    log.error("Could not send confirmation mail!", ex);
                } catch (Exception ex) {
                    log.error("Could not send confirmation mail!", ex);
                }
            }
            String savedCartUUIDSessionVariable = NodeDataUtil.getString(shopConfigNode, "savedCartUUIDSessionVariable");
            if (StringUtils.isNotBlank(savedCartUUIDSessionVariable)) {
                // store uuid of saved cart in session variable
                session.setAttribute(savedCartUUIDSessionVariable, cart.getUuid());
            }
        }
        // The shopping cart has been saved by the CRUDPage.updateNode() method.
        // Now we need to clean up the session (remove the old shopping cart)
        session.removeAttribute(getCartSessionVariable());
        return VIEW_SUCCESS;

    }

    private void sendConfirmationMail(DefaultShoppingCart cart, String fromEmail, String fromNameKey, String toEmail,
            String toName, String bccEmail, String template, String i18nBasename) throws MessagingException, Exception {
        String language = cart.getLanguage();
        if (!StringUtils.isBlank(language)) {
            template += "_" + language;
        }

        HashMap mailData = new HashMap();
        mailData.put("cart", cart);

        ShopMailUtil.mail(fromEmail, toEmail, "confirmation.mail", template, mailData);
    }

        /**
     * This implementation does a redirect:
     * - to the targetPage or errorPage property, depending on the view (result) , using it as a url.
     * - to the referer if the view argument was null. (i.e. if the command method was void or returned null explicitely)
     * The view parameter passed to the method is ignored.
     * The errorMessages map, if not empty is added as a url encoded json string parameter.
     * @param view
     * @throws IOException
     */
    public void renderHtml(String view) throws IOException {
        log.debug("CRUDPage.renderHtml(\"" + view + "\")");
        final boolean success = VIEW_SUCCESS.equals(view);
        final String target;
        if (success) {
            target = refererOr(getTargetPage());
        } else {
            target = refererOr(getErrorPage());
        }

        if (target == null) {
            throw new IllegalStateException("Nowhere to go !");
        }
//        final String targetAndParameters = addAdditionalParametersToUrl(target);

//        final String uri = BasePublicUserParagraphAction.addErrorMessagesToUrl(targetAndParameters, errorMessages);
        response.sendRedirect(target);
    }

    /**
     * Returns a uri (with prefixed context path) to the given uri if it is not null, or the request's referer otherwise.
     * @param uri
     * @return
     */
    protected String refererOr(String uri) {
        log.debug("CRUDPage.refererOr()");
        if (uri == null) {
            return request.getHeader("referer");
        } else {
            return request.getContextPath() + uri;
        }
    }

    /**
     * Gets the shop configuration node for the current "shopKey".
     *
     * @return
     * @todo instead of relying fully on the shopKey one could also work with URL
     *       patterns
     */
    public Content getShopConfigurationNode() {
        Content shopConfigNode = null;
        if (StringUtils.isNotBlank(shopKey)) {
            shopConfigNode = ContentUtil.getContent("config", "/modules/shop/config/shops/" + shopKey);
        } else {
            // 1.2 If no shop name was provided look for a matching url pattern in
            // all shop configurations
            // TODO: Implement url pattern matching
        }
        return shopConfigNode;
    }

    /**
     * Gets or creates a shopping cart object based on the shop configuration.
     * When a cart needs to be created the method will look for a cart class name
     * in the following order:
     * <ol>
     * <li><b>Shop configuration</b>:
     * config:/modules/shop/config/shops/<i>[my_shop}</i>/cartBeanType which leads
     * to config:/modules/crud/config/beans/<i>[my_cart_bean]</i>/beanClass</li>
     * <li><b>Default shop module configuration</b>:
     * config:/modules/shop/config/defaultShoppingCartClass</li>
     * <li><b>Shop module</b>:
     * info.magnolia.module.shop.ShopModule.DEFAULT_CART_CLASS_NAME</li>
     *
     * @param shopConfigNode
     * @return the cart object
     */
    public ShoppingCart getShoppingCart(Content shopConfigNode) {
        ShoppingCart cart = null;
        if (shopConfigNode != null) {
            // 1. if the bean location was not provided in the request
            if (StringUtils.isBlank(getCartSessionVariable())) {
                setCartSessionVariable(NodeDataUtil.getString(shopConfigNode, "cartSessionVariable", ShopModule.DEFAULT_CART_SESSION_VARIABLE_NAME));
            }
            if (StringUtils.isBlank(getCartSessionVariable())) {
                return null;
            }
            // 2. once you now where to look... get the cart (or create it if it does
            // not exist)
            cart = (ShoppingCart) request.getSession().getAttribute(getCartSessionVariable());
            if (cart == null) {
                log.debug("No ShoppingCart object found at " + getCartSessionVariable());
                // create a shopping cart by looking for the cart class name
                // in the bean configuration
                String cartBeanType = NodeDataUtil.getString(shopConfigNode, "cartBeanType");
//                CRUDModule crudConfig = CRUDModule.getModuleConfig();
//                MgnlDataBeanController itemController = (MgnlDataBeanController) crudConfig.getBeans().get(cartBeanType);
//                String cartClassName = itemController.getBeanClass();
                // NEW: Get that info from the OCM configuration!
                // @TODO: Why ar the class descriptors in a Collection instead of a Map? For now
                // simply search for the node with the right name...
                String queryString = "/jcr:root/modules/ocm/config/classDescriptors/element(" + cartBeanType + ",mgnl:contentNode)";
                Collection matching = QueryUtil.query("config", queryString, "xpath", "mgnl:contentNode");
                if (matching.size() != 1) {
                    log.error(matching.size() + " class descriptors found for bean type " + cartBeanType);
                    return null;
                }
                Content cartClassDescriptor = (Content) matching.iterator().next();
                String cartClassName = NodeDataUtil.getString(cartClassDescriptor, "className", ShopModule.DEFAULT_CART_CLASS_NAME);
                if (StringUtils.isBlank(cartClassName)) {
                    cartClassName = shopConfigNode.getNodeData("cartClass").getString();
                }
                if (StringUtils.isBlank(cartClassName)) {
                    cartClassName = ShopModule.getInstance().getCartClassQualifiedName();
                }
                log.debug("Creating new cart of type " + cartClassName);
                try {
                    // create new cart
                    Class cartClass = Class.forName(cartClassName);
                    cart = (ShoppingCart) cartClass.newInstance();
                    request.getSession().setAttribute(getCartSessionVariable(), cart);
                } catch (ClassNotFoundException ex) {
                    log.error("Shop is misconfigured: Could not instantiate shopping cart of class " + cartClassName, ex);
                    getErrorMessages().put("error.on.cart", "shop.misconfigured");
                } catch (InstantiationException ex) {
                    log.error("Shop is misconfigured: Could not instantiate shopping cart of class " + cartClassName, ex);
                    getErrorMessages().put("error.on.cart", "shop.misconfigured");
                } catch (IllegalAccessException ex) {
                    log.error("Shop is misconfigured: Could not instantiate shopping cart of class " + cartClassName, ex);
                    getErrorMessages().put("error.on.cart", "shop.misconfigured");
                }
            }
        }
        return cart;
    }

    public Boolean getSendConfirmation() {
        return sendConfirmation;
    }

    public void setSendConfirmation(Boolean sendConfirmation) {
        this.sendConfirmation = sendConfirmation;
    }

    public String getShop() {
        return shopKey;
    }

    public void setShop(String shopKey) {
        this.shopKey = shopKey;
    }

    public String getShopKey() {
        return shopKey;
    }

    public void setShopKey(String shopKey) {
        this.shopKey = shopKey;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * @return the errorMessages
     */
    public Map getErrorMessages() {
        return errorMessages;
    }

    /**
     * @param errorMessages the errorMessages to set
     */
    public void setErrorMessages(Map errorMessages) {
        this.errorMessages = errorMessages;
    }

    /**
     * @return the cartSessionVarName
     */
    public String getCartSessionVariable() {
        return cartSessionVariable;
    }

    /**
     * @param cartSessionVarName the cartSessionVarName to set
     */
    public void setCartSessionVariable(String cartSessionVariable) {
        this.cartSessionVariable = cartSessionVariable;
    }

    /**
     * @return the targetPage
     */
    public String getTargetPage() {
        return targetPage;
    }

    /**
     * @param targetPage the targetPage to set
     */
    public void setTargetPage(String targetPage) {
        this.targetPage = targetPage;
    }

    /**
     * @return the errorPage
     */
    public String getErrorPage() {
        return errorPage;
    }

    /**
     * @param errorPage the errorPage to set
     */
    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }
}
