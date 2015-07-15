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
package info.magnolia.module.shop;

import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.exceptions.ShopConfigurationException;
import info.magnolia.module.shop.processors.FlatHierarchyStrategy;
import info.magnolia.module.shop.processors.HierarchyStrategy;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.objectfactory.Classes;

import org.apache.commons.lang.StringUtils;

/**
 * Configuration class for each shop.
 * @author tmiyar
 *
 */
public class ShopConfiguration {

    private String name;
    private String cartBeanType;
    private String defaultPriceCategoryName;
    private String savedCartUUIDSessionVariable;
    private String priceCategoryManagerClassQualifiedName;
    private String cartClassQualifiedName;
    private String cartHierarchyStrategyClass = FlatHierarchyStrategy.class.getCanonicalName();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The name of the cart bean
     * @deprecated Deprecated since v2.2.3 - not used anywhere
     */
    @Deprecated
    public String getCartBeanType() {
        return cartBeanType;
    }

    /**
     * @param cartBeanType
     * @deprecated Deprecated since v2.2.3 - not used anywhere
     */
    @Deprecated
    public void setCartBeanType(String cartBeanType) {
        this.cartBeanType = cartBeanType;
    }

    public String getDefaultPriceCategoryName() {
        return defaultPriceCategoryName;
    }

    public void setDefaultPriceCategoryName(String defaultPriceCategoryKey) {
        this.defaultPriceCategoryName = defaultPriceCategoryKey;
    }

    /**
     * @return A name of a session variable containing the saved cart's uuid - but it is not set anywhere
     * @deprecated Deprecated since v2.2.3 - not used anywhere
     */
    public String getSavedCartUUIDSessionVariable() {
        return savedCartUUIDSessionVariable;
    }

    /**
     * @param savedCartUUIDSessionVariable
     * @deprecated Deprecated since v2.2.3 - not used anywhere
     */
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

    public String getCartHierarchyStrategyClass() {
        return cartHierarchyStrategyClass;
    }

    public void setCartHierarchyStrategyClass(String cartHierarchyStrategyClass) {
        if (StringUtils.isBlank(cartHierarchyStrategyClass)) {
            this.cartHierarchyStrategyClass = FlatHierarchyStrategy.class.getCanonicalName();
        } else {
            this.cartHierarchyStrategyClass = cartHierarchyStrategyClass;
        }
    }

    public HierarchyStrategy getHierarchyStrategy() throws ShopConfigurationException {
        try {
            return Classes.quietNewInstance(cartHierarchyStrategyClass);
        } catch (Exception e) {
            throw new ShopConfigurationException("Unable to instantiate cart hierarchy strategy for class " + cartHierarchyStrategyClass);
        }
    }
}
