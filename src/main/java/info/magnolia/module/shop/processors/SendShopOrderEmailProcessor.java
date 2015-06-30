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

import java.util.Map;

import javax.jcr.Node;

import info.magnolia.context.MgnlContext;
import info.magnolia.module.form.processors.FormProcessorFailedException;
import info.magnolia.module.form.processors.SendContactEMailProcessor;
import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.util.ShopUtil;

/**
 * Default processor to send an email to customer with all the data from his order.
 * @author tmiyar
 *
 */
public class SendShopOrderEmailProcessor extends SendContactEMailProcessor{

    @Override
    public void internalProcess(Node content, Map<String, Object> parameters)
            throws FormProcessorFailedException {
        try {
            //add current shopping cart to the parameters map
            String cartId = (String) MgnlContext.getAttribute("cartId");
            DefaultShoppingCartImpl cart = (DefaultShoppingCartImpl) ShopUtil.getShoppingCart();
            if (cart == null) {
                throw new FormProcessorFailedException("cart.not.found");
                
            }
            
            parameters.put("cart",cart);
            parameters.put("cartId",cartId);
            super.internalProcess(content, parameters);
        } catch (Exception e) {
            throw new FormProcessorFailedException("Error while proccessing your shopping cart");
        }
    }
    
}
