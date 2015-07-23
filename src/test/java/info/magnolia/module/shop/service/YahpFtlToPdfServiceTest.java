package info.magnolia.module.shop.service;

import static org.junit.Assert.*;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;

public class YahpFtlToPdfServiceTest extends RepositoryTestCase {

    public static final String TEST_SHOP_NAME = "testShop";
    public static final String TEST_SHOPPING_CART_NUMBER = "178";
    
    Session shoppingCartSession;
    Node testShoppingCartShopNode;
    Node testShoppingCartNode;
    Node cartItem_0;
    Node cartItem_1;
    
    YahpFtlToPdfService yahpFtlToPdfService;
    FreemarkerHelper freemarkerHelper;
    ResourceFinalizer resourceFinalizer;
    PropertyUtil propertyUtil;
    
    
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        MockWebContext ctx = new MockWebContext();
        shoppingCartSession = new MockSession(ShopRepositoryConstants.SHOPPING_CARTS);
        ctx.addSession(ShopRepositoryConstants.SHOPPING_CARTS, shoppingCartSession);
        testShoppingCartShopNode = NodeUtil.createPath(shoppingCartSession.getRootNode(), '/' + TEST_SHOP_NAME, NodeTypes.Folder.NAME);
        testShoppingCartNode = NodeUtil.createPath(testShoppingCartShopNode,  '/' + TEST_SHOPPING_CART_NUMBER, NodeTypes.ContentNode.NAME);
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
        testCreateCartItem();
        InputStream is = this.getClass().getResourceAsStream(RefactorPackageNameTask.V_2_3_0_INVOICE_RESOURCE_PATH);
        String ftlStr = IOUtils.toString(is);
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("shoppingCart", testShoppingCartNode);
        dataModel.put("PropertyUtil", propertyUtil);
        ByteArrayOutputStream baos = yahpFtlToPdfService.ftl2pdf(ftlStr, dataModel);
        assertNotNull(baos);
        is.close();
        baos.close();
    }

    @Test
    public void testProcessInvoiceNodeToPdf() {
        fail("Not yet implemented");
    }

    @Test
    public void testOutputStreamToNewWindow() {
        //fail("Not yet implemented");
    }

    @Test
    public void testOutputStreamToTempFile() {
        //fail("Not yet implemented");
    }
    
    @Test
    public void testCreateCartItem() throws RepositoryException {
        assertNotNull(testShoppingCartNode.setProperty("billingAddressFirstname", "trung.luu"));
        assertNotNull(testShoppingCartNode.setProperty("billingAddressMail", "trung.luu@pyramid-consulting.com"));
        assertNotNull(testShoppingCartNode.setProperty("billingAddressStreet", "364 Cong Hoa, Tan Binh district, Ho Chi Minh city, Vietnam"));
        assertNotNull(testShoppingCartNode.setProperty("grossTotalExclTax", "30.44"));
        assertNotNull(testShoppingCartNode.setProperty("grossTotalInclTax", "32.75"));
        assertNotNull(testShoppingCartNode.setProperty("itemTaxTotal", "2.31"));
        assertNotNull(testShoppingCartNode.setProperty("name", "178"));
        assertNotNull(testShoppingCartNode.setProperty("ocm_classname", DefaultShoppingCartImpl.class.getName()));
        assertNotNull(testShoppingCartNode.setProperty("orderDate", "Jul 23, 2015 4:10:22 PM"));
        assertNotNull(testShoppingCartNode.setProperty("shippingCostTaxIncluded", false));
        assertNotNull(testShoppingCartNode.setProperty("taxIncluded", false));
        assertNotNull(testShoppingCartNode.setProperty("userIP", "0:0:0:0:0:0:0:1:52925"));
        
        assertNotNull(cartItem_0.setProperty("itemTax", "1.52"));
        assertNotNull(cartItem_0.setProperty("itemTotal", "20"));
        assertNotNull(cartItem_0.setProperty("ocm_classname", ShoppingCartItem.class.getName()));
        assertNotNull(cartItem_0.setProperty("productSubTitle", "Dead bodies are showing up in shallow graves on the empty construction lot of Vincent Plum Bail Bonds. No one is sure who the killer is, or why the victims have been offed, but what is clear is that Stephanie’s name is on the killer’s list."));
        assertNotNull(cartItem_0.setProperty("productTitle", "Smokin' Seventeen: A Stephanie Plum Novel"));
        assertNotNull(cartItem_0.setProperty("productUUID", "f1e6596e-6415-4e72-88b7-6f9afcfb9430"));
        assertNotNull(cartItem_0.setProperty("quantity", 2L));
        
        assertNotNull(cartItem_1.setProperty("itemTax", "0.793"));
        assertNotNull(cartItem_1.setProperty("itemTotal", "10.44"));
        assertNotNull(cartItem_1.setProperty("ocm_classname", ShoppingCartItem.class.getName()));
        assertNotNull(cartItem_1.setProperty("productSubTitle", "Adele"));
        assertNotNull(cartItem_1.setProperty("productTitle", "Adele"));
        assertNotNull(cartItem_1.setProperty("productUUID", "3e3d1ba0-41d3-4c80-a318-e01eba293ca7"));
        assertNotNull(cartItem_1.setProperty("quantity", 1L));
    }

}
