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

import static org.junit.Assert.assertEquals;

import info.magnolia.context.MgnlContext;
import info.magnolia.importexport.DataTransporter;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.test.RepositoryTestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class.
 */
public class ShopProductAccesorRepositoryTest extends RepositoryTestCase {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        MgnlContext.setAttribute(ShopUtil.ATTRIBUTE_SHOPNAME, "sampleShop");

        Session session = MgnlContext.getJCRSession("data");
        NodeTypeManagerImpl ntTypeMgr = (NodeTypeManagerImpl) session.getWorkspace().getNodeTypeManager();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("mgnl-nodetypes/test-magnoliaAnddata-nodetypes.xml");
        ntTypeMgr.registerNodeTypes(inputStream, "text/xml", true);

        inputStream = getClass().getClassLoader().getResourceAsStream("mgnl-nodetypes/test-shop-nodetypes.xml");
        ntTypeMgr.registerNodeTypes(inputStream, "text/xml", true);

        File[] files = new File(getClass().getClassLoader().getResource("mgnl-bootstrap/shop/data-types").getPath()).listFiles();
        for (File file : files) {
            DataTransporter.importXmlStream(new FileInputStream(file), "data", "/", "", false, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW, true, true);
        }

        files = new File(getClass().getClassLoader().getResource("mgnl-bootstrap-samples/shop/").getPath()).listFiles();
        for (File file : files) {
            String path = StringUtils.replace(file.getName(), ".xml", "");
            path = StringUtils.replace(path, ".", "/");
            String repository = StringUtils.substringBefore(path, "/");
            path = StringUtils.substringAfter(path, repository);
            DataTransporter.importXmlStream(new FileInputStream(file), repository, path, "", false, ImportUUIDBehavior.IMPORT_UUID_COLLISION_THROW, true, true);
        }
    }

    @Test
    public void testNodesAreInNaturalOrder() throws Exception {
        // GIVEN
        String[] correctOrder = new String[] { "p101", "p102", "p103", "p104", "p1021", "p1022", "p1023", "p1031", "p1041", "p1042", "p4011" };

        // WHEN
        List<Node> books = ShopProductAccesor.getProductsByProductCategory("003de673-34a6-4b71-bab1-237f8b85b78a");

        // THEN
        for (int i = 0; i < correctOrder.length; i++) {
            assertEquals(correctOrder[i], books.get(i).getName());
        }
    }
}
