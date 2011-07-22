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
package info.magnolia.module.shop.save;


import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.ExclusiveWrite;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.data.save.DataSaveHandler;

/**
 * This class creates the basic hierarchy of data for the shop,
 * together with a shop menu below website.
 * @author tmiyar
 *
 */
public class ShopSaveHandler extends DataSaveHandler {

    @Override
    public boolean save() {
        boolean success = super.save();
        if (success) {
            
            synchronized (ExclusiveWrite.getInstance()) {

                HierarchyManager hm = MgnlContext.getHierarchyManager(this.getRepository());
                try {
                    // get the node to save
                    Content page = this.getPageNode(hm);

                    if (page == null) {
                        // an error should have been logged in getPageNode() avoid NPEs!
                        return false;
                    }

                    Content node = this.getSaveNode(hm, page);
                    //need to check itemtype due to http://jira.magnolia-cms.com/browse/MGNLDATA-126
                    if(node.getItemType().getSystemName().equals("shop")) {
                        addDefaultSubNodes(node);
                        addFolderNodesWithShopName(hm, "shopCarts", node.getName());
                        addFolderNodesWithShopName(hm, "shopProducts", node.getName());
                        addFolderNodesWithShopName(hm, "shopProductCategories", node.getName());
                        addNewShopInMenu(node);
                    }
                    
                }
                catch (RepositoryException re) {
                    log.error(re.getMessage(), re);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void addFolderNodesWithShopName(HierarchyManager hm, String path, String shopName) throws RepositoryException {
        Content newFolder = hm.getContent(path + "/" + shopName, true, new ItemType("dataFolder"));
        newFolder.getParent().save();
    }

    /**
     * Creates a main menu in adminInterface configuration for the new shop.
     */
    protected void addNewShopInMenu(Content newShopNode) throws RepositoryException {
        HierarchyManager hm = MgnlContext.getHierarchyManager(ContentRepository.CONFIG);
        
        String icon = "/.resources/icons/16/dot.gif";
        Content menuNode = hm.getContent("/modules/adminInterface/config/menu/" + newShopNode.getName(), true, ItemType.CONTENTNODE);
        //Adds the node datas
        addMenuNodeDatas("/.resources/icons/24/shoppingcart.gif", "MgnlAdminCentral.showTree('shop', '/shops/" + newShopNode.getName() +"', true);", newShopNode.getName(), menuNode);
        addShopSubMenu(menuNode, "taxCategories", icon, "MgnlAdminCentral.showTree('shop', '/shops/" + newShopNode.getName() +"/taxCategories"+"', true);", "tax categories");
        addShopSubMenu(menuNode, "currencies", icon, "MgnlAdminCentral.showTree('shop', '/shops/" + newShopNode.getName() +"/currencies"+"', true);", "currencies");
        addShopSubMenu(menuNode, "priceCategories", icon, "MgnlAdminCentral.showTree('shop', '/shops/" + newShopNode.getName() +"/priceCategories"+"', true);", "price categories");
        addShopSubMenu(menuNode, "productCategories", icon, "MgnlAdminCentral.showTree('shopProductCategory', '/shopProductCategories/" + newShopNode.getName() +"', true);", "product categories");
        addShopSubMenu(menuNode, "products", icon, "MgnlAdminCentral.showTree('shopProduct', '/shopProducts/" + newShopNode.getName() +"', true);", "products");
        menuNode.orderBefore(menuNode.getName(), "data");
        menuNode.getParent().save();
        
    }
    
    
    protected void addShopSubMenu(Content parent, String menuName, String icon, String onClick, String label) throws AccessDeniedException, RepositoryException {
        final Content menu = ContentUtil.getOrCreateContent(parent, menuName, ItemType.CONTENTNODE);
        addMenuNodeDatas(icon, onClick, label, menu);
       
    }

    private void addMenuNodeDatas(String icon, String onClick, String label,
            final Content menu) throws AccessDeniedException,
            RepositoryException {
        NodeDataUtil.getOrCreateAndSet(menu, "icon", icon);
        NodeDataUtil.getOrCreateAndSet(menu, "onclick", onClick);
        NodeDataUtil.getOrCreateAndSet(menu, "label", label);
    }

    /**
     * Creates the basic structure of a shop.
     */
    protected void addDefaultSubNodes(Content node) throws AccessDeniedException, PathNotFoundException, RepositoryException {
        node.createContent("taxCategories", new ItemType("shopTaxCategories"));
        node.createContent("currencies", new ItemType("shopCurrencies"));
        node.createContent("priceCategories", new ItemType("shopPriceCategories"));
        node.save();
        
    }
    

}
