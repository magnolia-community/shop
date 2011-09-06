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
package info.magnolia.module.shop.search;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.util.SelectorUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.shop.accessors.ShopProductAccesor;
import info.magnolia.module.shop.util.CustomDataUtil;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.shop.util.ShopLinkUtil.ParamType;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Product list when selecting a product category.
 * @author tmiyar
 *
 */
public class ProductListTypeCategory extends AbstractProductListType {

    public ProductListTypeCategory(Content siteRoot, Content content) {
        super(siteRoot, content);
    }

    @Override
    protected String getPagerLink() {
        Content currentPage = MgnlContext.getAggregationState().getMainContent();
        return ShopLinkUtil.createLinkFromContentWithSelectors(currentPage, SelectorUtil.getSelector());
    }

    @Override
    public List<Content> getResult() {
        List<Content> productList = new ArrayList<Content>();
        String productCategory = ShopLinkUtil.getSelectedCategoryUUID();
        if (StringUtils.isNotEmpty(productCategory)) {
          productList = ShopProductAccesor.getProductsByProductCategory(productCategory);
          
        } 
        return productList;
    }

    @Override
    public String getTitle() {
        String name = ShopLinkUtil.getParamValue(ParamType.CATEGORY);
        try {
            Content category = new I18nContentWrapper(CustomDataUtil.getProductCategoryNode(name));
            return ShopUtil.getMessages().getWithDefault("productList.productCategory", new Object[]{category.getTitle()}, "");
        } catch (Exception e) {
            
        };
        return "";
    }

}
