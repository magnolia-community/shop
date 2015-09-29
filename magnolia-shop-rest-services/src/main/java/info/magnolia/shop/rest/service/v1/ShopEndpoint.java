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
package info.magnolia.shop.rest.service.v1;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import info.magnolia.cms.security.User;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.commands.CommandsManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;
import info.magnolia.module.mail.MailModule;
import info.magnolia.module.mail.MailTemplate;
import info.magnolia.module.mail.templates.MailAttachment;
import info.magnolia.module.mail.templates.MgnlEmail;
import info.magnolia.rest.AbstractEndpoint;
import info.magnolia.shop.ShopConfiguration;
import info.magnolia.shop.ShopNodeTypes;
import info.magnolia.shop.accessors.ShopAccessor;
import info.magnolia.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.shop.beans.ShoppingCart;
import info.magnolia.shop.exceptions.ShopConfigurationException;
import info.magnolia.shop.rest.beans.Product;
import info.magnolia.shop.rest.beans.ProductInfo;
import info.magnolia.shop.rest.beans.QuantityInfo;
import info.magnolia.shop.rest.beans.ShoppingCartWithItems;
import info.magnolia.shop.rest.service.ShopEndpointDefinition;
import info.magnolia.shop.util.ShopUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.util.ISO9075;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by will on 23.06.15.
 *
 * @param <D> The endpoint definition
 */

@Path("/v1/shop")
@Api(value = "/v1/shop", description = "The shop REST API")
public class ShopEndpoint<D extends ShopEndpointDefinition> extends AbstractEndpoint<D> {

    private static final String STATUS_MESSAGE_OK = "OK";
    private static final String STATUS_MESSAGE_BAD_REQUEST = "Request not understood due to errors or malformed syntax";
    private static final String STATUS_MESSAGE_UNAUTHORIZED = "Unauthorized";
    private static final String STATUS_MESSAGE_ACCESS_DENIED = "Access denied";
    private static final String STATUS_MESSAGE_CART_NOT_FOUND = "Cart not found";
    private static final String STATUS_MESSAGE_NO_PRODUCTS_FOUND = "No products found";
    private static final String STATUS_MESSAGE_ITEM_NOT_FOUND = "Item not found";
    private static final String STATUS_MESSAGE_ERROR_OCCURRED = "Error occurred";
    private static final String STATUS_MESSAGE_ERROR_CART_COULD_NOT_BE_CREATED = "Cart could not be created";
    private static final String PPRODUCTS_WORKSPACE = "shopProducts";
    public static final String LEGAL_ENTITY_ID_KEY = "legalEntityID";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DamTemplatingFunctions damTemplatingFunctions;

    private MailModule mailModule;

    @Inject
    private CommandsManager cm;

    @Inject
    protected ShopEndpoint(D endpointDefinition, DamTemplatingFunctions damTemplatingFunctions, MailModule mailModule) {
        super(endpointDefinition);
        this.damTemplatingFunctions = damTemplatingFunctions;
        this.mailModule = mailModule;
    }

    /**
     * Returns a list of products. If no product category has been provided, it will return all products of the selected shop.
     *
     * @param shopName Name of the shop
     * @param productCategoryUUID Optional UUID of a product category to filter the products
     * @return REST response containing the shopping cart - or 401, 404, 500
     * message
     */
    @GET
    @Path("/{shopName}/products")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Get products", notes = "Loads all products or the products of a selected category including image links and price info for user's price category", response = Product.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 403, message = STATUS_MESSAGE_ACCESS_DENIED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_NO_PRODUCTS_FOUND),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response getProducts(@ApiParam(value = "Name of the shop configuration",required=true ) @PathParam("shopName") String shopName,
                                @ApiParam(value = "Optional uuid of product category to limit list of products the category") @QueryParam("category") String productCategoryUUID) {

        // 0. set the language from the header info
        String language = getLanguageFromRequestHeader();
        if (language != null) {
            MgnlContext.getAggregationState().setLocale(new Locale(language));
        }

        // 1. get the products
        ArrayList<Product> products = null;
        String query = "/jcr:root/" + ISO9075.encodePath(shopName) + "//element(*," + ShopNodeTypes.SHOP_PRODUCT + ")";
        if (StringUtils.isNotBlank(productCategoryUUID)) {
            query += "[jcr:contains(productCategoryUUIDs/., '" + productCategoryUUID + "')]";
        }
        try {
            NodeIterator matching = QueryUtil.search(PPRODUCTS_WORKSPACE, query, "xpath", ShopNodeTypes.SHOP_PRODUCT);
            // 2. wrap the nodes in a Product bean so RESTEasy can convert it
            products = new ArrayList<>();
            while (matching.hasNext()) {
                Product product = new Product(matching.nextNode(), shopName);
                // convert the image uuids to links
                String imageUUID, imageLink;
                for (int i=0; i<product.getImages().size(); i++) {
                    imageUUID = product.getImages().get(i);
                    imageLink = damTemplatingFunctions.getAssetLink(imageUUID, "shop-image");
                    product.getImages().set(i, imageLink);
                }
                products.add(product);
            }
        } catch (RepositoryException e) {
            log.error("Could not get products with query " + query, e);
        }

        if (products != null) {
            HashMap<String, Object> container = new HashMap<>();
            container.put("products", products);
            return Response.ok(container).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


    /**
     * Returns the current users shopping cart.
     *
     * @param shopName Name of the shop
     * @return REST response containing the shopping cart - or 401, 404, 500
     * message
     */
    @GET
    @Path("/{shopName}/carts/current")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(
            value = "Get the shopping cart",
            notes = "Gets the shopping cart of the current user in the provided shop. If the current user does not have a shopping cart yet, it will return a 404.",
            response = ShoppingCartWithItems.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_CART_NOT_FOUND),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response getCurrentUsersShoppingCart(@ApiParam(value = "Name of the shop configuration",required=true ) @PathParam("shopName") String shopName) {

        ShoppingCart cart = ShopUtil.getShoppingCart(shopName);

        if (cart != null) {
            return Response.ok(cart).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Creates a shopping cart for the current user. If the cart already existed
     * it will not create a new one but rather return the existing one.
     *
     * @param shopName Name of the shop (as multiple shops would be
     * theoretically supported)
     * @return REST response containing the shopping cart - or 401, 404, 500
     * message
     */
    @POST
    @Path("/{shopName}/carts")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Init new cart", notes = "Initializes a cart for the current user and the provided shop. If the user already has a cart, it will return the existing one.", response = ShoppingCartWithItems.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
        @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
        @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_CART_COULD_NOT_BE_CREATED)
    })
    public Response initializeCurrentUsersShoppingCart(@ApiParam(value = "Name of the shop configuration",required=true ) @PathParam("shopName") String shopName) {

        ShopUtil.setShoppingCartInSession(shopName);
        ShoppingCart cart = ShopUtil.getShoppingCart(shopName);
        if (cart != null) {
            String legalEntity = getCurrentLegalEntityName();
            cart.setCustomerNumber(legalEntity);
            return Response.ok(cart).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getCurrentLegalEntityName() {
        // TODO: check property name
        String legalEntity = MgnlContext.getUser().getProperty(LEGAL_ENTITY_ID_KEY);
        if (StringUtils.isBlank(legalEntity)) {
            User user = MgnlContext.getUser();
            log.error("User {} has no value set for {}", user.getName(), LEGAL_ENTITY_ID_KEY);
            if (!user.getName().equals("anonymous")) {
                log.info("Will use username " + user.getName() + " as legal entity name");
                legalEntity = MgnlContext.getUser().getName();
            }
        }
        return legalEntity;
    }

    /**
     * Updates the data on the current users cart. For now it will not update
     * the cart items. Use the specific item methods for this.
     *
     * @param shopName Name of the shop
     * @param body Data to be filled into the shopping cart.
     * @return REST response containing the shopping cart - or 401, 404, 500
     * message
     */
    @PUT
    @Path("/{shopName}/carts/current")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(
            value = "Update address data in cart",
            notes = "Setts the passed in order, billing and shipping addresses in the cart.",
            response = ShoppingCartWithItems.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_CART_NOT_FOUND),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response updateShoppingCart(
            @ApiParam(value = "Name of the shop configuration",required=true ) @PathParam("shopName") String shopName,
            @ApiParam(value = "Body containing the data to update") info.magnolia.shop.rest.beans.ShoppingCart body) {

        ShoppingCart cart = ShopUtil.getShoppingCart(shopName);

        if (cart == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
            // let's try updating the cart in the session with BeanUtils.
            Map updateData = BeanUtils.describe(body);
            cart.updateCartData(updateData);
            return Response.ok(cart).build();
        } catch (IllegalAccessException e) {
            log.error("Could not update cart", e);
        } catch (InvocationTargetException e) {
            log.error("Could not update cart", e);
        } catch (NoSuchMethodException e) {
            log.error("Could not update cart", e);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    }

    /**
     * Adds a product to the shopping cart.
     *
     * @param shopName Name of the shop
     * @return REST response containing the updated shopping cart - or 401, 404, 500
     * message
     */
    @POST
    @Path("/{shopName}/carts/current/items")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Add item to cart", notes = "Adds a product to the cart of the current user and returns the updated cart", response = ShoppingCartWithItems.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_CART_NOT_FOUND),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response addProductToShoppingCart(
            @ApiParam(value = "Name of the shop configuration",required=true ) @PathParam("shopName") String shopName,
            @ApiParam(value = "Body containing the info of the product to be added" ,required=true ) ProductInfo body) {

        // 0. set the language from the header info
        String language = getLanguageFromRequestHeader();
        if (language != null) {
            MgnlContext.getAggregationState().setLocale(new Locale(language));
        }

        ShoppingCart cart = ShopUtil.getShoppingCart(shopName);

        if (cart != null) {
            cart.addToShoppingCart(body.getProductUUID(), body.getQuantity(), null);
            return Response.ok(cart).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Updates a shopping cart item.
     *
     * @param shopName Name of the shop
     * @return REST response containing the updated shopping cart - or 401, 404, 500
     * message
     */
    @PUT
    @Path("/{shopName}/carts/current/items/{itemName}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Updates item in cart", notes = "Sets the quantity of the selected cart item to the new value provided in the body", response = ShoppingCartWithItems.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_CART_NOT_FOUND),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response updateShoppingCartItem(@ApiParam(value = "Name of the shop configuration",required=true ) @PathParam("shopName") String shopName,
                                           @ApiParam(value = "Unique name of item within a cart (see \"name\" attribute in cart item)",required=true ) @PathParam("itemName") String itemName,
                                           @ApiParam(value = "payload containing the new quantity this item should be set to." ,required=true ) QuantityInfo body) {

        ShoppingCart cart = ShopUtil.getShoppingCart(shopName);

        if (cart != null) {
            ShopUtil.updateItemQuantity(shopName, body.getQuantity(), itemName);
            return Response.ok(cart).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Removes an item from the shopping cart.
     *
     * @param shopName Name of the shop
     * @return REST response containing the updated shopping cart - or 401, 404, 500
     * message
     */
    @DELETE
    @Path("/{shopName}/carts/current/items/{itemName}")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Removes item from cart", notes = "Removes an item from the cart of the current user and returns the updated cart", response = ShoppingCartWithItems.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_CART_NOT_FOUND),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response deleteShoppingCartItem(@ApiParam(value = "Name of the shop configuration",required=true ) @PathParam("shopName") String shopName,
                                           @ApiParam(value = "Unique name of item within a cart (see \"name\" attribute in cart item)",required=true ) @PathParam("itemName") String itemName) {

        ShoppingCart cart = ShopUtil.getShoppingCart(shopName);

        if (cart != null) {
            ShopUtil.updateItemQuantity(shopName, 0, itemName);
            return Response.ok(cart).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/{shopName}/carts/current/process")
    @Produces({MediaType.APPLICATION_JSON})
    @ApiOperation(value = "Processes cart", notes = "Saves cart and sends mails to shop owner and customer", response = ShoppingCartWithItems.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK),
            @ApiResponse(code = 401, message = STATUS_MESSAGE_UNAUTHORIZED),
            @ApiResponse(code = 404, message = STATUS_MESSAGE_CART_NOT_FOUND),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response processShoppingCart(@ApiParam(value = "Name of the shop configuration",required=true ) @PathParam("shopName") String shopName) {
        ShoppingCart cart = ShopUtil.getShoppingCart(shopName);
        if (cart == null) {
            log.error("Cart not found!");
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // save cart
        ShopConfiguration shopConfiguration = null;

        try {
            shopConfiguration = new ShopAccessor(shopName).getShopConfiguration();
        } catch (Exception e) {
            log.error("cant get shop configuration for " + shopName, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        // save the cart
        cart.onPreSave(shopConfiguration);
        try {
            cart.onSave(shopConfiguration);
        } catch (RepositoryException e) {
            log.error("Could not save cart", e);
        } catch (ShopConfigurationException e) {
            log.error("Could not save cart", e);
        }
        cart.onPostSave(shopConfiguration);

        // send contact mail
        // mailTemplate
        Map<String, Object> params = new HashMap<>();
        params.put("cart", cart);
        if (cart instanceof DefaultShoppingCartImpl) {
            String to = null; // will be set by template
            String from = ((DefaultShoppingCartImpl) cart).getOrderAddressMail();
            
            if (StringUtils.isBlank(from)) {
                from = ((DefaultShoppingCartImpl) cart).getBillingAddressMail();
            }
            try {
                List<MailAttachment> mailAttachments = new ArrayList<MailAttachment>();
                sendTemplateMail(shopName + "ContactMail", mailAttachments, from, to, params);
            } catch (Exception e) {
                log.error("Could not send contact mail from " + from + " to " + to, e);
            }
        } else {
            log.error("Could not send contact mail as shopping cart does not extend DefaultShoppingCartImpl");
        }
        // send confirmation mail
        if (cart instanceof DefaultShoppingCartImpl) {
            String to = ((DefaultShoppingCartImpl) cart).getOrderAddressMail();
            if (StringUtils.isBlank(to)) {
                to = ((DefaultShoppingCartImpl) cart).getBillingAddressMail();
            }
            String from = null; // will be set by template
            try {
                List<MailAttachment> mailAttachments = new ArrayList<MailAttachment>();
                sendTemplateMail(shopName + "ConfirmationMail_" + cart.getLanguage(), mailAttachments, from, to, params);
            } catch (Exception e) {
                log.error("Could not send confirmation mail from " + from + " to " + to, e);
            }
        } else {
            log.error("Could not send confirmation mail as shopping cart does not extend DefaultShoppingCartImpl");
        }
        return Response.ok(cart).build();
    }

    @GET
    @Path("/{shopName}/orders/currentLegalEntity")
    @Produces({MediaType.APPLICATION_JSON})//, "application/xml" })
    @ApiOperation(value = "Get orders of current legal entity", notes = "Gets all orders of the legal entity of the current user. If the current user does not have any orders yet, it will return a 404.", response = ShoppingCartWithItems.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "No orders found"),
            @ApiResponse(code = 500, message = "Error occurred") })

    public Response getOrdersOfCurentLegalEntity(@ApiParam(value = "Name of the shop configuration",required=true ) @PathParam("shopName") String shopName) {

        try {
            Collection<ShoppingCart> carts = ShopUtil.getOrdersByCustomerNumber(shopName, getCurrentLegalEntityName());
            return Response.ok(carts).build();
        } catch (RepositoryException e) {
            log.error("Could not get carts for current legal entity.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void sendTemplateMail(String templateName, List<MailAttachment> mailAttachments, String from, String to, Map<String, Object> parameters) throws Exception {
        Iterator<MailTemplate> iter = mailModule.getTemplatesConfiguration().iterator();
        
        mailModule.getTemplatesConfiguration();
        
        MailTemplate template = null;
        while (iter.hasNext()) {
            template = iter.next();
            if (!template.getName().equals(templateName)) {
                template = null;
            } else {
                break;
            }
        }
        if (template != null) {
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(MailTemplate.MAIL_TO, to);
            if (StringUtils.isBlank(to)) {
                params.put(MailTemplate.MAIL_TO, template.getTo());
            }
            params.put(MailTemplate.MAIL_FROM, from);
            if (StringUtils.isBlank(from)) {
                params.put(MailTemplate.MAIL_FROM, template.getFrom());
            }
            params.putAll(template.getParameters());
            if (parameters != null) {
                params.putAll(parameters);
            }

            
            MgnlEmail email = mailModule.getFactory().getEmailFromTemplate(template.getName(), mailAttachments, params);
            // no need to specifically add recipient > already added from the "params" in the getEmailFromTemplate() methode
//            email.addRecipient(Message.RecipientType.TO, new InternetAddress((String) params.get(MailTemplate.MAIL_TO)));
            email.setFrom(new InternetAddress((String) params.get(MailTemplate.MAIL_FROM)));
            
            email.setBody(template.getText());
            
            // FIXME 19-Aug-2015/nhattan: look like a bug of mail module.
            // email.setBodyFromResourceFile();
            
            mailModule.getHandler().sendMail(email);
        } else {
            log.error("mail template is not configure: " + templateName);
        }
    }

    private String getLanguageFromRequestHeader() {
        String acceptLanguage = MgnlContext.getWebContext().getRequest().getHeader("Accept-Language");
        if (StringUtils.isBlank(acceptLanguage)) {
            return null;
        }
        String[] parts = StringUtils.split(acceptLanguage, ",");    // e.g. de-de, de, en;q=0.5, fr;q=0.2
        String selectedLanguage = parts[0].trim();                  // e.g. de-de
        selectedLanguage = StringUtils.substringBefore(selectedLanguage, ";");  // e.g. de-de;
        selectedLanguage = StringUtils.substringBefore(selectedLanguage, "-");  // e.g. de

        return selectedLanguage;
    }

}
