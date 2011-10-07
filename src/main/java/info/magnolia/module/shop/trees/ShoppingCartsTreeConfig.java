/**
 * This file Copyright (c) 2008-2010 Magnolia International
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
package info.magnolia.module.shop.trees;

import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.MetaData;
import info.magnolia.cms.exchange.ActivationManagerFactory;
import info.magnolia.cms.gui.control.ContextMenu;
import info.magnolia.cms.gui.control.ContextMenuItem;
import info.magnolia.cms.gui.control.FunctionBar;
import info.magnolia.cms.gui.control.FunctionBarItem;
import info.magnolia.cms.gui.control.Tree;
import info.magnolia.cms.gui.control.TreeColumn;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.module.data.trees.GenericDataAdminTreeConfig;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * Tree configuration for shoppingCarts repository tree.
 * @author tmiyar
 *
 */
public class ShoppingCartsTreeConfig extends GenericDataAdminTreeConfig {

    public void prepareTree(Tree tree, boolean browseMode, HttpServletRequest request) {
        final Messages msgs = getMessages();

        tree.setIconOndblclick(tree.getJavascriptTree() + ".selectedNode.itemType == '"
            + ItemType.CONTENTNODE.getSystemName()
            + "'? mgnlTreeMenuOpenDialog("
            + tree.getJavascriptTree()
            + ",'.magnolia/dialogs/shopCart.html"
            + "') : '';");

        tree.addItemType(ItemType.NT_FOLDER, "/.resources/icons/16/folder.gif");
        tree.addItemType(ItemType.CONTENTNODE.getSystemName(), "/.resources/icons/16/cubes.gif");
        
        tree.addItemType(ItemType.MGNL_NODE_DATA);
        TreeColumn titleColumn = TreeColumn.createLabelColumn(tree, msgs.get("shoppingCarts.list.title"), true);
        titleColumn.setWidth(3);

        TreeColumn column1 = new TreeColumn(tree.getJavascriptTree(), request);
        column1.setName(StringUtils.EMPTY);
        column1.setTitle(msgs.get("tree.config.value")); //$NON-NLS-1$
        column1.setIsNodeDataValue(true);
        column1.setWidth(3);
        column1.setHtmlEdit();

        TreeColumn columnIcons = TreeColumn.createActivationColumn(tree, msgs.get("shoppingCarts.list.status"));
        columnIcons.setIconsPermission(true);

        TreeColumn dateColumn = TreeColumn.createMetaDataColumn(tree, msgs.get("shoppingCarts.list.date"), MetaData.LAST_MODIFIED, "yy-MM-dd, HH:mm");
        dateColumn.setWidth(2);

        tree.addColumn(titleColumn);
        tree.addColumn(column1);
            
        if (isAdminInstance() || hasAnyActiveSubscriber()) {
            tree.addColumn(columnIcons);
        }
        
        tree.addColumn(dateColumn);
        


    }

    public void prepareContextMenu(Tree tree, boolean browseMode, HttpServletRequest request) {
        final Messages msgs = getMessages();

        ContextMenuItem menuNewFolder = new ContextMenuItem("newFolder");

        menuNewFolder.setLabel(msgs.get("shoppingCarts.menu.newFolder"));
        menuNewFolder.setIcon(request.getContextPath() + "/.resources/icons/16/folder_add.gif");
        menuNewFolder.setOnclick(tree.getJavascriptTree() + ".createNode('" + ItemType.NT_FOLDER + "');");

        ContextMenuItem menuEditDocument = new ContextMenuItem("edit");
        menuEditDocument.setLabel(msgs.get("shoppingCarts.menu.edit"));
        menuEditDocument.setIcon(request.getContextPath() + "/.resources/icons/16/document_edit.gif");
        menuEditDocument.setOnclick("mgnlTreeMenuOpenDialog("
            + tree.getJavascriptTree()
            + ",'.magnolia/dialogs/shoppingCarts.html');");
        menuEditDocument.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot("
                + tree.getJavascriptTree()
                + ")");

        ContextMenuItem menuDelete = new ContextMenuItem("delete");
        menuDelete.setLabel(msgs.get("tree.config.menu.delete"));
        menuDelete.setIcon(request.getContextPath() + "/.resources/icons/16/delete2.gif");
        menuDelete.setOnclick(tree.getJavascriptTree() + ".deleteNode();");
        menuDelete.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot("
            + tree.getJavascriptTree()
            + ")");
        menuDelete.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite("
            + tree.getJavascriptTree()
            + ")");

        ContextMenuItem menuCopy = new ContextMenuItem("copy");
        menuCopy.setLabel(msgs.get("tree.config.menu.copy"));
        menuCopy.setIcon(request.getContextPath() + "/.resources/icons/16/copy.gif");
        menuCopy.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot("
            + tree.getJavascriptTree()
            + ")");
        menuCopy.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotNodeData("
            + tree.getJavascriptTree()
            + ")");
        menuCopy.setOnclick(tree.getJavascriptTree() + ".copyNode();");
        menuCopy.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite("
            + tree.getJavascriptTree()
            + ")");

        ContextMenuItem menuCut = new ContextMenuItem("move");
        menuCut.setLabel(msgs.get("tree.config.menu.move"));
        menuCut.setIcon(request.getContextPath() + "/.resources/icons/16/up_down.gif");
        menuCut
            .addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot(" + tree.getJavascriptTree() + ")");
        menuCut.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotNodeData("
            + tree.getJavascriptTree()
            + ")");
        menuCut.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite("
            + tree.getJavascriptTree()
            + ")");
        menuCut.setOnclick(tree.getJavascriptTree() + ".cutNode();");

        ContextMenuItem menuActivateExcl = new ContextMenuItem("activate");
        menuActivateExcl.setLabel(msgs.get("tree.config.menu.activate"));
        menuActivateExcl.setIcon(request.getContextPath() + "/.resources/icons/16/arrow_right_green.gif");
        menuActivateExcl.setOnclick(tree.getJavascriptTree() + ".activateNode(" + Tree.ACTION_ACTIVATE + ",false);");
        menuActivateExcl.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot("
            + tree.getJavascriptTree()
            + ")");
        menuActivateExcl.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotNodeData("
            + tree.getJavascriptTree()
            + ")");
        menuActivateExcl.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite("
            + tree.getJavascriptTree()
            + ")");

        ContextMenuItem menuActivateIncl = new ContextMenuItem("activateIncl");
        menuActivateIncl.setLabel(msgs.get("tree.config.menu.activateInclSubs"));
        menuActivateIncl.setIcon(request.getContextPath() + "/.resources/icons/16/arrow_right_green_double.gif");
        menuActivateIncl.setOnclick(tree.getJavascriptTree() + ".activateNode(" + Tree.ACTION_ACTIVATE + ",true);");
        menuActivateIncl.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot("
            + tree.getJavascriptTree()
            + ")");
        menuActivateIncl.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotNodeData("
            + tree.getJavascriptTree()
            + ")");
        menuActivateIncl.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotContentNode("
            + tree.getJavascriptTree()
            + ")");
        menuActivateIncl.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite("
            + tree.getJavascriptTree()
            + ")");

        ContextMenuItem menuDeactivate = new ContextMenuItem("deactivate");
        menuDeactivate.setLabel(msgs.get("tree.config.menu.deactivate"));
        menuDeactivate.setIcon(request.getContextPath() + "/.resources/icons/16/arrow_left_red.gif");
        menuDeactivate.setOnclick(tree.getJavascriptTree() + ".deactivateNode(" + Tree.ACTION_DEACTIVATE + ");");
        menuDeactivate.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotRoot("
            + tree.getJavascriptTree()
            + ")");
        menuDeactivate.addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotNodeData("
            + tree.getJavascriptTree()
            + ")");
        menuDeactivate.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite("
            + tree.getJavascriptTree()
            + ")");

        if (!ActivationManagerFactory.getActivationManager().hasAnyActiveSubscriber()) {
            menuActivateExcl.addJavascriptCondition("new mgnlTreeMenuItemConditionBoolean(false)");
            menuActivateIncl.addJavascriptCondition("new mgnlTreeMenuItemConditionBoolean(false)");
            menuDeactivate.addJavascriptCondition("new mgnlTreeMenuItemConditionBoolean(false)");
        }

        ContextMenuItem menuExport = new ContextMenuItem("export");
        menuExport.setLabel(msgs.get("tree.menu.export"));
        menuExport.setIcon(request.getContextPath() + "/.resources/icons/16/export1.gif");
        // keep versions
        menuExport.setOnclick(tree.getJavascriptTree() + ".exportNode(true);");

        ContextMenuItem menuImport = new ContextMenuItem("import");
        menuImport.setLabel(msgs.get("tree.menu.import"));
        menuImport.setIcon(request.getContextPath() + "/.resources/icons/16/import2.gif");
        menuImport.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite("
            + tree.getJavascriptTree()
            + ")");
        menuImport.setOnclick(tree.getJavascriptTree() + ".importNode(this);");



        if(!browseMode){
            tree.addMenuItem(menuNewFolder);
            tree.addMenuItem(menuEditDocument);
            tree.addSeparator();
            tree.addMenuItem(menuDelete);
            tree.addSeparator();
            tree.addMenuItem(menuCut);
            tree.addMenuItem(menuCopy);
            tree.addSeparator();
            tree.addMenuItem(menuActivateExcl);
            tree.addMenuItem(menuActivateIncl);
            tree.addMenuItem(menuDeactivate);
            tree.addSeparator();
            tree.addMenuItem(menuImport);
            tree.addMenuItem(menuExport);
            tree.addSeparator();
        }
        tree.addMenuItem(ContextMenuItem.getRefreshMenuItem(tree, msgs, request));
    }

    public void prepareFunctionBar(Tree tree, boolean browseMode, HttpServletRequest request) {

        if(browseMode){
            return;
        }
        FunctionBar bar = tree.getFunctionBar();
        ContextMenu menu = tree.getMenu();
        bar.addMenuItem(new FunctionBarItem(menu.getMenuItemByName("newFolder")));
        bar.addMenuItem(new FunctionBarItem(menu.getMenuItemByName("edit")));
        bar.addMenuItem(new FunctionBarItem(menu.getMenuItemByName("delete")));

        bar.addMenuItem(null);
        bar.addMenuItem(new FunctionBarItem(menu.getMenuItemByName("activate")));
        bar.addMenuItem(new FunctionBarItem(menu.getMenuItemByName("deactivate")));
        bar.addMenuItem(null);
        tree.addFunctionBarItem(FunctionBarItem.getRefreshFunctionBarItem(tree, getMessages(), request));
    }

}
