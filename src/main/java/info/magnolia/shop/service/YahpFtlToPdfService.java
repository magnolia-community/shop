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
package info.magnolia.shop.service;

import info.magnolia.freemarker.FreemarkerHelper;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.util.SessionUtil;
import info.magnolia.shop.ShopRepositoryConstants;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.allcolor.yahp.converter.CYaHPConverter;
import org.allcolor.yahp.converter.IHtmlToPdfTransformer;
import org.allcolor.yahp.converter.IHtmlToPdfTransformer.CConvertException;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;

import freemarker.template.TemplateException;

/**
 * Use Yahp (http://www.allcolor.org/YaHPConverter/) library to implement FTL to PDF service.
 * A bridge between Magnolia Freemarker Helper and Allcolor Yahp Converter.
 */
@Singleton
public class YahpFtlToPdfService implements FtlToPdfService {

    private static Logger LOG = LoggerFactory.getLogger(YahpFtlToPdfService.class);

    private FreemarkerHelper freemarkerHelper;
    private ResourceFinalizer resourceFinalizer;

    @Inject
    public YahpFtlToPdfService(FreemarkerHelper freemarkerHelper, ResourceFinalizer resourceFinalizer) {
        this.freemarkerHelper = freemarkerHelper;
        this.resourceFinalizer = resourceFinalizer;
    }

    @Override
    public ByteArrayOutputStream ftl2pdf(String ftlContents, Map<String, Object> inputModel) {
        // Render content
        ByteArrayInputStream bais = new ByteArrayInputStream(ftlContents.getBytes());
        Reader bufferedReader = new InputStreamReader(bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(baos);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            freemarkerHelper.render(bufferedReader, inputModel, writer);
            writer.flush();
            // Convert to pdf
            CYaHPConverter converter = new CYaHPConverter();
            Map<String, String> properties = new HashMap<String, String>();
            properties.put(IHtmlToPdfTransformer.PDF_RENDERER_CLASS, IHtmlToPdfTransformer.FLYINGSAUCER_PDF_RENDERER);
            converter.convertToPdf(baos.toString(), IHtmlToPdfTransformer.A4P, Collections.EMPTY_LIST, "file:///temp/html/", result, properties);
            result.flush();
            return result;
        } catch (TemplateException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } catch (CConvertException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            resourceFinalizer.finalizeResources(bufferedReader, bais, writer, baos);
        }
        return null;
    }

    @Override
    public ByteArrayOutputStream processInvoiceNodeToPdf(Node node) {
        Node shopNode = null;
        try {
            String nodePath = node.getPath();
            Node parentNode = node.getParent();
            if (!parentNode.getParent().isSame(node.getSession().getRootNode())) return null;
            shopNode = SessionUtil.getNode(ShopRepositoryConstants.SHOPS, parentNode.getPath());
            LOG.debug("Processing data from {} to pdf.", nodePath);
        } catch (RepositoryException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
        String inputFtlStr = PropertyUtil.getString(shopNode, FtlToPdfService.CONFIGURED_INVOICE_TEMPLATE_PROPERTY_NAME);
        if (StringUtils.isBlank(inputFtlStr)) return null;
        Map<String, Object> inputModel = new HashMap<String, Object>();
        inputModel.put("shoppingCart", node);
        inputModel.put("PropertyUtil", new PropertyUtil());
        return ftl2pdf(inputFtlStr, inputModel);
    }

    @Override
    public void outputStreamToNewWindow(final ByteArrayOutputStream baos, String mimeType, String fileType) {
        if (baos == null) return;
        try {
            StreamResource.StreamSource source = new StreamResource.StreamSource() {
                @Override
                public InputStream getStream() {
                    return new ByteArrayInputStream(baos.toByteArray());
                }
            };
            File outputPdf = File.createTempFile("file-" + System.currentTimeMillis(), fileType);
            outputPdf.deleteOnExit();
            String fileName = outputPdf.getName();
            StreamResource resource = new StreamResource(source, fileName);
            // Accessing the DownloadStream via getStream() will set its cacheTime to whatever is set in the parent
            // StreamResource. By default it is set to 1000 * 60 * 60 * 24, thus we have to override it beforehand.
            // A negative value or zero will disable caching of this stream.
            resource.setCacheTime(-1);
            resource.getStream().setParameter("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
            resource.getStream().setParameter("Content-Type", "application/force-download;");
            resource.setMIMEType(mimeType);
            Page.getCurrent().open(resource, "Download file", true);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            resourceFinalizer.finalizeResources(baos);
        }
    }

    @Override
    public File outputStreamToTempFile(final ByteArrayOutputStream baos) {
        if (baos == null) return null;
        File outputPdf = null;
        OutputStream outputStream = null;
        try {
            outputPdf = File.createTempFile("invoice-" + System.currentTimeMillis(), ".pdf");
            outputPdf.deleteOnExit();
            outputStream = new FileOutputStream(outputPdf);
            baos.writeTo(outputStream);
            baos.flush();
            outputStream.flush();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            resourceFinalizer.finalizeResources(outputStream, baos);
        }
        return outputPdf;
    }

}