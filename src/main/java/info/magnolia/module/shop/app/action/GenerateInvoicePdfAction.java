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
import info.magnolia.freemarker.FreemarkerHelper;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.ui.api.event.AdmincentralEventBus;
import info.magnolia.ui.framework.action.AbstractRepositoryAction;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.inject.Named;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.DocumentException;

import freemarker.template.TemplateException;

/**
 * Generate invoice PDF action class.
 */
public class GenerateInvoicePdfAction extends AbstractRepositoryAction<GenerateInvoicePdfActionDefinition> {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateInvoicePdfAction.class);

    private FreemarkerHelper freemarkerHelper;

    public GenerateInvoicePdfAction(GenerateInvoicePdfActionDefinition definition, JcrItemAdapter item, @Named(AdmincentralEventBus.NAME) EventBus eventBus, FreemarkerHelper freemarkerHelper) {
        super(definition, item, eventBus);
        this.freemarkerHelper = freemarkerHelper;
    }

    @Override
    protected void onExecute(JcrItemAdapter item) throws RepositoryException {
        Item jcrItem = item.getJcrItem();
        if (jcrItem.isNode()) {
            Node node = (Node) jcrItem;
            Node parentNode = node.getParent();
            if (parentNode.getParent().isSame(node.getSession().getRootNode())) {
                try {
                    Writer writer = new OutputStreamWriter(new FileOutputStream("invoice.html"));
                    freemarkerHelper.render("invoice.ftl", node, writer);
                    writer.flush();
                    writer.close();
                    ShopUtil.generatePdfInvoice();
                    LOG.warn("do sth... with {} ", node.getPath());
                } catch (FileNotFoundException e) {
                    LOG.info(e.getMessage(), e);
                } catch (TemplateException e) {
                    LOG.info(e.getMessage(), e);
                } catch (IOException e) {
                    LOG.info(e.getMessage(), e);
                } catch (DocumentException e) {
                    LOG.info(e.getMessage(), e);
                }
            }
        }
    }

}
