/**
 * This file Copyright (c) 2003-2011 Magnolia International
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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;
import info.magnolia.module.templatingkit.paragraphs.InternalTeaserModel;
import info.magnolia.module.templatingkit.templates.STKTemplateModel;
import info.magnolia.module.templatingkit.util.STKUtil;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Teaser to a single product.
 * @author will
 */
public class ProductTeaserModel extends InternalTeaserModel {

    private static Logger log = LoggerFactory.getLogger(ProductTeaserModel.class);
    private Content siteRoot = null;

    public ProductTeaserModel(Content content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);
        RenderingModel currParent = parent;
        while (!(currParent instanceof STKTemplateModel)) {
            currParent = currParent.getParent();
        }
        siteRoot = ((STKTemplateModel) currParent).getSiteRoot();
    }

    @Override
    public Content getTarget() {
        Content shopRoot = null;
        try {
            shopRoot = STKUtil.getContentByTemplateCategorySubCategory(siteRoot, "feature", "productDetail");
        } catch (RepositoryException ex) {
            log.error("Could not get shopHome page", ex);
        }
        if (shopRoot != null) {
            return STKUtil.wrap(shopRoot);
        } else {
            return null;

        }
    }

    public Content getProduct() {
        String productUUID = NodeDataUtil.getString(content, "productUUID");
        if (StringUtils.isNotBlank(productUUID)) {
            return new I18nContentWrapper(ContentUtil.getContentByUUID("data", productUUID));
        }
        return null;
    }

    public String getProductDetailPageLink() throws RepositoryException {
        String categoryUUID = NodeDataUtil.getString(content, "productCategoryUUID");
        Content product = this.getProduct();
        if (product != null) {
            Content detailPage = STKUtil.getContentByTemplateCategorySubCategory(
                    siteRoot, "feature", "productDetail");

            String selector = ShopLinkUtil.createProductSelector(product);

            if (StringUtils.isNotEmpty(categoryUUID)) {
                Content category = new I18nContentWrapper(ContentUtil.getContentByUUID("data", categoryUUID));
                selector = ShopLinkUtil.createProductAndProductCategorySelector(product, category);
            }
            return ShopLinkUtil.createLinkFromContentWithSelectors(detailPage, selector);
        }
        return "";
    }
}
