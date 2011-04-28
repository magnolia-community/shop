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

import java.util.Locale;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentSupportFactory;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.shop.ShopModule;
import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.beans.ShoppingCart;
import info.magnolia.module.templating.MagnoliaTemplatingUtilities;
import info.magnolia.module.templatingkit.util.STKUtil;
import info.magnolia.objectfactory.Classes;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShopUtil {

  private static Logger log = LoggerFactory.getLogger(ShopUtil.class);

  /**
   * Gets the shop current node, assumes one shop per site.
   */
  public static Content getShopRoot(Content siteRoot) {
    Content shopRoot = null;
    try {
      shopRoot = STKUtil.getContentByTemplateCategorySubCategory(siteRoot,
          "feature", "shopHome");
    } catch (RepositoryException e) {
      log.error("No template found with category feature, subcategory shopHome");
    }

    return shopRoot;
  }

  public static String getCategoryLink(Content category, Content siteRoot) {
    String link = MagnoliaTemplatingUtilities.getInstance().createLink(
        getShopRoot(siteRoot));
    return link.replace(".html", "." + category.getUUID() + ".html");
  }

  public static Messages getMessages() {
    Locale currentLocale = I18nContentSupportFactory.getI18nSupport()
        .getLocale();
    final Messages msg = MessagesManager.getMessages(
        "info.magnolia.module.shop.messages", currentLocale);
    return msg;
  }

  public static void setShoppingCart() {
    String shopName = getShopName();

    if (StringUtils.isNotEmpty(shopName)) {

      DefaultShoppingCartImpl cart = (DefaultShoppingCartImpl) getShoppingCart();
      if (cart == null) {
        Content priceCategory = ShopModule.getInstance()
            .getCurrentShopConfiguration(shopName).getPriceCategoryManager()
            .getPriceCategoryInUse();
        cart = Classes.quietNewInstance(ShopModule.getInstance().getCartClassQualifiedName(), priceCategory);
        cart.setLanguage(getLanguage());
        MgnlContext.setAttribute("shoppingCart", cart, Context.SESSION_SCOPE);
      }
    }
  }

  public static String getLanguage() {
    return I18nContentSupportFactory.getI18nSupport()
    .getLocale().getLanguage();
  }

  public static String getShopName() {
    return (String) MgnlContext.getAttribute("shopName");
  }

  public static ShoppingCart getShoppingCart() {
    
    return (ShoppingCart) MgnlContext.getAttribute("shoppingCart");
  }
  
  /**
   * Gets localized string from data repository.
   * 
   */
  public String getString(Content node, String nodeDataName) {
    Locale currentLocale = I18nContentSupportFactory.getI18nSupport()
        .getLocale();
    return NodeDataUtil.getString(node, nodeDataName + "_"
        + currentLocale.getLanguage(), NodeDataUtil.getString(node,
        nodeDataName));
  }

}
