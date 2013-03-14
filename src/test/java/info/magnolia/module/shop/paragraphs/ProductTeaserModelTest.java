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
package info.magnolia.module.shop.paragraphs;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.wrapper.I18nNodeWrapper;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.module.templatingkit.templates.pages.STKPage;
import info.magnolia.registry.RegistrationException;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockNode;
import info.magnolia.test.mock.jcr.MockSession;
import info.magnolia.test.mock.jcr.NodeTestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ProductTeaserModel.
 */
public class ProductTeaserModelTest {

    private MockNode product;
    private final String propertiesStr = "product.properties";
    private MockSession session;
    private final STKTemplatingFunctions stkFunctions = mock(STKTemplatingFunctions.class);

    @Before
    public void setUp() throws RepositoryException, Content2BeanException, IOException, RegistrationException {

        MockWebContext ctx = new MockWebContext();
        TemplateDefinitionRegistry registry = mock(TemplateDefinitionRegistry.class);
        Collection definitions = new ArrayList();
        STKPage page = new STKPage();
        page.setCategory("feature");
        page.setSubcategory("product-detail");
        definitions.add(page);

        ComponentsTestUtil.setInstance(MockWebContext.class, ctx);
        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());
        ComponentsTestUtil.setInstance(TemplateDefinitionRegistry.class, registry);
        MgnlContext.setInstance(ctx);
        session = new MockSession("data");
        ctx.addSession("data", session);
        product = (MockNode) session.getRootNode();

        NodeTestUtil.createSubnodes(product, getClass().getResourceAsStream(propertiesStr));

        when(stkFunctions.siteRoot((Node)anyObject())).thenReturn(product);
        when(registry.getTemplateDefinitions()).thenReturn(definitions);

    }
    @Test
    public void getProductTest() throws RepositoryException {
        //GIVEN
        Node productNode = NodeUtil.getNodeByIdentifier("data", "productid");
        productNode.setProperty("productUUID", product.getIdentifier());
        ProductTeaserModel model = new ProductTeaserModel(productNode, null, null, stkFunctions, null, null);
        //WHEN
        Node product = model.getProduct();
        //THEN
        assertTrue(NodeUtil.isWrappedWith(product, I18nNodeWrapper.class));
    }
}
