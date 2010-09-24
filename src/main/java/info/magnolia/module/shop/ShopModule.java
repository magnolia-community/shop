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

/**
 * This is the configuration bean of your Magnolia module. It has to be
 * registered in the module descriptor file under
 * src/main/resources/META-INF/magnolia/mymodule.xml.
 * 
 * The bean properties used in this class will be initialized by Content2Bean
 * which means that properties of in the node config:/modules/mymodule/config/*
 * are populated to this bean when the module is initialized.
 */
public class ShopModule {
  private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ShopModule.class);

  private static ShopModule instance;

  private String cartClassQualifiedName;

  public static final String DEFAULT_CART_SESSION_VARIABLE_NAME = "shoppingCart";
  public static final String DEFAULT_CART_CLASS_NAME = "info.magnolia.module.shop.beans.DefaultShoppingCart";
  public static final String CARTS_FOLDER_NAME = "shoppingCarts";

  /*
   * Required default constructor
   */
  public ShopModule() {
    instance = this;
  }

  public static ShopModule getInstance() {
    return instance;
  }

  public String getCartClassQualifiedName() {
    return cartClassQualifiedName;
  }

  public void setCartClassQualifiedName(String cartClassQualifiedName) {
    this.cartClassQualifiedName = cartClassQualifiedName;
  }

}
