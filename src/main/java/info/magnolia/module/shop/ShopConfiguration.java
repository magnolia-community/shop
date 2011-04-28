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
package info.magnolia.module.shop;

public class ShopConfiguration {

  private String name;
  private String cartBeanType;
  private String cartSessionVariable;
  private String defaultPriceCategoryKey;
  private String savedCartUUIDSessionVariable;
  private String shopDataRootPath;
  private String cartsFolderName;
  private PriceCategoryManager priceCategoryManager;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCartBeanType() {
    return cartBeanType;
  }

  public void setCartBeanType(String cartBeanType) {
    this.cartBeanType = cartBeanType;
  }

  public String getCartSessionVariable() {
    return cartSessionVariable;
  }

  public void setCartSessionVariable(String cartSessionVariable) {
    this.cartSessionVariable = cartSessionVariable;
  }

  public String getDefaultPriceCategoryKey() {
    return defaultPriceCategoryKey;
  }

  public void setDefaultPriceCategoryKey(String defaultPriceCategoryKey) {
    this.defaultPriceCategoryKey = defaultPriceCategoryKey;
  }

  public String getSavedCartUUIDSessionVariable() {
    return savedCartUUIDSessionVariable;
  }

  public void setSavedCartUUIDSessionVariable(
      String savedCartUUIDSessionVariable) {
    this.savedCartUUIDSessionVariable = savedCartUUIDSessionVariable;
  }

  public String getShopDataRootPath() {
    return shopDataRootPath;
  }

  public void setShopDataRootPath(String shopDataRootPath) {
    this.shopDataRootPath = shopDataRootPath;
  }

  public PriceCategoryManager getPriceCategoryManager() {
    return priceCategoryManager;
  }

  public void setPriceCategoryManager(PriceCategoryManager priceCategoryManager) {
    this.priceCategoryManager = priceCategoryManager;
  }

  public String getCartsFolderName() {
    return cartsFolderName;
  }

  public void setCartsFolderName(String cartsFolderName) {
    this.cartsFolderName = cartsFolderName;
  }  
}