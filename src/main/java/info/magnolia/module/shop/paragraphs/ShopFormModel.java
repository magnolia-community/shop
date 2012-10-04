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

import info.magnolia.module.form.templates.components.FormModel;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.objectfactory.Components;
import info.magnolia.templating.functions.TemplatingFunctions;
import javax.jcr.Node;

/**
 * The ShopFormModel only makes sure that a {@link ShopStartStepFormEngine} is 
 * being used. This form engine will look in the cart for a form token if none
 * was found otherwise. That way users can continue shopping without loosing
 * already entered checkout form data.
 *
 * @author uscheidegger@fastforward.ch
 */
public class ShopFormModel extends FormModel {

    public ShopFormModel(Node content, RenderableDefinition definition, RenderingModel<?> parent, TemplatingFunctions functions) {
        super(content, definition, parent, functions);
    }

    @Override
    protected ShopStartStepFormEngine createFormEngine() {
        return Components.newInstance(ShopStartStepFormEngine.class, content, definition);
    }

}
