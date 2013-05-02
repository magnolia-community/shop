/**
 * This file Copyright (c) 2008-2011 Magnolia International
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

import info.magnolia.cms.gui.control.SelectOption;
import info.magnolia.cms.gui.misc.CssConstants;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import javax.jcr.PropertyType;

/**
 * Select element for the grid dialog which gets its options from a query.
 * @author Will Scheidegger
 */
public class GridQuerySelect extends QuerySelect implements GridSubDialog {

    @Override
    public void drawHtml(Writer out) throws IOException {
        GridSelect control = new GridSelect("${index}_" + this.getName(), this.getValue());
        control.setType(this.getConfigValue("type", PropertyType.TYPENAME_STRING));
        if (this.getConfigValue("saveInfo").equals("false")) {
            control.setSaveInfo(false);
        }
        control.setCssClass(CssConstants.CSSCLASS_SELECT);
        control.setCssStyles("width", this.getConfigValue("width", "100%"));
        control.setEvent("onchange", this.getConfigValue("onchange", this.getParent().getName() + "DynamicTable.persist();"), true);

        // translate (not possible in init since not a sub of the tab then)
        for (Iterator iter = this.getOptions().iterator(); iter.hasNext();) {
            SelectOption option = (SelectOption) iter.next();
            option.setLabel(this.getMessage(option.getLabel()));
        }
        control.setOptions(this.getOptions());

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
