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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This dialog works the same way as the {@link ConfigurableTreeDialog} except
 * that it will add the root folder name at the beginning of the tree name. So
 * if you set the treeName config value to "ProductTree" and the node you are
 * editing is /mycompany/products/merchandising, the complete tree name will be
 * "mycompanyProductTree". This means that after saving the dialog, the tree
 * "mycompanyProductTree" will be reopened at the position of the node that has
 * been edited (unless a specific jsExecutedAfterSaving is configured).
 * @author will
 */
public class RootSpecificConfigurableTreeDialog extends ConfigurableTreeDialog {

    private static final Logger log = LoggerFactory.getLogger(RootSpecificConfigurableTreeDialog.class);

    /**
     * @param name
     * @param request
     * @param response
     * @param configNode
     */
    public RootSpecificConfigurableTreeDialog(String name, HttpServletRequest request, HttpServletResponse response, Content configNode) {
        super(name, request, response, configNode);
    }

    @Override
    public String getJsExecutedAfterSaving() {
        // TODO: Test if this step is really still necessary, now that treeName
        // has accessor methods (should be set when the dialog gets instantiated)
        if (StringUtils.isBlank(getTreeName())) {
            // try getting the treeName from the config node
            if (this.getConfigNode().getNodeData("treeName").isExist()) {
                setTreeName(this.getConfigNode().getNodeData("treeName").getString());
            }
        }
        
        if (StringUtils.isBlank(getTreeName())) {
            log.debug("need to take super.js (" + super.getJsExecutedAfterSaving() + ")");
            return super.getJsExecutedAfterSaving();
        } else {
            // get the root folder name from the request parameter "mgnlPath"
            String rootFolderName = StringUtils.substringBefore(StringUtils.substringAfter(path, "/"), "/");
            String js = "opener.mgnl.data.DataTree.reloadAfterEdit('" + rootFolderName + getTreeName() + "', '" + this.path + "/" + this.nodeName + "')";
            log.debug("my js: " + js);
            return js;
        }
    }
}
