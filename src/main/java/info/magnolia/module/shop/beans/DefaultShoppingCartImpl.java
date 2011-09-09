/**
 * This file Copyright (c) 2003-2011 Magnolia International
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
package info.magnolia.module.shop.beans;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.module.ocm.beans.OCMNumberedBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default shopping cart implementation with order, billing and shipping
 * addresses allowing only one cart item per product (i.e. when adding the same
 * product multiple times, the quantity of the cart item will be increased).
 * 
 * @author will
 */
public class DefaultShoppingCartImpl extends OCMNumberedBean implements ShoppingCart, Serializable {

    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(DefaultShoppingCartImpl.class);
    private String language;
    private Date orderDate;
    private Date targetDeliveryDate;
    private Date deliveryDate;
    private String userIP;
    private ArrayList<ShoppingCartItem> cartItems;
    private String priceCategoryUUID;
    private String orderAddressCompany;
    private String orderAddressCompany2;
    private String orderAddressFirstname;
    private String orderAddressLastname;
    private String orderAddressSex;
    private String orderAddressTitle;
    private String orderAddressStreet;
    private String orderAddressStreet2;
    private String orderAddressZip;
    private String orderAddressCity;
    private String orderAddressState;
    private String orderAddressCountry;
    private String orderAddressPhone;
    private String orderAddressMobile;
    private String orderAddressMail;
    private String shippingAddressCompany;
    private String shippingAddressCompany2;
    private String shippingAddressFirstname;
    private String shippingAddressLastname;
    private String shippingAddressSex;
    private String shippingAddressTitle;
    private String shippingAddressStreet;
    private String shippingAddressStreet2;
    private String shippingAddressZip;
    private String shippingAddressCity;
    private String shippingAddressState;
    private String shippingAddressCountry;
    private String shippingAddressPhone;
    private String shippingAddressMobile;
    private String shippingAddressMail;
    private String billingAddressCompany;
    private String billingAddressCompany2;
    private String billingAddressFirstname;
    private String billingAddressLastname;
    private String billingAddressSex;
    private String billingAddressTitle;
    private String billingAddressStreet;
    private String billingAddressStreet2;
    private String billingAddressZip;
    private String billingAddressCity;
    private String billingAddressState;
    private String billingAddressCountry;
    private String billingAddressPhone;
    private String billingAddressMobile;
    private String billingAddressMail;
    private Boolean acceptedGTC;
    private Double cartDiscountRate;
    private Boolean taxIncluded = false;

    public DefaultShoppingCartImpl() {
        this(null);
    }

    public DefaultShoppingCartImpl(Content priceCategory) {
        super();
        cartItems = new ArrayList();
        if (priceCategory != null) {
            setPriceCategoryUUID(priceCategory.getUUID());
        }
    }

    /**
     * Addes a product to the cart. If there is already a cart item for this
     * product the items quantity will be increased
     *
     * @param productUUID
     * @param quantity
     * @return
     */
    public int addToShoppingCart(String productUUID, int quantity) {
        return addToShoppingCart(productUUID, quantity, null);
    }

    public int addToShoppingCart(String productUUID, int quantity, Map options) {
        int quantityAdded = 0;
        if (productUUID != null) {
            int indexOfProductInCart = indexOfProduct(productUUID, options);
            if (indexOfProductInCart >= 0) {
                ShoppingCartItem existingCartItem = (ShoppingCartItem) getCartItems().get(indexOfProductInCart);
                existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            } else {
                double price = 0.0;
                String queryString = "//*[@jcr:uuid='" + productUUID
                        + "']/prices/element(*,mgnl:contentNode)[@priceCategoryUUID = '" + this.getPriceCategoryUUID() + "']";
                Collection<Content> matching = QueryUtil.query("data", queryString, "xpath", "mgnl:contentNode");
                if (!matching.isEmpty()) {
                    Content priceNode = new I18nContentWrapper(matching.iterator().next());
                    if (priceNode.getNodeData("price").isExist()) {
                        price = priceNode.getNodeData("price").getDouble();
                    }
                }
                this.getCartItems().add(new ShoppingCartItem(this, productUUID, quantity, price, options));
            }
            quantityAdded = quantity;
        }
        return quantityAdded;
    }

    /**
     * Removes the cart item containing the product with the passed in UUID.
     *
     * @param productUUID
     * @todo When multiple items with the same product should be allowed this will
     *       not work anymore.
     */
    public void removeFromShoppingCart(String productUUID) {
        int indexOfProductInCart = indexOfProduct(productUUID);
        if (indexOfProductInCart >= 0) {
            getCartItems().remove(indexOfProductInCart);
        }
    }

    /**
     *
     * @param productUUID
     * @return the index of the cart item containing the desired product
     */
    public int indexOfProduct(String productUUID) {
        return indexOfProduct(productUUID, null);
    }

    public int indexOfProduct(String productUUID, Map options) {
        ShoppingCartItem currentCartItem;
        for (int i = 0; i < cartItems.size(); i++) {
            currentCartItem = (ShoppingCartItem) cartItems.get(i);
            if (currentCartItem.getProductUUID().equals(productUUID)) {
                // the product matches -> check the options
                if (currentCartItem.isOptionsMatching(options)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public ArrayList<ShoppingCartItem> getCartItems() {
        return cartItems;
    }

    public String getPriceCategoryUUID() {
        return priceCategoryUUID;
    }

    public void setPriceCategoryUUID(String uuid) {
        this.priceCategoryUUID = uuid;
        if (StringUtils.isNotBlank(uuid)) {
            Content priceCat = ContentUtil.getContentByUUID("data", uuid);
            if (priceCat != null) {
                if (priceCat.getNodeData("taxIncluded").isExist()) {
                    setTaxIncluded(priceCat.getNodeData("taxIncluded").getBoolean());
                }
            }
        }
    }

    public void addCartItem(ShoppingCartItem newItem) {
        cartItems.add(newItem);
    }

    public int getCartItemsCount() {
        return cartItems.size();
    }

    public String getOrderAddressCompany() {
        return orderAddressCompany;
    }

    public void setOrderAddressCompany(String orderAddressCompany) {
        this.orderAddressCompany = orderAddressCompany;
    }

    public String getOrderAddressCompany2() {
        return orderAddressCompany2;
    }

    public void setOrderAddressCompany2(String orderAddressCompany2) {
        this.orderAddressCompany2 = orderAddressCompany2;
    }

    public Boolean getAcceptedGTC() {
        return acceptedGTC;
    }

    public void setAcceptedGTC(Boolean acceptedGTC) {
        this.acceptedGTC = acceptedGTC;
    }

    public String getOrderAddressFirstname() {
        return orderAddressFirstname;
    }

    public void setOrderAddressFirstname(String orderAddressFirstname) {
        this.orderAddressFirstname = orderAddressFirstname;
    }

    public String getOrderAddressLastname() {
        return orderAddressLastname;
    }

    public void setOrderAddressLastname(String orderAddressLastname) {
        this.orderAddressLastname = orderAddressLastname;
    }

    public String getOrderAddressSex() {
        return orderAddressSex;
    }

    public void setOrderAddressSex(String orderAddressSex) {
        this.orderAddressSex = orderAddressSex;
    }

    public String getOrderAddressTitle() {
        return orderAddressTitle;
    }

    public void setOrderAddressTitle(String orderAddressTitle) {
        this.orderAddressTitle = orderAddressTitle;
    }

    public String getOrderAddressStreet() {
        return orderAddressStreet;
    }

    public void setOrderAddressStreet(String orderAddressStreet) {
        this.orderAddressStreet = orderAddressStreet;
    }

    public String getOrderAddressStreet2() {
        return orderAddressStreet2;
    }

    public void setOrderAddressStreet2(String orderAddressStreet2) {
        this.orderAddressStreet2 = orderAddressStreet2;
    }

    public String getOrderAddressZip() {
        return orderAddressZip;
    }

    public void setOrderAddressZip(String orderAddressZip) {
        this.orderAddressZip = orderAddressZip;
    }

    public String getOrderAddressCity() {
        return orderAddressCity;
    }

    public void setOrderAddressCity(String orderAddressCity) {
        this.orderAddressCity = orderAddressCity;
    }

    public String getOrderAddressState() {
        return orderAddressState;
    }

    public void setOrderAddressState(String orderAddressState) {
        this.orderAddressState = orderAddressState;
    }

    public String getOrderAddressCountry() {
        return orderAddressCountry;
    }

    public void setOrderAddressCountry(String orderAddressCountry) {
        this.orderAddressCountry = orderAddressCountry;
    }

    public String getOrderAddressPhone() {
        return orderAddressPhone;
    }

    public void setOrderAddressPhone(String orderAddressPhone) {
        this.orderAddressPhone = orderAddressPhone;
    }

    public String getOrderAddressMobile() {
        return orderAddressMobile;
    }

    public void setOrderAddressMobile(String orderAddressMobile) {
        this.orderAddressMobile = orderAddressMobile;
    }

    public String getOrderAddressMail() {
        return orderAddressMail;
    }

    public void setOrderAddressMail(String orderAddressMail) {
        this.orderAddressMail = orderAddressMail;
    }

    public String getShippingAddressCompany() {
        return shippingAddressCompany;
    }

    public void setShippingAddressCompany(String shippingAddressCompany) {
        this.shippingAddressCompany = shippingAddressCompany;
    }

    public String getShippingAddressCompany2() {
        return shippingAddressCompany2;
    }

    public void setShippingAddressCompany2(String shippingAddressCompany2) {
        this.shippingAddressCompany2 = shippingAddressCompany2;
    }

    public String getShippingAddressFirstname() {
        return shippingAddressFirstname;
    }

    public void setShippingAddressFirstname(String shippingAddressFirstname) {
        this.shippingAddressFirstname = shippingAddressFirstname;
    }

    public String getShippingAddressLastname() {
        return shippingAddressLastname;
    }

    public void setShippingAddressLastname(String shippingAddressLastname) {
        this.shippingAddressLastname = shippingAddressLastname;
    }

    public String getShippingAddressSex() {
        return shippingAddressSex;
    }

    public void setShippingAddressSex(String shippingAddressSex) {
        this.shippingAddressSex = shippingAddressSex;
    }

    public String getShippingAddressTitle() {
        return shippingAddressTitle;
    }

    public void setShippingAddressTitle(String shippingAddressTitle) {
        this.shippingAddressTitle = shippingAddressTitle;
    }

    public String getShippingAddressStreet() {
        return shippingAddressStreet;
    }

    public void setShippingAddressStreet(String shippingAddressStreet) {
        this.shippingAddressStreet = shippingAddressStreet;
    }

    public String getShippingAddressStreet2() {
        return shippingAddressStreet2;
    }

    public void setShippingAddressStreet2(String shippingAddressStreet2) {
        this.shippingAddressStreet2 = shippingAddressStreet2;
    }

    public String getShippingAddressZip() {
        return shippingAddressZip;
    }

    public void setShippingAddressZip(String shippingAddressZip) {
        this.shippingAddressZip = shippingAddressZip;
    }

    public String getShippingAddressCity() {
        return shippingAddressCity;
    }

    public void setShippingAddressCity(String shippingAddressCity) {
        this.shippingAddressCity = shippingAddressCity;
    }

    public String getShippingAddressState() {
        return shippingAddressState;
    }

    public void setShippingAddressState(String shippingAddressState) {
        this.shippingAddressState = shippingAddressState;
    }

    public String getShippingAddressCountry() {
        return shippingAddressCountry;
    }

    public void setShippingAddressCountry(String shippingAddressCountry) {
        this.shippingAddressCountry = shippingAddressCountry;
    }

    public String getShippingAddressPhone() {
        return shippingAddressPhone;
    }

    public void setShippingAddressPhone(String shippingAddressPhone) {
        this.shippingAddressPhone = shippingAddressPhone;
    }

    public String getShippingAddressMobile() {
        return shippingAddressMobile;
    }

    public void setShippingAddressMobile(String shippingAddressMobile) {
        this.shippingAddressMobile = shippingAddressMobile;
    }

    public String getShippingAddressMail() {
        return shippingAddressMail;
    }

    public void setShippingAddressMail(String shippingAddressMail) {
        this.shippingAddressMail = shippingAddressMail;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getUserIP() {
        return userIP;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }

    public String getBillingAddressCompany() {
        return billingAddressCompany;
    }

    public void setBillingAddressCompany(String billingAddressCompany) {
        this.billingAddressCompany = billingAddressCompany;
    }

    public String getBillingAddressCompany2() {
        return billingAddressCompany2;
    }

    public void setBillingAddressCompany2(String billingAddressCompany2) {
        this.billingAddressCompany2 = billingAddressCompany2;
    }

    public String getBillingAddressFirstname() {
        return billingAddressFirstname;
    }

    public void setBillingAddressFirstname(String billingAddressFirstname) {
        this.billingAddressFirstname = billingAddressFirstname;
    }

    public String getBillingAddressLastname() {
        return billingAddressLastname;
    }

    public void setBillingAddressLastname(String billingAddressLastname) {
        this.billingAddressLastname = billingAddressLastname;
    }

    public String getBillingAddressSex() {
        return billingAddressSex;
    }

    public void setBillingAddressSex(String billingAddressSex) {
        this.billingAddressSex = billingAddressSex;
    }

    public String getBillingAddressTitle() {
        return billingAddressTitle;
    }

    public void setBillingAddressTitle(String billingAddressTitle) {
        this.billingAddressTitle = billingAddressTitle;
    }

    public String getBillingAddressStreet() {
        return billingAddressStreet;
    }

    public void setBillingAddressStreet(String billingAddressStreet) {
        this.billingAddressStreet = billingAddressStreet;
    }

    public String getBillingAddressStreet2() {
        return billingAddressStreet2;
    }

    public void setBillingAddressStreet2(String billingAddressStreet2) {
        this.billingAddressStreet2 = billingAddressStreet2;
    }

    public String getBillingAddressZip() {
        return billingAddressZip;
    }

    public void setBillingAddressZip(String billingAddressZip) {
        this.billingAddressZip = billingAddressZip;
    }

    public String getBillingAddressCity() {
        return billingAddressCity;
    }

    public void setBillingAddressCity(String billingAddressCity) {
        this.billingAddressCity = billingAddressCity;
    }

    public String getBillingAddressState() {
        return billingAddressState;
    }

    public void setBillingAddressState(String billingAddressState) {
        this.billingAddressState = billingAddressState;
    }

    public String getBillingAddressCountry() {
        return billingAddressCountry;
    }

    public void setBillingAddressCountry(String billingAddressCountry) {
        this.billingAddressCountry = billingAddressCountry;
    }

    public String getBillingAddressPhone() {
        return billingAddressPhone;
    }

    public void setBillingAddressPhone(String billingAddressPhone) {
        this.billingAddressPhone = billingAddressPhone;
    }

    public String getBillingAddressMobile() {
        return billingAddressMobile;
    }

    public void setBillingAddressMobile(String billingAddressMobile) {
        this.billingAddressMobile = billingAddressMobile;
    }

    public String getBillingAddressMail() {
        return billingAddressMail;
    }

    public void setBillingAddressMail(String billingAddressMail) {
        this.billingAddressMail = billingAddressMail;
    }

    public Double getCartDiscountRate() {
        return cartDiscountRate;
    }

    public void setCartDiscountRate(Double cartDiscountRate) {
        this.cartDiscountRate = cartDiscountRate;
    }

    public BigDecimal getGrossTotalExclTaxBigDecimal() {
        BigDecimal total = new BigDecimal("0");
        Iterator itemsIter = getCartItems().iterator();
        ShoppingCartItem currItem;
        int i = 0;
        while (itemsIter.hasNext()) {
            currItem = (ShoppingCartItem) itemsIter.next();
            total = total.add(currItem.getItemTotalExclTaxBigDecimal());
        }
        return total;
    }

    public double getGrossTotalExclTax() {
        BigDecimal total = getGrossTotalExclTaxBigDecimal();
        if (total != null) {
            return total.doubleValue();
        }
        return 0;
    }

    public BigDecimal getGrossTotalInclTaxBigDecimal() {
        BigDecimal total = new BigDecimal("0");
        Iterator itemsIter = getCartItems().iterator();
        ShoppingCartItem currItem;
        while (itemsIter.hasNext()) {
            currItem = (ShoppingCartItem) itemsIter.next();
            total = total.add(currItem.getItemTotalInclTaxBigDecimal());
        }
        return total;
    }

    public double getGrossTotalInclTax() {
        BigDecimal total = getGrossTotalInclTaxBigDecimal();
        if (total != null) {
            return total.doubleValue();
        }
        return 0;
    }

    public BigDecimal getItemTaxTotalBigDecimal() {
        BigDecimal total = new BigDecimal("0");
        Iterator itemsIter = getCartItems().iterator();
        ShoppingCartItem currItem;
        while (itemsIter.hasNext()) {
            currItem = (ShoppingCartItem) itemsIter.next();
            total = total.add(currItem.getItemTaxBigDecimal());
        }
        return total;
    }

    public double getItemTaxTotal() {
        BigDecimal total = getItemTaxTotalBigDecimal();
        if (total != null) {
            return total.doubleValue();
        }
        return 0;
    }

    public double getGrossTotal() {
        double total = 0;
        Iterator itemsIter = getCartItems().iterator();
        ShoppingCartItem currItem;
        while (itemsIter.hasNext()) {
            currItem = (ShoppingCartItem) itemsIter.next();
            total += currItem.getItemTotal();
        }
        return total;
    }

    public double getCartDiscount() {
        double cartDiscount = 0;
        double grossTotal = getGrossTotal();
        if (grossTotal > 0 && cartDiscountRate != null && cartDiscountRate.doubleValue() > 0) {
            cartDiscount = grossTotal * cartDiscountRate.doubleValue() / 100;
        }
        return cartDiscount;
    }

    public double getNetTotal() {
        double netTotal = getGrossTotal();
        if (netTotal > 0 && cartDiscountRate != null && cartDiscountRate.doubleValue() > 0) {
            netTotal = netTotal - (netTotal * cartDiscountRate.doubleValue() / 100);
        }
        return netTotal;
    }

    public Date getTargetDeliveryDate() {
        return targetDeliveryDate;
    }

    public void setTargetDeliveryDate(Date targetDeliveryDate) {
        this.targetDeliveryDate = targetDeliveryDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * @return the taxIncluded
     */
    public Boolean getTaxIncluded() {
        return taxIncluded;
    }

    /**
     * @param taxIncluded
     *          the taxIncluded to set
     */
    public void setTaxIncluded(Boolean taxIncluded) {
        this.taxIncluded = taxIncluded;
    }
}
