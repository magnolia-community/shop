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
package info.magnolia.module.shop.beans;

import ch.fastforward.magnolia.crud.CRUDModule;
import ch.fastforward.magnolia.crud.MgnlDataBean;
import ch.fastforward.magnolia.crud.MgnlDataBeanController;
import info.magnolia.cms.core.Content;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author will
 */
public class ShoppingCartController extends MgnlDataBeanController {

  private static Logger log = LoggerFactory.getLogger(ShoppingCartController.class);
  private String cartItemBeanType = "shoppingCartItem";

  @Override
  public Content nodeForBean(MgnlDataBean bean, boolean update, String parentPath) {
    log.debug("ShoppingCartController.nodeForBean()");
    // create/update the cart node
    Content node = super.nodeForBean(bean, update, parentPath);
    if (node != null) {
      DefaultShoppingCart cart = (DefaultShoppingCart) bean;
      cart.setUUID(node.getUUID());
      // get the item controller
      CRUDModule crudConfig = CRUDModule.getModuleConfig();
      MgnlDataBeanController itemController = (MgnlDataBeanController) crudConfig.getBeans().get(getCartItemBeanType());
      ShoppingCartItem cartItem;
      Content itemNode;
      // create/update the item nodes
      for (int i = 0; i < cart.getCartItemsCount(); i++) {
        cartItem = (ShoppingCartItem) cart.getCartItems().get(i);
        if (StringUtils.isBlank(cartItem.getName())) {
          cartItem.setName("item_" + i);
        }
        log.debug("saving cart item " + cartItem.getName() + " mit controller " + itemController.getClass().getName());
        // itemNode = itemController.nodeForBean((MgnlDataBean)
        // cart.getCartItems().get(i), update, cart.getName());
        // TODO: This only creates a new node (if UUID is null) but does not
        // update an existng one...
        itemNode = itemController.createNodeForBeanAndRelativePath(cartItem, node.getHandle());
        log.debug("cart item node: " + itemNode);
      }
    }
    return node;
  }

  public String getCartItemBeanType() {
    return cartItemBeanType;
  }

  public void setCartItemBeanType(String cartItemBeanType) {
    this.cartItemBeanType = cartItemBeanType;
  }
}
