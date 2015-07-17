/**
 * This file Copyright (c) 2008-2015 Magnolia International
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
package info.magnolia.module.shop.components;

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.form.templates.components.FormFieldModel;
import info.magnolia.module.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;

import java.util.Collection;

import javax.jcr.Node;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

/**
 * Form element to select the shipping option taken from the shop configuration.
 * 
 * @param <RD> RenderableDefinition
 * @author Will Scheidegger
 */
public class ShippingOptionSelectionParagraphModel<RD extends RenderableDefinition> extends FormFieldModel<RD> {

    public ShippingOptionSelectionParagraphModel(Node content, RD definition, RenderingModel<?> parent, TemplatingFunctions functions) {
        super(content, definition, parent, functions);
    }

    public Collection<Node> getOptions() {
        // get the shipping options uuids
        DefaultShoppingCartImpl cart = (DefaultShoppingCartImpl) ShopUtil.getShoppingCart(ShopUtil.getShopName());
        if (StringUtils.isNotBlank(cart.getShippingAddressCountry())) {
            try {
                Node shippingCountry = NodeUtil.getNodeByIdentifier("data", cart.getShippingAddressCountry());
                if (shippingCountry != null) {
                    // get the shipping options subnode
                    Node shippingOptionsCollectionNode = shippingCountry.getNode("shippingOptions");
                    if (shippingOptionsCollectionNode != null) {
                        PropertyIterator uuidIter = shippingOptionsCollectionNode.getProperties();
                        if (uuidIter.hasNext()) {
                            // get all shipping options for this country
                            String queryString = "/jcr:root//*[";
                            while (uuidIter.hasNext()) {
                                queryString += "@jcr:uuid='" + uuidIter.nextProperty().getString() + "' or ";
                            }
                            queryString = StringUtils.substringBeforeLast(queryString, " or ");
                            return ShopUtil.xpathQuery("data", queryString, "shopShippingOption", false);
                        }

                    }
                }
            } catch (RepositoryException e) {
                return null;
            }
        }
        return null;
    }
}
