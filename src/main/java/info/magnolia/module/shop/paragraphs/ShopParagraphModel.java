/**
 * This file Copyright (c) 2010-2011 Magnolia International
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
package info.magnolia.module.shop.paragraphs;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.SelectorUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.wrapper.I18nNodeWrapper;
import info.magnolia.module.dms.beans.Document;
import info.magnolia.module.shop.ShopConfiguration;
import info.magnolia.module.shop.accessors.ShopAccesor;
import info.magnolia.module.shop.accessors.ShopProductAccesor;
import info.magnolia.module.shop.beans.ShoppingCart;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templatingkit.STKModule;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.module.templatingkit.navigation.LinkImpl;
import info.magnolia.module.templatingkit.templates.components.AbstractItemListModel;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shop paragraph model, used on productdetail and productlist paragraphs.
 *
 * @author tmiyar
 *
 */
public class ShopParagraphModel extends AbstractItemListModel<TemplateDefinition> {


    private static Logger log = LoggerFactory
    .getLogger(ShopParagraphModel.class);
    private Node siteRoot = null;
    protected static final String[] ALLOWED_IMAGE_TYPES = new String[]{"gif", "jpg", "jpeg", "png"};

    public ShopParagraphModel(Node content, TemplateDefinition definition,
            RenderingModel<?> parent, STKTemplatingFunctions stkFunctions,
            TemplatingFunctions templatingFunctions, STKModule stkModule) {
        super(content, definition, parent, stkFunctions, templatingFunctions);
        this.siteRoot = stkFunctions.siteRoot(content);
    }

    public ShoppingCart getShoppingCart() {
        return ShopUtil.getShoppingCart();
    }

    public Node getSiteRoot() {
        return siteRoot;
    }

    /**
     * Gets product selected from the url, using selector.
     *
     */
    public Node getProduct() {

        Node product;
        try {
            String productId = SelectorUtil.getSelector(2);
            if(StringUtils.isNotEmpty(productId)){
                product = new ShopProductAccesor(productId).getNode();
                return product;
            }
        } catch (Exception e) {
            //item not found
        }

        return null;
    }

    public TemplateProductPriceBean getProductPriceBean(Node product) {
        ShopConfiguration shopConfiguration;
        try {
            shopConfiguration = new ShopAccesor(ShopUtil.getShopName()).getShopConfiguration();

            Node priceCategory = ShopUtil.getShopPriceCategory(shopConfiguration);

            Node currency = ShopUtil.getCurrencyByUUID(PropertyUtil.getString(
                    priceCategory, "currencyUUID"));
            Node tax = getTaxByUUID(PropertyUtil.getString(product,"taxCategoryUUID"));

            TemplateProductPriceBean bean = new TemplateProductPriceBean();
            bean.setFormatting(PropertyUtil.getString(currency, "formatting"));
            bean.setPrice(getProductPriceByCategory(product, priceCategory.getIdentifier()));
            bean.setCurrency(PropertyUtil.getString(currency, "title"));
            boolean taxIncluded = PropertyUtil.getBoolean(priceCategory,
                    "taxIncluded", false);
            if (taxIncluded) {
                bean.setTaxIncluded(ShopUtil.getMessages().get("tax.included"));
            } else {
                bean.setTaxIncluded(ShopUtil.getMessages().get("tax.no.included"));
            }

            bean.setTax(PropertyUtil.getString(tax, "tax"));
            return bean;
        } catch (Exception e) {
            return new TemplateProductPriceBean();
        }
    }

    public Collection<Node> getOptionSets(Node product) {
        ArrayList<Node> optionSets = null;
        try {
            optionSets = new ArrayList<Node>(templatingFunctions.children(product, "shopProductOptions"));
        } catch (RepositoryException e) {
            log.error("Cant get product options ",e);
            return null;
        }
        return ShopUtil.transformIntoI18nContentList(optionSets);
    }

    public Collection<Node> getOptions(Node option) {
        ArrayList<Node> options = null;
        try {
            options = new ArrayList<Node>(templatingFunctions.children(option,"shopProductOption"));
        } catch (RepositoryException e) {
            log.error("Cant get product options ",e);
            return null;
        }
        return ShopUtil.transformIntoI18nContentList(options);
    }

    public String getCurrencyTitle() {
        return ShopUtil.getCurrencyTitle();
    }

    public String getCurrencyFormatting() {
        return ShopUtil.getCurrencyFormatting();
    }

    public Node getTaxByUUID(String uuid) {
        try {
            return new I18nNodeWrapper(NodeUtil.getNodeByIdentifier("data", uuid));
        } catch (RepositoryException e) {
            log.error("Cant get tax category " + uuid, e);
        }
        return null;
    }

    protected Double getProductPriceByCategory(Node product,
            String priceCategoryUUID) throws ValueFormatException, RepositoryException {
        Node pricesNode = product.getNode("prices");
        if (pricesNode.hasNodes()) {
            for (NodeIterator iterator = pricesNode.getNodes(); iterator.hasNext();) {
                Node priceNode = (Node) iterator.next();
                if(!priceNode.isNodeType(MgnlNodeType.NT_METADATA)) {
                    Node price = new I18nNodeWrapper(priceNode);
                    if (PropertyUtil.getString(price, "priceCategoryUUID").equals(
                            priceCategoryUUID)) {
                        return PropertyUtil.getProperty(price, "price").getDouble();
                    }
                }
            }
        }
        return null;
    }

    /**
     * returns the link to the shopping cart page based on template
     * category-subcategory.
     */
    public String getShoppingCartLink() {
        Node shoppingCartPage;
        try {
            shoppingCartPage = ShopUtil.getContentByTemplateCategorySubCategory(
                    siteRoot, "feature", "shopping-cart");
            return new LinkImpl(shoppingCartPage, templatingFunctions).getHref();
        } catch (Exception e) {
            log.error("Cant get shopping cart page", e);
        }
        return "";
    }

    /**
     * get images folder items.
     */
    protected List<String> getKeys() {
        Node product = getProduct();
        Node dmsFolder =  stkFunctions.getReferencedContent(product,
                "imageDmsUUID", "dms");
        if (dmsFolder == null) {
            return new ArrayList<String>();
        }
        List<String> keys = new ArrayList<String>();
        try {
            dmsFolder = dmsFolder.getParent();

            Collection<Node> children = templatingFunctions.children(dmsFolder, "mgnl:contentNode");

            for (Iterator<Node> iterator = children.iterator(); iterator.hasNext();) {
                Node imageNode = iterator.next();
                if (showImage(new Document(ContentUtil.asContent(imageNode)))) {
                    keys.add(imageNode.getUUID());
                }
            }

        } catch (Exception e) {

        }
        return keys;
    }

    protected boolean showImage(Document doc){
        return ArrayUtils.contains(ALLOWED_IMAGE_TYPES, doc.getFileExtension().toLowerCase());
    }

    public String getProductDetailPageLink(Node product) {
        try {
            return ShopLinkUtil.getProductDetailPageLink(templatingFunctions, product, siteRoot);
        } catch (RepositoryException e) {
            return "";
        }
    }

    @Override
    protected void filter(List<Node> itemList) {
    }

    @Override
    protected int getMaxResults() {
        try {
            if (content.hasProperty("maxResults")) {
                return (int)content.getProperty("maxResults").getLong();
            }
            return Integer.MAX_VALUE;
        } catch (RepositoryException e) {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    protected void sort(List<Node> itemList) {
    }

    @Override
    protected List<Node> search() throws RepositoryException {

        List<Node> productList = new ArrayList<Node>();
        String productCategory = MgnlContext.getAggregationState().getMainContent().getUUID();
        if (StringUtils.isNotEmpty(productCategory)) {
            productList = ShopProductAccesor.getProductsByProductCategory(productCategory);
        }

        return productList;
    }

}
