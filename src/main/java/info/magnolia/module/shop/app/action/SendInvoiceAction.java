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

import info.magnolia.context.MgnlContext;
import info.magnolia.event.EventBus;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.mail.MailModule;
import info.magnolia.module.mail.templates.MailAttachment;
import info.magnolia.module.mail.templates.MgnlEmail;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.module.shop.service.ResourceFinalizer;
import info.magnolia.module.shop.service.YahpFtlToPdfService;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.event.AdmincentralEventBus;
import info.magnolia.ui.framework.action.AbstractRepositoryAction;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Send invoice with PDF attachment action class.
 */
public class SendInvoiceAction extends AbstractRepositoryAction<SendInvoiceActionDefinition> {

    private static final Logger LOG = LoggerFactory.getLogger(SendInvoiceAction.class);
    public static final String INVOICE_MAIL_TO_PROP_NAME = "invoiceMailTo";
    public static final String INVOICE_MAIL_SUBJECT_PROP_NAME = "invoiceMailSubject";
    public static final String INVOICE_BILLING_ADDRESS_MAIL_PROP_NAME = "billingAddressMail";
    
    @Inject
    private UiContext uiContext;

    @Inject
    private MailModule mailModule;

    @Inject
    private YahpFtlToPdfService yahpHtmlToPdfService;

    @Inject
    private ResourceFinalizer resourceFinalizer;

    public SendInvoiceAction(SendInvoiceActionDefinition definition, JcrItemAdapter item, @Named(AdmincentralEventBus.NAME) EventBus eventBus) {
        super(definition, item, eventBus);
    }

    @Override
    protected void onExecute(JcrItemAdapter item) throws RepositoryException {
        Item jcrItem = item.getJcrItem();
        if (!jcrItem.isNode()) return;
        Node node = (Node) jcrItem;
        Node parentNode = node.getParent();
        if (!parentNode.getParent().isSame(node.getSession().getRootNode())) return;
        
        Session shopsSession = MgnlContext.getJCRSession(ShopRepositoryConstants.SHOPS);
        Node shopNode = shopsSession.getRootNode().getNode(parentNode.getName());
        ByteArrayOutputStream baos = null;
        try {
            List<MailAttachment> attachments = new ArrayList<MailAttachment>();
            baos = yahpHtmlToPdfService.processInvoiceNodeToPdf(node);
            File pdfFile = yahpHtmlToPdfService.outputStreamToTempFile(baos);
            MailAttachment pdfAttachment = new MailAttachment(pdfFile, pdfFile.getName(), "customer invoice", "attachment");
            attachments.add(0, pdfAttachment);
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("shoppingCart", node);
            MgnlEmail email = mailModule.getFactory().getEmailFromType(parameters, "freemarker", "text/html", attachments);
            email.setToList(PropertyUtil.getString(node, INVOICE_BILLING_ADDRESS_MAIL_PROP_NAME, StringUtils.EMPTY));
            email.setCcList(PropertyUtil.getString(shopNode, INVOICE_MAIL_TO_PROP_NAME, StringUtils.EMPTY));
            email.setSubject(PropertyUtil.getString(shopNode, INVOICE_MAIL_SUBJECT_PROP_NAME, StringUtils.EMPTY));
            email.setBody(null);
            mailModule.getHandler().sendMail(email);
            
            uiContext.openNotification(MessageStyleTypeEnum.INFO, true, "Customer invoice #" + node.getName() + " sent!");
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RepositoryException(e);
        } finally {
            resourceFinalizer.finalizeResources(baos);
        }
    }

}
