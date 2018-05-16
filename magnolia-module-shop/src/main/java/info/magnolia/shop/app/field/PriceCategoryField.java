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
package info.magnolia.shop.app.field;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.PropertysetItem;
import com.vaadin.v7.ui.Field;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.shop.ShopRepositoryConstants;
import info.magnolia.shop.app.field.definition.PriceCategoryFieldDefinition;
import info.magnolia.ui.form.field.AbstractCustomMultiField;
import info.magnolia.ui.form.field.StaticField;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.definition.StaticFieldDefinition;
import info.magnolia.ui.form.field.definition.TextFieldDefinition;
import info.magnolia.ui.form.field.factory.FieldFactoryFactory;
import info.magnolia.ui.vaadin.integration.NullItem;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Field for price categories.
 */
public class PriceCategoryField
        extends AbstractCustomMultiField<PriceCategoryFieldDefinition, PropertysetItem> {

    private GridLayout root;
    private SimpleTranslator i18n;

    private static final Logger log = LoggerFactory.getLogger(PriceCategoryField.class);

    public PriceCategoryField(PriceCategoryFieldDefinition definition, FieldFactoryFactory fieldFactoryFactory, I18nContentSupport i18nContentSupport, ComponentProvider componentProvider, Item relatedFieldItem, SimpleTranslator i18n) {
        super(definition, fieldFactoryFactory, i18nContentSupport, componentProvider, relatedFieldItem);
        this.i18n = i18n;

    }

    @Override
    protected Component initContent() {
        addStyleName("linkfield");
        root = new GridLayout();
        root.setWidth("520px");
        root.setColumns(4);
        initFields();
        return root;
    }

    @Override
    protected void initFields(PropertysetItem fieldValues) {
        root.removeAllComponents();
        List<List<ConfiguredFieldDefinition>> fields = createFiledDefinitions();
        if (fields.size() > 1) {
            root.setRows(fields.size());
        } else {
            root.setRows(1);
        }
        Iterator iter = fields.iterator();
        root.addComponent(new StaticField(i18n.translate("shopProducts.pricing.prices.title.label")));
        root.addComponent(new StaticField(i18n.translate("shopProducts.pricing.prices.currency.label")));
        root.addComponent(new StaticField(i18n.translate("shopProducts.pricing.prices.tax.label")));
        root.addComponent(new StaticField(i18n.translate("shopProducts.pricing.prices.price.label")));
        while (iter.hasNext()) {
            List<ConfiguredFieldDefinition> list = (List<ConfiguredFieldDefinition>) iter.next();
            for (ConfiguredFieldDefinition fieldDefinition : list) {
                Field<?> field = createLocalField(fieldDefinition, new DefaultProperty<NullItem>(new NullItem()), false);
                if (fieldDefinition instanceof TextFieldDefinition) {
                    if (fieldValues.getItemProperty(fieldDefinition.getName()) != null) {
                        field.setPropertyDataSource(fieldValues.getItemProperty(fieldDefinition.getName()));
                    } else {
                        fieldValues.addItemProperty(fieldDefinition.getName(), field.getPropertyDataSource());
                    }
                    field.addValueChangeListener(selectionListener);
                }
                root.addComponent(field);
            }
        }
    }

    /**
     * Shop needs special handling of the price categories. We need to construct fields accroding to the price categories defined under shops/priceCategories.
     * We can have zero to x priceCategories and each of them consists of three static fields and one text field
     */
    private List<List<ConfiguredFieldDefinition>> createFiledDefinitions() {
        List<List<ConfiguredFieldDefinition>> res = new ArrayList<List<ConfiguredFieldDefinition>>();
        if (relatedFieldItem instanceof JcrNodeAdapter) {
            Node productNode = ((JcrNodeAdapter) relatedFieldItem).getJcrItem();
            try {
                String shopName = StringUtils.substringBefore(StringUtils.substringAfter(productNode.getPath(), "/"), "/");
                String sql = "select * from [shopPriceCategory] where isdescendantnode([/" + shopName + "])";
                NodeIterator iter = QueryUtil.search(ShopRepositoryConstants.SHOPS, sql, Query.JCR_SQL2, "shopPriceCategory");
                while (iter.hasNext()) {
                    Node priceCategoryNode = iter.nextNode();
                    String title = PropertyUtil.getString(priceCategoryNode, "title", "");
                    String tax = PropertyUtil.getBoolean(priceCategoryNode, "taxIncluded", false) ? "incl." : "excl.";
                    String currency = PropertyUtil.getString(MgnlContext.getJCRSession(ShopRepositoryConstants.SHOPS).getNodeByIdentifier(PropertyUtil.getString(priceCategoryNode, "currencyUUID")), "title", "");
                    List<ConfiguredFieldDefinition> fieldDefinitions = new ArrayList<ConfiguredFieldDefinition>();

                    StaticFieldDefinition fieldDefinition = new StaticFieldDefinition();
                    fieldDefinition.setLabel("");
                    fieldDefinition.setValue(title);
                    fieldDefinition.setName("title");
                    fieldDefinitions.add(fieldDefinition);

                    fieldDefinition = new StaticFieldDefinition();
                    fieldDefinition.setLabel("");
                    fieldDefinition.setValue(currency);
                    fieldDefinition.setName("currency");
                    fieldDefinitions.add(fieldDefinition);

                    fieldDefinition = new StaticFieldDefinition();
                    fieldDefinition.setLabel("");
                    fieldDefinition.setValue(tax);
                    fieldDefinition.setName("tax");
                    fieldDefinitions.add(fieldDefinition);

                    TextFieldDefinition textFieldDefinition = new TextFieldDefinition();
                    textFieldDefinition.setLabel("");
                    textFieldDefinition.setName(priceCategoryNode.getIdentifier());
                    fieldDefinitions.add(textFieldDefinition);

                    res.add(fieldDefinitions);
                }
            } catch (RepositoryException e) {
                log.error("Unable to create price fields.", e);
            }
        }
        return res;
    }

    @Override
    public Class<? extends PropertysetItem> getType() {
        return PropertysetItem.class;
    }
}
