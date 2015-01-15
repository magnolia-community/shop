/**
 * This file Copyright (c) 2013-2015 Magnolia International
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

import info.magnolia.cms.util.QueryUtil;
import info.magnolia.commands.CommandsManager;
import info.magnolia.event.EventBus;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcr.util.SessionUtil;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.framework.action.MarkNodeAsDeletedAction;
import info.magnolia.ui.framework.action.MarkNodeAsDeletedActionDefinition;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import java.util.List;

import javax.inject.Named;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delete action that will mark all shop related stuff as deleted.<br/>
 * FIXME 1: entries from appLauncher are not removed because it is not possible to override execute method (its final) - MGNLUI-2344<br/>
 * FIXME 2: similar as above but for /modules/shop/apps/<shopToRemoveApps><br/>
 * FIXME 3: subnodes are not marked as deleted - MAGNOLIA-5337<br/>
 */
public class ShopMarkNodeAsDeletedAction extends MarkNodeAsDeletedAction {

    private static final Logger log = LoggerFactory.getLogger(ShopMarkNodeAsDeletedAction.class);

    private SimpleTranslator i18n;

    public ShopMarkNodeAsDeletedAction(MarkNodeAsDeletedActionDefinition definition, JcrItemAdapter item, CommandsManager commandsManager, @Named("admincentral") EventBus eventBus, UiContext uiContext, SimpleTranslator i18n) {
        super(definition, item, commandsManager, eventBus, uiContext, i18n);
        this.i18n = i18n;
        resolveRelatedItems(item.getJcrItem());
    }

    public ShopMarkNodeAsDeletedAction(MarkNodeAsDeletedActionDefinition definition, List<JcrItemAdapter> items, CommandsManager commandsManager, @Named("admincentral") EventBus eventBus, UiContext uiContext, SimpleTranslator i18n) {
        super(definition, items, commandsManager, eventBus, uiContext, i18n);
        this.i18n = i18n;
        for (JcrItemAdapter item : items) {
            resolveRelatedItems(item.getJcrItem());
        }
    }

    @Override
    protected void onPreExecute() throws Exception {
        super.onPreExecute();

        Node item = (Node) jcrItem;
        String shopName = item.getName();
        String sql = "select * from [mgnl:page] where [mgnl:template] = 'shop:pages/shopHome'";
        NodeIterator iter = QueryUtil.search("website", sql);
        while (iter.hasNext()) {
            Node node = iter.nextNode();
            if (!node.hasProperty("currentShop")) {
                continue;
            }
            if (shopName.equals(node.getName())) {
                throw new ActionExecutionException(i18n.translate("shop.exists.error"));
            }
        }
    }

    private void resolveRelatedItems(Item jcrItem) {
        try {
            String shopName = jcrItem.getName();
            String shopPath = "/" + shopName;
            addItem(ShopRepositoryConstants.SHOPS, shopPath);
            addItem(ShopRepositoryConstants.SHOP_PRODUCTS, shopPath);
            addItem(ShopRepositoryConstants.SHOP_SUPPLIERS, shopPath);
            addItem(ShopRepositoryConstants.SHOPPING_CARTS, shopPath);
        } catch (RepositoryException e) {
            log.error("Unable to obtain jcr item name.", e);
        }
    }

    private void addItem(String workspace, String path) {
        Node node = SessionUtil.getNode(workspace, path);
        if (node != null) {
            getItems().add(new JcrNodeAdapter(node));
        }
    }
}
