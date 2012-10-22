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
import info.magnolia.module.shop.accessors.ShopProductAccesor;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.module.templatingkit.templates.AbstractSTKTemplateModel;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;

import java.util.List;

import javax.jcr.Node;

import org.apache.commons.lang.StringUtils;

/**
 * Displays the tags assigned to productcategories by using categorization module.
 * Only for enterprise users.
 * @author tmiyar
 * @param <RD>
 *
 */
public class ShopTagCloudParagraph<RD extends TemplateDefinition> extends AbstractSTKTemplateModel<TemplateDefinition> {

    private Node siteRoot = null;
    
    public ShopTagCloudParagraph(Node content, TemplateDefinition definition,
            RenderingModel<?> parent, STKTemplatingFunctions stkFunctions,
            TemplatingFunctions templatingFunctions) {
        super(content, definition, parent, stkFunctions, templatingFunctions);
        siteRoot = stkFunctions.siteRoot(content);
    }

    public List<Node> getTagCloud() {
      
      List<Node> contentList = (List<Node>) templatingFunctions.search("data", "select * from category", "JCR_SQL2", "");
      if (contentList != null) {
          return (List<Node>) ShopUtil.transformIntoI18nContentList(contentList);
      }
      return null;

    }
    
    public int getNumberOfItemsCategorizedWith(String categoryUUID) {
      return ShopProductAccesor.getProductsByProductCategory(categoryUUID).size();
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
