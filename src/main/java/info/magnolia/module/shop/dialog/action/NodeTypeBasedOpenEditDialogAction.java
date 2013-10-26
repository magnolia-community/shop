/**
 * This file Copyright (c) 2013 Magnolia International
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
package info.magnolia.module.shop.dialog.action;

import info.magnolia.event.EventBus;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.event.AdmincentralEventBus;
import info.magnolia.ui.dialog.formdialog.FormDialogPresenterFactory;
import info.magnolia.ui.framework.action.OpenEditDialogAction;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;

import javax.inject.Named;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Action that opens edit dialog based on node type. If dialog isn't defined parent class action is executed.
 */
public class NodeTypeBasedOpenEditDialogAction extends OpenEditDialogAction {

    private JcrItemAdapter itemToEdit;

    public NodeTypeBasedOpenEditDialogAction(NodeTypeBasedOpenEditDialogActionDefinition definition, JcrItemAdapter itemToEdit, FormDialogPresenterFactory formDialogPresenterFactory, UiContext uiContext, @Named(AdmincentralEventBus.NAME) EventBus eventBus) {
        super(definition, itemToEdit, formDialogPresenterFactory, uiContext, eventBus);
        this.itemToEdit = itemToEdit;
    }

    @Override
    public void execute() throws ActionExecutionException {
        if (getDefinition() instanceof NodeTypeBasedOpenEditDialogActionDefinition) {
            NodeTypeBasedOpenEditDialogActionDefinition definition = (NodeTypeBasedOpenEditDialogActionDefinition) getDefinition();
            String nodeType = null;
            if (itemToEdit.getJcrItem().isNode()) {
                try {
                    nodeType = ((Node) itemToEdit.getJcrItem()).getPrimaryNodeType().getName();
                } catch (RepositoryException e) {
                    throw new ActionExecutionException(e);
                }
            }
            if (nodeType != null && definition.getMappings().containsKey(nodeType)) {
                getDefinition().setDialogName(definition.getMappings().get(nodeType));
            }
        }
        super.execute();
    }
}
