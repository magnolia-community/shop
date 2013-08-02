/**
 * This file Copyright (c) 2008-2013 Magnolia International
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
package info.magnolia.module.shop.dialog;

import info.magnolia.cms.gui.dialog.DialogEdit;
import info.magnolia.cms.gui.misc.CssConstants;
import java.io.IOException;
import java.io.Writer;
import javax.jcr.PropertyType;

/**
 * Regular text field for the grid dialog. Todo: The element does not respect the "type" attribute yet.
 * @author Will Scheidegger
 */
public class GridDialogEdit extends DialogEdit implements GridSubDialog {

    @Override
    public void drawHtml(Writer out) throws IOException {
        GridEdit control = new GridEdit("${index}_" + this.getName(), this.getValue());
        control.setType(this.getConfigValue("type", PropertyType.TYPENAME_STRING)); //$NON-NLS-1$
        if (this.getConfigValue("saveInfo").equals("false")) { //$NON-NLS-1$ //$NON-NLS-2$
            control.setSaveInfo(false);
        }
        control.setCssClass(CssConstants.CSSCLASS_EDIT);
        control.setRows(this.getConfigValue("rows", "1")); //$NON-NLS-1$ //$NON-NLS-2$
        control.setCssStyles("width", this.getConfigValue("width", "100%")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//        if (this.getConfigValue("onchange", null) != null) { //$NON-NLS-1$
//            control.setEvent("onchange", this.getConfigValue("onchange")); //$NON-NLS-1$ //$NON-NLS-2$
//        }
        control.setEvent("onchange", this.getConfigValue("onchange", this.getParent().getName() + "DynamicTable.persist();"), true);
        this.drawHtmlPre(out);
        out.write(control.getHtml());
        this.drawHtmlPost(out);
    }

    @Override
    public String getName() {
        return this.getParent().getName() + "_" + super.getName();
    }

    @Override
    public String getValue() {
        // value will be set by JavaScript
        return "";
    }

    @Override
    public void drawHtmlPre(Writer out) throws IOException {
        out.write("<td>");
    }

    @Override
    public void drawHtmlPost(Writer out) throws IOException {
        out.write("</td>");
    }

    public String getNodeName() {
        return super.getName();
    }

}
