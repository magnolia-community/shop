/**
 * This file Copyright (c) 2003-2009 Magnolia International
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

import info.magnolia.cms.gui.control.ContextMenu;
import info.magnolia.cms.gui.control.ContextMenuItem;
import info.magnolia.cms.gui.control.FunctionBar;
import info.magnolia.cms.gui.control.FunctionBarItem;
import info.magnolia.cms.gui.control.Tree;
import info.magnolia.cms.gui.control.TreeColumn;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.module.admininterface.AbstractTreeConfiguration;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 * See {@linkplain http://www.fastforward.ch/web/ff/dokumentation/entwickler/magnolia_configurable_tree.html}
 * for documentation on how to use this class.
 *
 * @author willscheidegger@mac.com
 */
public class ConfigurableTreeConfiguration extends AbstractTreeConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ConfigurableTreeConfiguration.class);
    private Collection<Map> nodeTypes;
    private Collection<Map> columns;
    private Collection<Map> contextMenuItems;
    private Collection<Map> functionBarItems;
    private String onIconDoubleClick = null;
    private static final String LABEL_COLUMN = "label";
    private static final String ACTIVATION_COLUMN = "activation";
    private static final String STANDARD_COLUMN = "standard";
    private static final String NODEDATA_COLUMN = "nodeData";
    private static final String NODEDATAVALUE_COLUMN = "nodeDataValue";
    private static final String METADATA_COLUMN = "metaData";

    /**
     * @return the nodeTypes
     */
    public Collection getNodeTypes() {
        return nodeTypes;
    }

    /**
     * @param nodeTypes the nodeTypes to set
     */
    public void setNodeTypes(Collection nodeTypes) {
        this.nodeTypes = nodeTypes;
    }

    public void prepareTree(Tree tree, boolean browseMode, HttpServletRequest request) {
        final Messages msgs = getMessages();

        tree.setIconPage(Tree.ICONDOCROOT + "folder_cubes.gif"); //$NON-NLS-1$

        // add additional node types to be displayed by the tree
        if (nodeTypes != null && !nodeTypes.isEmpty()) {
            Iterator<Map> nodeTypesIter = nodeTypes.iterator();
            Map currentTypeDefinition;
            while (nodeTypesIter.hasNext()) {
                currentTypeDefinition = nodeTypesIter.next();
                // TODO: Test how the custom icon thing works!
                if (currentTypeDefinition.get("icon") != null) {
                    tree.addItemType((String) currentTypeDefinition.get("nodeType"), (String) currentTypeDefinition.get("icon"));
                } else {
                    tree.addItemType((String) currentTypeDefinition.get("nodeType"));
                }
            }
        } else {
            log.error("No node types defined");
        }

        // add columns to be displayed
        if (columns != null && !columns.isEmpty()) {
            Iterator<Map> columnsIter = columns.iterator();
            Map currentColumnDefinition;
            String columnType;
            String columnTitle;
            TreeColumn col;
            int columnCounter = 0;
            boolean addColumn;
            boolean editable;
            while (columnsIter.hasNext()) {
                col = null;
                addColumn = true;
                currentColumnDefinition = columnsIter.next();
                columnType = (String) currentColumnDefinition.get("type");
                columnTitle = msgs.getWithDefault((String) currentColumnDefinition.get("colTitle"), (String) currentColumnDefinition.get("colTitle"));
                if (currentColumnDefinition.get("editable") != null) {
                    editable = ((Boolean) currentColumnDefinition.get("editable")).booleanValue();
                } else {
                    editable = false;
                }

                // create a column depending on the type
                if (StringUtils.isNotBlank(columnType)) {
                    if (columnType.equalsIgnoreCase(ConfigurableTreeConfiguration.LABEL_COLUMN)) {
                        col = TreeColumn.createLabelColumn(tree, columnTitle, editable);
                    } else if (columnType.equalsIgnoreCase(ConfigurableTreeConfiguration.NODEDATAVALUE_COLUMN)) {
                        col = TreeColumn.createColumn(tree, columnTitle);
                        col.setIsNodeDataValue(true);
                    } else if (columnType.equalsIgnoreCase(ConfigurableTreeConfiguration.ACTIVATION_COLUMN)) {
                        // only add the activation column type if there is an active subscriber
                        if (isAdminInstance() || hasAnyActiveSubscriber()) {
                            col = TreeColumn.createActivationColumn(tree, columnTitle);
                            col.setIconsPermission(true);
                        }
                    } else if (columnType.equalsIgnoreCase(ConfigurableTreeConfiguration.NODEDATA_COLUMN)) {
                        if (currentColumnDefinition.get("dateFormat") != null) {
                            col = TreeColumn.createNodeDataColumn(tree, columnTitle, (String) currentColumnDefinition.get("nodeData"), (String) currentColumnDefinition.get("dateFormat"));
                        } else {
                            col = TreeColumn.createNodeDataColumn(tree, columnTitle, (String) currentColumnDefinition.get("nodeData"), editable);
                        }
                        // TODO: add the nodeDataColumn with specified renderer
                    } else if (columnType.equalsIgnoreCase(ConfigurableTreeConfiguration.METADATA_COLUMN)) {
                        col = TreeColumn.createMetaDataColumn(tree, columnTitle, (String) currentColumnDefinition.get("nodeData"), (String) currentColumnDefinition.get("dateFormat"));
                    } else {
                        col = TreeColumn.createColumn(tree, columnTitle);
                        // TODO: add the column type with specified renderer
                    }
                }

                // configure additional stuff in the column
                if (col != null) {
                    // add the width to the column
                    if (currentColumnDefinition.get("width") != null) {
                        col.setWidth(((Number) currentColumnDefinition.get("width")).intValue());
                        // TODO: figure out what this is doing:
                        col.setHtmlEdit();
                    }
                    // per default only add the first column in browse mode
                    if (browseMode && columnCounter > 0 && addColumn) {
                        if (currentColumnDefinition.get("showColInBrowseMode") != null && ((Boolean) currentColumnDefinition.get("showColInBrowseMode")).booleanValue()) {
                            // add the column despite the browse mode
                        } else {
                            addColumn = false;
                        }
                    }
                    if (addColumn) {
                        tree.addColumn(col);
                    }

                    columnCounter++;
                }
            }
        } else {
            log.error("No columns defined!");
        }

        // handle icon doubleclicks
        if (StringUtils.isNotBlank(onIconDoubleClick)) {
            onIconDoubleClick = StringUtils.replace(onIconDoubleClick, "${tree}", tree.getJavascriptTree());
            if (!onIconDoubleClick.endsWith(";")) {
                onIconDoubleClick += ";";
            }
            tree.setIconOndblclick(onIconDoubleClick);
        }
    }

    public void prepareContextMenu(Tree tree, boolean browseMode, HttpServletRequest request) {
        final Messages msgs = getMessages();

        // add the menu items according to the configuration
        if (contextMenuItems != null && !contextMenuItems.isEmpty()) {
            Iterator<Map> menuItemsIter = contextMenuItems.iterator();
            Map currentItemDefinition;
            ContextMenuItem menuItem;
            String type, name;
            while (menuItemsIter.hasNext()) {
                currentItemDefinition = menuItemsIter.next();
                type = (String) currentItemDefinition.get("type");
                if (!browseMode || (currentItemDefinition.get("showInBrowseMode") != null && ((Boolean) currentItemDefinition.get("showInBrowseMode")))) {
                    if (type != null && type.equalsIgnoreCase("separator")) {
                        tree.addSeparator();
                    } else {
                        name = (String) currentItemDefinition.get("name");
                        menuItem = new ContextMenuItem(name);
                        menuItem.setLabel(msgs.getWithDefault((String) currentItemDefinition.get("label"), (String) currentItemDefinition.get("label")));
                        menuItem.setIcon(request.getContextPath() + (String) currentItemDefinition.get("icon"));
                        String onClick = (String) currentItemDefinition.get("onClick");
                        if (StringUtils.isNotBlank(onClick)) {
                            onClick = StringUtils.replace(onClick, "${tree}", tree.getJavascriptTree());
                            if (!onClick.endsWith(";")) {
                                onClick += ";";
                            }
                            menuItem.setOnclick(onClick);
                        }
                        if (currentItemDefinition.get("javascriptConditions") != null) {
                            Map jsConditions = (Map) currentItemDefinition.get("javascriptConditions");
                            Iterator conditionsIter = jsConditions.values().iterator();
                            String condition;
                            while (conditionsIter.hasNext()) {
                                condition = (String) conditionsIter.next();
                                condition = StringUtils.replace(condition, "${tree}", tree.getJavascriptTree());
                                menuItem.addJavascriptCondition(condition);
                            }
                        }
                        // check if the menu item requires an active subscriber
                        if (!hasAnyActiveSubscriber() && currentItemDefinition.get("requiresActiveSubscriber") != null && ((Boolean) currentItemDefinition.get("requiresActiveSubscriber")).booleanValue()) {
                            menuItem.addJavascriptCondition("new mgnlTreeMenuItemConditionBoolean(false)"); //$NON-NLS-1$
                        }
                        tree.addMenuItem(menuItem);
                    }
                }
            }
        }

    }

    public void prepareFunctionBar(Tree tree, boolean browseMode, HttpServletRequest request) {
        final Messages msgs = getMessages();
        FunctionBar bar = tree.getFunctionBar();
        ContextMenu menu = tree.getMenu();
        if (functionBarItems != null && !functionBarItems.isEmpty()) {
            Iterator<Map> menuItemsIter = functionBarItems.iterator();
            Map currentItemDefinition;
            ContextMenuItem menuItem;
            String type;
            while (menuItemsIter.hasNext()) {
                currentItemDefinition = menuItemsIter.next();
                type = (String) currentItemDefinition.get("type");
                if (!browseMode || (currentItemDefinition.get("showInBrowseMode") != null && ((Boolean) currentItemDefinition.get("showInBrowseMode")))) {
                    if (type != null && type.equalsIgnoreCase("separator")) {
                        tree.addFunctionBarItem(null);
                    } else {
                        String name = (String) currentItemDefinition.get("name");
                        if (StringUtils.isNotBlank(name)) {
                            if (menu.getMenuItemByName(name) != null) {
                                tree.addFunctionBarItemFromContextMenu(name);
                            } else {
                                log.debug("Not existing in context menu \"" + name + "\" - gotta do it by hand... which is not supported yet.");
                                // TODO: add the menu item the classic way...
                            }
                        }
                    }
                }
            }
        } else {
            log.error("No function bar items defined!");
        }
        tree.addFunctionBarItem(null);
        tree.addFunctionBarItemFromContextMenu("activate");
        tree.addFunctionBarItemFromContextMenu("deactivate");
        tree.addFunctionBarItem(null);
        tree.addFunctionBarItem(FunctionBarItem.getRefreshFunctionBarItem(tree, msgs, request));
    }

    /**
     * @return the columns
     */
    public Collection<Map> getColumns() {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(Collection<Map> columns) {
        this.columns = columns;
    }

    /**
     * @return the contextMenuItems
     */
    public Collection<Map> getContextMenuItems() {
        return contextMenuItems;
    }

    /**
     * @param contextMenuItems the contextMenuItems to set
     */
    public void setContextMenuItems(Collection<Map> contextMenuItems) {
        this.contextMenuItems = contextMenuItems;
    }

    /**
     * @return the onIconDoubleClick
     */
    public String getOnIconDoubleClick() {
        return onIconDoubleClick;
    }

    /**
     * @param onIconDoubleClick the onIconDoubleClick to set
     */
    public void setOnIconDoubleClick(String onIconDoubleClick) {
        this.onIconDoubleClick = onIconDoubleClick;
    }

    /**
     * @return the functionBarItems
     */
    public Collection<Map> getFunctionBarItems() {
        return functionBarItems;
    }

    /**
     * @param functionBarItems the functionBarItems to set
     */
    public void setFunctionBarItems(Collection<Map> functionBarItems) {
        this.functionBarItems = functionBarItems;
    }
}
