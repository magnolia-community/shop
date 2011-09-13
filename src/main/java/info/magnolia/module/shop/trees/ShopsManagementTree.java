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

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.exchange.ExchangeException;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.util.AlertUtil;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.ExclusiveWrite;
import info.magnolia.module.data.trees.GenericDataAdminTree;

/**
 * Will trigger the deletion of the shop related data like shopProducts.
 * @author tmiyar
 *
 */
public class ShopsManagementTree extends GenericDataAdminTree {

    private static Logger log = LoggerFactory.getLogger(ShopsManagementTree.class);
    
    public ShopsManagementTree(String name, HttpServletRequest request,
            HttpServletResponse response) {
        super(name, request, response);
        String rootpath = this.getRootPath();
    }
    
    public String delete() {
        String deleteNode = this.getRequest().getParameter("deleteNode"); //$NON-NLS-1$
        
        try {
            synchronized (ExclusiveWrite.getInstance()) {
                deleteNode(path, deleteNode);
                checkAndDeleteNode("/shopCarts", deleteNode);
                checkAndDeleteNode("/shopProducts", deleteNode);
                checkAndDeleteNode("/shopProductCategories", deleteNode);
                //need to remove the menu
                Content menuItem = ContentUtil.getContent(ContentRepository.CONFIG, "/modules/adminInterface/config/menu/" + deleteNode);
                if(menuItem != null) {
                    Content menu = menuItem.getParent();
                    menuItem.delete();
                    menu.save();
                }
                
            }
        }
        catch (Exception e) {
            log.error("can't delete", e);
            AlertUtil.setMessage(MessagesManager.get("tree.error.delete") + " " + AlertUtil.getExceptionMessage(e));
        }
        return VIEW_TREE;
    }

    private void checkAndDeleteNode(String path, String deleteNode)
            throws ExchangeException, RepositoryException {
        if(ContentUtil.getContent("data", path + "/"+ deleteNode) != null) {
            deleteNode(path, deleteNode);
        }
    }

}
