/**
 * This file Copyright (c) 2013-2015 Magnolia International
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.MgnlTestCase;
import info.magnolia.test.mock.jcr.MockNode;
import info.magnolia.test.mock.jcr.MockNodeIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.lucene.queryParser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for ShopProductAccessor.
 */
public class ShopProductAccessorTest extends MgnlTestCase {

    Context ctx = mock(Context.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        MgnlContext.setInstance(ctx);
        ComponentsTestUtil.setImplementation(I18nContentSupport.class, DefaultI18nContentSupport.class);
    }

    @Test
    public void getProductsBySQLRepositoryException() throws Exception {
        // GIVEN
        when(ctx.getJCRSession(anyString())).thenThrow(new RepositoryException());
        // WHEN
        List<Node> result = ShopProductAccessor.getProductsBySQL("someQueryStr");
        // THEN no exception occurs and the result is empty list
        assertNotNull(result);

    }

    @Test
    public void getProductsBySQLParseExceptionTest() throws Exception {
        // GIVEN
        when(ctx.getJCRSession(anyString())).thenThrow(new RepositoryException(new ParseException()));
        // WHEN
        List<Node> result = ShopProductAccessor.getProductsBySQL("someQueryStr");
        // THEN no exception occurs and the result is empty list
        assertNotNull(result);
    }

    @Test
    public void escapeSqlTest() throws Exception {
        // GIVEN
        final String querry = "- \\ ' \" ";
        // WHEN
        final String result = ShopProductAccessor.escapeSql(querry);
        // THEN no exception occurs and the result is empty list
        assertEquals("\\- \\\\ '' \\\" ", result);
    }

    @Test
    public void testOrderIsPreservedWhenGettingProductsBySQLQuery() throws LoginException, RepositoryException {
        // GIVEN
        final Collection<Node> nodes = new ArrayList<Node>();
        final MockNode a = new MockNode("a");
        a.setPrimaryType("shopProduct");
        final MockNode b = new MockNode("b");
        b.setPrimaryType("shopProduct");
        final MockNode c = new MockNode("c");
        c.setPrimaryType("shopProduct");

        nodes.add(a);
        nodes.add(b);
        nodes.add(c);

        final NodeIterator iter = new MockNodeIterator(nodes);

        final Session session = mock(Session.class);
        final Workspace wp = mock(Workspace.class);
        final QueryManager manager = mock(QueryManager.class);
        final Query query = mock(Query.class);
        final QueryResult queryResult = mock(QueryResult.class);

        when(ctx.getJCRSession(ShopRepositoryConstants.SHOP_PRODUCTS)).thenReturn(session);
        when(session.getWorkspace()).thenReturn(wp);
        when(wp.getQueryManager()).thenReturn(manager);
        when(manager.createQuery(anyString(), anyString())).thenReturn(query);
        when(query.execute()).thenReturn(queryResult);
        when(queryResult.getNodes()).thenReturn(iter);

        // WHEN
        List<Node> result = ShopProductAccessor.getProductsBySQL("amazing query");

        // THEN
        assertEquals("a", result.get(0).getName());
        assertEquals("b", result.get(1).getName());
        assertEquals("c", result.get(2).getName());
    }

    @Test
    public void testCreateGetProductsByProductCategoryQuery() throws LoginException, RepositoryException {
        // GIVEN
        final NodeIterator iter = new MockNodeIterator(new HashSet<Node>());
        final Session session = mock(Session.class);
        final Workspace wp = mock(Workspace.class);
        final QueryManager manager = mock(QueryManager.class);
        final Query query = mock(Query.class);
        final QueryResult queryResult = mock(QueryResult.class);

        when(ctx.getJCRSession("shopProducts")).thenReturn(session);
        when(ctx.getAttribute(ShopUtil.ATTRIBUTE_SHOPNAME)).thenReturn("sampleShop");
        when(session.getWorkspace()).thenReturn(wp);
        when(wp.getQueryManager()).thenReturn(manager);
        when(manager.createQuery(anyString(), anyString())).thenReturn(query);
        when(query.execute()).thenReturn(queryResult);
        when(queryResult.getNodes()).thenReturn(iter);

        // WHEN
        ShopProductAccessor.getTaggedProducts("tag-uuid");

        // THEN
        Mockito.verify(manager, Mockito.times(1)).createQuery(
                Mockito.contains("tag-uuid"),
                Mockito.eq(javax.jcr.query.Query.SQL));
    }
}
