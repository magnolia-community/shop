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

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.wrapper.JCRMgnlPropertiesFilteringNodeWrapper;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templatingkit.templates.category.TemplateCategoryUtil;
import info.magnolia.ui.form.field.definition.CheckboxFieldDefinition;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

/**
 * Field definition of product categories.
 */
public class ProductCategoriesFieldDefinition extends CheckboxFieldDefinition {

    private String category = "feature";

    private String subcategory = "product-category";

    private List<CheckboxFieldDefinition> fields;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public List<CheckboxFieldDefinition> getFields(Node productNode) {
        initFields(productNode);
        return fields;
    }

    protected void initFields(Node productNode) {
        fields = new ArrayList<CheckboxFieldDefinition>();
        if (productNode != null) {
            try {
                // TODO cleanup, move it to field or factory?
                if (productNode.hasNode("productCategoryUUIDs")) {
                    Node categoryUUIDs = productNode.getNode("productCategoryUUIDs");
                    PropertyIterator it = new JCRMgnlPropertiesFilteringNodeWrapper(categoryUUIDs).getProperties();
                    List<Node> productCategories = new ArrayList<Node>();
                    String shopName = StringUtils.substringBefore(StringUtils.substringAfter(productNode.getPath(), "/shopProducts/"), "/");
                    while (it.hasNext()) {
                        Property prop = it.nextProperty();
                        String uuid = prop.getValue().getString();
                        Node categoryNode = NodeUtil.getNodeByIdentifier("website", uuid);
                        productCategories.add(categoryNode);
                    }

                    Node shop = ShopUtil.getShopRootByShopName(shopName).getJCRNode();
                    List<Node> categories = new ArrayList<Node>();
                    if (shop != null) {
                        categories = TemplateCategoryUtil.getContentListByTemplateCategorySubCategory(shop, getCategory(), getSubcategory());
                    }

                    for (Node category : categories) {
                        CheckboxFieldDefinition checkboxFieldDefinition = new CheckboxFieldDefinition();
                        checkboxFieldDefinition.setButtonLabel(PropertyUtil.getString(category, "title"));
                        checkboxFieldDefinition.setLabel("");
                        for (Node productCategory : productCategories) {
                            if (productCategory.getName().equals(category.getName())) {
                                checkboxFieldDefinition.setDefaultValue("true");
                            }
                        }
                        fields.add(checkboxFieldDefinition);
                    }
                }
            } catch (RepositoryException e) {
                // TODO log error
            }
        }
    }
}
