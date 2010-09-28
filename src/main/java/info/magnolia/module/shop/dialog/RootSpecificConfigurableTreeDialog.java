/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
