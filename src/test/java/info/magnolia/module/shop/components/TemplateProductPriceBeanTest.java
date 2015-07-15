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
package info.magnolia.module.shop.components;

import static org.junit.Assert.assertEquals;

import info.magnolia.context.MgnlContext;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.mock.MockContext;
import info.magnolia.test.mock.MockWebContext;

import java.util.Locale;

import org.junit.After;
import org.junit.Test;

/**
 * Test case.
 * @author tmiyar
 *
 */
public class TemplateProductPriceBeanTest {

    @Test
    public void getPricesWebContextNullTest() {
        //GIVEN
        MockContext ctx = new MockContext();
        ComponentsTestUtil.setInstance(MockContext.class, ctx);
        MgnlContext.setInstance(ctx);
        MgnlContext.setLocale(new Locale("en"));
        TemplateProductPriceBean priceBean = new TemplateProductPriceBean();
        priceBean.setPrice(99);
        priceBean.setFormatting("#,##0.00");
        //WHEN
        String price = priceBean.getPrice();
        //THEN
        assertEquals("Must return a price even if no webcontext present", "99.00", price );
    }

    @Test
    public void getPricesWebContextNotNullTest() {
        //GIVEN
        MockWebContext ctx = new MockWebContext();
        ComponentsTestUtil.setInstance(MockWebContext.class, ctx);
        MgnlContext.setInstance(ctx);
        MgnlContext.setLocale(new Locale("en"));
        TemplateProductPriceBean priceBean = new TemplateProductPriceBean();
        priceBean.setPrice(99);
        priceBean.setFormatting("#,##0.00");
        //WHEN
        String price = priceBean.getPrice();
        //THEN
        assertEquals("Must return a price even if no webcontext present", "99.00", price );
    }
    @After
    public void tearDown() throws Exception {
        ComponentsTestUtil.clear();
        MgnlContext.setInstance(null);
    }
}
