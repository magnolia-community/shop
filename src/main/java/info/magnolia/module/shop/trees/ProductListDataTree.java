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
package info.magnolia.module.shop.trees;

import java.util.List;

import javax.jcr.PropertyType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import info.magnolia.cms.core.MetaData;
import info.magnolia.cms.gui.control.ContextMenuItem;
import info.magnolia.cms.gui.control.Select;
import info.magnolia.cms.gui.control.Tree;
import info.magnolia.cms.gui.control.TreeColumn;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.util.AlertUtil;
import info.magnolia.module.admininterface.AdminTreeMVCHandler;
import info.magnolia.module.admininterface.trees.ConfigTreeConfiguration;
import info.magnolia.module.data.DataConsts;
import info.magnolia.module.shop.accessors.ShopProductAccesor;
import info.magnolia.module.shop.util.ShopUtil;

/**
 * Product list tree, used with multiselect control and custom control used in productTeaser, it just displays the
 * products for selection.
 * 
 * @author tmiyar
 * 
 */
public class ProductListDataTree extends AdminTreeMVCHandler {

  public ProductListDataTree(String name, HttpServletRequest request,
      HttpServletResponse response) {
    super(name, request, response);
  }

  public void init() {
      String shopName = ShopUtil.getShopName();
      if(StringUtils.isEmpty(shopName)) {
          AlertUtil.setMessage(MessagesManager.getMessages(ShopUtil.I18N_BASENAME).get("warn.noshop"));
      } 
      this.setRootPath(ShopUtil.getPath(ShopProductAccesor.SHOP_PRODUCTS_FOLDER, shopName));
      super.init();
    
      this.setConfiguration(new ConfigTreeConfiguration() {
          final Messages msgs = getMessages();

          public void prepareTree(Tree tree, boolean browseMode,
                  HttpServletRequest request) {

              tree.addItemType(DataConsts.FOLDER_ITEMTYPE, DataConsts.FOLDER_ICON,
                false);
              tree.addItemType("dataItem", DataConsts.FOLDER_ICON, false);
              tree.addItemType("dataItemNode", Tree.DEFAULT_ICON_CONTENTNODE, false);
    
                TreeColumn column0 = new TreeColumn(tree.getJavascriptTree(), request);
                column0.setHtmlEdit();
                column0.setIsLabel(true);
                column0.setWidth(3);
                
                TreeColumn colTitle = new TreeColumn();
                colTitle.setJavascriptTree(tree.getJavascriptTree());
                colTitle.setWidth(1);
                colTitle.setName(DataConsts.TYPE_TITLE);
                colTitle.setTitle(msgs.get("module.data.tree.type.column.title.label"));
        
                TreeColumn column1 = new TreeColumn(tree.getJavascriptTree(), request);
                column1.setName(StringUtils.EMPTY);
                column1.setTitle(msgs.get("tree.config.value")); //$NON-NLS-1$
                column1.setIsNodeDataValue(true);
                column1.setWidth(3);
                column1.setHtmlEdit();
        
                TreeColumn column2 = new TreeColumn(tree.getJavascriptTree(), request);
                column2.setName(StringUtils.EMPTY);
                column2.setTitle(msgs.get("tree.config.type")); //$NON-NLS-1$
                column2.setIsNodeDataType(true);
                column2.setWidth(1);
                Select typeSelect = new Select();
                typeSelect.setName(tree.getJavascriptTree()
                    + TreeColumn.EDIT_NAMEADDITION);
                typeSelect.setSaveInfo(false);
                typeSelect.setCssClass(TreeColumn.EDIT_CSSCLASS_SELECT);
                typeSelect
                    .setEvent(
                        "onblur", tree.getJavascriptTree() + ".saveNodeData(this.value,this.options[this.selectedIndex].text)"); //$NON-NLS-1$
                typeSelect.setOptions(PropertyType.TYPENAME_STRING, Integer
                    .toString(PropertyType.STRING));
                typeSelect.setOptions(PropertyType.TYPENAME_BOOLEAN, Integer
                    .toString(PropertyType.BOOLEAN));
                typeSelect.setOptions(PropertyType.TYPENAME_LONG, Integer
                    .toString(PropertyType.LONG));
                typeSelect.setOptions(PropertyType.TYPENAME_DOUBLE, Integer
                    .toString(PropertyType.DOUBLE));
                // todo:
                // typeSelect.setOptions(PropertyType.TYPENAME_DATE,Integer.toString(PropertyType.DATE));
                column2.setHtmlEdit(typeSelect.getHtml());
        
                TreeColumn columnIcons = new TreeColumn(tree.getJavascriptTree(),
                    request);
                columnIcons.setCssClass(StringUtils.EMPTY);
                columnIcons.setTitle(msgs.get("tree.config.status")); //$NON-NLS-1$
                columnIcons.setWidth(1);
                columnIcons.setIsIcons(true);
                columnIcons.setIconsActivation(true);
                columnIcons.setIconsPermission(true);
        
                TreeColumn column4 = new TreeColumn(tree.getJavascriptTree(), request);
                column4.setName(MetaData.LAST_MODIFIED);
                column4.setIsMeta(true);
                column4.setDateFormat("yy-MM-dd, HH:mm"); //$NON-NLS-1$
                column4.setWidth(2);
                column4.setTitle(msgs.get("tree.config.date")); //$NON-NLS-1$
        
                tree.addColumn(column0);
    
                if (!browseMode) {
                  tree.addColumn(column1);
                  tree.addColumn(colTitle);
                  tree.addColumn(column2);
        
                  if (isAdminInstance() || hasAnyActiveSubscriber()) {
                    tree.addColumn(columnIcons);
                  }
                  tree.addColumn(column4);
                }
        
          }

          public void prepareContextMenu(Tree tree, boolean browseMode,
                  HttpServletRequest request) {
              super.prepareContextMenu(tree, browseMode, request);
              if (!browseMode) {
                  List menu = tree.getMenu().getMenuItems();
                  menu.remove(0);
                  menu.remove(0);
                  menu.remove(0);
        
                  ContextMenuItem menuNewFolder = new ContextMenuItem("newFolder");
                  menuNewFolder.setLabel(msgs
                      .get("module.data.tree.data.menu.newFolder"));
                  menuNewFolder.setIcon(request.getContextPath() + Tree.ICONDOCROOT
                      + "folder_add.gif");
                  menuNewFolder.setOnclick(tree.getJavascriptTree() + ".createNode('"
                      + DataConsts.FOLDER_ITEMTYPE + "');");
                  menuNewFolder
                      .addJavascriptCondition("new mgnlTreeMenuItemConditionSelectedNotNodeData(" //$NON-NLS-1$
                          + tree.getJavascriptTree() + ")"); //$NON-NLS-1$
        
                  menu.add(0, menuNewFolder);
              }
          }

    });
      
  }

  @Override
  public String copy() {
    return VIEW_TREE;
  }

  @Override
  public String move() {
    return VIEW_TREE;
  }

  @Override
  public String renameNode(String newLabel) {
    return VIEW_TREE;
  }

  @Override
  public String saveValue() {
    return VIEW_TREE;
  }

}
