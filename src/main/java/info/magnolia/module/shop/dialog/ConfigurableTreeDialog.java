/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.magnolia.module.shop.dialog;

import info.magnolia.cms.core.Content;
import info.magnolia.module.data.dialogs.DataDialog;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This dialog is needed to refresh a {@link ch.fastforward.magnolia.module.utils.trees.ConfigurableTreeConfiguration}
 * properly after you save the dialog. You will need to configure the dialog
 * with the name of the tree though.<br />
 * You can specify what should happen after you save a dialog by entering a
 * JavaScript in a "jsExecutedAfterSaving" nodeData of the dialog. If you do not
 * specify anything, the data dialogs will try to refresh the table at it's
 * current position, using the path... and the node type name. This however
 * means that the tree name has to correspond with the node type name. For the
 * configurable tree which is capable of displaying and editing multiple node
 * types this will not work. Hence this class where you can manually specify the
 * name of the tree which should be desplayed after saving by setting the
 * "treeName" nodeData in the dialog configuration.<br />
 * <br />
 * If you want to use the same dialog in several trees, you might find
 * {@link RootSpecificConfigurableTreeDialog} useful.
 *
 * @author will
 * @see RootSpecificConfigurableTreeDialog
 */
public class ConfigurableTreeDialog extends DataDialog {

    private static final Logger log = LoggerFactory.getLogger(ConfigurableTreeDialog.class);
    private String treeName;

    /**
     * @param name
     * @param request
     * @param response
     * @param configNode
     */
    public ConfigurableTreeDialog(String name, HttpServletRequest request, HttpServletResponse response, Content configNode) {
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
            String js = "opener.mgnl.data.DataTree.reloadAfterEdit('" + getTreeName() + "', '" + this.path + "/" + this.nodeName + "')";
            log.debug("my js: " + js);
            return js;
        }
    }

    /**
     * @return the treeName
     */
    public String getTreeName() {
        return treeName;
    }

    /**
     * @param treeName the treeName to set
     */
    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }
}
