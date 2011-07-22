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

import java.text.MessageFormat;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.core.search.QueryResult;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.shop.util.CustomDataUtil;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;
import info.magnolia.module.templatingkit.search.SearchResultModel;
import info.magnolia.module.templatingkit.templates.STKTemplateModel;

/**
 * Idea was to really extend very few methods from the parent class, but repository and query content result cannot
 * be extended.
 * @author tmiyar
 *
 */
public class ProductSearchResultModel extends SearchResultModel {
    
    private static final Logger log = LoggerFactory.getLogger(ProductSearchResultModel.class);
    private static final String SEARCH_QUERY_PATTERN = "select * from product where jcr:path like ''{0}/%'' and contains(*, ''{1}'') order by jcr:path";
    
    protected String repository = "data";
    private Content siteRoot = null;
    
    public ProductSearchResultModel(Content content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);
        if(parent instanceof STKTemplateModel) {
            this.siteRoot = ((STKTemplateModel) parent).getSiteRoot();
            
        }
    }
    
    protected String generateSimpleQuery(String input) {
        //escape single quote
        String searchString = input.replace("'", "''");
        return MessageFormat.format(SEARCH_QUERY_PATTERN, new String[]{this.getPath(), searchString});
    }

    public String getPath() {
        try {
            return CustomDataUtil.getShopNode(ShopUtil.getShopName()).getHandle();
        } catch (Exception e) {
            return "";
        }
        
    }
    
    public String getQueryStr() {
        return MgnlContext.getParameter("queryProductsStr");
    }
    
    public String getPageLink(int i) {
        String link = "";
        try {
            link = ShopLinkUtil.getProductListSearchLink(siteRoot);
            String current = "&amp;currentPage=";
            link =  link + "?queryProductsStr=" + getQueryStr() + current + i;
        } catch (Exception e) {
            log.error("could not find search result page");
        }
        return link;
    }
    
    public Collection<Content> getResult() {
        this.execute();
        return ShopUtil.transformIntoI18nContentList(this.getQueryResult());
        
    }
    
    public String execute() {

        Query q = null;
        String queryString = generateSimpleQuery(this.getQueryStr());
        if (StringUtils.isBlank(queryString)){
            return null;
        }
        try {
            maxResultsPerPage = getMaxResultsPerPage();
            q = MgnlContext.getQueryManager(repository).createQuery(queryString, "sql");

            QueryResult queryResult = q.execute();

            count = pagedQuery(queryResult.getContent("product"), getOffset(), maxResultsPerPage);

            numPages = count/maxResultsPerPage;
            if((count % maxResultsPerPage) > 0 ) {
                numPages++;
            }

        }
        catch (Exception e) {
            log.error(MessageFormat.format(
                "{0} caught while parsing query for search term [{1}] - query is [{2}]: {3}", //$NON-NLS-1$
                new Object[]{e.getClass().getName(), q, queryString, e.getMessage()}), e);
        }

        return "";

    }

}
