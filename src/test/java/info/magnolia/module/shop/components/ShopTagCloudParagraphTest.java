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
package info.magnolia.module.shop.components;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.shop.paragraphs.ShopTagCloudParagraph;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.MgnlTestCase;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockNode;
import info.magnolia.test.mock.jcr.MockSession;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ShopTagCloudParagraph.
 */
public class ShopTagCloudParagraphTest extends MgnlTestCase {

    private MockNode rootNode;
    private final STKTemplatingFunctions stkFunctions = mock(STKTemplatingFunctions.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockWebContext ctx = new MockWebContext();
        Session session = new MockSession("category");
        ctx.addSession("category", session);
        MgnlContext.setInstance(ctx);
        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());

        rootNode = (MockNode) session.getRootNode();
        when(stkFunctions.siteRoot((Node)anyObject())).thenReturn(rootNode);
    }
    @Test
    public void getProductTest() throws RepositoryException {
        //GIVEN
        final String itemType = "mgnl:category";
        rootNode.addNode("item1", itemType);
        rootNode.addNode("item2", "someOtherItemType");
        rootNode.addNode("item3", itemType);

        ShopTagCloudParagraph paragraph = new ShopTagCloudParagraph(null, null, null, stkFunctions, null);
        //WHEN
        List<Node> list = paragraph.getTagCloud();
        //THEN
        assertEquals(2, list.size());
    }
}
