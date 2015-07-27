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
package info.magnolia.module.shop.processors;

import ch.fastforward.magnolia.ocm.atomictypeconverter.MgnlAtomicTypeConverterProvider;
import ch.fastforward.magnolia.ocm.beans.OCMBean;
import ch.fastforward.magnolia.ocm.ext.MgnlConfigMapperImpl;
import ch.fastforward.magnolia.ocm.ext.MgnlObjectConverterImpl;
import info.magnolia.context.SystemContext;
import info.magnolia.module.form.processors.AbstractFormProcessor;
import info.magnolia.module.form.processors.FormProcessorFailedException;
import info.magnolia.module.shop.ShopConfiguration;
import info.magnolia.module.shop.accessors.ShopAccessor;
import info.magnolia.module.shop.beans.ShoppingCart;
import info.magnolia.module.shop.util.ShopUtil;

import java.util.Map;

import javax.jcr.Node;

import info.magnolia.objectfactory.Components;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.atomictypeconverter.impl.DefaultAtomicTypeConverterProvider;
import org.apache.jackrabbit.ocm.manager.cache.impl.RequestObjectCacheImpl;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.manager.objectconverter.impl.ProxyManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Saves the shoppingcart into data module using ocm.
 */
public class SaveAndConfirmFormProcessor extends AbstractFormProcessor {
    private static Logger log = LoggerFactory.getLogger(SaveAndConfirmFormProcessor.class);
    @Override
    protected void internalProcess(Node content, Map<String, Object> parameters)
            throws FormProcessorFailedException {
        String shopName = ShopUtil.getShopName();
        ShopConfiguration shopConfiguration = null;

        try {
            shopConfiguration = new ShopAccessor(shopName).getShopConfiguration();
        } catch (Exception e) {
            log.error("cant get shop configuration for " + shopName);
            throw new FormProcessorFailedException("configuration.not.found");
        }

        ShoppingCart cart = ShopUtil.getShoppingCart(shopName);
        if (cart == null) {
            log.error("Shopping cart could not be found!");
            throw new FormProcessorFailedException("cart.not.found");
        }
        cart.updateCartData(parameters);

        try {
            // NEW: Save via OCM
            Mapper mapper = new MgnlConfigMapperImpl();
            RequestObjectCacheImpl requestObjectCache = new RequestObjectCacheImpl();
            DefaultAtomicTypeConverterProvider converterProvider = new MgnlAtomicTypeConverterProvider();
            MgnlObjectConverterImpl oc = new MgnlObjectConverterImpl(mapper, converterProvider, new ProxyManagerImpl(), requestObjectCache);

            ObjectContentManager ocm = new ObjectContentManagerImpl(Components.getComponent(SystemContext.class).getJCRSession("shoppingCarts"), mapper);
            ((ObjectContentManagerImpl) ocm).setObjectConverter(oc);
            ((ObjectContentManagerImpl) ocm).setRequestObjectCache(requestObjectCache);

            if (StringUtils.isBlank(((OCMBean) cart).getUuid())) {
                // Cart has not been saved before (this would most likely be the standard case)
                // Set the parent path according to the shop configuration
                String parentPath = "/" + shopName;
                if (shopConfiguration != null) {
                    parentPath = shopConfiguration.getHierarchyStrategy().getCartParentPath(shopName);
                }
                ((OCMBean) cart).setParentPath(parentPath);
                ocm.insert(cart);
                ocm.save();
            } else {
                // @TODO: Should we handle the "update" case as well?
            }
            // MSHOP-194: resetting the shopping cart here instead of in the confirmation template.
            ShopUtil.resetShoppingCart(shopName);
        } catch (Exception e) {
            //initialize new cart
            // when a exception occurs, why would we want to reset the shopping cart?!
//            MgnlContext.removeAttribute(shopName + "_" + ShopUtil.ATTRIBUTE_SHOPPINGCART);
//            ShopUtil.setShoppingCartInSession(shopName);
            throw new FormProcessorFailedException("Error while proccessing your shopping cart");
        }

    }

}
