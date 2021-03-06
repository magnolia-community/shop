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
package info.magnolia.module.shop.accessors;

import info.magnolia.cms.util.QueryUtil;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.module.shop.util.ShopUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * shop product.
 *
 * @author tmiyar
 */
public class ShopProductAccessor extends DefaultCustomDataAccessor {

    private static Logger log = LoggerFactory.getLogger(ShopProductAccessor.class);
    public static String SHOP_PRODUCTS_FOLDER = "shopProducts";

    public ShopProductAccessor(String name) throws Exception {
        super(name);
    }

    @Override
    protected Node getNode(String name) {
        String path = ShopUtil.getPath(ShopUtil.getShopName());
        return super.getNodeByName(path, "shopProduct", name);
    }

    public static List<Node> getProductsByProductCategory(String productCategory) {

        String shopName = ShopUtil.getShopName();

        if (StringUtils.isNotEmpty(shopName)) {
            //use old JCR SQL to preserver natural order of elements
            String query = "select * from mgnl:contentNode  where jcr:path like '" + ShopUtil.getPath(shopName)
                    + "/%' and contains(*, '" + productCategory + "')";

            return getProductsBySQL(query);

        }

        return null;
    }

    public static List<Node> getTaggedProducts(String tagUUID) {

        String shopName = ShopUtil.getShopName();

        if (StringUtils.isNotEmpty(shopName)) {
            //use old JCR SQL to preserver natural order of elements
            String query = "select * from mgnl:contentNode  where jcr:path like '" + ShopUtil.getPath(shopName)
                    + "/%' and contains(*, '" + tagUUID + "')";

            return getProductsBySQL(query);

        }

        return null;

    }

    public static List<Node> getProductsByFulltext(String queryStr) {

        String shopName = ShopUtil.getShopName();

        if (StringUtils.isNotEmpty(shopName)) {
            //use old JCR SQL to preserver natural order of elements
            String query = "select * from shopProduct where jcr:path like '/" + shopName
                    + "/%' and contains(*, '" + escapeSql(queryStr) + "')";

            return getProductsBySQL(query);

        }

        return null;

    }

    public static List<Node> getProductsBySQL(String query) {

        Collection<Node> productCollection = null;

        NodeIterator test = null;
        try {
            test = QueryUtil.search(ShopRepositoryConstants.SHOP_PRODUCTS, query, javax.jcr.query.Query.SQL, "shopProduct");
        } catch (NullPointerException e) {
            if (e.getStackTrace()[0].getClassName().equals("org.apache.jackrabbit.core.query.lucene.DescendantSelfAxisQuery$DescendantSelfAxisScorer")) {
                // ignore - lucene bug - https://issues.apache.org/jira/browse/JCR-3407
                return new ArrayList<Node>();
            } else {
                throw e;
            }
        } catch (InvalidQueryException e) {
            log.error("Invalid query", e);
        } catch (RepositoryException e) {

            if (e.getCause() != null && ParseException.class.equals(e.getCause().getClass())) {
                log.debug("Invalid querry '{}'.", query, e);
                return new ArrayList<Node>();
            }
            log.error("Cant read Products", e);
            return new ArrayList<Node>();
        }

        productCollection = IteratorUtils.toList(test);
        return (List<Node>) ShopUtil.transformIntoI18nContentList(productCollection);
    }

    public static String escapeSql(final String input) {
        String output = StringEscapeUtils.escapeSql(input); // escape '
        output = output.replace("\\", "\\\\"); // escape \ first!
        output = output.replace("-", "\\-").replace("\"", "\\\""); // escape the rest of the SQL characters (-,")
        return output;
    }

}
