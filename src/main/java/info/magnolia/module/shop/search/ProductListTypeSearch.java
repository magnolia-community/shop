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
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.shop.accessors.ShopProductAccesor;
import info.magnolia.module.shop.util.CustomDataUtil;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Search product listitng.
 * @author tmiyar
 *
 */
public class ProductListTypeSearch extends AbstractProductListType {

    private static final String SEARCH_QUERY_PATTERN = "select * from shopProduct where jcr:path like ''" 
        + ShopUtil.getPath(ShopProductAccesor.SHOP_PRODUCTS_FOLDER) + "/{0}/%'' and contains(*, ''{1}'') order by jcr:path";
    private String repository = "data";
    
    public ProductListTypeSearch(Content siteRoot, Content content) {
        super(siteRoot, content);
    }
    
    @Override
    protected String getPagerLink() {
        String link = ShopLinkUtil.getProductListSearchLink(getSiteRoot());
        link =  link + "?queryProductsStr=" + getQueryStr();
        return link;
    }

    @Override
    public List<Content> getResult() {
        List<Content> searchResult = new ArrayList<Content>();
        String queryString = generateSimpleQuery(this.getQueryStr());
        if (StringUtils.isBlank(queryString)){
            return searchResult;
        }
        
        searchResult = (List<Content>) QueryUtil.query(getRepository(), queryString );
        
        return searchResult;
    }
    
    protected String generateSimpleQuery(String searchString) {
        //escape single quote
        searchString = searchString.replace("'", "''");
        return MessageFormat.format(SEARCH_QUERY_PATTERN, new String[]{ShopUtil.getShopName(), searchString});
    }
    
    protected String getPath() {
        try {
            return CustomDataUtil.getShopNode(ShopUtil.getShopName()).getHandle();
        } catch (Exception e) {
            return "";
        }
    }

    protected String getQueryStr() {
        return MgnlContext.getParameter("queryProductsStr");
    }

    protected String getRepository() {
        return repository;
    }    

}
