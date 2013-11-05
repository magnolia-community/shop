/**
 * This file Copyright (c) 2013 Magnolia International
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

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.shop.ShopNodeTypes;
import info.magnolia.repository.RepositoryConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;

/**
 * Task that will create apps for shops that were created before migration.
 */
public class CreateAppsForExistingShops extends AbstractRepositoryTask {

    public CreateAppsForExistingShops(String name, String description) {
        super(name, description);
    }

    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException {
        Session session = installContext.getJCRSession("shops");
        Iterable<Node> children = NodeUtil.getNodes(session.getRootNode(), ShopNodeTypes.SHOP);
        for (Node child : children) {
            createShopApps(child);
            createShopEntryInAppLauncher(child);
        }
    }

    protected void createShopApps(Node node) throws RepositoryException {
        Node appsNode = MgnlContext.getJCRSession(RepositoryConstants.CONFIG).getNode("/modules/shop/apps");

        // shop app
        Node shopNode = appsNode.addNode(node.getName(), NodeTypes.ContentNode.NAME);
        shopNode.setProperty("extends", "../shop");
        shopNode.addNode("subApps", NodeTypes.Folder.NAME).addNode("browser", NodeTypes.ContentNode.NAME).addNode("workbench", NodeTypes.ContentNode.NAME).setProperty("path", "/" + node.getName());

        // products app
        Node productsNode = appsNode.addNode(node.getName() + "Products", NodeTypes.ContentNode.NAME);
        productsNode.setProperty("extends", "../shopProducts");
        productsNode.addNode("subApps", NodeTypes.Folder.NAME).addNode("browser", NodeTypes.ContentNode.NAME).addNode("workbench", NodeTypes.ContentNode.NAME).setProperty("path", "/" + node.getName());

        // shopping carts app
        Node shoppingCartsNode = appsNode.addNode(node.getName() + "ShoppingCarts", NodeTypes.ContentNode.NAME);
        shoppingCartsNode.setProperty("extends", "../shoppingCarts");
        shoppingCartsNode.addNode("subApps", NodeTypes.Folder.NAME).addNode("browser", NodeTypes.ContentNode.NAME).addNode("workbench", NodeTypes.ContentNode.NAME).setProperty("path", "/" + node.getName());

        // shop suppliers app
        Node suppliersNode = appsNode.addNode(node.getName() + "Suppliers", NodeTypes.ContentNode.NAME);
        suppliersNode.setProperty("extends", "../shopSuppliers");
        suppliersNode.addNode("subApps", NodeTypes.Folder.NAME).addNode("browser", NodeTypes.ContentNode.NAME).addNode("workbench", NodeTypes.ContentNode.NAME).setProperty("path", "/" + node.getName());
    }

    protected void createShopEntryInAppLauncher(Node node) throws RepositoryException {
        Node appLauncherNode = MgnlContext.getJCRSession(RepositoryConstants.CONFIG).getNode("/modules/ui-admincentral/config/appLauncherLayout/groups");
        Node shopNode = appLauncherNode.addNode(node.getName(), NodeTypes.ContentNode.NAME);
        Node groupsNode = shopNode.addNode("apps", NodeTypes.ContentNode.NAME);
        groupsNode.addNode(node.getName(), NodeTypes.ContentNode.NAME);
        groupsNode.addNode(node.getName() + "Products", NodeTypes.ContentNode.NAME);
        groupsNode.addNode(node.getName() + "Suppliers", NodeTypes.ContentNode.NAME);
        groupsNode.addNode(node.getName() + "ShoppingCarts", NodeTypes.ContentNode.NAME);
        shopNode.setProperty("color", "#000000");
        shopNode.setProperty("label", StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(node.getName()), " "));
    }
}
