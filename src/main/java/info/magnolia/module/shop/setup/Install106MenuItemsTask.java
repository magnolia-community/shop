/**
 * This file Copyright (c) 2003-2013 Magnolia International
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
package info.magnolia.module.shop.setup;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.shop.util.ShopUtil;
import java.util.Collection;
import javax.jcr.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This task installs the new menu items (suppliers, countries, shipping options)
 * in all shops it finds. It also creates the necessary root nodes for these
 * data items.
 * 
 * @author Will Scheidegger (fastforward)
 */
public class Install106MenuItemsTask extends AbstractTask {
    private static final String icon = "/.resources/icons/16/dot.gif";
    private static final Logger log = LoggerFactory.getLogger(Install106MenuItemsTask.class);

    public Install106MenuItemsTask(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }

    public void execute(InstallContext installContext) throws TaskExecutionException {
        // get all shops
        String queryString = "/jcr:root/shops/element(*,shop)";
        Collection<Content> shops = QueryUtil.query("data", queryString, "xpath", "shop");
        
        // for each shop install menu items
        for (Content shop: shops) {
            installMenuItemsForShop(shop);
            createRootFolders(shop);
        }
    }
    
    private void installMenuItemsForShop(Content shopNode) {
        // get the shop menu item
        Content shopMenuItem = ContentUtil.getContent("config", "/modules/adminInterface/config/menu/" + shopNode.getName());
        
        // check and install sub items
        if (shopMenuItem != null) {
            addShopSubMenu(shopMenuItem, "suppliers", icon, "MgnlAdminCentral.showTree('shopSupplier', '" + ShopUtil.getPath("shopSuppliers", shopNode.getName()) + "', true);", "menu.suppliers");
            addShopSubMenu(shopMenuItem, "countries", icon, "MgnlAdminCentral.showTree('shop', '" + ShopUtil.getPath(shopNode.getName(), "countries") +"', true);", "menu.countries");
            addShopSubMenu(shopMenuItem, "shippingOptions", icon, "MgnlAdminCentral.showTree('shop', '" + ShopUtil.getPath(shopNode.getName(), "shippingOptions") +"', true);", "menu.shippingOptions");
        }
    }
        
    private void addShopSubMenu(Content parent, String menuName, String icon, String onClick, String label) {
        final Content menu;
        try {
            if (!parent.hasContent(menuName)) {
                menu = ContentUtil.getOrCreateContent(parent, menuName, ItemType.CONTENTNODE);
                addMenuNodeDatas(icon, onClick, label, menu);
            }
        } catch (AccessDeniedException ex) {
            log.error("Could not create shop sub-menu item \"" + menuName + "\"", ex);
        } catch (RepositoryException ex) {
            log.error("Could not create shop sub-menu item \"" + menuName + "\"", ex);
        }
    }
    
    private void addMenuNodeDatas(String icon, String onClick, String label,
            final Content menu) throws AccessDeniedException,
            RepositoryException {
        NodeDataUtil.getOrCreateAndSet(menu, "icon", icon);
        NodeDataUtil.getOrCreateAndSet(menu, "onclick", onClick);
        NodeDataUtil.getOrCreateAndSet(menu, "label", label);
        NodeDataUtil.getOrCreateAndSet(menu, "i18nBasename", "info.magnolia.module.shop.messages");
    }

    private void createRootFolders(Content shop) {
        try {
            HierarchyManager hm = MgnlContext.getHierarchyManager("data");
            Content root = hm.getRoot();
            // create "shopSupplier" root node if necessary
            Content shopSuppliersRootFOlder = ContentUtil.createPath(hm, "/shopSuppliers/" + shop.getName(), new ItemType("dataFolder"), true);
            if (!shop.hasContent("countries")) {
                ContentUtil.getOrCreateContent(shop, "countries", new ItemType("shopCountries"), true);
            }
            if (!shop.hasContent("shippingOptions")) {
                ContentUtil.getOrCreateContent(shop, "shippingOptions", new ItemType("shopShippingOptions"), true);
            }
        } catch (RepositoryException ex) {
            log.error("Could not check for / create folders for suppliers, countries or shippingOptions", ex);
        }
    }
    
    
}
