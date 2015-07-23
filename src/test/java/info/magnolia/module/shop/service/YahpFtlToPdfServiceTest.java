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
package info.magnolia.module.shop.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.freemarker.FreemarkerHelper;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.beans.ShoppingCartItem;
import info.magnolia.module.shop.setup.RefactorPackageNameTask;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test class of YahpFtlToPdfService.
 */
public class YahpFtlToPdfServiceTest extends RepositoryTestCase {

    public static final String TEST_SHOP_NAME = "testShop";
    public static final String TEST_SHOPPING_CART_NUMBER = "178";

    Session shoppingCartsSession;
    Node testShoppingCartShopNode;
    Node testShoppingCartNode;
    Node cartItem_0;
    Node cartItem_1;

    YahpFtlToPdfService yahpFtlToPdfService;

    FreemarkerHelper freemarkerHelper;
    ResourceFinalizer resourceFinalizer;
    PropertyUtil propertyUtil;

    Session shopsSession;
    Node shopNode;

    String ftlStr;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockWebContext ctx = new MockWebContext();

        shoppingCartsSession = new MockSession(ShopRepositoryConstants.SHOPPING_CARTS);
        ctx.addSession(ShopRepositoryConstants.SHOPPING_CARTS, shoppingCartsSession);
        testShoppingCartShopNode = NodeUtil.createPath(shoppingCartsSession.getRootNode(), '/' + TEST_SHOP_NAME, NodeTypes.Folder.NAME);
        testShoppingCartNode = NodeUtil.createPath(testShoppingCartShopNode, '/' + TEST_SHOPPING_CART_NUMBER, NodeTypes.ContentNode.NAME);

        shopsSession = new MockSession(ShopRepositoryConstants.SHOPS);
        ctx.addSession(ShopRepositoryConstants.SHOPS, shopsSession);
        shopNode = NodeUtil.createPath(shopsSession.getRootNode(), '/' + TEST_SHOP_NAME, NodeTypes.ContentNode.NAME);// ShopNodeTypes.SHOP

        ctx.setAttribute(TEST_SHOP_NAME + "_" + ShopUtil.ATTRIBUTE_SHOPPINGCART, new DefaultShoppingCartImpl(), MockWebContext.SESSION_SCOPE);

        NodeUtil.createPath(testShoppingCartNode, "/cartItems/0/options", NodeTypes.ContentNode.NAME);
        NodeUtil.createPath(testShoppingCartNode, "/cartItems/1/options", NodeTypes.ContentNode.NAME);

        cartItem_0 = testShoppingCartNode.getNode("/cartItems/0");
        cartItem_1 = testShoppingCartNode.getNode("/cartItems/1");

        freemarkerHelper = new FreemarkerHelper();
        resourceFinalizer = new ResourceFinalizer();
        yahpFtlToPdfService = new YahpFtlToPdfService(freemarkerHelper, resourceFinalizer);
        propertyUtil = new PropertyUtil();

        ctx.setLocale(Locale.ENGLISH);
        MgnlContext.getInstance().setLocale(Locale.ENGLISH);
    }

    @Test
    public void testFtl2pdf() throws IOException, RepositoryException {
        // GIVEN
        testCreateCartItem();
        InputStream is = this.getClass().getResourceAsStream(RefactorPackageNameTask.V_2_3_0_INVOICE_RESOURCE_PATH);
        ftlStr = IOUtils.toString(is);
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("shoppingCart", testShoppingCartNode);
        dataModel.put("PropertyUtil", propertyUtil);

        // WHEN
        ByteArrayOutputStream baos = yahpFtlToPdfService.ftl2pdf(ftlStr, dataModel);

        // THEN
        assertNotNull(baos);

        is.close();
        baos.close();
    }

    @Ignore
    @Test
    public void testProcessInvoiceNodeToPdf() throws RepositoryException, IOException {
        // GIVEN
        testCreateCartItem();
        testCreateShopNodeWithConfiguredTemplate();

        // WHEN
        ByteArrayOutputStream baos = yahpFtlToPdfService.processInvoiceNodeToPdf(testShoppingCartNode);

        // THEN
        assertNotNull(baos);
    }

    @Test(expected = NullPointerException.class)
    public void testOutputStreamToNewWindow_IfByteArrayNull_ReturnNullPointer() {
        // GIVEN
        String mimeType = "application/pdf";
        String fileType = "pdf";
        ByteArrayOutputStream baos = mock(ByteArrayOutputStream.class);
        when(baos.toByteArray()).thenReturn(null);

        // WHEN
        yahpFtlToPdfService.outputStreamToNewWindow(baos, mimeType, fileType);

        // THEN
        // Throw NullPointerException
    }

    @Test
    public void testOutputStreamToTempFile_IfByteArrayNull_ReturnNull() {
        // GIVEN

        // WHEN
        File actual = yahpFtlToPdfService.outputStreamToTempFile(null);

        // THEN
        assertNull(actual);
    }

    @Test
    public void testOutputStreamToTempFile_IfHaveByteArray_ReturnNull() {
        // GIVEN
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);

        // WHEN
        File actual = yahpFtlToPdfService.outputStreamToTempFile(baos);

        // THEN
        assertNotNull(actual);
    }

    @Test
    public void testCreateCartItem() throws RepositoryException {
        assertNotNull(testShoppingCartNode.setProperty("billingAddressFirstname", "abc"));
        assertNotNull(testShoppingCartNode.setProperty("billingAddressMail", "aaa@bbb.com"));
        assertNotNull(testShoppingCartNode.setProperty("billingAddressStreet", "abc abc abc"));
        assertNotNull(testShoppingCartNode.setProperty("grossTotalExclTax", Double.parseDouble("30.44")));
        assertNotNull(testShoppingCartNode.setProperty("grossTotalInclTax", Double.parseDouble("32.75")));
        assertNotNull(testShoppingCartNode.setProperty("itemTaxTotal", Double.parseDouble("2.31")));
        assertNotNull(testShoppingCartNode.setProperty("name", "178"));
        assertNotNull(testShoppingCartNode.setProperty("ocm_classname", DefaultShoppingCartImpl.class.getName()));
        assertNotNull(testShoppingCartNode.setProperty("orderDate", Calendar.getInstance()));
        assertNotNull(testShoppingCartNode.setProperty("shippingCostTaxIncluded", false));
        assertNotNull(testShoppingCartNode.setProperty("taxIncluded", false));
        assertNotNull(testShoppingCartNode.setProperty("userIP", "0:0:0:0:0:0:0:1:52925"));

        assertNotNull(cartItem_0.setProperty("itemTax", Double.parseDouble("1.52")));
        assertNotNull(cartItem_0.setProperty("itemTotal", Double.parseDouble("20")));
        assertNotNull(cartItem_0.setProperty("ocm_classname", ShoppingCartItem.class.getName()));
        assertNotNull(cartItem_0.setProperty("productSubTitle", "Dead bodies are showing up in shallow graves on the empty construction lot of Vincent Plum Bail Bonds. No one is sure who the killer is, or why the victims have been offed, but what is clear is that Stephanie’s name is on the killer’s list."));
        assertNotNull(cartItem_0.setProperty("productTitle", "Smokin' Seventeen: A Stephanie Plum Novel"));
        assertNotNull(cartItem_0.setProperty("productUUID", "f1e6596e-6415-4e72-88b7-6f9afcfb9430"));
        assertNotNull(cartItem_0.setProperty("quantity", 2L));

        assertNotNull(cartItem_1.setProperty("itemTax", Double.parseDouble("0.793")));
        assertNotNull(cartItem_1.setProperty("itemTotal", Double.parseDouble("10.44")));
        assertNotNull(cartItem_1.setProperty("ocm_classname", ShoppingCartItem.class.getName()));
        assertNotNull(cartItem_1.setProperty("productSubTitle", "Adele"));
        assertNotNull(cartItem_1.setProperty("productTitle", "Adele"));
        assertNotNull(cartItem_1.setProperty("productUUID", "3e3d1ba0-41d3-4c80-a318-e01eba293ca7"));
        assertNotNull(cartItem_1.setProperty("quantity", 1L));
    }

    @Test
    public void testCreateShopNodeWithConfiguredTemplate() throws IOException, RepositoryException {
        // GIVEN
        InputStream is = this.getClass().getResourceAsStream(RefactorPackageNameTask.V_2_3_0_INVOICE_RESOURCE_PATH);
        ftlStr = IOUtils.toString(is);
        // WHEN
        Property ftlProp = shopNode.setProperty(FtlToPdfService.CONFIGURED_INVOICE_TEMPLATE_PROPERTY_NAME, ftlStr);
        // THEN
        assertNotNull(ftlProp);
    }

}
