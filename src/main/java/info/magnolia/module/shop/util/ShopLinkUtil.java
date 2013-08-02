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
package info.magnolia.module.shop.util;

import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.cms.core.Path;
import info.magnolia.module.templatingkit.templates.category.TemplateCategoryUtil;
import info.magnolia.templating.functions.TemplatingFunctions;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util class for creating shop links.
 * @author tmiyar
 *
 */
public class ShopLinkUtil {

    private static final Logger log = LoggerFactory.getLogger(ShopLinkUtil.class);

    public static String getProductCategoryLink(TemplatingFunctions functions, Node category) {
        return functions.link(category);
    }

    public static String getProductListSearchLink(TemplatingFunctions functions, Node siteRoot) {
        String link = "";
        try {
            Node productSearchResultPage = TemplateCategoryUtil.getNearestContentByTemplateCategorySubCategory(siteRoot, "feature", "product-search-result", ShopUtil.getShopRoot());
            link = functions.link(productSearchResultPage);
        } catch (RepositoryException e) {
            log.error("Product search result link not found");
        }
        return link;
    }

    public static String getProductKeywordLink(TemplatingFunctions functions, Node siteRoot) {
        String link = "";
        try {
            Node productKeywordResultPage = TemplateCategoryUtil.getNearestContentByTemplateCategorySubCategory(siteRoot, "feature", "keyword-search-result", ShopUtil.getShopRoot());
            link = functions.link(productKeywordResultPage);
        } catch (RepositoryException e) {
            log.error("Product keyword search result link not found");
        }
        return link;
    }

    public static String createLinkFromContentWithSelectors(TemplatingFunctions functions, Node currentPage, String selector) {
        String link = functions.link(currentPage);
        String extension = StringUtils.substringAfterLast(link, ".");
        if(StringUtils.isNotEmpty(selector)) {
            selector = "~" + selector + "~";
        }
        return StringUtils.substringBeforeLast(link, extension) + selector + "." + extension;
    }

    public static String getProductDetailPageLink(TemplatingFunctions functions, Node product, Node siteRoot)
    throws RepositoryException {
        if (product != null) {
            Node detailPage = ShopUtil.getContentByTemplateCategorySubCategory(
                    siteRoot, "feature", "product-detail");

            String selector = createProductSelector(product);
            return ShopLinkUtil.createLinkFromContentWithSelectors(functions, detailPage, selector);
        }
        return "";
    }

    public static String getProductDetailPageLink(TemplatingFunctions functions, Node product, Node currentPage, Node siteRoot)
    throws RepositoryException {
        if (product != null) {
            // first check if there is a product detail page underneath the current
            // page (which should be a product category page). This will make the
            // current product category to stay highlighted in the navigation.
            Node detailPage = ShopUtil.getContentByTemplateCategorySubCategory(currentPage, "feature", "product-detail");
            if (detailPage == null) {
                // if no detail page was found search the whole site for a "generic"
                // detail page.
                detailPage = ShopUtil.getContentByTemplateCategorySubCategory(siteRoot, "feature", "product-detail");
            }

            String selector = createProductSelector(product);
            return ShopLinkUtil.createLinkFromContentWithSelectors(functions, detailPage, selector);
        }
        return "";
    }

    public static String createProductSelector(Node product) {
        return Path.getValidatedLabel(PropertyUtil.getString(product, "title")) + "~PRODUCT~" + Path.getValidatedLabel(PropertyUtil.getString(product, "name"));
    }

}
