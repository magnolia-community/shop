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
package info.magnolia.shop.app.field.factory;

import com.vaadin.v7.data.Item;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.shop.app.field.definition.ProductCategoriesFieldDefinition;
import info.magnolia.shop.util.ShopUtil;
import info.magnolia.ui.form.field.definition.OptionGroupFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.form.field.factory.OptionGroupFieldFactory;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for ProductCategoriesField.
 */
public class ProductCategoriesFieldFactory extends OptionGroupFieldFactory {

    private static final Logger log = LoggerFactory.getLogger(ProductCategoriesFieldFactory.class);

    public ProductCategoriesFieldFactory(OptionGroupFieldDefinition definition, Item relatedFieldItem, ComponentProvider componentProvider) {
        super(definition, relatedFieldItem, componentProvider);
    }

    @Override
    public List<SelectFieldOptionDefinition> getSelectFieldOptionDefinition() {
        List<SelectFieldOptionDefinition> res = new ArrayList<SelectFieldOptionDefinition>();
        if (item instanceof JcrNodeAdapter) {
            Node productNode = ((JcrNodeAdapter) item).getJcrItem();
            try {
                String shopName = StringUtils.substringBefore(StringUtils.substringAfter(productNode.getPath(), "/"), "/");
                List<Node> shopCategories = getShopAssociatedCategories(shopName);

                for (Node category : shopCategories) {
                    SelectFieldOptionDefinition field = new SelectFieldOptionDefinition();
                    field.setLabel(PropertyUtil.getString(category, "title"));
                    field.setValue(category.getIdentifier());
                    res.add(field);
                }
            } catch (RepositoryException e) {
                log.error("Error while creating product category fields.", e);
            }
        }
        return res;
    }

    private List<Node> getShopAssociatedCategories(String shopName) {
        List<Node> categories = new ArrayList<Node>();
        if (StringUtils.isBlank(shopName)) {
            return categories;
        }
//        try {
            ProductCategoriesFieldDefinition definition = (ProductCategoriesFieldDefinition) this.definition;
            Node shop = ShopUtil.getShopRootByShopName(shopName);
            if (shop != null) {
                // TODO: find MTE equivalent
//                categories = TemplateCategoryUtil.getContentListByTemplateCategorySubCategory(shop, definition.getCategory(), definition.getSubcategory());
            } else {
                log.warn("No shop found with name " + shopName);
            }
//        } catch (RepositoryException e) {
//            log.error("Unable to obtain categories for shop " + shopName + ".", e);
//        }
        return categories;
    }
}
