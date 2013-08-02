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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.templatingkit.navigation.Link;
import info.magnolia.module.templatingkit.templates.category.TemplateCategory;
import info.magnolia.module.templatingkit.templates.category.TemplateCategoryUtil;
import info.magnolia.templating.functions.TemplatingFunctions;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a link to the product detail page for a selected product.
 * @author will
 *
 */
public class ShopProductLinkImpl implements Link {

    private static Logger log = LoggerFactory.getLogger(ShopProductLinkImpl.class);
    private Node node;
    private TemplatingFunctions functions;

    public ShopProductLinkImpl(TemplatingFunctions functions, Node node) {
        this.node = node;
        this.functions = functions;
    }

    public String getTitle() {
        return StringUtils.defaultIfEmpty(ContentUtil.asContent(node).getTitle(), ContentUtil.asContent(node).getName());
    }

    public String getNavigationTitle() {
        String navigationTitle = ContentUtil.asContent(node).getNodeData("navigationTitle").getString();
        return StringUtils.defaultIfEmpty(StringUtils.defaultIfEmpty(navigationTitle, ContentUtil.asContent(node).getTitle()), ContentUtil.asContent(node).getName());
    }

    public String getHref() {
        Content currentPage = MgnlContext.getAggregationState().getMainContent();
        Content siteRoot = null;
        try {
            siteRoot = TemplateCategoryUtil.findParentWithTemplateCategory(currentPage, TemplateCategory.HOME);
            if (siteRoot == null) {
                siteRoot = currentPage.getAncestor(0);
            }
        } catch (RepositoryException ex) {
            log.error("Could not get site root", ex);
        }

        try {
            return ShopLinkUtil.getProductDetailPageLink(functions, node, MgnlContext.getAggregationState().getMainContent().getJCRNode(), null);
        } catch (RepositoryException ex) {
            log.error("Failed to create product detail page link.", ex);
            return "";
        }
    }
}
