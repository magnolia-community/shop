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
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.ui.api.event.AdmincentralEventBus;
import info.magnolia.ui.framework.action.AbstractRepositoryAction;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.io.output.ByteArrayOutputStream;
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
                    String inputFtl = "/Volumes/RamDisk/git/shop/src/main/resources/info/magnolia/module/shop/invoice.html";
                    File outputPdf = File.createTempFile("invoice-" + System.currentTimeMillis(), ".pdf");
                    outputPdf.deleteOnExit();
                    Map<String, Object> input = new HashMap<String, Object>();
                    input.put("shoppingCart", node);
                    input.put("PropertyUtil", new PropertyUtil());
                    ftl2pdf(inputFtl, input, outputPdf.getAbsolutePath());
                    LOG.info("Processed data from {} to pdf {}.", node.getPath(), outputPdf.getPath());
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                    throw new RepositoryException(e);
                }
            }
        }
    }
    
    public ByteArrayOutputStream ftl2pdf(Reader inputFtl, Map<String, Object> model) throws TemplateException, IOException, DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(baos);
        freemarkerHelper.render(inputFtl, model, writer);
        writer.flush();
        writer.close();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        ShopUtil.generatePdfInvoice(new ByteArrayInputStream(baos.toByteArray()), result);
        result.flush();
        return result;
    }
    
    public void ftl2pdf(String ftlFileName, Map<String, Object> model, String pdfFileName) throws IOException, TemplateException, DocumentException {
        FileReader inputFtl = new FileReader(ftlFileName);
        ByteArrayOutputStream baos = ftl2pdf(inputFtl, model);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(pdfFileName));
        baos.writeTo(out);
        out.flush();
        out.close();
    }
}
