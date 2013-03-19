/**
 * This file Copyright (c) 2013 Magnolia International
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

import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.test.MgnlTestCase;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.lucene.queryParser.ParseException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ShopProductAccesor.
 */
public class ShopProductAccesorTest extends MgnlTestCase {

    Context ctx = mock(Context.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        MgnlContext.setInstance(ctx);
    }

    @Test
    public void getProductsBySQLRepositoryException() throws Exception {
        // GIVEN
        when(ctx.getJCRSession(anyString())).thenThrow(new RepositoryException());
        // WHEN
        List<Node> result = ShopProductAccesor.getProductsBySQL("someQueryStr");
        // THEN no exception occurs and the result is empty list
        assertNotNull(result);

    }

    @Test
    public void getProductsBySQLParseExceptionTest() throws Exception {
        // GIVEN
        when(ctx.getJCRSession(anyString())).thenThrow(new RepositoryException(new ParseException()));
        // WHEN
        List<Node> result = ShopProductAccesor.getProductsBySQL("someQueryStr");
        // THEN no exception occurs and the result is empty list
        assertNotNull(result);
    }

    @Test
    public void escapeSqlTest() throws Exception {
        // GIVEN
        final String querry = "- \\ ' \" ";
        // WHEN
        final String result = ShopProductAccesor.escapeSql(querry);
        // THEN no exception occurs and the result is empty list
        assertEquals("\\- \\\\ '' \\\" ", result);
    }
}
