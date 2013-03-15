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
package info.magnolia.module.shop.templates;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.context.MgnlContext;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockSession;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ShopSingletonParagraphTemplateModel.
 */
public class ShopSingletonParagraphTemplateModelTest extends RepositoryTestCase {

    private final DefaultI18nContentSupport i18n = mock(DefaultI18nContentSupport.class);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ComponentsTestUtil.setInstance(I18nContentSupport.class, i18n);

        MockWebContext ctx = new MockWebContext();
        Session session = new MockSession("data");
        ctx.addSession("data", session);
        Node product = session.getRootNode().addNode("product");

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("option_", product.getIdentifier());
        ctx.setParameters(parameters);
        MgnlContext.setInstance(ctx);
    }

    @Test
    public void addToCartTest() throws RepositoryException {
        //GIVEN
        ShopSingletonParagraphTemplateModel model = new ShopSingletonParagraphTemplateModel(null, null, null, null, null);
        //WHEN
        model.addToCart();
        //THEN
        verify(i18n, times(2)).hasProperty((Node)anyObject(), eq("title"));
    }
}