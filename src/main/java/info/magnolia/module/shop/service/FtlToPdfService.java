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
package info.magnolia.module.shop.service;

import java.io.File;
import java.util.Map;

import javax.jcr.Node;

import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 * Support generate PDF content from FTL input using info.magnolia.freemarker.FreemarkerHelper.
 */
public interface FtlToPdfService {

    static final String CONFIGURED_INVOICE_TEMPLATE_PROPERTY_NAME = "invoiceTemplate";

    /**
     * Process input FTL and data model (map of objects) then convert HTML output to PDF stream.
     * @param ftlContents
     * @param inputModel
     * @return ByteArrayOutputStream or null in case of exception.
     */
    ByteArrayOutputStream ftl2pdf(String ftlContents, Map<String, Object> dataModel);

    /**
     * A convenience service to directly process a shopping cart JCR node and generate PDF invoice using ftl2pdf function.
     * This use CONFIGURED_INVOICE_TEMPLATE_PROPERTY_NAME to retrieve configured FTL template from correspondence Shop.
     * @param node
     * @return PDF stream or null in case of exception.
     */
    ByteArrayOutputStream processInvoiceNodeToPdf(Node node);

    /**
     * Support stream in memory ByteArrayOutputStream to a new window (HTTP) using com.vaadin.server.StreamResource.
     * Note that "Closing a ByteArrayOutputStream has no effect".
     * Reference: info.magnolia.ui.framework.action.ExportAction.openFileInBlankWindow(String, String).
     */
    void outputStreamToNewWindow(final ByteArrayOutputStream baos, String mimeType, String fileType);

    /**
     * Support write in memory ByteArrayOutputStream to temporary file (which is set to delete on exit). 
     * Note that "Closing a ByteArrayOutputStream has no effect".
     * @return tempFile handler or null in case of exception.
     */
    File outputStreamToTempFile(final ByteArrayOutputStream byteArrayOutputStream);

}
