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
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import info.magnolia.shop.beans.DefaultShoppingCartImpl;

/**
 * Bean representation for the shopping cart without cart items.
 */
@ApiModel(description = "")
public class ShoppingCart extends DefaultShoppingCartImpl {
  
  private Boolean shippingSameAsBilling = null;
  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressCompany")
  public String getOrderAddressCompany() {
    return super.getOrderAddressCompany();
  }
  public void setOrderAddressCompany(String orderAddressCompany) {
    super.setOrderAddressCompany(orderAddressCompany);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressCompany2")
  public String getOrderAddressCompany2() {
    return super.getOrderAddressCompany2();
  }
  public void setOrderAddressCompany2(String orderAddressCompany2) {
    super.setOrderAddressCompany2(orderAddressCompany2);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressFirstname")
  public String getOrderAddressFirstname() {
    return super.getOrderAddressFirstname();
  }
  public void setOrderAddressFirstname(String orderAddressFirstname) {
    super.setOrderAddressFirstname(orderAddressFirstname);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressLastname")
  public String getOrderAddressLastname() {
    return super.getOrderAddressLastname();
  }
  public void setOrderAddressLastname(String orderAddressLastname) {
    super.setOrderAddressLastname(orderAddressLastname);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressSex")
  public String getOrderAddressSex() {
    return super.getOrderAddressSex();
  }
  public void setOrderAddressSex(String orderAddressSex) {
    super.setOrderAddressSex(orderAddressSex);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressTitle")
  public String getOrderAddressTitle() {
    return super.getOrderAddressTitle();
  }
  public void setOrderAddressTitle(String orderAddressTitle) {
    super.setOrderAddressTitle(orderAddressTitle);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressStreet")
  public String getOrderAddressStreet() {
    return super.getOrderAddressStreet();
  }
  public void setOrderAddressStreet(String orderAddressStreet) {
    super.setOrderAddressStreet(orderAddressStreet);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressStreet2")
  public String getOrderAddressStreet2() {
    return super.getOrderAddressStreet2();
  }
  public void setOrderAddressStreet2(String orderAddressStreet2) {
    super.setOrderAddressStreet2(orderAddressStreet2);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressZip")
  public String getOrderAddressZip() {
    return super.getOrderAddressZip();
  }
  public void setOrderAddressZip(String orderAddressZip) {
    super.setOrderAddressZip(orderAddressZip);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressCity")
  public String getOrderAddressCity() {
    return super.getOrderAddressCity();
  }
  public void setOrderAddressCity(String orderAddressCity) {
    super.setOrderAddressCity(orderAddressCity);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressState")
  public String getOrderAddressState() {
    return super.getOrderAddressState();
  }
  public void setOrderAddressState(String orderAddressState) {
    super.setOrderAddressState(orderAddressState);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressCountry")
  public String getOrderAddressCountry() {
    return super.getOrderAddressCountry();
  }
  public void setOrderAddressCountry(String orderAddressCountry) {
    super.setOrderAddressCountry(orderAddressCountry);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressPhone")
  public String getOrderAddressPhone() {
    return super.getOrderAddressPhone();
  }
  public void setOrderAddressPhone(String orderAddressPhone) {
    super.setOrderAddressPhone(orderAddressPhone);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressMobile")
  public String getOrderAddressMobile() {
    return super.getOrderAddressMobile();
  }
  public void setOrderAddressMobile(String orderAddressMobile) {
    super.setOrderAddressMobile(orderAddressMobile);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("orderAddressMail")
  public String getOrderAddressMail() {
    return super.getOrderAddressMail();
  }
  public void setOrderAddressMail(String orderAddressMail) {
    super.setOrderAddressMail(orderAddressMail);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressCompany")
  public String getBillingAddressCompany() {
    return super.getBillingAddressCompany();
  }
  public void setBillingAddressCompany(String billingAddressCompany) {
    super.setBillingAddressCompany(billingAddressCompany);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressCompany2")
  public String getBillingAddressCompany2() {
    return super.getBillingAddressCompany2();
  }
  public void setBillingAddressCompany2(String billingAddressCompany2) {
    super.setBillingAddressCompany2(billingAddressCompany2);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressFirstname")
  public String getBillingAddressFirstname() {
    return super.getBillingAddressFirstname();
  }
  public void setBillingAddressFirstname(String billingAddressFirstname) {
    super.setBillingAddressFirstname(billingAddressFirstname);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressLastname")
  public String getBillingAddressLastname() {
    return super.getBillingAddressLastname();
  }
  public void setBillingAddressLastname(String billingAddressLastname) {
    super.setBillingAddressLastname(billingAddressLastname);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressSex")
  public String getBillingAddressSex() {
    return super.getBillingAddressSex();
  }
  public void setBillingAddressSex(String billingAddressSex) {
    super.setBillingAddressSex(billingAddressSex);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressTitle")
  public String getBillingAddressTitle() {
    return super.getBillingAddressTitle();
  }
  public void setBillingAddressTitle(String billingAddressTitle) {
    super.setBillingAddressTitle(billingAddressTitle);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressStreet")
  public String getBillingAddressStreet() {
    return super.getBillingAddressStreet();
  }
  public void setBillingAddressStreet(String billingAddressStreet) {
    super.setBillingAddressStreet(billingAddressStreet);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressStreet2")
  public String getBillingAddressStreet2() {
    return super.getBillingAddressStreet2();
  }
  public void setBillingAddressStreet2(String billingAddressStreet2) {
    super.setBillingAddressStreet2(billingAddressStreet2);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressZip")
  public String getBillingAddressZip() {
    return super.getBillingAddressZip();
  }
  public void setBillingAddressZip(String billingAddressZip) {
    super.setBillingAddressZip(billingAddressZip);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressCity")
  public String getBillingAddressCity() {
    return super.getBillingAddressCity();
  }
  public void setBillingAddressCity(String billingAddressCity) {
    super.setBillingAddressCity(billingAddressCity);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressState")
  public String getBillingAddressState() {
    return super.getBillingAddressState();
  }
  public void setBillingAddressState(String billingAddressState) {
    super.setBillingAddressState(billingAddressState);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressContry")
  public String getBillingAddressCountry() {
    return super.getBillingAddressCountry();
  }
  public void setBillingAddressContry(String billingAddressCountry) {
    super.setBillingAddressCountry(billingAddressCountry);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressPhone")
  public String getBillingAddressPhone() {
    return super.getBillingAddressPhone();
  }
  public void setBillingAddressPhone(String billingAddressPhone) {
    super.setBillingAddressPhone(billingAddressPhone);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressMobile")
  public String getBillingAddressMobile() {
    return super.getBillingAddressMobile();
  }
  public void setBillingAddressMobile(String billingAddressMobile) {
    super.setBillingAddressMobile(billingAddressMobile);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("billingAddressMail")
  public String getBillingAddressMail() {
    return super.getBillingAddressMail();
  }
  public void setBillingAddressMail(String billingAddressMail) {
    super.setBillingAddressMail(billingAddressMail);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressCompany")
  public String getShippingAddressCompany() {
    return super.getShippingAddressCompany();
  }
  public void setShippingAddressCompany(String shippingAddressCompany) {
    super.setShippingAddressCompany(shippingAddressCompany);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressCompany2")
  public String getShippingAddressCompany2() {
    return super.getShippingAddressCompany2();
  }
  public void setShippingAddressCompany2(String shippingAddressCompany2) {
    super.setShippingAddressCompany2(shippingAddressCompany2);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressFirstname")
  public String getShippingAddressFirstname() {
    return super.getShippingAddressFirstname();
  }
  public void setShippingAddressFirstname(String shippingAddressFirstname) {
    super.setShippingAddressFirstname(shippingAddressFirstname);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressLastname")
  public String getShippingAddressLastname() {
    return super.getShippingAddressLastname();
  }
  public void setShippingAddressLastname(String shippingAddressLastname) {
    super.setShippingAddressLastname(shippingAddressLastname);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressSex")
  public String getShippingAddressSex() {
    return super.getShippingAddressSex();
  }
  public void setShippingAddressSex(String shippingAddressSex) {
    super.setShippingAddressSex(shippingAddressSex);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressTitle")
  public String getShippingAddressTitle() {
    return super.getShippingAddressTitle();
  }
  public void setShippingAddressTitle(String shippingAddressTitle) {
    super.setShippingAddressTitle(shippingAddressTitle);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressStreet")
  public String getShippingAddressStreet() {
    return super.getShippingAddressStreet();
  }
  public void setShippingAddressStreet(String shippingAddressStreet) {
    super.setShippingAddressStreet(shippingAddressStreet);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressStreet2")
  public String getShippingAddressStreet2() {
    return super.getShippingAddressStreet2();
  }
  public void setShippingAddressStreet2(String shippingAddressStreet2) {
    super.setShippingAddressStreet2(shippingAddressStreet2);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressZip")
  public String getShippingAddressZip() {
    return super.getShippingAddressZip();
  }
  public void setShippingAddressZip(String shippingAddressZip) {
    super.setShippingAddressZip(shippingAddressZip);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressCity")
  public String getShippingAddressCity() {
    return super.getShippingAddressCity();
  }
  public void setShippingAddressCity(String shippingAddressCity) {
    super.setShippingAddressCity(shippingAddressCity);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressState")
  public String getShippingAddressState() {
    return super.getShippingAddressState();
  }
  public void setShippingAddressState(String shippingAddressState) {
    super.setShippingAddressState(shippingAddressState);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressContry")
  public String getShippingAddressCountry() {
    return super.getShippingAddressCountry();
  }
  public void setShippingAddressCountry(String shippingAddressCountry) {
    super.setShippingAddressCountry(shippingAddressCountry);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressPhone")
  public String getShippingAddressPhone() {
    return super.getShippingAddressPhone();
  }
  public void setShippingAddressPhone(String shippingAddressPhone) {
    super.setShippingAddressPhone(shippingAddressPhone);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressMobile")
  public String getShippingAddressMobile() {
    return super.getShippingAddressMobile();
  }
  public void setShippingAddressMobile(String shippingAddressMobile) {
    super.setShippingAddressMobile(shippingAddressMobile);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingAddressMail")
  public String getShippingAddressMail() {
    return super.getShippingAddressMail();
  }
  public void setShippingAddressMail(String shippingAddressMail) {
    super.setShippingAddressMail(shippingAddressMail);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("termsAccepted")
  public Boolean getTermsAccepted() {
    return super.getTermsAccepted();
  }
  public void setTermsAccepted(Boolean termsAccepted) {
    super.setTermsAccepted(termsAccepted);
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("shippingSameAsBilling")
  public Boolean getShippingSameAsBilling() {
    return shippingSameAsBilling;
  }
  public void setShippingSameAsBilling(Boolean shippingSameAsBilling) {
    this.shippingSameAsBilling = shippingSameAsBilling;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("customerNumber")
  public String getCustomerNumber() {
    return super.getCustomerNumber();
  }
  public void setCustomerNumber(String customerNumber) {
    super.setCustomerNumber(customerNumber);
  }
}
