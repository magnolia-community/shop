/**
 * This file Copyright (c) 2003-2011 Magnolia International
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
import info.magnolia.cms.gui.control.ControlImpl;
import info.magnolia.cms.gui.dialog.Dialog;
import info.magnolia.cms.gui.dialog.DialogBox;
import info.magnolia.cms.gui.dialog.DialogControlImpl;
import info.magnolia.cms.gui.misc.CssConstants;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.module.shop.util.ShopUtil;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

/**
 * Product prices.
 * @author will
 */
public class DialogProductPrices extends DialogBox {

    private DecimalFormat priceFormat;

    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content storageNode, Content configNode)
            throws RepositoryException {
        super.init(request, response, storageNode, configNode);
        // make sure the custom saveHandler is set
        if (StringUtils.isBlank(this.getConfigValue("saveHandler"))) {
            this.setConfig("saveHandler", this.getClass().getName() + "SaveHandler");
        }
        priceFormat = new DecimalFormat("0.00");
    }

    @Override
    public void drawHtml(Writer out) throws IOException {
        this.drawHtmlPre(out);
        //products are stored per shop
        String shopName = findShopName();

        Content priceCategoriesNode = ContentUtil.getContent("data", "/shops/" + shopName + "/priceCategories");

        if (priceCategoriesNode == null) {
            out.write("no price categories found");
        } else {
            // get all price categories
            Collection priceCategories = priceCategoriesNode.getChildren("shopPriceCategory");

            if (priceCategories.isEmpty()) {
                out.write("no price categories found");
            } else {
                //add shopname for the save handler
                out.write("<input type=\"hidden\" name=\"shopName\" value=\""
                        + shopName + "\" />");
                // draw a table with price category name, currency, incl./excl. vat and
                // price
                // TODO: Localize!
                out.write("<table cellspacing=\"2\" cellpadding=\"0\" border=\"0\">");
                out.write("<tr valign=\"top\"><th>Preiskategorie</th><th>WŠhrung</th><th>MwSt.</th><th>Preis</th></tr>");
                Iterator<Content> priceCategoryIter = priceCategories.iterator();
                Content currPriceCat, currency;
                String currencyUUID;
                int i = 0;
                while (priceCategoryIter.hasNext()) {
                    currPriceCat = priceCategoryIter.next();
                    out.write("<tr valign=\"top\">");
                    out.write("<td>" + currPriceCat.getNodeData("title_de").getString() + "</td>");
                    currencyUUID = currPriceCat.getNodeData("currencyUUID").getString();
                    out.write("<td align=\"center\">");
                    if (StringUtils.isNotBlank(currencyUUID)) {
                        currency = ContentUtil.getContentByUUID("data", currencyUUID);
                        if (currency != null) {
                            out.write(currency.getNodeData("name").getString());
                        }
                    }
                    out.write("</td>");
                    out.write("<td>");
                    if (currPriceCat.getNodeData("taxIncluded").getBoolean()) {
                        out.write("inkl. MwSt.");
                    } else {
                        out.write("exkl. MwSt.");
                    }
                    out.write("</td>");
                    out.write("<td>");
                    // get the price value
                    Double value = null;
                    String queryString = "";
                    if (this.getStorageNode() != null) {
                        queryString = "SELECT * FROM mgnl:contentNode WHERE jcr:path LIKE '" + this.getStorageNode().getHandle()
                                + "/" + this.getName() + "/%' and priceCategoryUUID = '" + currPriceCat.getUUID() + "'";
                        Iterator<Content> priceIter = QueryUtil.query("data", queryString, "sql", "mgnl:contentNode").iterator();
                        if (priceIter.hasNext()) {
                            value = priceIter.next().getNodeData("price").getDouble();
                        }
                    }
                    if (value != null) {
                        this.setValue(priceFormat.format(value));
                    }
                    GridEdit control = new GridEdit(this.getName() + "_price_" + i, this.getValue());
                    control.setType(this.getConfigValue("type", PropertyType.TYPENAME_STRING));
                    if (this.getConfigValue("saveInfo").equals("false")) { //$NON-NLS-1$ //$NON-NLS-2$
                        control.setSaveInfo(false);
                    }
                    control.setCssClass(CssConstants.CSSCLASS_EDIT);
                    control.setRows(this.getConfigValue("rows", "1"));
                    control.setCssStyles("width", this.getConfigValue("width", "100%"));
                    if (this.getConfigValue("onchange", null) != null) {
                        control.setEvent("onchange", this.getConfigValue("onchange"));
                    }
                    out.write(control.getHtml());

                    out.write("<input type=\"hidden\" name=\"" + this.getName() + "_priceCategoryUUID_" + i + "\" value=\""
                            + currPriceCat.getUUID() + "\" />");
                    out.write("</td>");
                    out.write("</tr>");
                    i++;
                }
                out.write("</table>");
                out.write(this.getHtmlSaveInfo());
                this.drawHtmlPost(out);
            }
        }
    }

    protected String findShopName() {
        String shopName = ShopUtil.getShopName(this.getStorageNode());
        if (StringUtils.isNotBlank(shopName)) {
            return shopName;
        }
        DialogControlImpl dialog = this.getParent();
        while (dialog != null && !(dialog instanceof Dialog)) {
            dialog = dialog.getParent();
        }
        if (dialog != null && StringUtils.isNotEmpty(dialog.getConfigValue("path"))) {
            String path = dialog.getConfigValue("path");
            int lastIndex = path.lastIndexOf("/") + 1;
            if (lastIndex > 0) {
                return path.substring(lastIndex);
            }
        }
        return "";
    }

    /**
     * @todo this should be in a "control" class, not a dialog class
     * @return
     */
    private String getHtmlSaveInfo() {
        StringBuffer html = new StringBuffer();
        html.append("<input type=\"hidden\"");
        html.append(" name=\"mgnlSaveInfo\"");
        html.append(" value=\"" + ControlImpl.escapeHTML(this.getName()) + "," + PropertyType.TYPENAME_STRING + ","
                + ControlImpl.VALUETYPE_MULTIPLE + "," + ControlImpl.RICHEDIT_NONE + "," + ControlImpl.ENCODING_NO + "\"");
        html.append(" />");
        return html.toString();
    }
}
