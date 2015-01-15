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
package info.magnolia.module.shop.util;

import static org.junit.Assert.*;

import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.wrapper.HTMLEscapingNodeWrapper;
import info.magnolia.jcr.wrapper.I18nNodeWrapper;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.jcr.MockNode;

import java.util.ArrayList;
import java.util.Collection;

import javax.jcr.Node;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ShopUtil.
 */
public class ShopUtilTest extends RepositoryTestCase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());
    }

    @Test
    public void wrapWithI18nTest() {
        //GIVEN
        Node node = null;
        //WHEN
        Node wrapped = ShopUtil.wrapWithI18n(node);
        //THEN
        assertNull(wrapped);

        //GIVEN
        node = new MockNode("notWrapped");
        //WHEN
        wrapped = ShopUtil.wrapWithI18n(node);
        //THEN
        assertTrue(wrapped instanceof I18nNodeWrapper);

        //GIVEN
        node = new HTMLEscapingNodeWrapper(new I18nNodeWrapper(new MockNode("alreadyWrapped")), false);
        //WHEN
        wrapped = ShopUtil.wrapWithI18n(node);
        //THEN
        assertTrue(wrapped instanceof HTMLEscapingNodeWrapper);
        assertTrue(NodeUtil.isWrappedWith(wrapped, I18nNodeWrapper.class));
    }

    @Test
    public void wrapWithHTMLTest() {
        //GIVEN
        Node node = null;
        //WHEN
        Node wrapped = ShopUtil.wrapWithHTML(node, false);
        //THEN
        assertNull(wrapped);

        //GIVEN
        node = new I18nNodeWrapper(new MockNode("notWrapped"));
        //WHEN
        wrapped = ShopUtil.wrapWithHTML(node, true);
        //THEN
        assertTrue(wrapped instanceof HTMLEscapingNodeWrapper);

        //GIVEN
        node = new HTMLEscapingNodeWrapper(new I18nNodeWrapper(new MockNode("alreadyWrapped")), false);
        //WHEN
        wrapped = ShopUtil.wrapWithHTML(node, false);
        //THEN
        assertTrue(wrapped instanceof HTMLEscapingNodeWrapper);
        assertTrue(NodeUtil.isWrappedWith(wrapped, I18nNodeWrapper.class));
    }

    @Test
    public void transformIntoI18nContentList() {
        //GIVEN
        Node inputNode = new MockNode("notWrapped");
        Collection<Node> contentList = new ArrayList<Node>();
        contentList.add(inputNode);
        //WHEN
        Collection<Node> collection = ShopUtil.transformIntoI18nContentList(contentList);
        //THEN
        for (Node node: collection) {
            assertTrue(node instanceof I18nNodeWrapper);
        }
    }
}
