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
package info.magnolia.module.shop.app.field.factory;

import info.magnolia.cms.util.QueryUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.shop.ShopNodeTypes;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.module.shop.app.field.definition.ProductSupplierSelectFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.form.field.factory.SelectFieldFactory;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;

/**
 * Field factory for product supplier.
 */
public class ProductSupplierSelectFieldFactory extends SelectFieldFactory<ProductSupplierSelectFieldDefinition> {

    private Item relatedFieldItem;

    private static final Logger log = LoggerFactory.getLogger(ProductSupplierSelectFieldFactory.class);

    public ProductSupplierSelectFieldFactory(ProductSupplierSelectFieldDefinition definition, Item relatedFieldItem) {
        super(definition, relatedFieldItem);
        this.relatedFieldItem = relatedFieldItem;
    }

    @Override
    public List<SelectFieldOptionDefinition> getSelectFieldOptionDefinition() {
        List<SelectFieldOptionDefinition> res = new ArrayList<SelectFieldOptionDefinition>();
        if (relatedFieldItem instanceof JcrNodeAdapter) {
            Node productNode = ((JcrNodeAdapter) relatedFieldItem).getJcrItem();
            try {
                String shopName = StringUtils.substringBefore(StringUtils.substringAfter(productNode.getPath(), "/"), "/");
                String sql = "select * from [shopSupplier] where isdescendantnode([/" + shopName + "])";
                NodeIterator iter = QueryUtil.search(ShopRepositoryConstants.SHOP_SUPPLIERS, sql, Query.JCR_SQL2, ShopNodeTypes.SHOP_SUPPLIER);

                while(iter.hasNext()) {
                    Node supplier = iter.nextNode();
                    SelectFieldOptionDefinition fieldDefinition = new SelectFieldOptionDefinition();
                    fieldDefinition.setLabel(PropertyUtil.getString(supplier, "company", "") + " " + PropertyUtil.getString(supplier, "city", ""));
                    fieldDefinition.setValue(supplier.getIdentifier());
                    res.add(fieldDefinition);
                }
            } catch (RepositoryException e) {
                log.error("Unable to obtain suppliers for product " + productNode, e);
            }
        }
        return res;
    }
}
