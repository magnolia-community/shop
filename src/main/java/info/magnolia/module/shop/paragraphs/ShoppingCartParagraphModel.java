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
package info.magnolia.module.shop.paragraphs;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.beans.ShoppingCart;
import info.magnolia.module.shop.beans.ShoppingCartItem;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;
import info.magnolia.module.templatingkit.navigation.LinkImpl;
import info.magnolia.module.templatingkit.util.STKUtil;

/**
 * Shopping cart paragraph.
 * 
 * @author tmiyar
 * 
 */
public class ShoppingCartParagraphModel extends ShopParagraphModel {

  public ShoppingCartParagraphModel(Content content,
      RenderableDefinition definition, RenderingModel parent) {
    super(content, definition, parent);

  }

  @Override
  public String execute() {
    String command = MgnlContext.getParameter("command");
    if (StringUtils.isNotEmpty(command) && StringUtils.equals(command, "add")
        || StringUtils.equals(command, "substract") || StringUtils.equals(command, "removeall")) {
      String productUUID = MgnlContext.getParameter("product");
      updateItemQuantity(productUUID, command);
    }
    return "";
  }

  protected void updateItemQuantity(String productUUID, String command) {
    ShoppingCart shoppingCart = getShoppingCart();
    int indexOfProductInCart = ((DefaultShoppingCartImpl) shoppingCart)
        .indexOfProduct(productUUID);
    if (indexOfProductInCart >= 0) {

      ShoppingCartItem shoppingCartItem = (ShoppingCartItem) shoppingCart
          .getCartItems().get(indexOfProductInCart);
      int quantity = shoppingCartItem.getQuantity();

      if (command.equals("add")) {
        shoppingCartItem.setQuantity(++quantity);
      } else if (command.equals("substract") || command.equals("removeall")) {
        if (quantity <= 1 || command.equals("removeall")) {
          shoppingCart.getCartItems().remove(indexOfProductInCart);
        } else {
          shoppingCartItem.setQuantity(--quantity);
        } // quantity <=1

      } // else command
    } // else product on cart
  }

  public String getCommandLink(String command, String productUUID) {
    return new LinkImpl(MgnlContext.getAggregationState().getMainContent())
        .getHref()
        + "?command=" + command + "&product=" + productUUID;
  }

  public String getCheckoutFormLink() {
    try {
      Content formPage = STKUtil.getContentByTemplateCategorySubCategory(
          getSiteRoot(), "feature", "checkoutform");
      return new LinkImpl(formPage).getHref();
    } catch (RepositoryException e) {
      // TODO
    }
    return "";
  }

}
