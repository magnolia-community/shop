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

import java.util.Collection;
import java.util.List;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.module.shop.util.ShopUtil;

/**
 * shop product.
 * @author tmiyar
 *
 */
public class ShopProductAccesor extends DefaultCustomDataAccesor {
    
    public static String SHOP_PRODUCTS_FOLDER = "shopProducts";
    
    public ShopProductAccesor(String name) throws Exception {
        super(name);
    }

    protected Content getNode(String name) throws Exception {
        String path = ShopUtil.getPath(SHOP_PRODUCTS_FOLDER, ShopUtil.getShopName());
        return super.getNodeByName(path, "shopProduct", name);
    }
    
    public static List<Content> getProductsByProductCategory(String productCategory) {
        String xpath = ShopUtil.getPath("jcr:root", SHOP_PRODUCTS_FOLDER, 
            ShopUtil.getShopName())+ "//element(*,shopProduct)[jcr:contains(productCategoryUUIDs/., '"
            + productCategory + "')]";
        List<Content> productList = (List<Content>) QueryUtil.query("data", xpath, Query.XPATH);
        return ShopUtil.transformIntoI18nContentList(productList);
    }
    
    public static Collection<Content> getTaggedProducts(String categoryUUID) {
        
        String query = "select * from shopProduct where jcr:path like '" + ShopUtil.getPath("shopProducts", ShopUtil.getShopName()) 
          + "/%' and contains(tags, '" + categoryUUID + "')";
        
        return QueryUtil.query("data", query);
    }

}
