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
package info.magnolia.module.shop.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.shop.beans.CartItemOption;
import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.beans.ShoppingCart;
import info.magnolia.module.shop.beans.ShoppingCartItem;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.templating.functions.TemplatingFunctions;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.module.templatingkit.navigation.Link;
import info.magnolia.module.templatingkit.navigation.LinkImpl;
import info.magnolia.module.templatingkit.templates.pages.STKPage;
import info.magnolia.module.templatingkit.templates.pages.STKPageModel;

import java.util.HashMap;
import java.util.Iterator;
import javax.jcr.PathNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets the extended breadcrumb and navigation for shop items.
 * @author tmiyar
 *
 */
public class ShopSingletonParagraphTemplateModel extends STKPageModel {

    public ShopSingletonParagraphTemplateModel(Node content,
            STKPage definition, RenderingModel<?> parent,
            STKTemplatingFunctions stkFunctions,
            TemplatingFunctions templatingFunctions) {
        super(content, definition, parent, stkFunctions, templatingFunctions);
    }

    private static Logger log = LoggerFactory.getLogger(ShopSingletonParagraphTemplateModel.class);
    private String currentShopName = "";

    

    @Override
    public String execute() {
        MgnlContext.setAttribute(ShopUtil.ATTRIBUTE_SHOPNAME, getCurrentShopName(),
                Context.SESSION_SCOPE);
        ShopUtil.setShoppingCartInSession();
        String command = MgnlContext.getParameter("command");

        if (StringUtils.isNotEmpty(command)) {
            if (StringUtils.equals(command, "addToCart")) {
                addToCart();
                return "";
            } else if (StringUtils.equals(command, "add")
                    || StringUtils.equals(command, "subtract") || StringUtils.equals(command, "removeall")) {
                String productUUID = MgnlContext.getParameter("product");
                updateItemQuantity(productUUID, command);
            }
        }

        return super.execute();
    }

    protected String getCurrentShopName() {
        if (StringUtils.isEmpty(currentShopName)) {
            Content shopRoot = ShopUtil.getShopRoot();
            if (shopRoot != null) {
                currentShopName = NodeDataUtil.getString(shopRoot, "currentShop");
            }
        }
        return currentShopName;
    }

    public Collection<Link> getBreadcrumb() throws RepositoryException {
        List<Link> items = new ArrayList<Link>();

        // add website nodes
        Content root = ContentUtil.asContent(getSiteRoot());
        Content current = ContentUtil.asContent(content);
        while (current.getLevel() >= root.getLevel()) {
            // only the nodes that are not hidden in navigation
            if (!NodeDataUtil.getBoolean(current, "hideInNav", false)) {
                items.add(new LinkImpl(current.getJCRNode(), templatingFunctions));
            }
            if (current.getLevel() == 0) {
                break;
            }
            current = current.getParent();
        }

        Collections.reverse(items);
        return items;
    }

    public void resetShoppingCart() {
        // initialize new cart
        MgnlContext.getWebContext().getRequest().getSession().removeAttribute(
                "shoppingCart");
        ShopUtil.setShoppingCartInSession();
    }

    protected void addToCart() {
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
        HashMap<String, CartItemOption> options = new HashMap<String, CartItemOption>();
        String currKey, optionUUID;
        Content optionNode, optionSetNode;
        CartItemOption cio;
        while (keysIter.hasNext()) {
            currKey = (String) keysIter.next();
            if (currKey.startsWith("option_")) {
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
            log.error("Cannot add item to cart because no \"product\" parameter was found in the request");
        } else {
            ShoppingCart cart = ShopUtil.getShoppingCart();
            int success = cart.addToShoppingCart(product, quantity, options);
            if (success <= 0) {
                log.error("Cannot add item to cart because no product for "
                        + product + " could be found");

            }
        }
    }

    protected void updateItemQuantity(String productUUID, String command) {
        ShoppingCart shoppingCart = ShopUtil.getShoppingCart();
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
            ShoppingCartItem shoppingCartItem = (ShoppingCartItem) shoppingCart.getCartItems().get(indexOfProductInCart);
            int quantity = shoppingCartItem.getQuantity();

            if (command.equals("add")) {
                shoppingCartItem.setQuantity(++quantity);
            } else if (command.equals("subtract") || command.equals("removeall")) {
                if (quantity <= 1 || command.equals("removeall")) {
                    shoppingCart.getCartItems().remove(indexOfProductInCart);
                } else {
                    shoppingCartItem.setQuantity(--quantity);
                } // quantity <=1

            } // else command
        } // else product on cart
    }
}
