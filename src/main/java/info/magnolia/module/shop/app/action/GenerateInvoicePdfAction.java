/**
 * This file Copyright (c) 2013-2015 Magnolia International
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
package info.magnolia.module.shop.app.action;

import info.magnolia.event.EventBus;
import info.magnolia.module.shop.service.ResourceFinalizer;
import info.magnolia.module.shop.service.YahpFtlToPdfService;
import info.magnolia.ui.api.event.AdmincentralEventBus;
import info.magnolia.ui.framework.action.AbstractRepositoryAction;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;

import javax.inject.Named;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generate invoice PDF action class.
 */
public class GenerateInvoicePdfAction extends AbstractRepositoryAction<GenerateInvoicePdfActionDefinition> {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateInvoicePdfAction.class);
    private YahpFtlToPdfService yahpHtmlToPdfService;
    private ResourceFinalizer resourceFinalizer;

    public GenerateInvoicePdfAction(GenerateInvoicePdfActionDefinition definition, JcrItemAdapter item, @Named(AdmincentralEventBus.NAME) EventBus eventBus, YahpFtlToPdfService yahpHtmlToPdfService, ResourceFinalizer resourceFinalizer) {
        super(definition, item, eventBus);
        this.resourceFinalizer = resourceFinalizer;
    }

    @Override
    protected void onExecute(JcrItemAdapter item) throws RepositoryException {
        Item jcrItem = item.getJcrItem();
        if (jcrItem.isNode()) {
            ByteArrayOutputStream baos = null;
            try {
                baos = yahpHtmlToPdfService.processInvoiceNodeToPdf((Node) jcrItem);
                yahpHtmlToPdfService.outputStreamToNewWindow(baos, "application/pdf", ".pdf");
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                throw new RepositoryException(e);
            } finally {
                resourceFinalizer.finalizeResources(baos);
            }
        }
    }

}
