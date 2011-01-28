/**
 * This file Copyright (c) 2003-2009 Magnolia International
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
package info.magnolia.module.shop.trees;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.exchange.Syndicator;
import info.magnolia.cms.util.FactoryUtil;
import info.magnolia.module.admininterface.AdminTreeMVCHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import info.magnolia.cms.util.Rule;
import info.magnolia.context.MgnlContext;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author will
 */
public class ConfigurableAdminTree extends AdminTreeMVCHandler {
    public static Logger log = LoggerFactory.getLogger(ConfigurableAdminTree.class);

    public ConfigurableAdminTree(String name, HttpServletRequest request, HttpServletResponse response) {
        super(name, request, response);
    }

    /*    protected Context getCommandContext(String commandName) {
    Context context = MgnlContext.getInstance();

    // set general parameters (repository, path, ..)
    context.put(Context.ATTRIBUTE_REPOSITORY, this.getRepository());
    if (this.pathSelected != null) {
    // pathSelected is null in case of delete operation, it should be the responsibility of the caller
    // to set the context attributes properly
    context.put(Context.ATTRIBUTE_PATH, this.pathSelected);
    }

    if (commandName.equals("activate")) {
    context.put(BaseActivationCommand.ATTRIBUTE_SYNDICATOR, getActivationSyndicator(this.pathSelected));
    }

    return context;
    }*/

    @Override
    public Syndicator getActivationSyndicator(String path) {
        boolean recursive = (this.getRequest().getParameter("recursive") != null); //$NON-NLS-1$
        Rule rule = new Rule();

        rule.addAllowType(ItemType.NT_METADATA);
        rule.addAllowType(ItemType.NT_RESOURCE);

        // folders are handled by the recursive mechanism
        if (recursive) {
            ConfigurableTreeConfiguration conf = (ConfigurableTreeConfiguration) this.getConfiguration();
            Iterator nodeTypesIter = conf.getNodeTypes().iterator();
            Map currNodeTypeMap;
            while (nodeTypesIter.hasNext()) {
//                log.debug("We should add node type " + nodeTypesIter.next());
//                rule.addAllowType(newNodeName);
                currNodeTypeMap = (Map) nodeTypesIter.next();
                rule.addAllowType((String) currNodeTypeMap.get("nodeType"));
            }
//                rule.addAllowType(DataConsts.MODULE_DATA_CONTENT_NODE_TYPE);
        }

        Syndicator syndicator = (Syndicator) FactoryUtil.newInstance(Syndicator.class);
        syndicator.init(MgnlContext.getUser(), this.getRepository(), ContentRepository.getDefaultWorkspace(this.getRepository()), rule);

        return syndicator;
    }
}
