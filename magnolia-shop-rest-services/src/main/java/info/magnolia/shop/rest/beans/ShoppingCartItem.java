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
package info.magnolia.shop.rest.beans;


import com.fasterxml.jackson.annotation.JsonProperty;
import info.magnolia.shop.beans.DefaultShoppingCartImpl;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Bean representation of the cart item.
 */
@ApiModel(description = "")
public class ShoppingCartItem extends info.magnolia.shop.beans.ShoppingCartItem {
  
//  private String name = null;
//  private String productUUID = null;
//  private String productNumber = null;
//  private Integer quantity = null;
//  private Float unitPrice = null;
//  private Float unitPriceExclTax = null;
//  private Float itemDiscountRate = null;
//  private Float itemTaxRate = null;
//  private Float itemTotal = null;
//  private Float itemTax = null;
//  private Float itemTotalExclTax = null;
//  private Float itemTotalInclTax = null;
//  private String productTitle = null;
//  private String productSubTitle = null;
//  private String productDescription = null;

  public ShoppingCartItem(DefaultShoppingCartImpl cart, String productUUID, int quantity, double unitPrice) {
    super(cart, productUUID, quantity, unitPrice);
  }


  /**
   * unique id of the item _within_ the cart.
   **/
  @ApiModelProperty(value = "unique id of the item _within_ the cart")
  @JsonProperty("name")
  public String getName() {
    return super.getName();
  }

  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("productUUID")
  public String getProductUUID() {
    return super.getProductUUID();
  }
  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("productNumber")
  public String getProductNumber() {
    return super.getProductNumber();
  }
  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("quantity")
  public int getQuantity() {
    return super.getQuantity();
  }
  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("unitPrice")
  public Double getUnitPrice() {
    return super.getUnitPrice();
  }
  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("unitPriceExclTax")
  public double getUnitPriceExclTax() {
    return super.getUnitPriceExclTax();
  }
  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("itemDiscountRate")
  public BigDecimal getItemDiscountRate() {
    return super.getItemDiscountRate();
  }
  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("itemTaxRate")
  public BigDecimal getItemTaxRate() {
    return super.getItemTaxRate();
  }
  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("itemTotal")
  public double getItemTotal() {
    return super.getItemTotal();
  }
  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("itemTax")
  public double getItemTax() {
    return super.getItemTax();
  }
  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("itemTotalExclTax")
  public double getItemTotalExclTax() {
    return super.getItemTotalExclTax();
  }
  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("itemTotalInclTax")
  public double getItemTotalInclTax() {
    return super.getItemTotalInclTax();
  }
  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("productTitle")
  public String getProductTitle() {
    return super.getProductTitle();
  }
  
  /**
   * i.e. productDescription1
   **/
  @ApiModelProperty(value = "i.e. productDescription1")
  @JsonProperty("productSubTitle")
  public String getProductSubTitle() {
    return super.getProductSubTitle();
  }
  
  /**
   * i.e. productDescription2
   **/
  @ApiModelProperty(value = "i.e. productDescription2")
  @JsonProperty("productDescription")
  public String getProductDescription() {
    return super.getProductDescription();
  }
}
