/**
 * This file Copyright (c) 2010-2013 Magnolia International
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

import info.magnolia.dam.templating.functions.DamTemplatingFunctions;
import info.magnolia.jcr.util.ContentMap;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.module.templatingkit.style.CssSelectorBuilder;
import info.magnolia.module.templatingkit.templates.components.InternalTeaserModel;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Teaser to a single product.
 * @author will
 */
public class ProductTeaserModel extends InternalTeaserModel<TemplateDefinition> {

    private static Logger log = LoggerFactory.getLogger(ProductTeaserModel.class);
    private Node siteRoot = null;

    public ProductTeaserModel(Node content, TemplateDefinition definition, RenderingModel<?> parent, STKTemplatingFunctions stkFunctions, CssSelectorBuilder cssSelectorBuilder, TemplatingFunctions templatingFunctions, DamTemplatingFunctions damTemplatingFunctions) {
        super(content, definition, parent, stkFunctions, cssSelectorBuilder, templatingFunctions, damTemplatingFunctions);
        siteRoot = stkFunctions.siteRoot(content);
    }


    @Override
    public ContentMap getTarget() {

        Node shopRoot = ShopUtil.getContentByTemplateCategorySubCategory(siteRoot, "feature", "product-detail");

        if (shopRoot != null) {
            shopRoot = ShopUtil.wrapWithHTML(ShopUtil.wrapWithI18n(shopRoot), true);
            return templatingFunctions.asContentMap(shopRoot);
        } else {
            return null;

        }
    }

    public Node getProduct() {
        String productUUID = PropertyUtil.getString(content, "productUUID");

        Node product = null;
        if (StringUtils.isNotBlank(productUUID)) {
            try {
                product = NodeUtil.getNodeByIdentifier(ShopRepositoryConstants.SHOP_PRODUCTS, productUUID);
                return ShopUtil.wrapWithI18n(product);
            } catch (RepositoryException e) {
                log.error("Can't find Product with UUID "+productUUID);
            }
        }
        return null;
    }

    public String getProductDetailPageLink() throws RepositoryException {
        Node product = this.getProduct();
        if (product != null) {
            return ShopLinkUtil.getProductDetailPageLink(templatingFunctions, product, siteRoot);
        }
        return "";
    }
}
