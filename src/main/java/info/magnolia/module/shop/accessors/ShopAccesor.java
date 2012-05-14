/**
 * This file Copyright (c) 2010-2011 Magnolia International
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
package info.magnolia.module.shop.accessors;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.shop.ShopConfiguration;
import info.magnolia.module.shop.util.ShopUtil;

/**
 * shop object.
 * @author tmiyar
 *
 */
public class ShopAccesor extends DefaultCustomDataAccesor {
    
    public static String SHOP_SHOPS_FOLDER = "shops";
    
    public ShopAccesor(String name) throws Exception {
        super(name);
    }

    protected Content getNode(String name) throws Exception {
        String path = ShopUtil.getPath(SHOP_SHOPS_FOLDER);
        return super.getNodeByName(path, "shop", name);
    }
    
    public ShopConfiguration getShopConfiguration() {
        Content shopNode = getNode();
        if(shopNode != null) {
            try {
                ShopConfiguration shopConfiguration = new ShopConfiguration();
                shopConfiguration.setCartBeanType(NodeDataUtil.getString(shopNode, "cartBeanType", ""));
                shopConfiguration.setCartClassQualifiedName(NodeDataUtil.getString(shopNode, "cartClassQualifiedName", ""));
                shopConfiguration.setCartSessionVariable(NodeDataUtil.getString(shopNode, "cartSessionVariable", ""));
                shopConfiguration.setDefaultPriceCategoryName(NodeDataUtil.getString(shopNode, "defaultPriceCategoryName", ""));
                shopConfiguration.setName(NodeDataUtil.getString(shopNode, "name", ""));
                shopConfiguration.setPriceCategoryManagerClassQualifiedName(NodeDataUtil.getString(shopNode, "priceCategoryManagerClassQualifiedName", ""));
                shopConfiguration.setSavedCartUUIDSessionVariable(NodeDataUtil.getString(shopNode, "savedCartUUIDSessionVariable", ""));
                return shopConfiguration;
            } catch (Exception e) {
                log.error("Cant read shop configuration for " + getName(), e);
            }
            
        }
        return null;
    }

}
