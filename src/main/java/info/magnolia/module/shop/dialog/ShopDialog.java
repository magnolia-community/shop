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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import info.magnolia.cms.core.Content;
import info.magnolia.module.admininterface.SaveHandler;
import info.magnolia.module.data.dialogs.TypeSelectDataDialog;

/**
 * due to http://jira.magnolia-cms.com/browse/MGNLDATA-126, need to
 * set a different js for parent and subnodes.
 * @author tmiyar
 *
 */
public class ShopDialog extends TypeSelectDataDialog {

    public ShopDialog(String name, HttpServletRequest request,
            HttpServletResponse response, Content configNode) {
        super(name, request, response, configNode);
    }

    @Override
    protected boolean onPostSave(SaveHandler handler) {
        //need to check path due to http://jira.magnolia-cms.com/browse/MGNLDATA-126
        if(this.path.equals("/shops")) {
            this.setJsExecutedAfterSaving("window.opener.parent.location.href = window.opener.parent.location.href;");
        } else {
            //TODO: ugly hack for keeping the tree root path specified in config/menu for each shop elements.
            this.setJsExecutedAfterSaving("mgnlDialogReloadOpener();");
            
        }
        return super.onPostSave(handler);
    }
}
