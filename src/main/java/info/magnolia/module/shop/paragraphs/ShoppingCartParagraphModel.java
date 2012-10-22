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

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.ContentMap;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.shop.templates.ShopSingletonParagraphTemplateModel;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templatingkit.STKModule;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.module.templatingkit.navigation.LinkImpl;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shopping cart paragraph.
 * 
 * @author tmiyar
 * 
 */
public class ShoppingCartParagraphModel extends ShopParagraphModel {

    private static Logger log = LoggerFactory.getLogger(ShopSingletonParagraphTemplateModel.class);

    public ShoppingCartParagraphModel(Node content,
            TemplateDefinition definition, RenderingModel<?> parent,
            STKTemplatingFunctions stkFunctions,
            TemplatingFunctions templatingFunctions, STKModule stkModule) {
        super(content, definition, parent, stkFunctions, templatingFunctions, stkModule);
    }


    @Override
    public String execute() {

        return "";
    }

    public ContentMap getContentByIdentifier(String identifier) {
        try {
            contentMap = new ContentMap(NodeUtil.getNodeByIdentifier("data", identifier));
        } catch (RepositoryException e) {
            log.error("Can't find Option with uuid"+identifier, e);
        }
        return contentMap;
    }


    public String getCommandLink(String command, String productUUID, int index) {
        return new LinkImpl(MgnlContext.getAggregationState().getMainContent().getJCRNode() , templatingFunctions).getHref()
        + "?command=" + command + "&product=" + productUUID + "&item=" + index;
    }

    public String getCheckoutFormLink() {
        try {
            Node formPage = ShopUtil.getContentByTemplateCategorySubCategory(
                    getSiteRoot(), "feature", "checkoutform");
            return new LinkImpl(formPage, templatingFunctions).getHref();
        } catch (Exception e) {
            // TODO
        }
        return "";
    }
}
