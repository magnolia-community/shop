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
package info.magnolia.module.shop.util;

import java.util.Collection;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;

/**
 * Refactored STKPager.
 *
 */
public class ShopProductPager {

    private int count;
    private String link;
    private int maxResultsPerPage;
    private Collection items;
    private String position;
    private int numPages;

    public ShopProductPager(String link, Collection items, Content content) {
        this.count = items.size();
        this.items = items;
        this.link = link;

        getPagerProperties(content);
        numPages = calculateNumberOfPages();
    }
    
    public Collection getPageItems() {

        Collection subList = items;
        int offset = getOffset();
        if(count > 0) {
            int limit = maxResultsPerPage + offset;
            if(items.size() < limit) {
                limit = count;
            }
            subList = ((List)items).subList(offset, limit);

        }
        return subList;
    }
    
    public int getCount() {
        return count;
    }

    protected int getOffset() {
        int offset = 0;
        int currentPage = getCurrentPage();
        if(currentPage > 1) {
            offset = (currentPage -1) * maxResultsPerPage;
        }
        if(offset > count) {
            int itemsOnLastPage = offset - count;
            offset = count - itemsOnLastPage;
        }
        return offset;
    }

    public int getCurrentPage() {
        int currentPage = 1;
        if(MgnlContext.getParameter("currentPage") != null && (Integer.parseInt(MgnlContext.getParameter("currentPage")) > 1)) {
            currentPage = Integer.parseInt(MgnlContext.getParameter("currentPage"));
        }

        return currentPage;
    }

    public String getPageLink(int i) {
        String current = "currentPage=";
        if (this.link.indexOf('?') > 0) {
            final String query = StringUtils.substringAfter(this.link, "?");
            StringBuilder newQuery = new StringBuilder("?");
            String[] params = query.split("&");
            boolean pageSet = false;
            int cnt = 0;
            for (String param : params) {
                if (param.startsWith(current)) {
                    newQuery.append(current).append(i);
                    pageSet = true;
                } else {
                    newQuery.append(param);
                }
                cnt++;
                if (cnt < params.length) {
                    newQuery.append("&");
                }
            }
            if (!pageSet) {
                if (newQuery.length() > 1) {
                    newQuery.append("&");
                }
                newQuery.append(current).append(i);

            }
            return StringUtils.substringBefore(this.link, "?") + newQuery.toString();
        } else {
            String link =  this.link + "?" + current + i;
            return link;
        }
    }

    public int getNumPages() {
        return numPages;
    }

    public int getBeginIndex() {
        if (getCurrentPage() - 2 <= 1) {
            return 1;
        } else {
            return getCurrentPage() - 2;
        }
    }

    public int getEndIndex() {
        if (getCurrentPage() + 2 >= getNumPages()) {
            return getNumPages();
        } else {
            return getCurrentPage() + 2;
        }
    }

    public String getPosition() {
        return position;
    }

    protected void getPagerProperties(Content content) {

        maxResultsPerPage = 10000;
        try {
            if(content.hasNodeData("maxResultsPerPage")){
                int max = (int) content.getNodeData("maxResultsPerPage").getLong();
                if(max > 0) {
                    maxResultsPerPage = max;
                }
            }

            position = "top";
            if( content.hasNodeData("position") ){
                position = content.getNodeData("position").getString();
            }

        } catch (RepositoryException e) {
            //use defaults
        }
    }

    protected int calculateNumberOfPages() {
        int numPages = count/maxResultsPerPage;
        if((count % maxResultsPerPage) > 0 ) {
            numPages++;
        }
        return numPages;
    }

    
}


