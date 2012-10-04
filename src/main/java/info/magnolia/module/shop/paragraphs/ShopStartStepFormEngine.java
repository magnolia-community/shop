/**
 * This file Copyright (c) 2012 Magnolia International
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
package info.magnolia.module.shop.paragraphs;

import javax.jcr.Node;

import info.magnolia.module.form.engine.FormStateTokenMissingException;
import info.magnolia.module.form.templates.components.FormParagraph;
import info.magnolia.module.form.templates.components.multistep.StartStepFormEngine;
import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.rendering.context.RenderingContext;

import org.apache.commons.lang.StringUtils;

/**
 * This form engine also looks in the shopping cart for a form token, before it
 * creates a new one. That way, shoppers can continue shopping without loosing 
 * already entered data in the checkout form.
 * @author will
 */
public class ShopStartStepFormEngine extends StartStepFormEngine {

    public ShopStartStepFormEngine(Node configurationNode, FormParagraph configurationParagraph,RenderingContext context) {
        super(configurationNode, configurationParagraph, context);
    }

    /**
     * Finds the token from a requests parameter, shopping cart, or Ð since this
     * is the first step Ð creates a new form state if the form is being
     * submitted.
     */
    @Override
    protected String getFormStateToken() throws FormStateTokenMissingException {
        String formStateToken = null;
        DefaultShoppingCartImpl cart = (DefaultShoppingCartImpl) ShopUtil.getShoppingCart();
        try {
            // try the standard way to get the form token
            formStateToken = super.getFormStateToken();
            if (cart != null) {
                cart.setFormStateToken(formStateToken);
            }
            return formStateToken;
        } catch (FormStateTokenMissingException e) {
            // next, look in cart!
            if (cart != null) {
                formStateToken = cart.getFormStateToken();
                if (StringUtils.isNotBlank(formStateToken)) {
                    return formStateToken;
                }
            }
            if (isFormSubmission()) {
                formStateToken = createAndSetFormState().getToken();
                cart.setFormStateToken(formStateToken);
                return formStateToken;
            }
            // The token is allowed to be missing when the first step is submitted.
            throw e;
        }
    }
}
