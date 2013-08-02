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
package info.magnolia.module.shop.trees;

import javax.servlet.http.HttpServletRequest;

import info.magnolia.cms.gui.control.ContextMenu;
import info.magnolia.cms.gui.control.ContextMenuItem;
import info.magnolia.cms.gui.control.Tree;

/** 
 * we add a column title.
 * @author tmiyar
 *
 */
public class ProductsTreeConfig extends GenericShopsTreeConfig{

    @Override
    public void prepareFunctionBar(Tree tree, boolean browseMode,
            HttpServletRequest request) {
        super.prepareFunctionBar(tree, browseMode, request);
        ContextMenu menu = tree.getMenu();
        ContextMenuItem newProduct = menu.getMenuItemByName("new");
        newProduct.setLabel("New Product");
        ContextMenuItem newProductOptions = new ContextMenuItem("newProductOptions");
             
        newProductOptions.setLabel("new product options");
        newProductOptions.setIcon(request.getContextPath() + "/.resources/icons/16/document_add.gif");
        newProductOptions.setOnclick("mgnl.data.DataTree.create("
                + tree.getJavascriptTree()
                + ".selectedNode.id, '"
                + "shopProductOptions"
                + "');");
        menu.getMenuItems().add(2, newProductOptions);
        newProductOptions.addJavascriptCondition("new mgnlTreeMenuItemConditionPermissionWrite(" //$NON-NLS-1$
                + tree.getJavascriptTree()
                + ")"); //$NON-NLS-1$
        
        //FunctionBar bar = tree.getFunctionBar();
        //bar.getMenuItems().add(2, newProductOptions);
    }

    
}
