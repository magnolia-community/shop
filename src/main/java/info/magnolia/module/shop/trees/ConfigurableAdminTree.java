/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import info.magnolia.module.data.DataConsts;
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
