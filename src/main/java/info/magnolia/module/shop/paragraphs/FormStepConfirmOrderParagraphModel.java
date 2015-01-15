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
package info.magnolia.module.shop.paragraphs;


import info.magnolia.module.form.templates.components.SubStepFormModel;
import info.magnolia.module.shop.beans.ShoppingCart;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Checkout step paragraph. Need to get the current shopping cart.
 * @author tmiyar
 *
 */
public class FormStepConfirmOrderParagraphModel extends SubStepFormModel {
    private Node siteRoot = null;

    public FormStepConfirmOrderParagraphModel(Node content,
            RenderableDefinition definition, RenderingModel<?> parent,
            TemplatingFunctions functions) {
        super(content, definition, parent, functions);
        siteRoot = Components.getComponent(STKTemplatingFunctions.class).siteRoot(content);

    }

    public ShoppingCart getShoppingCart() {
        return ShopUtil.getShoppingCart();
    }

    public String getCurrencyTitle() {
        return ShopUtil.getCurrencyTitle();
    }

    public String getCurrencyFormatting() {
        return ShopUtil.getCurrencyFormatting();
    }

    public String getProductDetailPageLink(Node product) {
        try {
            return ShopLinkUtil.getProductDetailPageLink(functions, product, siteRoot);
        } catch (RepositoryException e) {
            return "";
        }
    }

}
