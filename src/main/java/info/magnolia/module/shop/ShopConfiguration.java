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

import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.exceptions.ShopConfigurationException;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.objectfactory.Classes;

/**
 * Configuration class for each shop.
 * @author tmiyar
 *
 */
public class ShopConfiguration {

  private String name;
  private String cartBeanType;
  private String cartSessionVariable;
  private String defaultPriceCategoryName;
  private String savedCartUUIDSessionVariable;
  private String priceCategoryManagerClassQualifiedName;
  private String cartClassQualifiedName;

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

  public String getDefaultPriceCategoryName() {
    return defaultPriceCategoryName;
  }

  public void setDefaultPriceCategoryName(String defaultPriceCategoryKey) {
    this.defaultPriceCategoryName = defaultPriceCategoryKey;
  }

  public String getSavedCartUUIDSessionVariable() {
    return savedCartUUIDSessionVariable;
  }

  public void setSavedCartUUIDSessionVariable(
      String savedCartUUIDSessionVariable) {
    this.savedCartUUIDSessionVariable = savedCartUUIDSessionVariable;
  }


    public DefaultPriceCategoryManagerImpl getPriceCategoryManager() throws ShopConfigurationException {
        try {
            return Classes.quietNewInstance(getPriceCategoryManagerClassQualifiedName(), getDefaultPriceCategoryName(), getName());
        } catch (Exception e) {
            throw new ShopConfigurationException("Unable to instantiate price category manager class");
        }
        
    }
  
    public String getPriceCategoryManagerClassQualifiedName() {
        return priceCategoryManagerClassQualifiedName;
    }
    
    public void setPriceCategoryManagerClassQualifiedName(
            String priceCategoryManagerClassName) {
        this.priceCategoryManagerClassQualifiedName = priceCategoryManagerClassName;
    }

    public String getCartClassQualifiedName() {
        return cartClassQualifiedName;
    }

    public void setCartClassQualifiedName(String cartClassQualifiedName) {
        this.cartClassQualifiedName = cartClassQualifiedName;
    }
    
    public DefaultShoppingCartImpl getCartClass() throws ShopConfigurationException {
        try {
            return Classes.quietNewInstance(getCartClassQualifiedName(), ShopUtil.getShopPriceCategory(this));
        } catch (Exception e) {
            throw new ShopConfigurationException("Unable to instantiate cart class");
        }
    }
  
}
