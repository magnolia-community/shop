/**
 * This file Copyright (c) 2010-2013 Magnolia International
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import info.magnolia.cms.core.AggregationState;
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.module.templatingkit.templates.pages.STKPage;
import info.magnolia.registry.RegistrationException;
import info.magnolia.templating.functions.TemplatingFunctions;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockNode;
import info.magnolia.test.mock.jcr.MockSession;
import info.magnolia.test.mock.jcr.NodeTestUtil;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case.
 * @author tmiyar
 *
 */
public class ShopParagraphModelTest {

    private MockNode root;
    private String propertiesStr = "product.properties";

    @Before
    public void setUp() throws RepositoryException, Content2BeanException, IOException, RegistrationException {
        MockWebContext ctx = new MockWebContext();
        Node mainContent = mock(Node.class);
        when(mainContent.getIdentifier()).thenReturn("123");
        ctx.getAggregationState().setMainContentNode(mainContent);
        ComponentsTestUtil.setInstance(MockWebContext.class, ctx);
        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());
        MgnlContext.setInstance(ctx);
        MockSession session = new MockSession(ShopRepositoryConstants.SHOP_PRODUCTS);
        ctx.addSession(ShopRepositoryConstants.SHOP_PRODUCTS, session);
        root = (MockNode) session.getRootNode();

        NodeTestUtil.createSubnodes(root, getClass().getResourceAsStream(propertiesStr));

    }
    @Test
    public void getProductPriceByCategoryTest() throws RepositoryException {
        //GIVEN
        Node productNode = NodeUtil.getNodeByIdentifier(ShopRepositoryConstants.SHOP_PRODUCTS, "productid");
        //WHEN
        ShopParagraphModel model = new ShopParagraphModel(productNode, new STKPage(), null, mock(STKTemplatingFunctions.class), mock(TemplatingFunctions.class), null);
        //THEN
        assertNotNull(model.getProductPriceByCategory(productNode, "pricecat2"));
    }

    @Test
    public void getProductPriceByCategoryNullTest() throws RepositoryException {
        //GIVEN
        Node productNode = NodeUtil.getNodeByIdentifier(ShopRepositoryConstants.SHOP_PRODUCTS, "productid");
        //WHEN
        ShopParagraphModel model = new ShopParagraphModel(productNode, new STKPage(), null, mock(STKTemplatingFunctions.class), mock(TemplatingFunctions.class), null);
        //THEN
        assertNull(model.getProductPriceByCategory(productNode, "xxx"));
    }

    @Test
    public void testGetItemsWillNotFailOnNPE() throws RepositoryException {
        // GIVEN
        final Node content = mock(Node.class);
        final STKPage definition = new STKPage();
        final TemplatingFunctions templatingFunctions = mock(TemplatingFunctions.class);
        final STKTemplatingFunctions stkFunctions = new STKTemplatingFunctions(templatingFunctions, null, null, null, null, null, null);

        when(templatingFunctions.page(content)).thenReturn(content);

        final ShopParagraphModel model = new ShopParagraphModel(content, definition, null, stkFunctions, templatingFunctions, null);

        AggregationState state = MgnlContext.getAggregationState();
        System.out.println("Atate:" + state);
        System.out.println("CN: " + state.getMainContentNode());
        // WHEN
        model.getItems();

        // THEN
        // no exception

    }

    @After
    public void tearDown() throws Exception {
        ComponentsTestUtil.clear();
        MgnlContext.setInstance(null);
    }


}
