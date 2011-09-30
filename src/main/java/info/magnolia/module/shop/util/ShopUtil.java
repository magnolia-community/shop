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
package info.magnolia.module.shop.util;

import info.magnolia.cms.security.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentSupportFactory;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.shop.ShopConfiguration;
import info.magnolia.module.shop.accessors.ShopAccesor;
import info.magnolia.module.shop.accessors.ShopProductAccesor;
import info.magnolia.module.shop.accessors.ShopProductCategoryAccesor;
import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.beans.ShoppingCart;
import info.magnolia.module.templatingkit.templates.category.TemplateCategoryUtil;
import javax.jcr.PathNotFoundException;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Paragraphs util class.
 * @author tmiyar
 *
 */
public class ShopUtil {

    private static Logger log = LoggerFactory.getLogger(ShopUtil.class);
    public static String ATTRIBUTE_SHOPNAME = "shopName";
    public static String ATTRIBUTE_SHOPPINGCART = "shoppingCart";
    
    public static String SHOP_TEMPLATE_NAME = "shopHome";
    public static String I18N_BASENAME = "info.magnolia.module.shop.messages";

    /**
     * Gets the shop current node.
     */
    public static Content getShopRoot() {
        Content currContent = MgnlContext.getAggregationState().getMainContent();
        try {
         while (currContent.getLevel() >= 0) {
                if (TemplateCategoryUtil.getTemplateSubCategory(currContent).equals("shopHome")) {
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
     * Used in product dialog, for getting productCategories, productPrices... and
     * the storageNode is null.
     */
    public static String getShopNameFromPath() {
        String mgnlPath = MgnlContext.getParameter("mgnlPath");
        String shopName = "";
        if(StringUtils.isNotEmpty(mgnlPath)) {
            String[] pathSplit = StringUtils.split(mgnlPath, "/");
            if(pathSplit.length >= 2){
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

    public static List<Content> transformIntoI18nContentList(List<Content> contentList) {
        List<Content> i18nProductList = new ArrayList<Content>();
        for (Content content : contentList) {
            i18nProductList.add(new I18nContentWrapper(content));
        }
        return i18nProductList;
    }

    public static String getCurrencyTitle() {

        ShopConfiguration shopConfiguration;
        try {
            shopConfiguration = new ShopAccesor(getShopName()).getShopConfiguration();

            if (shopConfiguration != null) {
                Content priceCategory = getShopPriceCategory(shopConfiguration);
                Content currency = getCurrencyByUUID(NodeDataUtil.getString(priceCategory,
                        "currencyUUID"));
                return NodeDataUtil.getString(currency, "title");
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
                Content priceCategory = getShopPriceCategory(shopConfiguration);
                Content currency = getCurrencyByUUID(NodeDataUtil.getString(priceCategory,
                        "currencyUUID"));
                return NodeDataUtil.getString(currency, "formatting");
            }
        } catch (Exception e) {
            //nothing
        }
        return "";
    }

    public static Content getShopPriceCategory(ShopConfiguration shopConfiguration) {
        if (shopConfiguration != null) {
            try {
                return shopConfiguration.getPriceCategoryManager().getPriceCategoryInUse();
            } catch (Exception e) {
                log.error("Error in shop " + getShopName(), e);
            }
        }
        return null;
    }

    public static Content getCurrencyByUUID(String uuid) {
        return new I18nContentWrapper(ContentUtil.getContentByUUID("data", uuid));

    }
    
    public static String getPath(boolean removeEndToken, String... strings ) {
        String path = "/";
        
        for (String string : strings) {
            path += string + "/";
        }
        if(removeEndToken) {
            path = StringUtils.chomp(path, "/");
        }
        return path;
    }
    
    public static String getPath(String... strings ) {
        return getPath(true, strings);
    }
    
    public static List<Content> findTaggedProducts(String tagUUID) {
        List<Content> productList = new ArrayList<Content>();
        Map<String, Content> productMap = new HashMap<String, Content>();
        try {
            
            Collection<Content> productCategories = ShopProductCategoryAccesor.getTaggedProductCategories(tagUUID);
            //for each category, get all products
            for (Iterator<Content> iterator = productCategories.iterator(); iterator
                    .hasNext();) {
                Content productCategoryNode = iterator.next();
                productList.addAll(ShopProductAccesor.getProductsByProductCategory(productCategoryNode.getUUID()));
            }
            
            //remove duplicates
            for (Content content : productList) {
                productMap.put(content.getUUID(), content);
            }
            productList.clear();
            productList.addAll(productMap.values());
        } catch (Exception e) {
            //return empty list
        }
        return productList;
    }
}
