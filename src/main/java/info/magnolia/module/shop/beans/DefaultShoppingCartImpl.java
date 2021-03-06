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
package info.magnolia.module.shop.beans;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.module.shop.util.ShopUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.fastforward.magnolia.ocm.beans.OCMNumberedBean;

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
    private String formStateToken;
    private String language;
    private Date orderDate;
    private Date targetDeliveryDate;
    private Date deliveryDate;
    private Date paymentDate;
    private String paymentType;
    private String paymentID;
    private String userIP;
    private final ArrayList<ShoppingCartItem> cartItems;
    private String priceCategoryUUID;
    private String shippingOptionUUID;
    private String shippingOptionTitle;
    private BigDecimal shippingCost;
    private BigDecimal shippingCostTaxRate;
    private Boolean shippingCostTaxIncluded = false;
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
    private Boolean termsAccepted;
    private Double cartDiscountRate;
    private Boolean taxIncluded = false;
    private Boolean taxFree = false;

    public DefaultShoppingCartImpl() {
        this(null);
    }

    public DefaultShoppingCartImpl(Node priceCategory) {
        super();
        cartItems = new ArrayList<ShoppingCartItem>();
        if (priceCategory != null) {
            try {
                setPriceCategoryUUID(priceCategory.getIdentifier());
            } catch (RepositoryException e) {
                log.error("Cant read the price category", e);
            }
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
    @Override
    public int addToShoppingCart(String productUUID, int quantity) {
        return addToShoppingCart(productUUID, quantity, null);
    }

    @Override
    public int addToShoppingCart(String productUUID, int quantity, Map<String, CartItemOption> options) {
        int quantityAdded = 0;
        if (productUUID != null) {
            int indexOfProductInCart = indexOfProduct(productUUID, options);
            if (indexOfProductInCart >= 0) {
                ShoppingCartItem existingCartItem = getCartItems().get(indexOfProductInCart);
                existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            } else {
                double price = 0.0;
                String queryString = "//*[@jcr:uuid='" + productUUID
                        + "']/prices/element(*,mgnl:contentNode)[@priceCategoryUUID = '" + this.getPriceCategoryUUID() + "']";
                Collection<Content> matching = QueryUtil.query(ShopRepositoryConstants.SHOP_PRODUCTS, queryString, "xpath", "mgnl:contentNode");
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
     * not work anymore.
     */
    @Override
    public void removeFromShoppingCart(String productUUID) {
        int indexOfProductInCart = indexOfProduct(productUUID);
        if (indexOfProductInCart >= 0) {
            getCartItems().remove(indexOfProductInCart);
        }
    }

    /**
     * @param productUUID
     * @return the index of the cart item containing the desired product
     */
    public int indexOfProduct(String productUUID) {
        return indexOfProduct(productUUID, null);
    }

    public int indexOfProduct(String productUUID, Map<String, CartItemOption> options) {
        ShoppingCartItem currentCartItem;
        for (int i = 0; i < cartItems.size(); i++) {
            currentCartItem = cartItems.get(i);
            if (currentCartItem.getProductUUID().equals(productUUID)) {
                // the product matches -> check the options
                if (currentCartItem.isOptionsMatching(options)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public ArrayList<ShoppingCartItem> getCartItems() {
        return cartItems;
    }

    public String getPriceCategoryUUID() {
        return priceCategoryUUID;
    }

    public void setPriceCategoryUUID(String uuid) {
        this.priceCategoryUUID = uuid;
        if (StringUtils.isNotBlank(uuid)) {
            Node priceCat = null;
            try {
                priceCat = NodeUtil.getNodeByIdentifier(ShopRepositoryConstants.SHOPS, uuid);
            } catch (RepositoryException e) {
                log.error("Don't find PriceCategory with uuid" + uuid, e);
            }
            if (priceCat != null) {
                setTaxIncluded(PropertyUtil.getBoolean(priceCat, "taxIncluded", false));
            }
        }
    }

    public void addCartItem(ShoppingCartItem newItem) {
        cartItems.add(newItem);
    }

    @Override
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

    public Boolean getTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(Boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }

    /**
     * @deprecated Use {@link #getTermsAccepted()}
     * @return
     */
    @Deprecated
    public Boolean getAcceptedGTC() {
        return getTermsAccepted();
    }

    /**
     * @deprecated Use {@link #setTermsAccepted()}
     * @return
     */
    @Deprecated
    public void setAcceptedGTC(Boolean acceptedGTC) {
        setTermsAccepted(acceptedGTC);
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
        // reset shippingOptionUUID
        if (shippingAddressCountry == null) {
            setShippingOptionUUID(null);
        } else {
            Collection<Content> shippingOptions = ShopUtil.getShippingOptions();
            if (shippingOptions.isEmpty()) {
                setShippingOptionUUID(null);
            } else {
                setShippingOptionUUID(shippingOptions.iterator().next().getUUID());
            }
        }
        // set taxFree
        if (StringUtils.isNotBlank(shippingAddressCountry)) {
            // get country
            String queryString = "/jcr:root/shops/" + ShopUtil.getShopName() + "/countries/element(*,shopCountry)[@name='" + shippingAddressCountry + "']";
            Collection<Content> matching = QueryUtil.query("data", queryString, "xpath", "shopCountry");
            if (!matching.isEmpty()) {
                Content country = matching.iterator().next();
                setTaxFree(!NodeDataUtil.getBoolean(country, "liableToVAT", true));
            }
        } else {
            setTaxFree(false);
        }
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

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
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

    public BigDecimal getGrossItemsTotalExclTaxBigDecimal() {
        BigDecimal total = new BigDecimal("0");
        Iterator<ShoppingCartItem> itemsIter = getCartItems().iterator();
        ShoppingCartItem currItem;
        while (itemsIter.hasNext()) {
            currItem = itemsIter.next();
            total = total.add(currItem.getItemTotalExclTaxBigDecimal());
        }
        return total;
    }

    public double getGrossItemsTotalExclTax() {
        BigDecimal total = getGrossItemsTotalExclTaxBigDecimal();
        if (total != null) {
            return total.doubleValue();
        }
        return 0;
    }

    public BigDecimal getNetItemsTotalExclTaxBigDecimal() {
        BigDecimal grossItemsTotal = getGrossItemsTotalExclTaxBigDecimal();
        Double discountRate = getCartDiscountRate();
        if (discountRate != null && discountRate.doubleValue() > 0) {
            return grossItemsTotal.multiply(new BigDecimal("" + (1 - discountRate)));
        } else {
            return grossItemsTotal;
        }
    }

    public double getNetItemsTotalExclTax() {
        BigDecimal total = getNetItemsTotalExclTaxBigDecimal();
        if (total != null) {
            return total.doubleValue();
        }
        return 0;
    }

    public BigDecimal getGrossItemsTotalInclTaxBigDecimal() {
        BigDecimal total = new BigDecimal("0");
        Iterator<ShoppingCartItem> itemsIter = getCartItems().iterator();
        ShoppingCartItem currItem;
        while (itemsIter.hasNext()) {
            currItem = itemsIter.next();
            if (getTaxFree()) {
                total = total.add(currItem.getItemTotalExclTaxBigDecimal());
            } else {
                total = total.add(currItem.getItemTotalInclTaxBigDecimal());
            }
        }
        return total;
    }

    public double getGrossItemsTotalInclTax() {
        BigDecimal total = getGrossItemsTotalInclTaxBigDecimal();
        if (total != null) {
            return total.doubleValue();
        }
        return 0;
    }

    public BigDecimal getNetItemsTotalInclTaxBigDecimal() {
        BigDecimal grossItemsTotal = getGrossItemsTotalInclTaxBigDecimal();
        Double discountRate = getCartDiscountRate();
        if (discountRate != null && discountRate.doubleValue() > 0) {
            return grossItemsTotal.multiply(new BigDecimal("" + (1 - discountRate)));
        } else {
            return grossItemsTotal;
        }
    }

    public double getNetItemsTotalInclTax() {
        BigDecimal total = getNetItemsTotalInclTaxBigDecimal();
        if (total != null) {
            return total.doubleValue();
        }
        return 0;
    }

    /**
     * @deprecated Use {@link #getGrossItemsTotalExclTaxBigDecimal()}
     * @return
     */
    @Deprecated
    public BigDecimal getGrossTotalExclTaxBigDecimal() {
        return getGrossItemsTotalExclTaxBigDecimal();
    }

    /**
     * @deprecated Use {@link #getGrossItemsTotalExclTax()}
     * @return
     */
    @Deprecated
    public double getGrossTotalExclTax() {
        return getGrossItemsTotalExclTax();
    }

    /**
     * @deprecated Use {@link #getGrossItemsTotalInclTaxBigDecimal()}
     * @return
     */
    @Deprecated
    public BigDecimal getGrossTotalInclTaxBigDecimal() {
        return getGrossItemsTotalInclTaxBigDecimal();
    }

    /**
     * @deprecated Use {@link #getGrossItemsTotalInclTax()}
     * @return
     */
    @Deprecated
    public double getGrossTotalInclTax() {
        return getGrossItemsTotalInclTax();
    }

    /**
     * @deprecated Use {@link #getCartTotalInclTax()}
     * @return
     */
    @Deprecated
    public double getGrossTotal() {
        double total = 0;
        Iterator<ShoppingCartItem> itemsIter = getCartItems().iterator();
        ShoppingCartItem currItem;
        while (itemsIter.hasNext()) {
            currItem = itemsIter.next();
            total += currItem.getItemTotal();
        }
        return total;
    }

    /**
     * Sums up the cart items tax and applies the discount rate if there is any.
     * 
     * @return
     */
    public BigDecimal getItemTaxTotalBigDecimal() {
        BigDecimal total = new BigDecimal("0");
        if (!getTaxFree()) {
            Iterator<ShoppingCartItem> itemsIter = getCartItems().iterator();
            ShoppingCartItem currItem;
            while (itemsIter.hasNext()) {
                currItem = itemsIter.next();
                BigDecimal up = currItem.getItemTaxBigDecimal();
                if (up != null) {
                    total = total.add(up);
                }
            }
            if (getCartDiscountRate() != null && getCartDiscountRate() > 0) {
                total = total.multiply(new BigDecimal("" + (1 - getCartDiscountRate())));
            }
        }
        return total;
    }

    public double getItemTaxTotal() {
        if (getTaxFree()) {
            return 0;
        } else {
            BigDecimal total = getItemTaxTotalBigDecimal();
            if (total != null) {
                return total.doubleValue();
            }
            return 0;
        }
    }

    public double getCartDiscount() {
        double cartDiscount = 0;
        double grossTotal = getGrossTotal();
        if (grossTotal > 0 && cartDiscountRate != null && cartDiscountRate.doubleValue() > 0) {
            cartDiscount = grossTotal * cartDiscountRate.doubleValue() / 100;
        }
        return cartDiscount;
    }

    /**
     * @deprecated Use {@link #getNetItemsTotalExclTax() } or {@link #getNetItemsTotalInclTax()}
     * @return
     */
    @Deprecated
    public double getNetTotal() {
        double netTotal = getGrossTotal();
        if (netTotal > 0 && cartDiscountRate != null && cartDiscountRate.doubleValue() > 0) {
            netTotal = netTotal - netTotal * cartDiscountRate.doubleValue() / 100;
        }
        return netTotal;
    }

    /**
     * @return the shippingCost
     */
    public BigDecimal getShippingCostBigDecimal() {
        return shippingCost;
    }

    public BigDecimal getShippingCostInclTaxBigDecimal() {
        if (getShippingCostTaxIncluded()) {
            return shippingCost;
        } else if (getShippingCostTaxRate() != null) {
            return ShopUtil.getPriceIncludingTax(shippingCost, getShippingCostTaxRate());
        } else {
            return shippingCost;
        }
    }

    public double getShippingCostInclTax() {
        BigDecimal cost = getShippingCostInclTaxBigDecimal();
        if (cost != null) {
            return cost.doubleValue();
        }
        return 0;
    }

    public BigDecimal getShippingCostExclTaxBigDecimal() {
        if (!getShippingCostTaxIncluded()) {
            return shippingCost;
        } else if (getShippingCostTaxRate() != null) {
            return ShopUtil.getPriceExcludingTax(shippingCost, getShippingCostTaxRate());
        } else {
            return shippingCost;
        }
    }

    public double getShippingCostExclTax() {
        BigDecimal cost = getShippingCostExclTaxBigDecimal();
        if (cost != null) {
            return cost.doubleValue();
        }
        return 0;
    }

    public BigDecimal getShippingCostTax() {
        return ShopUtil.getTax(getShippingCostBigDecimal(), getShippingCostTaxIncluded(), getShippingCostTaxRate());
        /*
         * if (getShippingCostTaxRate() != null) { if
         * (getShippingCostTaxIncluded()) { // tax rate is in persent, e.g. 8
         * for 8% BigDecimal factor =
         * getShippingCostTaxRate().divide(HUNDRED.add(getShippingCostTaxRate()),
         * 10, BigDecimal.ROUND_HALF_UP); return
         * getShippingCostBigDecimal().multiply(factor); } else { return
         * getShippingCostBigDecimal().divide(HUNDRED).multiply(getShippingCostTaxRate());
         * } } else { return new BigDecimal("0");
         * }
         */
    }

    /**
     * Setter method for the shipping cost. Be aware that shipping cost is
     * usually set by setShippingOptionUUID().
     * 
     * @param shippingCost the shippingCost to set
     */
    public void setShippingCostBigDecimal(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public double getTotalWeight() {
        BigDecimal totalWeight = getTotalWeightBigDecimal();
        if (totalWeight != null) {
            return totalWeight.doubleValue();
        }
        return 0;
    }

    public BigDecimal getTotalWeightBigDecimal() {
        BigDecimal currItemWeight, totalWeight = new BigDecimal("0");
        Iterator<ShoppingCartItem> itemsIter = getCartItems().iterator();
        ShoppingCartItem currItem;
        while (itemsIter.hasNext()) {
            currItem = itemsIter.next();
            currItemWeight = currItem.getItemWeight();
            if (currItemWeight != null && currItemWeight.doubleValue() > 0) {
                totalWeight = totalWeight.add(currItemWeight);
            }
        }
        return totalWeight;
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
     * @param taxIncluded the taxIncluded to set
     */
    public void setTaxIncluded(Boolean taxIncluded) {
        this.taxIncluded = taxIncluded;
    }

    /**
     * @return the paymentDate
     */
    public Date getPaymentDate() {
        return paymentDate;
    }

    /**
     * @param paymentDate the paymentDate to set
     */
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * @return the paymentType
     */
    public String getPaymentType() {
        return paymentType;
    }

    /**
     * @param paymentType the paymentType to set
     */
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * @return the paymentID
     */
    public String getPaymentID() {
        return paymentID;
    }

    /**
     * @param paymentID the paymentID to set
     */
    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    /**
     * @return the taxFree
     */
    public Boolean getTaxFree() {
        return taxFree;
    }

    /**
     * @param taxFree the taxFree to set
     */
    public void setTaxFree(Boolean taxFree) {
        this.taxFree = taxFree;
    }

    /**
     * @return the shippingOptionUUID
     */
    public String getShippingOptionUUID() {
        return shippingOptionUUID;
    }

    /**
     * @param shippingOptionUUID the shippingOptionUUID to set
     */
    public void setShippingOptionUUID(String shippingOptionUUID) {
        this.shippingOptionUUID = shippingOptionUUID;
        if (StringUtils.isNotBlank(shippingOptionUUID)) {
            Content shippingOption = ContentUtil.getContentByUUID("data", shippingOptionUUID);
            if (shippingOption != null) {
                setShippingCostBigDecimal(ShopUtil.getShippingPriceForOptionBigDecimal(shippingOption, this));
                setShippingCostTaxIncluded(NodeDataUtil.getBoolean(shippingOption, "taxIncluded", false));
                setShippingOptionTitle(NodeDataUtil.getString(shippingOption, "title"));
                Content shippingOptionTaxCategory = ContentUtil.getContentByUUID("data", NodeDataUtil.getString(shippingOption, "taxCategoryUUID"));
                if (shippingOptionTaxCategory != null && shippingOptionTaxCategory.getNodeData("tax").isExist()) {
                    setShippingCostTaxRate(new BigDecimal("" + shippingOptionTaxCategory.getNodeData("tax").getDouble()));
                }
                return;
            }
            setShippingCostBigDecimal(shippingCost);
        }
        setShippingCostBigDecimal(null);
    }

    public BigDecimal getCartTotalInclTaxBigDecimal() {
        BigDecimal total = getNetItemsTotalInclTaxBigDecimal();
        if (total != null) {
            BigDecimal shipping = getShippingCostInclTaxBigDecimal();
            if (shipping != null) {
                total = total.add(shipping);
            }
        }
        return total;
    }

    public double getCartTotalInclTax() {
        BigDecimal total = getCartTotalInclTaxBigDecimal();
        if (total != null) {
            return total.doubleValue();
        }
        return 0;
    }

    public BigDecimal getCartTotalExclTaxBigDecimal() {
        BigDecimal total = getNetItemsTotalExclTaxBigDecimal();
        if (total != null) {
            total.add(getShippingCostExclTaxBigDecimal());
        }
        return total;
    }

    public double getCartTotalExclTax() {
        BigDecimal total = getCartTotalExclTaxBigDecimal();
        if (total != null) {
            return total.doubleValue();
        }
        return 0;
    }

    public BigDecimal getCartTaxBigDecimal() {
        BigDecimal total = getItemTaxTotalBigDecimal();
        if (total != null) {
            total = total.add(getShippingCostTax());
        }
        return total;
    }

    public double getCartTax() {
        BigDecimal total = getCartTaxBigDecimal();
        if (total != null) {
            return total.doubleValue();
        }
        return 0;
    }

    /**
     * @return the shippingCostTaxIncluded
     */
    public Boolean getShippingCostTaxIncluded() {
        return shippingCostTaxIncluded;
    }

    /**
     * @param shippingCostTaxIncluded the shippingCostTaxIncluded to set
     */
    public void setShippingCostTaxIncluded(Boolean shippingCostTaxIncluded) {
        this.shippingCostTaxIncluded = shippingCostTaxIncluded;
    }

    /**
     * @return the shippingCostTaxRate
     */
    public BigDecimal getShippingCostTaxRate() {
        return shippingCostTaxRate;
    }

    /**
     * @param shippingCostTaxRate the shippingCostTaxRate to set
     */
    public void setShippingCostTaxRate(BigDecimal shippingCostTaxRate) {
        this.shippingCostTaxRate = shippingCostTaxRate;
    }

    /**
     * @return the shippingOptionTitle
     */
    public String getShippingOptionTitle() {
        return shippingOptionTitle;
    }

    /**
     * @param shippingOptionTitle the shippingOptionTitle to set
     */
    public void setShippingOptionTitle(String shippingOptionTitle) {
        this.shippingOptionTitle = shippingOptionTitle;
    }

    /**
     * @return the formStateToken
     */
    public String getFormStateToken() {
        return formStateToken;
    }

    /**
     * @param formStateToken the formStateToken to set
     */
    public void setFormStateToken(String formStateToken) {
        this.formStateToken = formStateToken;
    }
}
