/**
 * This file Copyright (c) 2010-2015 Magnolia International
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

import info.magnolia.cms.util.QueryUtil;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.QueryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.shop.ShopNodeTypes;
import info.magnolia.module.shop.ShopRepositoryConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

/**
 * Updates the carts and cart items node types from mgnl:contentNode to shopCart and shopCartItem.
 */
public class ChangeCartNodeTypesTask extends QueryTask {

    private static Logger log = LoggerFactory.getLogger(ChangeCartNodeTypesTask.class);
    // TODO: convert this to SQL2 so that we don't need to overwrite QueryTask.doExecute()
    private static final String QUERY = "/jcr:root//element(*,mgnl:contentNode)/cartItems";
    // TODO: having this error message in the task will break ShopModulVersionHandlerTest.testUpdateTo210() because the test expects no message in ctx. > Remove test or message?
//    private static final String QUERY_SCOPE_WARNING = "%s is a QueryTask. "
//            + "Mind that JCR queries may NOT account for unsaved changes in current session, e.g. from previous update tasks. "
//            + "To avoid incomplete updates, please use a NodeVisitorTask.";

    public ChangeCartNodeTypesTask(String name, String description) {
        super(name, description, ShopRepositoryConstants.SHOPPING_CARTS, QUERY);
    }

    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException {
        // we really want to guard against unsafe usages of the QueryTask!
//        installContext.warn(String.format(QUERY_SCOPE_WARNING, getClass().getSimpleName()));

        final NodeIterator nodeIterator = QueryUtil.search(ShopRepositoryConstants.SHOPPING_CARTS, QUERY, Query.XPATH);
        while(nodeIterator.hasNext()){
            operateOnNode(installContext, nodeIterator.nextNode());
        }
    }

    @Override
    protected void operateOnNode(InstallContext installContext, Node node) {
        try {
            // set parent node to "shopCart"
            log.debug("Setting node type of " + node.getParent().getPath() + " to " + ShopNodeTypes.SHOP_CART);
            node.getParent().setPrimaryType(ShopNodeTypes.SHOP_CART);
            // set all child nodes to "shopCartItem"
            final NodeIterator nodeIterator = node.getNodes();
            Node itemNode;
            while(nodeIterator.hasNext()){
                itemNode = nodeIterator.nextNode();
                log.debug("Setting node type of " + itemNode.getPath() + " to " + ShopNodeTypes.SHOP_CART_ITEM);
                itemNode.setPrimaryType(ShopNodeTypes.SHOP_CART_ITEM);
            }

        } catch (RepositoryException e) {
            log.error("Failed to set primary node types to {} and {} in cart {}", ShopNodeTypes.SHOP_CART, ShopNodeTypes.SHOP_CART_ITEM, node);
        }

    }
}
