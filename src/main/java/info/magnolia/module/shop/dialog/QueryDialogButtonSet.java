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
import info.magnolia.cms.gui.control.Button;
import info.magnolia.cms.gui.control.ControlImpl;
import info.magnolia.cms.gui.dialog.DialogButtonSet;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.cms.util.QueryUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This "button set" dialog will get its buttons by querying the repository
 * instead of reading them from the configuration. The configuration options are:
 * <ul>
 * <li><b>repository</b>: Name of the repository which should be queried for the
 * option nodes (default value: "data")</li>
 * <li><b>itemType</b>: Name of the item type which the query should be looking
 * for (default value: "nt:base")</li>
 * <li><b>path</b>: Limit the query to a specific branch of the tree in the
 * repository</li>
 * <li><b>query</b>: Complete query to fetch the option nodes. If a query is
 * specified, the path config option is ignored</li>
 * <li><b>type</b>: The query type - either "sql" or "xpath" (default value:
 * "sql")</li>
 * <li><b>nullValueLabel</b>: Label to be displayed as the first option with an
 * empty value, e.g. "Please select category..."</li>
 * <li><b>valueNodeData</b>: Name of the node data in the fetched option node
 * which should be used as the value of the option. Two special values are
 * accepted: "uuid" and "handle"</li>
 * <li><b>labelNodeData</b>: Name of the node data in the fetched option node
 * which contains the label that should be used of the option. If you want your
 * option lable to be made up of multiple nodeData values simply add the names
 * in a comma separated list, e.g. "lastname, firstname". The values will then
 * be combined with a white space in-between.</li>
 * </ul>
 * @author will
 */
public class QueryDialogButtonSet extends DialogButtonSet {

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(QueryDialogButtonSet.class);

    @Override
    public void setOptions(Content configNode, boolean setDefaultSelected) {
        // setDefaultSelected: does not work properly (no difference between never stored and removed...)
        // therefor do only use for radio, not for checkbox
        List options = new ArrayList();
        try {
            Iterator it = getOptionNodes(configNode).iterator();
            while (it.hasNext()) {
                Content n = ((Content) it.next());
                String valueNodeData = this.getConfigValue("valueNodeData", "value");
                String labelNodeData = this.getConfigValue("labelNodeData", "label");

                String value = n.getName();
                if (valueNodeData.equals("uuid")) {
                    value = n.getUUID();
                } else if (valueNodeData.equals("handle")) {
                    value = n.getHandle();
                } else if (n.hasNodeData(valueNodeData)) {
                    value = NodeDataUtil.getString(n, valueNodeData);
                }
                
                String label;
                if (labelNodeData.indexOf(",") >= 0) {
                    label = "";
                    String[] labelNodeDatas = StringUtils.split(labelNodeData, ",");
                    labelNodeDatas = StringUtils.stripAll(labelNodeDatas);
                    for (int i = 0; i < labelNodeDatas.length; i++) {
                        log.debug("label "+i+" ("+labelNodeDatas[i]+"): "+NodeDataUtil.getString(n, labelNodeDatas[i]));
                        label += NodeDataUtil.getString(n, labelNodeDatas[i]) + " ";
                    }
                    StringUtils.strip(label);
                    if (StringUtils.isEmpty(label)) {
                        label = value;
                    }
                } else {
                    label = NodeDataUtil.getString(n, labelNodeData, value);//$NON-NLS-1$
                }

                //label = this.getMessage(label);
                Button button = new Button(this.getName(), value);
                // if (n.getNodeData("label").isExist()) button.setLabel(n.getNodeData("label").getString());
                button.setLabel(label);

                String iconSrc = n.getNodeData("iconSrc").getString(); //$NON-NLS-1$
                if (StringUtils.isNotEmpty(iconSrc)) {
                    button.setIconSrc(iconSrc);
                }

                if (setDefaultSelected && n.getNodeData("selected").getBoolean()) { //$NON-NLS-1$
                    button.setState(ControlImpl.BUTTONSTATE_PUSHED);
                }
                options.add(button);
            }
        } catch (RepositoryException e) {
            if (log.isDebugEnabled()) {
                log.debug("Exception caught: " + e.getMessage(), e); //$NON-NLS-1$
            }
        }
        this.setOptions(options);
    }

    @Override
    protected Collection getOptionNodes(Content configNode) throws PathNotFoundException, RepositoryException, AccessDeniedException {
        String repository = this.getConfigValue("repository", "data");
        String itemType = this.getConfigValue("itemType", "nt:base");
        String path = this.getConfigValue("path", "");
        String query = this.getConfigValue("query");
        String type = this.getConfigValue("type", "sql");

        String queryString;
        if (query != null && query.length() > 0) {
            queryString = query;
        } else {
            type = "sql";
            queryString = "SELECT * FROM " + itemType + " WHERE jcr:path LIKE '" + path + "/%'";
        }
        Collection items = QueryUtil.query(repository, queryString, type, itemType);
        return items;

/*        Content optionsNode = null;
        String queryString = "select * from coachFeature where jcr:path like '/coach/features/%'";
        Collection features = QueryUtil.query("data", queryString, "sql", "coachFeature");
        return features;*/
    }
    
    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content websiteNode, Content configNode)
            throws RepositoryException {
        super.init(request, response, websiteNode, configNode);
        setButtonType(ControlImpl.BUTTONTYPE_CHECKBOX);
        setOptions(configNode, false);
        setConfig("valueType", "multiple");
    }
}