/**
 * This file Copyright (c) 2003-2009 Magnolia International
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

import ch.fastforward.magnolia.crud.MgnlDataBean;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ShoppingCartItem extends MgnlDataBean implements Serializable {

  private static final long serialVersionUID = 1L;
  private static Logger log = LoggerFactory.getLogger(ShoppingCartItem.class);
  private String productUUID;
  private String productNumber;
  private int quantity;
  private BigDecimal unitPrice;
  private BigDecimal itemDiscountRate;
  private BigDecimal itemTaxRate;
  private String shoppingCartUUID;
  private DefaultShoppingCart cart;
  private String productTitle;
  private String productSubTitle;
  private String productDescription;

  public ShoppingCartItem(DefaultShoppingCart cart, Content product, int quantity, Content productPrice) {
    super();
    this.setCart(cart);
    this.setProduct(product);
    this.setQuantity(quantity);
    this.setProductPrice(productPrice);
  }

  public ShoppingCartItem(DefaultShoppingCart cart, String productUUID, int quantity, double unitPrice) {
    super();
    this.setCart(cart);
    Content product = ContentUtil.getContentByUUID("data", productUUID);
    this.setProduct(product);
    this.setQuantity(quantity);
    this.setUnitPrice(unitPrice);
  }

  public Content getProduct() {
    if (StringUtils.isNotBlank(productUUID)) {
      return ContentUtil.getContentByUUID("data", productUUID);
    }
    return null;
  }

  public void setProduct(Content product) {
    if (product != null) {
      this.productUUID = product.getUUID();
      log.debug("setting product " + product + " in cart item");
      log.debug("product number: " + NodeDataUtil.getString(product, "name"));
      setProductNumber(NodeDataUtil.getString(product, "name"));
      if (cart.getLanguage() != null) {
        setProductTitle(NodeDataUtil.getString(product, "title_" + cart.getLanguage()));
        setProductSubTitle(NodeDataUtil.getString(product, "productDescription1_" + cart.getLanguage()));
        setProductDescription(NodeDataUtil.getString(product, "productDescription2_" + cart.getLanguage()));
        if (product.getNodeData("taxCategoryUUID").isExist()) {
          Content taxCategory = ContentUtil
              .getContentByUUID("data", product.getNodeData("taxCategoryUUID").getString());
          if (taxCategory != null && taxCategory.getNodeData("tax").isExist()) {
            itemTaxRate = new BigDecimal(taxCategory.getNodeData("tax").getString());
          }
        }
      } else {
        log.error("Could not copy product description to cart item since no language was set in cart!");
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

  public void setUnitPrice(double unitPrice) {
    this.unitPrice = new BigDecimal("" + unitPrice);
  }

  public void setProductPrice(Content productPrice) {
    if (productPrice != null && productPrice.getNodeData("price").isExist()) {
      this.setUnitPrice(productPrice.getNodeData("price").getDouble());
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

  @Override
  public Map validate() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Map validate(String key) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public DefaultShoppingCart getCart() {
    return cart;
  }

  public void setCart(DefaultShoppingCart cart) {
    this.cart = cart;
    cart.addCartItem(this);
    this.setShoppingCartUUID(cart.getCartUUID());
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
      return (double) 0;
    }
  }

  public BigDecimal getItemTotalBigDecimal() {
    if (unitPrice != null) {
      BigDecimal total = unitPrice.multiply(new BigDecimal("" + quantity));
      if (itemDiscountRate != null && itemDiscountRate.floatValue() > 0 && itemDiscountRate.floatValue() <= 100) {
        total = total.multiply(new BigDecimal("100").subtract(itemDiscountRate)).divide(new BigDecimal("100"));
      }
      return total;
    } else {
      return null;
    }
  }

  public BigDecimal getItemTaxBigDecimal() {
    BigDecimal total = getItemTotalBigDecimal();
    if (total != null && itemTaxRate != null) {
      BigDecimal oneHundred = new BigDecimal("100");
      if (getCart().getTaxIncluded()) {
        // total = price including tax
        BigDecimal taxFactor = oneHundred.divide(itemTaxRate.add(oneHundred), 10, RoundingMode.HALF_UP);
        return total.subtract(total.multiply(taxFactor));
      } else {
        // toal = price excluding tax
        BigDecimal taxFactor = itemTaxRate.add(oneHundred).divide(oneHundred, 10, RoundingMode.HALF_UP);
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
      return (double) 0;
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
}
