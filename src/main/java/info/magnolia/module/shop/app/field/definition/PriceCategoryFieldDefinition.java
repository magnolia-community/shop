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
package info.magnolia.module.shop.app.field.definition;

import info.magnolia.cms.util.QueryUtil;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.definition.StaticFieldDefinition;
import info.magnolia.ui.form.field.definition.TextFieldDefinition;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

import org.apache.commons.lang.StringUtils;

/**
 * Definition class for price categories.
 */
public class PriceCategoryFieldDefinition extends ConfiguredFieldDefinition {

    private List<ConfiguredFieldDefinition> fields;
    private int rows;

    public int getRows() {
        return rows > 0 ? rows : 1;
    }

    public List<ConfiguredFieldDefinition> getFields(Node productNode) {
        initFields(productNode);
        return fields;
    }

    protected void initFields(Node productNode) {
        fields = new ArrayList<ConfiguredFieldDefinition>();
        if (productNode != null) {
            try {
                // TODO cleanup, move it to field or to factory?
                String shopName = StringUtils.substringBefore(StringUtils.substringAfter(productNode.getPath(), "/shopProducts/"), "/");
                String sql = "select * from [shopPriceCategory] where isdescendantnode([/shops/" + shopName + "])";
                NodeIterator iter = QueryUtil.search("data", sql, Query.JCR_SQL2, "shopPriceCategory");
                rows = 0;
                while (iter.hasNext()) {
                    Node node = iter.nextNode();
                    String priceCategory = PropertyUtil.getString(node, "title");
                    String tax = PropertyUtil.getBoolean(node, "taxIncluded", false) ? "incl." : "excl.";
                    Node currencyNode = NodeUtil.getNodeByIdentifier("data", PropertyUtil.getString(node, "currencyUUID"));
                    String currency = PropertyUtil.getString(currencyNode, "title");
                    Node pricesNode = productNode.getNode("prices");
                    String price = "";
                    Iterable<Node> iterable = NodeUtil.collectAllChildren(pricesNode);
                    for (Node n : iterable) {
                        if (n.hasProperty("priceCategoryUUID")) {
                            if (node.getIdentifier().equals(PropertyUtil.getString(n, "priceCategoryUUID"))) {
                                price = PropertyUtil.getString(n, "price");
                                break;
                            }
                        }
                    }

                    StaticFieldDefinition staticFieldDefinition = new StaticFieldDefinition();
                    staticFieldDefinition.setLabel("Price category");
                    staticFieldDefinition.setValue(priceCategory);
                    fields.add(staticFieldDefinition);

                    staticFieldDefinition = new StaticFieldDefinition();
                    staticFieldDefinition.setLabel("Currency");
                    staticFieldDefinition.setValue(currency);
                    fields.add(staticFieldDefinition);

                    staticFieldDefinition = new StaticFieldDefinition();
                    staticFieldDefinition.setLabel("VAT");
                    staticFieldDefinition.setValue(tax);
                    fields.add(staticFieldDefinition);

                    TextFieldDefinition textFieldDefinition = new TextFieldDefinition();
                    textFieldDefinition.setLabel("Price");
                    textFieldDefinition.setDefaultValue(price);
                    fields.add(textFieldDefinition);

                    rows++;
                }
            } catch (RepositoryException e) {
                // TODO log error
            }
        }
    }
}
