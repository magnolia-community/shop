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
package info.magnolia.module.shop.templates;

import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.module.templatingkit.templates.pages.STKPage;
import info.magnolia.module.templatingkit.templates.pages.STKPageModel;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.templating.functions.TemplatingFunctions;


import javax.jcr.Node;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton paragraph model, used in shopping cart paragraph.
 * @author tmiyar
 *
 */
public class ShopSingletonParagraphTemplateModel extends STKPageModel<STKPage> {

    private static Logger log = LoggerFactory.getLogger(ShopSingletonParagraphTemplateModel.class);
    private String currentShopName = "";

    public ShopSingletonParagraphTemplateModel(Node content, STKPage definition, RenderingModel<?> parent, STKTemplatingFunctions stkFunctions, TemplatingFunctions templatingFunctions, DamTemplatingFunctions damTemplatingFunctions) {
        super(content, definition, parent, stkFunctions, templatingFunctions, damTemplatingFunctions);
    }


    @Override
    public String execute() {
        String shopName = getCurrentShopName();
        MgnlContext.setAttribute(ShopUtil.ATTRIBUTE_SHOPNAME, shopName,
                Context.SESSION_SCOPE);
        ShopUtil.setShoppingCartInSession(shopName);
        String command = MgnlContext.getParameter("command");

        if (StringUtils.isNotEmpty(command)) {
            if (StringUtils.equals(command, "addToCart")) {
                ShopUtil.addToCart();
                return "";
            } else if (StringUtils.equals(command, "add")
                    || StringUtils.equals(command, "subtract") || StringUtils.equals(command, "removeall")) {
                String productUUID = MgnlContext.getParameter("product");
                ShopUtil.updateItemQuantity(productUUID, command, shopName);
            }
        }

        log.debug("Command " + command + " called");
        return super.execute();
    }

    protected String getCurrentShopName() {
        if (StringUtils.isEmpty(currentShopName)) {
            Node shopRoot = ShopUtil.getShopRoot();
            if (shopRoot != null) {
                currentShopName = PropertyUtil.getString(shopRoot, "currentShop");
            }
        }
        return currentShopName;
    }

    public void resetShoppingCart() {
        // initialize new cart
        MgnlContext.getWebContext().getRequest().getSession().removeAttribute(
        "shoppingCart");
        ShopUtil.setShoppingCartInSession(getCurrentShopName());
    }

    /**
     * @deprecated Use {@link ShopUtil#addToCart(String shopName)}
     */
    @Deprecated
    public void addToCart() {
        ShopUtil.addToCart(getCurrentShopName());
    }
    
    /**
     * Updates the quantity of a product in the cart. Attention: This only works for products with no options!
     * @param productUUID The uuid of the product for which the quantity needs to be changed.
     * @param command The operator (add, subtract, removeall)
     * @deprecated Use {@link ShopUtil#updateItemQuantity(String productUUID, String command, String shopName)
     */
    @Deprecated
    protected void updateItemQuantity(String productUUID, String command) {
        ShopUtil.updateItemQuantity(productUUID, command, getCurrentShopName());
    }
}
