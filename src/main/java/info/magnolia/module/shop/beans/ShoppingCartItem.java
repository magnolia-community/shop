/**
 * This file Copyright (c) 2010-2013 Magnolia International
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

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.shop.util.ShopUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.fastforward.magnolia.ocm.beans.OCMBean;

/**
 * Default shopping cart item bean containing all the product info. The item
 * also calculates the tax and discount prices.<br/>
 * NOTE: If you do the math in Java using doubles (or floats as a matter of
 * fact) you'll end up with something like 126.8999999999999 instead of 126.9.
 * According to this article
 * (http://java.sun.com/mailers/techtips/corejava/2007/tt0707.html#2) Sun
 * suggests to use BigDescimal objects. Since we need to store Double objects in
 * the repository, this leaves us with a bit of a conversion mess... and when
 * converting from double/float to BigDecimal apparently the only save way to go
 * is via a String (i.e. new BigDecimal("126.9")!
 *
 * @author will
 */
public class ShoppingCartItem extends OCMBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(ShoppingCartItem.class);
    private String productUUID;
    private String productNumber;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal itemDiscountRate;
    private BigDecimal itemTaxRate;
    private BigDecimal unitWeight;
    private BigDecimal unitHeight;
    private BigDecimal unitWidth;
    private BigDecimal unitDepth;
    private String shoppingCartUUID;
    private DefaultShoppingCartImpl cart;
    private String productTitle;
    private String productSubTitle;
    private String productDescription;
    private Map<String,CartItemOption> options;

    public ShoppingCartItem(DefaultShoppingCartImpl cart, Node product, int quantity, Node productPrice, Map<String,CartItemOption> options) {
        this(cart, product, quantity, productPrice);
        this.setOptions(options);
    }

    public ShoppingCartItem(DefaultShoppingCartImpl cart, Node product, int quantity, Node productPrice) {
        super();
        this.setCart(cart);
        this.setProduct(product);
        this.setQuantity(quantity);
        try {
            this.setProductPrice(productPrice);
        } catch (ValueFormatException e) {
            log.error("Property price is not a Double", e);
        } catch (RepositoryException e) {
            log.error("Can't find property price in product", e);
        }
    }

    public ShoppingCartItem(DefaultShoppingCartImpl cart, String productUUID, int quantity, double unitPrice, Map<String,CartItemOption> options) {
        this(cart, productUUID, quantity, unitPrice);
        this.setOptions(options);
    }

    public ShoppingCartItem(DefaultShoppingCartImpl cart, String productUUID, int quantity, double unitPrice) {
        super();
        this.setCart(cart);
        Node product = null;
        try {
            product = ShopUtil.wrapWithI18n(NodeUtil.getNodeByIdentifier("data", productUUID));
        } catch (RepositoryException e) {
            log.error("Can't find product with uuid" + productUUID);
        }
        this.setProduct(product);
        this.setQuantity(quantity);
        this.setUnitPrice(unitPrice);
    }

    public Node getProduct() {
        if (StringUtils.isNotBlank(productUUID)) {
            try {
                return NodeUtil.getNodeByIdentifier("data", productUUID);
            } catch (RepositoryException e) {
                log.error("Can't find Product with uuid" + productUUID);
            }
        }
        return null;
    }

    public void setProduct(Node product) {
        if (product != null) {
            try {
                this.productUUID = product.getIdentifier();
            } catch (RepositoryException e1) {
                log.error("Cant get the product id for " + this.productUUID,e1);
            }
            log.debug("setting product " + product + " in cart item");
            log.debug("product number: " + PropertyUtil.getString(product, "name"));
            setProductNumber(PropertyUtil.getString(product, "name"));

            setProductTitle(PropertyUtil.getString(product, "title"));
            setProductSubTitle(PropertyUtil.getString(product, "productDescription1"));
            setProductDescription(PropertyUtil.getString(product, "productDescription2"));
            try {
                if (product.hasProperty("taxCategoryUUID")) {
                    Node taxCategory = NodeUtil.getNodeByIdentifier("data", PropertyUtil.getString(product, "taxCategoryUUID"));
                    if (taxCategory != null && taxCategory.hasProperty("tax")) {
                        setItemTaxRate(new BigDecimal(PropertyUtil.getString(taxCategory, "tax")));
                    }
                }
            } catch (RepositoryException e) {
                log.error("Cant read tax category for " + this.productUUID,e);
            }
            String value;
            value = PropertyUtil.getString(product, "weight", "");
            if (StringUtils.isNotBlank(value)) {
                setUnitWeight(new BigDecimal(value));
            }
            value = PropertyUtil.getString(product, "height", "");
            if (StringUtils.isNotBlank(value)) {
                setUnitHeight(new BigDecimal(value));
            }
            value = PropertyUtil.getString(product, "width", "");
            if (StringUtils.isNotBlank(value)) {
                setUnitWidth(new BigDecimal(value));
            }
            value = PropertyUtil.getString(product, "depth", "");
            if (StringUtils.isNotBlank(value)) {
                setUnitDepth(new BigDecimal(value));
            }

        } else {
            this.productUUID = null;
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice.doubleValue();
    }

    /**
     * For shops with prices inclusive tax which need to display the price
     * exclusive tax too.
     * @return unit price minus tax for prices incl. tax or unit price for prices excl. tax.
     */
    public double getUnitPriceExclTax() {
        if (getCart().getTaxIncluded()) {
            // substrat tax
            BigDecimal oneHundred = new BigDecimal("100");
            BigDecimal taxFactor = oneHundred.divide(getItemTaxRate().add(oneHundred), 10, RoundingMode.HALF_UP);
            BigDecimal unitPriceExclTax = unitPrice.multiply(taxFactor);
            return unitPriceExclTax.doubleValue();
        } else {
            return getUnitPrice();
        }
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = new BigDecimal("" + unitPrice);
    }

    public void setProductPrice(Node productPrice) throws ValueFormatException, RepositoryException {
        if (productPrice != null && productPrice.hasProperty("price")) {
            this.setUnitPrice(PropertyUtil.getProperty(productPrice, "price").getDouble());
        }
    }

    public String getProductNumber() {
        return productNumber;
    }

    /**
     * @param productNumber
     *          the productNumber to set
     */
    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
        log.debug("product number: " + this.productNumber);
    }

    public String getProductUUID() {
        return productUUID;
    }

    public String getShoppingCartUUID() {
        return shoppingCartUUID;
    }

    public void setShoppingCartUUID(String shoppingCartUUID) {
        this.shoppingCartUUID = shoppingCartUUID;
    }

    public DefaultShoppingCartImpl getCart() {
        return cart;
    }

    public void setCart(DefaultShoppingCartImpl cart) {
        this.cart = cart;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductSubTitle() {
        return productSubTitle;
    }

    public void setProductSubTitle(String productSubTitle) {
        this.productSubTitle = productSubTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getItemTotal() {
        BigDecimal total = getItemTotalBigDecimal();
        if (total != null) {
            return total.doubleValue();
        } else {
            return 0;
        }
    }

    public BigDecimal getItemTotalBigDecimal() {
        if (unitPrice != null) {
            BigDecimal total = unitPrice.multiply(new BigDecimal("" + quantity));
            if (getItemDiscountRate() != null && getItemDiscountRate().floatValue() > 0 && getItemDiscountRate().floatValue() <= 100) {
                total = total.multiply(new BigDecimal("100").subtract(getItemDiscountRate())).divide(new BigDecimal("100"));
            }
            return total;
        } else {
            return null;
        }
    }

    public BigDecimal getItemTaxBigDecimal() {
        BigDecimal total = getItemTotalBigDecimal();
        if (total != null && getItemTaxRate() != null) {
            BigDecimal oneHundred = new BigDecimal("100");
            if (getCart().getTaxIncluded()) {
                // total = price including tax
                BigDecimal taxFactor = oneHundred.divide(getItemTaxRate().add(oneHundred), 10, RoundingMode.HALF_UP);
                return total.subtract(total.multiply(taxFactor));
            } else {
                // toal = price excluding tax
                BigDecimal taxFactor = getItemTaxRate().add(oneHundred).divide(oneHundred, 10, RoundingMode.HALF_UP);
                return total.multiply(taxFactor).subtract(total);
            }
        }
        return null;
    }

    public double getItemTax() {
        BigDecimal tax = getItemTaxBigDecimal();
        if (tax != null) {
            return tax.doubleValue();
        } else {
            return 0;
        }
    }

    public BigDecimal getItemTotalExclTaxBigDecimal() {
        BigDecimal total = getItemTotalBigDecimal();
        BigDecimal tax = getItemTaxBigDecimal();
        if (total != null) {
            if (tax != null) {
                if (getCart().getTaxIncluded()) {
                    return total.subtract(tax);
                }
            }
            return total;
        }
        return null;
    }

    public double getItemTotalExclTax() {
        BigDecimal total = getItemTotalExclTaxBigDecimal();
        if (total != null) {
            return total.doubleValue();
        }
        return 0;
    }

    public BigDecimal getItemTotalInclTaxBigDecimal() {
        BigDecimal total = getItemTotalBigDecimal();
        BigDecimal tax = getItemTaxBigDecimal();
        if (total != null) {
            if (tax != null) {
                if (!getCart().getTaxIncluded()) {
                    return total.add(tax);
                }
            }
            return total;
        }
        return null;
    }

    public double getItemTotalInclTax() {
        BigDecimal total = getItemTotalInclTaxBigDecimal();
        if (total != null) {
            return total.doubleValue();
        }
        return 0;
    }

    /**
     * @return the itemDiscountRate
     */
    public BigDecimal getItemDiscountRate() {
        return itemDiscountRate;
    }

    /**
     * @param itemDiscountRate the itemDiscountRate to set
     */
    public void setItemDiscountRate(BigDecimal itemDiscountRate) {
        this.itemDiscountRate = itemDiscountRate;
    }

    /**
     * @return the itemTaxRate
     */
    public BigDecimal getItemTaxRate() {
        return itemTaxRate;
    }

    /**
     * @param itemTaxRate the itemTaxRate to set
     */
    public void setItemTaxRate(BigDecimal itemTaxRate) {
        this.itemTaxRate = itemTaxRate;
    }

    /**
     * @return the options
     */
    public Map<String,CartItemOption> getOptions() {
        return options;
    }

    /**
     * @param options the options to set
     */
    public void setOptions(Map<String,CartItemOption> options) {
        this.options = options;
    }

    public boolean isOptionsMatching(Map<String,CartItemOption> options) {
        if ((options == null || options.isEmpty()) && (this.options == null || this.options.isEmpty())) {
            // both option sets are empty (or null) -> match!
            return true;
        } else if (options != null && this.options != null) {
            if (options.size() == this.options.size()) {
                // same amount of options -> we need to compare them one by one
                Iterator<String> keys = options.keySet().iterator();
                String currKey;
                while (keys.hasNext()) {
                    currKey = keys.next();
                    if (!this.options.containsKey(currKey) || !options.get(currKey).equals(this.options.get(currKey))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @return the unitWeight
     */
    public BigDecimal getUnitWeight() {
        return unitWeight;
    }

    /**
     * @param unitWeight the unitWeight to set
     */
    public void setUnitWeight(BigDecimal unitWeight) {
        this.unitWeight = unitWeight;
    }

    /**
     *
     * @return the total weight of this cart item or null if no weight was
     * specified
     */
    public BigDecimal getItemWeight() {
        if (unitWeight != null && unitWeight.doubleValue() > 0) {
            return unitWeight.multiply(new BigDecimal(quantity));
        }
        return null;
    }

    /**
     * @return the unitHeight
     */
    public BigDecimal getUnitHeight() {
        return unitHeight;
    }

    /**
     * @param unitHeight the unitHeight to set
     */
    public void setUnitHeight(BigDecimal unitHeight) {
        this.unitHeight = unitHeight;
    }

    /**
     * @return the unitWidth
     */
    public BigDecimal getUnitWidth() {
        return unitWidth;
    }

    /**
     * @param unitWidth the unitWidth to set
     */
    public void setUnitWidth(BigDecimal unitWidth) {
        this.unitWidth = unitWidth;
    }

    /**
     * @return the unitDepth
     */
    public BigDecimal getUnitDepth() {
        return unitDepth;
    }

    /**
     * @param unitDepth the unitDepth to set
     */
    public void setUnitDepth(BigDecimal unitDepth) {
        this.unitDepth = unitDepth;
    }
}
