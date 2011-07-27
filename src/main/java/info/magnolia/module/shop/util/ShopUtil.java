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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.beans.ShoppingCart;

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

    /**
     * Gets the shop current node, assumes one shop per site.
     * TODO: Use category subcategory
     */
    public static Content getShopRoot() {
        Content currentPage = MgnlContext.getAggregationState().getMainContent();
        try {
          while (currentPage != null && !currentPage.getTemplate().equals("shopHome")) {
              currentPage = currentPage.getParent();
          }
        } catch (RepositoryException e) {
          log.error("No template found with template name shopHome");
        }
    
        return currentPage;
    }
  
    public static Messages getMessages() {
        Locale currentLocale = I18nContentSupportFactory.getI18nSupport()
            .getLocale();
        final Messages msg = MessagesManager.getMessages(
            "info.magnolia.module.shop.messages", currentLocale);
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
                //do nothing
            }
            if (cart == null && shopConfiguration != null) {
                
                try {
                    cart = shopConfiguration.getCartClass();
                    MgnlContext.setAttribute("shoppingCart", cart, Context.SESSION_SCOPE);
                } catch (Exception e) {
                    
                }
            }
        }
    }

    public static String getShopName() {
        return (String) MgnlContext.getAttribute("shopName");
    }

    public static ShoppingCart getShoppingCart() {
        return (ShoppingCart) MgnlContext.getAttribute("shoppingCart");
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
  
    public static Content getShopPriceCategory(ShopConfiguration shopConfiguration) {
        if(shopConfiguration != null) {
            try {
                return shopConfiguration.getPriceCategoryManager().getPriceCategoryInUse();
            } catch (Exception e) {
                
            }
        }
        return null;
        

    }
    
    public static Content getCurrencyByUUID(String uuid) {
        return new I18nContentWrapper(ContentUtil.getContentByUUID("data", uuid));
    }

}
