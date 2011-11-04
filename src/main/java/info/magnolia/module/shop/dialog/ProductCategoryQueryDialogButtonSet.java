/**
 * This file Copyright (c) 2010-2011 Magnolia International
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
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templatingkit.util.STKUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.jcr.RepositoryException;
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
public class ProductCategoryQueryDialogButtonSet extends QueryDialogButtonSet {

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(ProductCategoryQueryDialogButtonSet.class);

    @Override
    public void setOptions(Content configNode, boolean setDefaultSelected) {
        // setDefaultSelected: does not work properly (no difference between never stored and removed...)
        // therefor do only use for radio, not for checkbox
        List options = new ArrayList();
        try {
            Collection<Content> optionNodes = getOptionNodes(configNode);
            if(optionNodes != null) {
                Iterator<Content> it = getOptionNodes(configNode).iterator();
                while (it.hasNext()) {
                    Content n = ((Content) it.next());
                    String valueNodeData = this.getConfigValue("valueNodeData", "uuid");
                    String labelNodeData = this.getConfigValue("labelNodeData", "name");
    
                    String value = n.getName();
                    if (valueNodeData.equals("uuid")) {
                        value = n.getUUID();
                    } else if (valueNodeData.equals("handle")) {
                        value = n.getHandle();
                    } else if (n.hasNodeData(valueNodeData)) {
                        value = NodeDataUtil.getString(n, valueNodeData);
                    }
                    
                    String label = NodeDataUtil.getString(n, labelNodeData, n.getName());
    
                    Button button = new IndentedButton(this.getName(), value, "width:" + n.getLevel() * 10+ "px;");
                    button.setLabel(label);
                    String iconSrc = n.getNodeData("iconSrc").getString();
                    if (StringUtils.isNotEmpty(iconSrc)) {
                        button.setIconSrc(iconSrc);
                    }
    
                    if (setDefaultSelected && n.getNodeData("selected").getBoolean()) { 
                        button.setState(ControlImpl.BUTTONSTATE_PUSHED);
                       
                    }
                    options.add(button);
                }
            }
        } catch (RepositoryException e) {
            if (log.isDebugEnabled()) {
                log.debug("Exception caught: " + e.getMessage(), e); //$NON-NLS-1$
            }
        }
        this.setOptions(options);
    }

    
    @Override
    protected Collection<Content> getOptionNodes(Content configNode) {
        String templateCategory = this.getConfigValue("category", "feature");
        String templateSubcategory = this.getConfigValue("subcategory", "product-category");
        String path = this.getConfigValue("path", "");
        if (StringUtils.isEmpty(path)) {
            path = ShopUtil.getShopNameFromPath();
        }
        if (StringUtils.isNotEmpty(path) && path.startsWith("/")) {
            path = path.substring(1);
        }
        
        Content shop = ShopUtil.getShopRootByShopName(path);
        if(shop != null) {
            try {
                List<Content> categories = STKUtil.getContentListByTemplateCategorySubCategory(shop, templateCategory, templateSubcategory);
                if(categories.isEmpty()) {
                    log.warn("Can not find categories for shop " + path);
                } else {
                    return categories;
                }
            } catch (RepositoryException e) {
                log.error("Error finding categories for shop " + path);
            }
            
        }
        return null;
    }
    
    
}
