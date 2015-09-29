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
package info.magnolia.shop.util;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.templating.functions.TemplatingFunctions;

import javax.jcr.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a link to the product detail page for a selected product.
 * @author will
 *
 */
public class ShopProductLinkImpl { // TODO: Fix 4 MTE - was: implements Link {

    private static Logger log = LoggerFactory.getLogger(ShopProductLinkImpl.class);
    private Node node;
    private TemplatingFunctions functions;

    public ShopProductLinkImpl(TemplatingFunctions functions, Node node) {
        this.node = node;
        this.functions = functions;
    }

    public String getTitle() {
        return PropertyUtil.getString(node, "title", PropertyUtil.getString(node, "name"));
    }

    public String getNavigationTitle() {
        return PropertyUtil.getString(node, "navigationTitle", getTitle());
    }

    public String getHref() {
        Node currentPage = MgnlContext.getAggregationState().getMainContentNode();
        Node siteRoot = null;
        // TODO: Fix 4 MTE
//        try {
//            siteRoot = TemplateCategoryUtil.findParentWithTemplateCategory(currentPage, TemplateCategory.HOME);
//            if (siteRoot == null) {
//                siteRoot = (Node) currentPage.getAncestor(0);
//            }
//        } catch (RepositoryException ex) {
//            log.error("Could not get site root", ex);
//        }
//
//        try {
//            return ShopLinkUtil.getProductDetailPageLink(functions, node, MgnlContext.getAggregationState().getMainContentNode(), null);
//        } catch (RepositoryException ex) {
//            log.error("Failed to create product detail page link.", ex);
//            return "";
//        }
        return "#";
    }
}
