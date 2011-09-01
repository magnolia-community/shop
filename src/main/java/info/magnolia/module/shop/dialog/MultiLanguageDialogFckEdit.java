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
import info.magnolia.context.MgnlContext;
import info.magnolia.module.fckeditor.dialogs.FckEditorDialog;
import info.magnolia.module.templatingkit.sites.Site;
import info.magnolia.module.templatingkit.sites.SiteManager;
import info.magnolia.module.templatingkit.util.STKUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The MultiLanguageDialogFckEdit is an FckEditDialog which will display an
 * editor for each langauge. The languages are currently read from
 * config:/server/i18n/content/[siteKey] where "siteKey" is the name of the root
 * node of the node the dialog is used on. The languages are stored exactly the
 * same way as they are in config:/server/i18n/content/locales. So depending on
 * the site you are using this dialog for, it will display different language
 * version fields.
 * 
 * TODO: Switch the language configuration technique to the official Magnolia
 * site configuration.
 * @author will
 */
public class MultiLanguageDialogFckEdit extends FckEditorDialog implements MultiLanguageDialogControl {

    /**
     * logger.
     */
    private static Logger log = LoggerFactory.getLogger(MultiLanguageDialogFckEdit.class);
    private List<String> languages;
    /**
     * Used to make sure that the javascript files are loaded only once.
     */
    private static final String ATTRIBUTE_FCKED_LOADED = "info.magnolia.cms.gui.dialog.fckedit.loaded";
    private static final String PARAM_COLORS = "colors";
    private static final String PARAM_COLORS_DEFAULT = "";
    private static final String PARAM_LISTS = "lists";
    private static final String PARAM_LISTS_DEFAULT = "true";
    private static final String PARAM_ALIGNMENT = "alignment";
    private static final String PARAM_ALIGNMENT_DEFAULT = "false";
    private static final String PARAM_ENTER_MODE = "enterMode";
    private static final String PARAM_SHIFT_ENTER_MODE = "shiftEnterMode";
    private static final String PARAM_ENTER_MODE_DEFAULT = "p";
    private static final String PARAM_SHIFT_ENTER_MODE_DEFAULT = "br";

    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content storageNode, Content configNode) throws RepositoryException {
        // First, try to set the languages by looking at the path of the storage
        // node
        if (storageNode != null) {
            initSiteKey(storageNode);
        } else {
            String mgnlPath = request.getParameter("mgnlPath");
            if (mgnlPath.startsWith("/")) {
                // this should be the case
                mgnlPath = mgnlPath.substring(1);
            }
            mgnlPath = StringUtils.substringBefore(mgnlPath, "/");
            setConfig("siteKey", mgnlPath);
        }
        // init the rest
        super.init(request, response, storageNode, configNode);
        // if the languages have not been initialized yet
        if (getConfigValue("siteKey") == null) {
            initSiteKey(storageNode);
        }
        initLanguages();
    }

    private void initSiteKey(Content storageNode) {
        if (storageNode != null) {
            try {
                Content rootDataNode = storageNode.getAncestor(1);
                setConfig("siteKey", rootDataNode.getName());
            } catch (PathNotFoundException ex) {
                log.error("Could not get level 1 node of " + storageNode.getHandle(), ex);
            } catch (RepositoryException ex) {
                log.error("Could not get level 1 node of " + storageNode.getHandle(), ex);
            }
        }
    }

    // TODO: MultiLanguageDialogEdit and MultiLanguageDialogFckEdit share some common code
    // which should be "outsourced"
    private void initLanguages() {
        // set the languages for the site key by assuming that the languages are
        // defined at default site definition
        Site site = null;
        if (getConfigValue("siteKey") != null) {
            String siteKey = getConfigValue("siteKey");
            site = SiteManager.Factory.getInstance().getSite(getConfigValue("siteKey"));
        }
        if (site == null && this.getStorageNode() != null) {
            site = STKUtil.getSite(this.getStorageNode());
        }
        if (site == null) {
            site = STKUtil.getSite();
        }
        Collection<Locale> locales = site.getI18n().getLocales();
        Iterator<Locale> localesIterator = locales.iterator();
        ArrayList<String> languageList = new ArrayList<String>();
        while (localesIterator.hasNext()) {
            languageList.add(localesIterator.next().getLanguage());
        }
        this.languages = languageList;
    }

    @Override
    public void drawHtml(Writer out) throws IOException {
        log.debug("Languages are: " + this.getLanguages());
        if (languages != null && languages.size() > 0) {
            // get the config values
            String jsInitFile = this.getConfigValue(PARAM_JS_INIT_FILE, PARAM_JS_INIT_FILE_DEFAULT);
            String customConfigurationPath = this.getConfigValue(PARAM_CUSTOM_CONFIGURATION_PATH, this.getConfigValue(
                    "customConfigurationPath",
                    PARAM_CUSTOM_CONFIGURATION_PATH_DEFAULT));
            String height = this.getConfigValue(PARAM_HEIGHT, PARAM_HEIGHT_DEFAULT);
            String width = this.getConfigValue(PARAM_WIDTH, PARAM_WIDTH_DEFAULT);

            this.drawHtmlPre(out);

            // load the script onece: if there are multiple instances
            if (getRequest().getAttribute(ATTRIBUTE_FCKED_LOADED) == null) {
                out.write("<script type=\"text/javascript\" src=\"" //$NON-NLS-1$
                        + this.getRequest().getContextPath() + "/.resources/fckeditor/fckeditor.js\"></script>"); //$NON-NLS-1$
                getRequest().setAttribute(ATTRIBUTE_FCKED_LOADED, "true"); //$NON-NLS-1$
            }

            for (int i = 0; i < languages.size(); i++) {
                String id = getName();

                if (id == null) {
                    log.error("Missing id for fckEditor instance"); //$NON-NLS-1$
                } else {
                    id += "_" + languages.get(i);
                }

                String var = getVarName() + "_" + languages.get(i);
//                String value = convertToView(getValue());
                String value = null;
                if (getStorageNode() != null) {
                    value = convertToView(getStorageNode().getNodeData(getName() + "_" + languages.get(i)).getString());
                }
                out.write("<label for=\"" + id + "\">" + languages.get(i) + "</label>");
                out.write("<script type=\"text/javascript\">"); //$NON-NLS-1$
                out.write("// <![CDATA[\n"); //$NON-NLS-1$

                // make the configuration accessible to the config javascript
                writeMgnlFCKConfig(out, id);

                out.write("var " + var + " = null;"); //$NON-NLS-1$ //$NON-NLS-2$
                out.write("fckInstance = new FCKeditor( '" + id + "' );"); //$NON-NLS-1$ //$NON-NLS-2$
                out.write("fckInstance.Value = '" + escapeJsValue(value) + "';"); //$NON-NLS-1$ //$NON-NLS-2$
                out.write("fckInstance.BasePath = '" + this.getRequest().getContextPath() + FCKEDIT_PATH + "';"); //$NON-NLS-1$ //$NON-NLS-2$

                if (StringUtils.isNotEmpty(height)) {
                    out.write("fckInstance.Height = '" + this.getConfigValue("height") + "';"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }

                if (StringUtils.isNotEmpty(width)) {
                    out.write("fckInstance.Width = '" + this.getConfigValue("width") + "';"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }

                // now set the custom configuration path
                if (StringUtils.isNotEmpty(customConfigurationPath)) {
                    out.write("fckInstance.Config['CustomConfigurationsPath'] = '" //$NON-NLS-1$
                            + this.getRequest().getContextPath() + customConfigurationPath + "';"); //$NON-NLS-1$
                }

                // here we pass the parameters to the custom configuration file --> via
                // javascript

                // start the initfile
                if (jsInitFile.length() > 0) {
                    out.write("// ]]>\n"); //$NON-NLS-1$
                    out.write("</script>"); //$NON-NLS-1$
                    out.write("<script type=\"text/javascript\" src=\"" //$NON-NLS-1$
                            + this.getRequest().getContextPath() + jsInitFile + "\"></script>\n"); //$NON-NLS-1$
                    out.write("<script type=\"text/javascript\">"); //$NON-NLS-1$
                    out.write("// <![CDATA[\n"); //$NON-NLS-1$
                }

                // finaly create the editor
                out.write("fckInstance.Create();"); //$NON-NLS-1$
                out.write(var + " = fckInstance;"); //$NON-NLS-1$
                out.write("// ]]>\n"); //$NON-NLS-1$
                out.write("</script>"); //$NON-NLS-1$

                // write the saveInfo for the writing back to the repository
                out.write("<input type='hidden' name='mgnlSaveInfo' value='" //$NON-NLS-1$
                        + id + ",String," //$NON-NLS-1$
                        + ControlImpl.VALUETYPE_SINGLE + "," //$NON-NLS-1$
                        + ControlImpl.RICHEDIT_FCK + "," //$NON-NLS-1$
                        + ControlImpl.ENCODING_NO + "' />"); //$NON-NLS-1$
            }

            this.drawHtmlPost(out);
        } else {
            super.drawHtml(out);
        }
    }

    private void writeMgnlFCKConfig(Writer out, String id) throws IOException {
        String css = this.getConfigValue(PARAM_CSS, PARAM_CSS_DEFAULT);
        String fonts = this.getConfigValue(PARAM_FONTS, PARAM_FONTS_DEFAULT);
        String fontSizes = this.getConfigValue(PARAM_FONT_SIZES, PARAM_FONT_SIZES_DEFAULT);
        String colors = this.getConfigValue(PARAM_COLORS, PARAM_COLORS_DEFAULT);
        String styles = this.getConfigValue(PARAM_STYLES, PARAM_STYLES_DEFAULT);
        String templates = this.getConfigValue(PARAM_TEMPLATES, PARAM_TEMPLATES_DEFAULT);

        String lists = this.getConfigValue(PARAM_LISTS, PARAM_LISTS_DEFAULT);
        String alignment = this.getConfigValue(PARAM_ALIGNMENT, PARAM_ALIGNMENT_DEFAULT);
        String tables = this.getConfigValue(PARAM_TABLES, PARAM_TABLES_DEFAULT);
        String images = this.getConfigValue(PARAM_IMAGES, PARAM_IMAGES_DEFAULT);
        String source = this.getConfigValue(PARAM_SOURCE, PARAM_SOURCE_DEFAULT);
        String enterMode = this.getConfigValue(PARAM_ENTER_MODE, PARAM_ENTER_MODE_DEFAULT);
        String shiftEnterMode = this.getConfigValue(PARAM_SHIFT_ENTER_MODE, PARAM_SHIFT_ENTER_MODE_DEFAULT);

        // create the the holder of the editors configs if not yet done
        out.write("if( window.MgnlFCKConfigs == null)\n");
        out.write("    window.MgnlFCKConfigs = new Object();\n");

        // add the config for this editor

        out.write("MgnlFCKConfigs." + id + " = new Object();\n");
        // string values
        out.write("MgnlFCKConfigs." + id + ".language = '" + MgnlContext.getUser().getLanguage() + "';\n");
        out.write("MgnlFCKConfigs." + id + ".contextPath = '" + getRequest().getContextPath() + "';\n");

        out.write("MgnlFCKConfigs." + id + ".repository = '" + getTopParent().getConfigValue("repository") + "';\n");
        out.write("MgnlFCKConfigs." + id + ".path = '" + getTopParent().getConfigValue("path") + "';\n");
        out.write("MgnlFCKConfigs." + id + ".nodeCollection = '" + getTopParent().getConfigValue("nodeCollection") + "';\n");
        out.write("MgnlFCKConfigs." + id + ".node = '" + getTopParent().getConfigValue("node") + "';\n");

        out.write("MgnlFCKConfigs." + id + ".css = '" + css + "';\n");
        out.write("MgnlFCKConfigs." + id + ".fonts = '" + fonts + "';\n");
        out.write("MgnlFCKConfigs." + id + ".fontSizes = '" + fontSizes + "';\n");
        out.write("MgnlFCKConfigs." + id + ".colors = '" + colors + "';\n");
        out.write("MgnlFCKConfigs." + id + ".styles = '" + styles + "';\n");
        out.write("MgnlFCKConfigs." + id + ".templates = '" + templates + "';\n");
        out.write("MgnlFCKConfigs." + id + ".enterMode = '" + enterMode + "';\n");
        out.write("MgnlFCKConfigs." + id + ".shiftEnterMode = '" + shiftEnterMode + "';\n");

        // boolean values
        out.write("MgnlFCKConfigs." + id + ".lists = " + lists + ";\n");
        out.write("MgnlFCKConfigs." + id + ".alignment = " + alignment + ";\n");
        out.write("MgnlFCKConfigs." + id + ".tables = " + tables + ";\n");
        out.write("MgnlFCKConfigs." + id + ".images = " + images + ";\n");
        out.write("MgnlFCKConfigs." + id + ".source = " + source + ";\n");
    }

    public List<String> getLanguages() {
        return this.languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
}
