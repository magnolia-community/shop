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
package info.magnolia.module.shop.processors;

import info.magnolia.cms.core.Content;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.form.processors.AbstractFormProcessor;
import info.magnolia.module.form.processors.FormProcessorFailedException;
import info.magnolia.module.ocm.ext.MgnlConfigMapperImpl;
import info.magnolia.module.ocm.ext.MgnlObjectConverterImpl;
import info.magnolia.module.shop.ShopConfiguration;
import info.magnolia.module.shop.ShopModule;
import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.util.ShopUtil;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.atomictypeconverter.impl.DefaultAtomicTypeConverterProvider;
import org.apache.jackrabbit.ocm.manager.cache.impl.RequestObjectCacheImpl;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.manager.objectconverter.impl.ProxyManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveAndConfirmFormProcessor extends AbstractFormProcessor {
  private static Logger log = LoggerFactory.getLogger(SaveAndConfirmFormProcessor.class);
  @Override
  protected void internalProcess(Content content, Map<String, Object> parameters)
      throws FormProcessorFailedException {
    
    try {
        DefaultShoppingCartImpl cart = (DefaultShoppingCartImpl) ShopUtil.getShoppingCart();
        if (cart == null) {
            log.error("Shopping cart could not be found!");
            throw new FormProcessorFailedException("cart.not.found");
            
        }
        loadCustomerData(cart, parameters);
        HttpServletRequest request = MgnlContext.getWebContext().getRequest();
        cart.setOrderDate(new Date());
        cart.setUserIP(request.getRemoteAddr() + ":" + request.getRemotePort());
    
        // NEW: Save via OCM
        Mapper mapper = new MgnlConfigMapperImpl();
        RequestObjectCacheImpl requestObjectCache = new RequestObjectCacheImpl();
        DefaultAtomicTypeConverterProvider converterProvider = new DefaultAtomicTypeConverterProvider();
        MgnlObjectConverterImpl oc = new MgnlObjectConverterImpl(mapper, converterProvider, new ProxyManagerImpl(), requestObjectCache);
        ObjectContentManager ocm = new ObjectContentManagerImpl(MgnlContext.getHierarchyManager("data").getWorkspace().getSession(), mapper);
        ((ObjectContentManagerImpl) ocm).setObjectConverter(oc);
        ((ObjectContentManagerImpl) ocm).setRequestObjectCache(requestObjectCache);
    
        if (StringUtils.isBlank(cart.getUuid())) {
            // Cart has not been saved before (this would most likely be the standard case)
            // Set the parent path according to the shop configuration
            ShopConfiguration shopConfiguration = ShopModule.getInstance().getCurrentShopConfiguration(ShopUtil.getShopName());
            cart.setParentPath(shopConfiguration.getShopDataRootPath() + "/" + shopConfiguration.getCartsFolderName());
            ocm.insert(cart);
            ocm.save();
            MgnlContext.setAttribute("cartId", cart.getName(), Context.SESSION_SCOPE);
            
            // @TODO: did ocm set the uuid and path? If not: How can we do that efficiently?
            log.debug("UUID of newly inserted shopping cart: " + cart.getUuid());
            log.debug("Path of newly inserted shopping cart: " + cart.getPath());
        }
    } catch (Exception e) {
        //initialize new cart
        MgnlContext.getWebContext().getRequest().getSession().removeAttribute("shoppingCart");
        ShopUtil.setShoppingCartInSession();
        throw new FormProcessorFailedException("Error while proccessing your shopping cart");
    }
    
  }
  protected void loadCustomerData(DefaultShoppingCartImpl cart, Map<String, Object> parameters ) {
    //billing address
    cart.setBillingAddressCompany((String) parameters.get("billingAddressCompany"));
    cart.setBillingAddressCompany2((String) parameters.get("billingAddressCompany2"));
    cart.setBillingAddressFirstname((String) parameters.get("billingAddressFirstname"));
    cart.setBillingAddressLastname((String) parameters.get("billingAddressLastname"));
    cart.setBillingAddressSex((String) parameters.get("billingAddressSex"));
    cart.setBillingAddressTitle((String) parameters.get("billingAddressTitle"));
    cart.setBillingAddressStreet((String) parameters.get("billingAddressStreet"));
    cart.setBillingAddressStreet2((String) parameters.get("billingAddressStreet2"));
    cart.setBillingAddressZip((String) parameters.get("billingAddressZip"));
    cart.setBillingAddressCity((String) parameters.get("billingAddressCity"));
    cart.setBillingAddressState((String) parameters.get("billingAddressState"));
    cart.setBillingAddressCountry((String) parameters.get("billingAddressCountry"));
    cart.setBillingAddressPhone((String) parameters.get("billingAddressPhone"));
    cart.setBillingAddressMobile((String) parameters.get("billingAddressMobile"));
    cart.setBillingAddressMail((String) parameters.get("billingAddressMail"));
    //shipping address
    if(StringUtils.isEmpty((String) parameters.get("shippingSameAsBilling"))) {
      cart.setShippingAddressCompany((String) parameters.get("shippingAddressCompany"));
      cart.setShippingAddressCompany2((String) parameters.get("shippingAddressCompany2"));
      cart.setShippingAddressFirstname((String) parameters.get("shippingAddressFirstname"));
      cart.setShippingAddressLastname((String) parameters.get("shippingAddressLastname"));
      cart.setShippingAddressSex((String) parameters.get("shippingAddressSex"));
      cart.setShippingAddressTitle((String) parameters.get("shippingAddressTitle"));
      cart.setShippingAddressStreet((String) parameters.get("shippingAddressStreet"));
      cart.setShippingAddressStreet2((String) parameters.get("shippingAddressStreet2"));
      cart.setShippingAddressZip((String) parameters.get("shippingAddressZip"));
      cart.setShippingAddressCity((String) parameters.get("shippingAddressCity"));
      cart.setShippingAddressState((String) parameters.get("shippingAddressState"));
      cart.setShippingAddressCountry((String) parameters.get("shippingAddressCountry"));
      cart.setShippingAddressPhone((String) parameters.get("shippingAddressPhone"));
      cart.setShippingAddressMobile((String) parameters.get("shippingAddressMobile"));
      cart.setShippingAddressMail((String) parameters.get("shippingAddressMail"));
    }
  }

}
