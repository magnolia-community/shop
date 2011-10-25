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
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.gui.control.ControlImpl;
import info.magnolia.cms.gui.dialog.Dialog;
import info.magnolia.cms.gui.dialog.DialogBox;
import info.magnolia.cms.gui.dialog.DialogButtonSet;
import info.magnolia.cms.gui.dialog.DialogFactory;
import info.magnolia.cms.gui.dialog.DialogHidden;
import info.magnolia.cms.gui.dialog.DialogStatic;
import info.magnolia.cms.gui.dialog.DialogTab;
import info.magnolia.cms.i18n.MessagesUtil;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.admininterface.SaveHandler;
import info.magnolia.module.data.DataConsts;
import info.magnolia.module.data.DataModule;
import info.magnolia.module.data.TypeDefinition;
import info.magnolia.module.data.dialogs.DataDialog;

import java.io.IOException;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This file is a copy of TypeSelectDataDialog, modified in order to open the correct
 * dialogs for shopProducts.
 * @author tmiyar
 *
 */
public class ProductTypeSelectDialog extends DataDialog {
    private final String paragraph;
    private static Logger log = LoggerFactory.getLogger(ProductTypeSelectDialog.class);
    
    public ProductTypeSelectDialog(String name, HttpServletRequest request,
            HttpServletResponse response, Content configNode) {
        super(name, request, response, configNode);
        paragraph = MgnlContext.getParameter("mgnlParagraph");
    }
    @Override
    protected boolean onPreSave(SaveHandler handler) {
        if (!StringUtils.isEmpty(paragraph)) {
            log.debug("presave - skip");
            return false;
        }

        return super.onPreSave(handler);
    }

    @Override
    protected boolean onPostSave(SaveHandler handler) {
        if (!StringUtils.isEmpty(paragraph)) {
            log.debug("postsave - skip");
            return false;
        }
        return super.onPostSave(handler);
    }

    /**
     * @see .DialogMVCHandler#createDialog(Content, Content)
     */
    @Override
    protected Dialog createDialog(Content configNode, Content websiteNode) throws RepositoryException {
        msgs = MessagesUtil.chainWithDefault(DataConsts.DATA_MESSAGES_FILE);

        List<TypeDefinition> typeDefinitions = DataModule.getInstance().getTypeDefinitions();
        if (paragraph != null || !"mgnlNew".equals(nodeName)) {
            log.debug("Dialog.editDialog:" + nodeName);
            final TypeDefinition type;
            // show the dialog of the chosen itemtype
            if (StringUtils.isNotEmpty(paragraph) || websiteNode != null) {
                String name = websiteNode == null ? paragraph : websiteNode.getJCRNode().getPrimaryNodeType().getName();
                String dialog = getDialogForType(name, typeDefinitions);
                configNode = configNode.getParent().getChildByName(dialog);
                // as always - guess the type name from the dialog name (and never mind that it is called "paragraph" ... wtf
                type = getType(typeDefinitions, name);
            } else if (configNode != null) {
                // again type from the dialog name
                type = getType(typeDefinitions, configNode.getName());
            } else {
                type = null;
            }
            // no matter where this is called from we are editing type instance so we are in data workspace
            resetHM();
            //this might not be a new node (see the conditions above) so we have to set a websiteNode here
            if (websiteNode == null && type != null) {
                websiteNode = getTypeInstanceNodeFromName(type,  nodeName);
                if (websiteNode != null) {
                    this.path = websiteNode.getParent().getHandle();
                    if (nodeName.indexOf("/") > -1) {
                        this.nodeName = StringUtils.substringAfterLast(this.nodeName, "/");
                    }
                }
            }
            // managed to get a website node, create edit dialog 
            return super.createDialog(configNode, websiteNode);
        }
        log.debug("Dialog.createDialog");
        Dialog dialog = DialogFactory.getDialogInstance(request, response, null, null);
        dialog.setLabel("Create New"); //$NON-NLS-1$
        dialog.setConfig("saveLabel","OK"); //$NON-NLS-1$ //$NON-NLS-2$

        DialogHidden h1 = DialogFactory.getDialogHiddenInstance(request, response, null, null);
        h1.setName("mgnlParagraphSelected"); //$NON-NLS-1$
        h1.setValue("true"); //$NON-NLS-1$
        h1.setConfig("saveInfo", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        dialog.addSub(h1);
        DialogHidden h2 = DialogFactory.getDialogHiddenInstance(request, response, null, null);
        h1.setName("mgnlRepository"); //$NON-NLS-1$
        h1.setValue("data"); //$NON-NLS-1$
        h1.setConfig("saveInfo", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        dialog.addSub(h2);

        DialogTab tab = dialog.addTab();

        DialogStatic c0 = DialogFactory.getDialogStaticInstance(request, response, null, null);

        c0.setConfig("line", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        c0.setValue("Select Data Type"); //$NON-NLS-1$
        c0.setBoxType((DialogBox.BOXTYPE_1COL));
        tab.addSub(c0);

        DialogButtonSet c1 = DialogFactory.getDialogButtonSetInstance(request, response, null, null);
        c1.setName("mgnlParagraph"); //$NON-NLS-1$
        c1.setButtonType(ControlImpl.BUTTONTYPE_RADIO);
        c1.setBoxType(DialogBox.BOXTYPE_1COL);
        c1.setConfig("saveInfo", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        c1.setConfig("width", "100%"); //$NON-NLS-1$ //$NON-NLS-2$

        // lookup 3 selection criteria to select the item types by
        int level = StringUtils.countMatches(path, "/") - getFolderCount(path);
        String parentName = getParentName(level);

        // we need the rootpath also because at level 1 not every type necessarily has the same rootpath
        String typeName = parentName == null ? this.getName(): parentName;

        if(parentName == null) {
            configNode = configNode.getParent().getChildByName("shopProduct");
            return super.createDialog(configNode, websiteNode);
        } else if(parentName.equals("shopProduct")) {
            configNode = configNode.getParent().getChildByName("shopProductOptions");
            return super.createDialog(configNode, websiteNode);
        } else if(parentName.equals("shopProductOptions")){
            configNode = configNode.getParent().getChildByName("shopProductOption");
            return super.createDialog(configNode, websiteNode);
        }

        // no types to add found: show message
        if(c1.getOptions().size() == 0) {
            dialog.setConfig("saveLabel", ""); // hide save button
            dialog.setConfig("saveOnclick", "");
            DialogStatic noItems = DialogFactory.getDialogStaticInstance(request, response, null, null);
            noItems.setValue(msgs.get("dialog.typeselectdata.noitems"));
            noItems.setBoxType((DialogBox.BOXTYPE_1COL));
            tab.addSub(noItems);
        } else {
            tab.addSub(c1);
        }

        return dialog;
    }

    private Content getTypeInstanceNodeFromName(TypeDefinition type, String name) {
        if (type == null) {
            log.error("Failed to determine node type from type name {}", paragraph);
        } else {
            final String nodePath = StringUtils.removeEnd(type.getRootPath(), "/") + "/" + name;
            if (hm.isExist(nodePath)) {
                try {
                    return hm.getContent(nodePath);
                } catch (RepositoryException e) {
                    log.error("Failed to set edited content to " + nodePath + " with " + e.getMessage(), e);
                }
            }
        }
        return null;
    }

    private void resetHM() {
        if (!"data".equals(this.hm.getName())) {
            log.info("resetting hm for dialog {} from [{}] to [data]", getName(), hm.getName());
            repository = "data";
            hm = MgnlContext.getHierarchyManager(repository);
        }
    }

    private TypeDefinition getType(List<TypeDefinition> typeDefinitions,
            final String typeName) {
        for (TypeDefinition def : typeDefinitions) {
            if (def.getName().equals(typeName)) {
                return def;
            }
        }
        return null;
    }

    /**
     * Get count of number of folders in given path (minus one). This is used to determine the level of type currently dealt with.
     */
    private int getFolderCount(String path) throws RepositoryException {
        HierarchyManager dataHm = MgnlContext.getInstance().getHierarchyManager(DataModule.getRepository());
        Content c = dataHm.getContent(path);
        int count = 0;
        do {
            if ("dataFolder".equals(c.getItemType().getSystemName())) {
                count++;
            }
            c = c.getParent();
        } while (!c.getHandle().equals("/"));
        if (count > 0) {
            // discount the root folder for the type
            count --;
        }
        return count;
    }

    /**
     * @return
     * @throws RepositoryException
     */
    private String getParentName(int level) throws RepositoryException {
        String parentName = null;
        String levelPath = path;
        for(int x = level; x > 1; x--) {
            Content content = ContentUtil.getContent(DataModule.getRepository(), levelPath);
            String levelTypeName = content.getItemType().getSystemName();
            // correct level for folder
            if(levelTypeName.equals(DataConsts.FOLDER_ITEMTYPE)) {
                level = level - 1;
            } else {
                if(StringUtils.isEmpty(parentName)) {
                    parentName = levelTypeName;
                }
            }
            levelPath = StringUtils.substringBeforeLast(levelPath, "/");
        }
        return parentName;
    }

    private String getDialogForType(String typeName, List<TypeDefinition> typeDefinitions) {
        for (TypeDefinition typeDefinition : typeDefinitions) {
            if(typeDefinition.getName().equalsIgnoreCase(typeName)) {
                return typeDefinition.getDialog();
            }
        }
        log.debug("No typeDefinition found with name " + typeName);
        return null;
    }

    /**
     * @see info.magnolia.module.admininterface.DialogMVCHandler#save()
     */
    @Override
    public String save() {
        resetHM();
        if (StringUtils.isEmpty(paragraph)) {
            return super.save();
        }
        try {
            // copy all parameters except mgnlDialog
            StringBuffer query = new StringBuffer();

            for (String key : MgnlContext.getParameters().keySet()) {
                // mgnlParagraph in the query will cause endless loop on saving ... mgnlDialog was used before mgnlParagraph to wrongly denote the paragraph type
                if (!key.equals("mgnlDialog")) { //$NON-NLS-1$
                    if (query.length() != 0) {
                        query.append("&"); //$NON-NLS-1$
                    }
                    query.append(key);
                    query.append("="); //$NON-NLS-1$
                    query.append(MgnlContext.getParameter(key));
                }

            }
            //http://localhost:8080/magnoliaAuthor/.magnolia/dialogs/.html?name=bla&mgnlRichEPaste=&mgnlJsCallback=opener.document.location.reload();window.close();&mgnlSaveInfo=comment,String,0,0,0&mgnlRichE=&mgnlNode=mgnlNew&comment=&mgnlParagraph=&mgnlRepository=data&mgnlNodeCollection=&mgnlPath=/example
            // http://localhost:8080/magnoliaAuthor/.magnolia/dialogs/example.html?mgnlPath=/example&mgnlRepository=data&mgnlNode=mgnlNew&mgnlCK=1203122029072
            response.sendRedirect(request.getContextPath() + "/.magnolia/dialogs/" + this.paragraph + ".html?" + query); //$NON-NLS-1$ //$NON-NLS-2$
        }
        catch (IOException e) {
            log.error("can't redirect to the paragraph-dialog", e); //$NON-NLS-1$
        }
        return VIEW_NOTHING;
    }

}
