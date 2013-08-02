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

import info.magnolia.cms.gui.dialog.DialogDate;
import info.magnolia.cms.gui.misc.CssConstants;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.util.DateUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import javax.jcr.PropertyType;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * Date field for the grid dialog.
 * @author Will Scheidegger
 */
public class GridDate extends DialogDate implements GridSubDialog {

    @Override
    public void drawHtml(Writer out) throws IOException {
        // setup the edit control
        GridEdit control = new GridEdit("${index}_" + this.getName(), this.getValue());
        control.setType(this.getConfigValue("type", PropertyType.TYPENAME_DATE)); //$NON-NLS-1$
        if (this.getConfigValue("saveInfo").equals("false")) { //$NON-NLS-1$ //$NON-NLS-2$
            control.setSaveInfo(false);
        }
        control.setCssClass(CssConstants.CSSCLASS_EDIT);
        control.setRows(this.getConfigValue("rows", "1")); //$NON-NLS-1$ //$NON-NLS-2$
        control.setCssStyles("width", this.getConfigValue("width", "100%")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        control.setEvent("onchange", this.getConfigValue("onchange", this.getParent().getName() + "DynamicTable.persist();"), true);

        // setup the button control
        this.getButton().setLabel(MessagesManager.get("dialog.date.select")); //$NON-NLS-1$
        this.getButton().setSaveInfo(false);


        String format = "yyyy-MM-dd"; //$NON-NLS-1$
        String jsFormat = "%Y-%m-%d"; //$NON-NLS-1$
        boolean displayTime = !this.getConfigValue("time", "false").equals("false");
        boolean singleClick = this.getConfigValue("doubleClick", "false").equals("false");
        if (displayTime) {
            format += "' 'HH:mm:ss";
            jsFormat += " %k:%M:%S";
        }

        String inputFieldId = "${index}_" + this.getName();
        getButton().setId("butt_"+inputFieldId);
        String buttonId = this.getButton().getId();
        String calId = "cal_"+buttonId;
        getButton().setOnclick(calId+".show()");

        final String calendarScript = "<script type=\"text/javascript\">" +
                "            var "+calId+" = Calendar.setup({\n" +
                "                inputField     :    \""+inputFieldId+"\"," +
                "                ifFormat       :    \""+jsFormat+"\"," +
                "                showsTime      :    "+String.valueOf(displayTime)+"," +
                "                timeFormat     :    \"24\"," +
                "                cache          :    true,"+
                "                button         :    \""+buttonId+"\"," +
                "                singleClick    :    \""+String.valueOf(singleClick)+"\"," +
                //"                eventName      :    \"focus\", "+
                "                step           :    1" +
                "            });</script>";

        this.getButton().setHtmlPost(calendarScript);

        if (this.getStorageNode() != null && this.getStorageNode().getNodeData(this.getName()).isExist()) {
            Calendar valueCalendar = this.getStorageNode().getNodeData(this.getName()).getDate();

            // valueCalendar is in UTC turn it back into the current timezone
            if (valueCalendar != null) {
                Calendar local = DateUtil.getLocalCalendarFromUTC(valueCalendar);
                String value = DateFormatUtils.format(local.getTime(), format);
                this.setValue(value);
            }
        }

        this.drawHtmlPre(out);
//        out.write(control.getHtml());
        String width = this.getConfigValue("width", "95%"); //$NON-NLS-1$ //$NON-NLS-2$
        out.write("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"" + width + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
        out.write("<tr><td style=\"width:100%\"  class=\"" + CssConstants.CSSCLASS_EDITWITHBUTTON + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
        out.write(control.getHtml());
        if (this.getConfigValue("buttonLabel", null) != null) { //$NON-NLS-1$
            String label = this.getConfigValue("buttonLabel"); //$NON-NLS-1$
            label = this.getMessage(label);
            this.getButton().setLabel(label);
        }
        for (int i = 0; i < this.getButtons().size(); i++) {
            out.write("</td><td></td><td class=\"" + CssConstants.CSSCLASS_EDITWITHBUTTON + "\">"); //$NON-NLS-1$ //$NON-NLS-2$
            out.write(this.getButton(i).getHtml());
        }
        out.write("</td></tr></table>"); //$NON-NLS-1$
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
