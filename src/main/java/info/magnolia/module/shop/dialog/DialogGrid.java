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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.gui.dialog.DialogControlImpl;
import info.magnolia.cms.gui.dialog.DialogMultiSelect;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.content2bean.Content2BeanUtil;
import info.magnolia.freemarker.FreemarkerUtil;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Grid dialog element for 2-dimensional data.
 * @author Will Scheidegger
 */
public class DialogGrid extends DialogMultiSelect {

    private static Logger log = LoggerFactory.getLogger(DialogGrid.class);

    /**
     * options (radio, checkbox...).
     */
//    private List gridControls = new ArrayList();
    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content storageNode, Content configNode)
            throws RepositoryException {
        super.init(request, response, storageNode, configNode);
        // make sure the custom saveHandler is set
        if (StringUtils.isBlank(this.getConfigValue("saveHandler"))) {
            this.setConfig("saveHandler", DialogGrid.class.getName() + "SaveHandler");
        }
        /*        Content gridControlsNode = ContentUtil.getContent(configNode, "gridControls");
        if (gridControlsNode != null) {
        // old style: all grid control nodes inside a contentNode "gridControls"
        setGridControls(gridControlsNode);
        } else {
        // new style: all contentnode children of the configNode _are_ grid control nodes
        Collection gridControlNodes = configNode.getChildren(ItemType.CONTENTNODE);
        setGridControls(gridControlNodes);
        }
        log.debug("gridControls: " + getGridControls());*/
        log.debug("subs: " + this.getSubs());
    }

    @Override
    public void drawHtml(Writer out) throws IOException {
        this.drawHtmlPre(out);
        out.write(FreemarkerUtil.process(DialogGrid.class, this));
        /*        this.drawHtmlPreSubs(out);
        this.drawSubs(out);
        this.drawHtmlPostSubs(out);*/
        this.drawHtmlPost(out);
    }

    public String getRowTemplateHTML() throws IOException {
        StringWriter out = new StringWriter();
        drawSubs(out);
        return out.toString();
    }

    @Override
    public void drawSubs(Writer out) throws IOException {
//        out.write(getLabelsHtml());
        out.write("<tr valign=\"top\" style=\"display: none;\" class=\"grid_row\">");
        super.drawSubs(out);
        out.write("<td>" + this.getDeleteButton() + "</td></tr>");
    }

    /*    @Override
    public void drawHtmlPreSubs(Writer out) throws IOException {
    // add tab to js object
    out.write("<table border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"100%\" class=\"mgnl_dialog_grid data\">");

    }

    @Override
    public void drawHtmlPostSubs(Writer out) throws IOException {
    out.write("</table>\n");
    out.write(getGridJS());
    }

    public String getLabelsHtml() {
    String html = "<tr valign=\"bottom\">";
    // For each configured control add the corresponding inner html
    Iterator controls = getSubs().iterator();
    while (controls.hasNext()) {
    DialogControlImpl control = (DialogControlImpl) controls.next();
    String label = control.getMessage(control.getLabel());
    html += "<th>" + label + "</th>";
    }
    html += "<td>" + this.getAddButton() + "</td></tr>";
    return html;
    }
     */
    /**
     * Called by the template. It renders the dynamic inner row using trimpaths templating mechanism.
     * @return HTML code
     */
    /*    @Override
    public String getInnerHtml() {
    String html = "<tr valign=\"top\">";
    // For each configured control add the corresponding inner html
    Iterator controls = getSubs().iterator();
    while (controls.hasNext()) {
    Map control = (Map) controls.next();
    log.debug("getting inner html for " + control.get("name"));
    String controlType = (String) control.get("controlType");
    controlType = controlType.toUpperCase().substring(0, 1) + controlType.substring(1);
    String name = "/" + StringUtils.replace(this.getClass().getName(), ".", "/") + controlType + "Inner.html";
    Map map = new HashMap();
    map.put("this", this);
    map.put("control", control);
    html += "<td>" + FreemarkerUtil.process(name, map) + "</td>";
    }
    html += "<td>" + this.getDeleteButton() + "</td></tr>";
    return html;
    }*/
    @Override
    public String getJSON() {
        List values = this.getValues();
        JSONArray json = JSONArray.fromObject(values);
//        JSONArray json = JSONArray.fromCollection(values);
        log.debug("JSON: " + json.toString());
        return json.toString();
    }

    @Override
    protected List readValues() {
        List values = new ArrayList();
        if (this.getStorageNode() != null) {
            try {
                Content node = ContentUtil.getContent(this.getStorageNode(), this.getName());
                if (node != null) {
                    Iterator it = node.getChildren().iterator();
                    while (it.hasNext()) {
                        Content rowNode = (Content) it.next();
                        values.add(Content2BeanUtil.toMap(rowNode));
                    }
                }
            } catch (Content2BeanException ex) {
                log.error("Could not convert value node to map.", ex);
            }
        }

        return values;
    }

    public String getSubsJSON() {
        Iterator subsIter = getSubs().iterator();
        DialogControlImpl currentSub;
        ArrayList dialogMaps = new ArrayList();
        Map dialogMap;
        String js;
        while (subsIter.hasNext()) {
            currentSub = (DialogControlImpl) subsIter.next();
            dialogMap = new HashMap();
            dialogMap.put("name", currentSub.getName());
            if (StringUtils.isNotBlank(currentSub.getLabel())) {
                dialogMap.put("label", currentSub.getLabel());
            }
            dialogMap.put("nodeName", ((GridSubDialog) currentSub).getNodeName());
            /*            if (StringUtils.isNotBlank(currentSub.getConfigValue("valueSetterJS"))) {
            js = currentSub.getConfigValue("valueSetterJS");
            if (js.startsWith("function")) {
            dialogMap.put("valueSetterJS", js);
            } else {
            dialogMap.put("valueSetterJS", "function(value) {" + js + "}");
            }
            } else {
            js = "function(value, index) {jQuery(\"#\" + index + \"_" + currentSub.getName() + ").val(value);}";
            dialogMap.put("valueSetterJS", js);
            }
            if (StringUtils.isNotBlank(currentSub.getConfigValue("valueGetterJS"))) {
            js = currentSub.getConfigValue("valueGetterJS");
            if (js.startsWith("function")) {
            dialogMap.put("valueGetterJS", js);
            } else {
            dialogMap.put("valueGetterJS", "function(value) {" + js + "}");
            }
            } else {
            js = "function(index) {jQuery(\"#\" + index + \"_" + currentSub.getName() + ").val();}";
            dialogMap.put("valueGetterJS", js);
            }*/
            dialogMaps.add(dialogMap);
        }
        JSONArray json = JSONArray.fromObject(dialogMaps);
//        JSONArray json = JSONArray.fromCollection(dialogMaps);

        log.debug("Subs JSON: " + json.toString());
        return json.toString();
    }

    public String getValueSetterJS(int index) {
        if (index >= 0 && index < getSubs().size()) {
            DialogControlImpl sub = (DialogControlImpl) getSubs().get(index);
            String js = sub.getConfigValue("valueSetterJS");
            if (StringUtils.isBlank(js)) {
                js = "function(index, value) {jQuery(\"#\" + index + \"_" + sub.getName() + "\").val(value);}";
            } else if (!js.startsWith("function")) {
                js = "function(index, value) {" + js + "}";

            }
            return js;
        }
        return null;
    }

    public String getValueGetterJS(int index) {
        if (index >= 0 && index < getSubs().size()) {
            DialogControlImpl sub = (DialogControlImpl) getSubs().get(index);
            String js = sub.getConfigValue("valueGetterJS");
//            if (StringUtils.isBlank(js)) {
                js = "function(index) {return jQuery(\"#\" + index + \"_" + sub.getName() + "\").val();}";
//            } else if (!js.startsWith("function")) {
//                js = "function(index) {" + js + "}";
//            }
            return js;
        }
        return null;
    }

    public String getColumnAccessorMethods() {
        String js = "";
        for (int i=0; i<getSubs().size(); i++) {
            js += getName() + "ColumnDefinitions[" + i + "].getValue = " + getValueGetterJS(i) + "\n";
            js += getName() + "ColumnDefinitions[" + i + "].setValue = " + getValueSetterJS(i) + "\n";
        }
        return js;
    }
}
