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

import java.util.List;

import javax.jcr.Node;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.module.templatingkit.templates.AbstractSTKTemplateModel;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.context.MgnlContext;

/**
 * Displays the tags assigned to productcategories by using categorization module.
 * Only for enterprise users.
 * @author tmiyar
 * @param <RD>
 *
 */
public class ShopTagCloudParagraph<RD extends TemplateDefinition> extends AbstractSTKTemplateModel<TemplateDefinition> {

    private static final Logger log = LoggerFactory.getLogger(ShopTagCloudParagraph.class);

    private Content siteRoot = null;
    
    public ShopTagCloudParagraph(Node content, TemplateDefinition definition,
            RenderingModel<?> parent, STKTemplatingFunctions stkFunctions,
            TemplatingFunctions templatingFunctions) {
        super(content, definition, parent, stkFunctions, templatingFunctions);
        siteRoot = ContentUtil.asContent(stkFunctions.siteRoot(content));
    }

    public List<Content> getTagCloud() {
      
      List<Content> contentList = (List<Content>) QueryUtil.query("data", "select * from category");
      if (contentList != null) {
          return ShopUtil.transformIntoI18nContentList(contentList);
      }
      return null;

    }
    
    public int getNumberOfItemsCategorizedWith(String categoryUUID) {
      return ShopUtil.findTaggedProducts(categoryUUID).size();
    }
    
    public String getProductListLink(String tagName, String tagDisplayName) {
        String link = "";
        String productKeywordResultPage = ShopLinkUtil.getProductKeywordLink(templatingFunctions, siteRoot);
        String replacement = "~" + tagName;
        if(StringUtils.isNotEmpty(tagDisplayName)) {
            replacement += "~" + tagDisplayName ; 
        }
        replacement += "~";
        String extension = "." + MgnlContext.getAggregationState().getExtension();
        replacement += extension;
        if(productKeywordResultPage != null) {
            link = productKeywordResultPage.replace(extension, replacement);
        }
        
        return link;
    
    }

    
}
