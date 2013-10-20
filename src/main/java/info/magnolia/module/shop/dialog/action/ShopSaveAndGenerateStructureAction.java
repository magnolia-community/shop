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
package info.magnolia.module.shop.dialog.action;

import info.magnolia.cms.core.Path;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.ui.admincentral.dialog.action.SaveDialogAction;
import info.magnolia.ui.admincentral.dialog.action.SaveDialogActionDefinition;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;

/**
 * Save action that generates whole shop structure when new shop is saved.
 */
public class ShopSaveAndGenerateStructureAction extends SaveDialogAction {

    private static final Logger log = LoggerFactory.getLogger(ShopSaveDialogAction.class);

    public ShopSaveAndGenerateStructureAction(SaveDialogActionDefinition definition, Item item, EditorValidator validator, EditorCallback callback) {
        super(definition, item, validator, callback);
    }

    @Override
    public void execute() throws ActionExecutionException {
        validator.showValidation(true);
        if (validator.isValid()) {
            final JcrNodeAdapter itemChanged = (JcrNodeAdapter) item;
            try {
                final Node node = itemChanged.applyChanges();
                if (node.hasProperty("name")) {
                    NodeUtil.renameNode(node, Path.getUniqueLabel(node.getSession(), node.getParent().getName(), PropertyUtil.getString(node, "name")));
                }
                if (item instanceof JcrNewNodeAdapter) {
                    createShopStructure(node);
                    createProductsStructure(node);
                    createShoppingCartsStructure(node);
                    createShopApps(node);
                    createShopEntryInAppLauncher(node);
                }
                node.getSession().save();
            } catch (final RepositoryException e) {
                throw new ActionExecutionException(e);
            }
            callback.onSuccess(getDefinition().getName());
        } else {
            log.info("Validation error(s) occurred. No save performed.");
        }
    }

    /**
     * Create structure of shop. This includes creation of price, tax and currency categories nodes under shop configuration.
     *
     * @param node shop node
     */
    protected void createShopStructure(Node node) throws RepositoryException {
        node.addNode("taxCategories", "shopTaxCategories");
        node.addNode("currencies", "shopCurrencies");
        node.addNode("priceCategories", "shopPriceCategories");
        node.addNode("countries", "shopCountries");
        node.addNode("shippingOptions", "shopShippingOptions");
    }

    /**
     * Create folder for new shop in shopProducts workspace.
     *
     * @param node shop node
     */
    protected void createProductsStructure(Node node) throws RepositoryException {
        Node root = MgnlContext.getJCRSession(ShopRepositoryConstants.SHOP_PRODUCTS).getRootNode();
        root.addNode(node.getName(), NodeTypes.Folder.NAME);
        root.getSession().save();
    }

    /**
     * Create folder for new shop in shoppingCarts workspace.
     *
     * @param node shop node
     */
    protected void createShoppingCartsStructure(Node node) throws RepositoryException {
        Node root = MgnlContext.getJCRSession(ShopRepositoryConstants.SHOPPING_CARTS).getRootNode();
        root.addNode(node.getName(), NodeTypes.Folder.NAME);
        root.getSession().save();
    }

    /**
     * Create shop apps for new shop.
     *
     * @param node shop node
     */
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

        appsNode.getSession().save();
    }

    /**
     * Registers shop apps into appLauncher.
     *
     * @param node shop node
     */
    protected void createShopEntryInAppLauncher(Node node) throws RepositoryException {
        Node appLauncherNode = MgnlContext.getJCRSession(RepositoryConstants.CONFIG).getNode("/modules/ui-admincentral/config/appLauncherLayout/groups");
        Node shopNode = appLauncherNode.addNode(node.getName(), NodeTypes.ContentNode.NAME);
        Node groupsNode = shopNode.addNode("apps", NodeTypes.ContentNode.NAME);
        groupsNode.addNode(node.getName(), NodeTypes.ContentNode.NAME);
        groupsNode.addNode(node.getName() + "Products", NodeTypes.ContentNode.NAME);
        groupsNode.addNode(node.getName() + "Suppliers", NodeTypes.ContentNode.NAME);
        groupsNode.addNode(node.getName() + "ShoppingCarts", NodeTypes.ContentNode.NAME);
        groupsNode.addNode(node.getName() + "Suppliers", NodeTypes.ContentNode.NAME);
        shopNode.setProperty("color", "#000000");
        shopNode.setProperty("label", StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(node.getName()), " "));

        appLauncherNode.getSession().save();
    }
}
