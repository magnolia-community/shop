package info.magnolia.module.shop.service;

import static org.junit.Assert.*;

import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockSession;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;

public class YahpFtlToPdfServiceTest extends RepositoryTestCase {

    public static final String TEST_SHOP_NAME = "testShop";
    public static final String TEST_SHOPPING_CART_NUMBER = "178";
    Session shoppingCartSession;
    Node testShoppingCartShopNode;
    Node testShoppingCartNode;
    
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
    }

    @Test
    public void testFtl2pdf() {
        //fail("Not yet implemented");
    }

    @Test
    public void testProcessInvoiceNodeToPdf() {
        //fail("Not yet implemented");
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
        assertNotNull(NodeUtil.createPath(testShoppingCartNode, "/cartItems/0/options", NodeTypes.ContentNode.NAME));
        assertNotNull(NodeUtil.createPath(testShoppingCartNode, "/cartItems/1/options", NodeTypes.ContentNode.NAME));
        assertNotNull(testShoppingCartNode.setProperty("billingAddressFirstname", "trung.luu"));
        assertNotNull(testShoppingCartNode.setProperty("billingAddressMail", "trung.luu@pyramid-consulting.com"));
        assertNotNull(testShoppingCartNode.setProperty("billingAddressStreet", "364 Cong Hoa, Tan Binh district, Ho Chi Minh city, Vietnam"));
        assertNotNull(testShoppingCartNode.setProperty("grossTotalExclTax", "30.44"));
        assertNotNull(testShoppingCartNode.setProperty("grossTotalInclTax", "32.75"));
        assertNotNull(testShoppingCartNode.setProperty("itemTaxTotal", "2.31"));
        assertNotNull(testShoppingCartNode.setProperty("name", "178"));
        assertNotNull(testShoppingCartNode.setProperty("ocm_classname", "info.magnolia.module.shop.beans.DefaultShoppingCartImpl"));
        assertNotNull(testShoppingCartNode.setProperty("orderDate", "Jul 23, 2015 4:10:22 PM"));
        assertNotNull(testShoppingCartNode.setProperty("shippingCostTaxIncluded", "false"));
        assertNotNull(testShoppingCartNode.setProperty("taxIncluded", "false"));
        assertNotNull(testShoppingCartNode.setProperty("userIP", "0:0:0:0:0:0:0:1:52925"));
    }

}
