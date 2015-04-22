/**
 * This file Copyright (c) 2015 Magnolia International
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
package info.magnolia.module.shop.setup;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.module.InstallContext;
import info.magnolia.test.RepositoryTestCase;

import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;

import org.junit.Test;

/**
 * Test class for {@link RegisterShopNodeTypeTask}.
 */
public class RegisterShopNodeTypeTaskTest extends RepositoryTestCase {

    @Test
    public void testRegisterShopNodeType() throws Exception {
        // GIVEN
        NodeTypeManager nodeTypeManager = MgnlContext.getJCRSession("shops").getWorkspace().getNodeTypeManager();

        RegisterShopNodeTypeTask registerShopNodeTypeTask = new RegisterShopNodeTypeTask("shops", "shop", true);

        InstallContext installContext = mock(InstallContext.class);
        when(installContext.getJCRSession("shops")).thenReturn(MgnlContext.getJCRSession("shops"));

        // WHEN
        registerShopNodeTypeTask.execute(installContext);

        // THEN
        NodeType nodeType = nodeTypeManager.getNodeType("shop");

        assertEquals("shop", nodeType.getName());
        assertFalse(nodeType.isMixin());
        assertTrue(nodeType.hasOrderableChildNodes());
        assertTrue(nodeType.isNodeType(NodeTypes.Content.NAME));
    }

    @Test
    public void testReregisterShopNodeType() throws Exception {
        // GIVEN
        NodeTypeManager nodeTypeManager = MgnlContext.getJCRSession("shops").getWorkspace().getNodeTypeManager();
        NodeTypeTemplate ntt = nodeTypeManager.createNodeTypeTemplate();
        ntt.setName("shop");
        ntt.setMixin(true);
        ntt.setOrderableChildNodes(false);
        ntt.setDeclaredSuperTypeNames(new String[]{NodeTypes.Folder.NAME});
        nodeTypeManager.registerNodeType(ntt, false);

        RegisterShopNodeTypeTask registerShopNodeTypeTask = new RegisterShopNodeTypeTask("shops", "shop", true);

        InstallContext installContext = mock(InstallContext.class);
        when(installContext.getJCRSession("shops")).thenReturn(MgnlContext.getJCRSession("shops"));

        // WHEN
        registerShopNodeTypeTask.execute(installContext);

        // THEN
        NodeType nodeType = nodeTypeManager.getNodeType("shop");

        assertEquals("shop", nodeType.getName());
        assertFalse(nodeType.isMixin());
        assertTrue(nodeType.hasOrderableChildNodes());
        assertTrue(nodeType.isNodeType(NodeTypes.Content.NAME));
        assertFalse(nodeType.isNodeType(NodeTypes.Folder.NAME));
    }
}
