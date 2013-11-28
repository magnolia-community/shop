/**
 * This file Copyright (c) 2010-2013 Magnolia International
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

import info.magnolia.jcr.util.SessionUtil;
import info.magnolia.module.shop.util.ShopUtil;

import javax.jcr.Node;

/**
 * Implement this class to get the category price used by i.e. user selected
 * shop
 *
 * @author tmiyar
 *
 */
public class DefaultPriceCategoryManagerImpl implements PriceCategoryManager {
    private String defaultPriceCategory;
    private String shopName;


    public DefaultPriceCategoryManagerImpl(String defaultPriceCategory, String shopName ) {
        this.defaultPriceCategory = defaultPriceCategory;
        this.shopName = shopName;

    }

    @Override
    public Node getPriceCategoryInUse() {
        Node priceCategory = SessionUtil.getNode(ShopRepositoryConstants.SHOPS, ShopUtil.getPath(shopName, "priceCategories", getDefaultPriceCategory()));
        return priceCategory;
    }

    public void setDefaultPriceCategory(String defaultPriceCategory) {
        this.defaultPriceCategory = defaultPriceCategory;
    }

    public String getDefaultPriceCategory() {
        return defaultPriceCategory;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

}
