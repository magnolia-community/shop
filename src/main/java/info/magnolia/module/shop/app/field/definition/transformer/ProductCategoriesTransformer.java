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

import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.wrapper.JCRMgnlPropertiesFilteringNodeWrapper;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.transformer.basic.BasicTransformer;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;

/**
 * Transformer for product categories. Categories are saved like this:
 * <pre>
 * (+) product
 *    (+) productCategoryUUIDs
 *       - 0=uuid
 *       - 1=uuid
 * </pre>
 */
public class ProductCategoriesTransformer extends BasicTransformer<Set<String>> {

    private static final Logger log = LoggerFactory.getLogger(ProductCategoriesTransformer.class);

    public ProductCategoriesTransformer(Item relatedFormItem, ConfiguredFieldDefinition definition, Class<Set<String>> type) {
        super(relatedFormItem, definition, type);
    }

    @Override
    public Set<String> readFromItem() {
        Set<String> uuids = new HashSet<String>();
        try {
            Node node = getProductCategoryUUIDsNode();
            if (node != null) {
                PropertyIterator it = new JCRMgnlPropertiesFilteringNodeWrapper(node).getProperties();
                while (it.hasNext()) {
                    Property property = it.nextProperty();
                    String uuid = property.getValue().getString();
                    uuids.add(uuid);
                }
            }
        } catch (RepositoryException e) {
            log.error("Could not obtain product categories.", e);
        }
        return uuids;
    }

    @Override
    public void writeToItem(Set<String> newValue) {
        JcrNodeAdapter rootItem = getRootItem();
        rootItem.getChildren().clear();
        setNewProperties(rootItem, newValue);
        removeOldProperties(rootItem);
        ((JcrNodeAdapter) relatedFormItem).addChild(rootItem);
    }


    private void setNewProperties(JcrNodeAdapter rootItem, Set<String> newValue) {
        int index = 0;
        for (String uuid : newValue) {
            rootItem.addItemProperty(String.valueOf(index), new DefaultProperty<String>(String.class, uuid));
            index++;
        }
    }

    private void removeOldProperties(JcrNodeAdapter rootItem) {
        Node node = getProductCategoryUUIDsNode();
        try {
            PropertyIterator iter = new JCRMgnlPropertiesFilteringNodeWrapper(node).getProperties();
            while (iter.hasNext()) {
                String propertyName = iter.nextProperty().getName();
                if (!rootItem.getItemPropertyIds().contains(propertyName)) {
                    rootItem.removeItemProperty(propertyName);
                }
            }
        } catch (RepositoryException e) {
            log.error("Unable to clean old properties from " + rootItem.getNodeName());
        }
    }

    private Node getProductCategoryUUIDsNode() {
        Node node = ((JcrNodeAdapter) relatedFormItem).getJcrItem();
        try {
            if (node.hasNode("productCategoryUUIDs")) {
                return node.getNode("productCategoryUUIDs");
            }
        } catch (RepositoryException e) {
            log.error("Unable to obtain product category uuids from product " + node);
        }
        return null;
    }

    private JcrNodeAdapter getRootItem() {
        Node rootNode = ((JcrNodeAdapter) relatedFormItem).getJcrItem();
        try {
            if (rootNode.hasNode("productCategoryUUIDs")) {
                return new JcrNodeAdapter(rootNode.getNode("productCategoryUUIDs"));
            }
        } catch (RepositoryException e) {
            log.error("Unable to obtain [prices] node from " + rootNode);
            return null;
        }
        return new JcrNewNodeAdapter(rootNode, NodeTypes.ContentNode.NAME, "productCategoryUUIDs");
    }
}
