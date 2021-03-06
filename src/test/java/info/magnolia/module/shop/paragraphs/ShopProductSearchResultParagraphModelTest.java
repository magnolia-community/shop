/**
 * This file Copyright (c) 2014-2015 Magnolia International
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

import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;

import info.magnolia.context.MgnlContext;
import info.magnolia.module.templatingkit.STKModule;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;
import info.magnolia.test.mock.MockWebContext;

import java.util.Collections;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link ShopProductSearchResultParagraphModel}.
 */
public class ShopProductSearchResultParagraphModelTest {

    private ShopProductSearchResultParagraphModel model;
    private MockWebContext context;
    private Node content;
    private TemplateDefinition templateDefinition;
    private RenderingModel<?> renderingModel;
    private STKTemplatingFunctions stkTemplatingFunctions;
    private TemplatingFunctions templatingFunctions;
    private STKModule stkModule;

    @Before
    public void setUp() {
        context = new MockWebContext();
        context.setParameters(Collections.EMPTY_MAP);
        MgnlContext.setInstance(context);

        content = mock(Node.class);
        templateDefinition = mock(TemplateDefinition.class);
        renderingModel = mock(RenderingModel.class);
        stkTemplatingFunctions = mock(STKTemplatingFunctions.class);
        templatingFunctions = mock(TemplatingFunctions.class);
        stkModule = new STKModule();

        model = new ShopProductSearchResultParagraphModel(content, templateDefinition, renderingModel, stkTemplatingFunctions, templatingFunctions, stkModule);
    }

    @After
    public void tearDown() {
        MgnlContext.setInstance(null);
    }

    @Test
    public void testReturnNullOnEmptyQuerySearchString() throws RepositoryException {
        // GIVEN

        // WHEN
        List<Node> result = model.search();

        // THEN
        assertNull(result);
        // no exception
    }

}
