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
package info.magnolia.module.shop.app.field.definition.transformer;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.predicate.AbstractPredicate;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.transformer.basic.BasicTransformer;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;

/**
 * Transformer for price categories. Entries are stored like this:<br/>
 * <pre>
 * (+) product<br/>
 *    (+) prices<br/>
 *       (+) 0<br/>
 *          - price<br/>
 *          - priceCategoryUUID<br/>
 *       (+) 1<br/>
 *          - price<br/>
 *          - priceCategoryUUID<br/>
 * </pre>
 */
public class PriceCategoryTransformer extends BasicTransformer<PropertysetItem> {

    private AbstractPredicate<Node> predicate = new AbstractPredicate<Node>() {
        @Override
        public boolean evaluateTyped(Node node) {
            try {
                return node.getName().matches("[0-9]+");
            } catch (RepositoryException e) {
                return false;
            }
        }
    };

    private static final Logger log = LoggerFactory.getLogger(PriceCategoryTransformer.class);

    public PriceCategoryTransformer(Item relatedFormItem, ConfiguredFieldDefinition definition, Class<PropertysetItem> type) {
        super(relatedFormItem, definition, type);
    }

    @Override
    public PropertysetItem readFromItem() {
        PropertysetItem newValues = new PropertysetItem();
        Node pricesNode = getPricesNode((JcrNodeAdapter) relatedFormItem);
        if (pricesNode != null) {
            Iterable<Node> children = getStoredChildren(pricesNode);
            for (Node child : children) {
                String price = getValueFromChildNode(child, "price");
                String priceCategoryUUID = getValueFromChildNode(child, "priceCategoryUUID");
                if (priceCategoryExists(priceCategoryUUID)) {
                    newValues.addItemProperty(priceCategoryUUID, new DefaultProperty<String>(String.class, price));
                } else {
                    log.warn("Unable to find shop price category with UUID [" + priceCategoryUUID + "].");
                }
            }
        }
        return newValues;
    }

    @Override
    public void writeToItem(PropertysetItem newValue) {
        JcrNodeAdapter rootItem = getRootItem();
        rootItem.getChildren().clear();
        setNewChildItem(rootItem, newValue);
        detachNonExistingChildren(rootItem);
        ((JcrNodeAdapter) relatedFormItem).addChild(rootItem);
    }

    private Node getPricesNode(JcrNodeAdapter relatedFormItem) {
        Node node = relatedFormItem.getJcrItem();
        try {
            if (node.hasNode("prices")) {
                return node.getNode("prices");
            }
        } catch (RepositoryException e) {
            log.error("Unable to obtain prices from product " + node);
        }
        return null;
    }


    private void detachNonExistingChildren(JcrNodeAdapter rootItem) {
        try {
            List<Node> children = NodeUtil.asList(NodeUtil.getNodes(rootItem.getJcrItem()));
            for (Node child : children) {
                if (rootItem.getChild(child.getName()) == null) {
                    JcrNodeAdapter toRemove = new JcrNodeAdapter(child);
                    rootItem.removeChild(toRemove);
                }
            }
        } catch (RepositoryException e) {
            log.error("Could remove children", e);
        }
    }

    private boolean priceCategoryExists(Object priceCategoryUUID) {
        if (priceCategoryUUID instanceof String) {
            try {
                return MgnlContext.getJCRSession(ShopRepositoryConstants.SHOPS).getNodeByIdentifier((String) priceCategoryUUID) != null;
            } catch (RepositoryException e) {
                return false;
            }
        }
        return false;
    }


    private String getValueFromChildNode(Node child, String property) {
        return PropertyUtil.getString(child, property, null);
    }

    private Iterable<Node> getStoredChildren(Node node) {
        Iterable<Node> nodes = null;
        try {
            if (node != null && !(relatedFormItem instanceof JcrNewNodeAdapter)) {
                nodes = NodeUtil.getNodes(node, predicate);
            }
        } catch (RepositoryException e) {
            log.error("Unable to get children for node: " + node, e);
        }
        return nodes;
    }

    private void setNewChildItem(JcrNodeAdapter rootItem, PropertysetItem newValue) {
        try {
            int index = 0;
            for (Object id : newValue.getItemPropertyIds()) {
                JcrNodeAdapter childItem = initializeChildItem(rootItem, rootItem.getJcrItem(), String.valueOf(index));
                childItem.addItemProperty("price", newValue.getItemProperty(id));
                childItem.addItemProperty("priceCategoryUUID", new DefaultProperty<String>(String.class, (String) id));
                index++;
            }
        } catch (RepositoryException e) {
            log.error("Unable to obtain jcr item from " + rootItem.getNodeName());
        }
    }

    private JcrNodeAdapter initializeChildItem(JcrNodeAdapter rootItem, Node rootNode, String childName) throws RepositoryException {
        JcrNodeAdapter childItem;
        if (!(rootItem instanceof JcrNewNodeAdapter) && rootNode.hasNode(childName)) {
            childItem = new JcrNodeAdapter(rootNode.getNode(childName));
        } else {
            childItem = new JcrNewNodeAdapter(rootNode, NodeTypes.ContentNode.NAME, childName);
        }
        rootItem.addChild(childItem);
        return childItem;
    }

    private JcrNodeAdapter getRootItem() {
        Node rootNode = ((JcrNodeAdapter) relatedFormItem).getJcrItem();
        try {
            if (rootNode.hasNode("prices")) {
                return new JcrNodeAdapter(rootNode.getNode("prices"));
            }
        } catch (RepositoryException e) {
            log.error("Unable to obtain [prices] node from " + rootNode);
            return null;
        }
        return new JcrNewNodeAdapter(rootNode, NodeTypes.ContentNode.NAME, "prices");
    }

}
