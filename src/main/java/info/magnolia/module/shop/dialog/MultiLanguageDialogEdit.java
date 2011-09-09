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
import info.magnolia.cms.gui.dialog.DialogEdit;
import info.magnolia.cms.gui.misc.CssConstants;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.templatingkit.sites.Site;
import info.magnolia.module.templatingkit.sites.SiteManager;
import info.magnolia.module.templatingkit.util.STKUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The MultiLanguageDialogEdit is an Edit dialog which will display an input
 * field for each langauge. The languages are currently read from
 * config:/server/i18n/content/[siteKey] where "siteKey" is the name of the root
 * node of the node the dialog is used on. The languages are stored exactly the
 * same way as they are in config:/server/i18n/content/locales. So depending on
 * the site you are using this dialog for, it will display different language
 * version fields.
 * <br />
 * <br />
 * todo: Switch the language configuration technique to the official Magnolia
 * site configuration.
 * @author will
 */
public class MultiLanguageDialogEdit extends DialogEdit implements MultiLanguageDialogControl {

    /**
     * logger.
     */
    private static Logger log = LoggerFactory.getLogger(MultiLanguageDialogEdit.class);
    private List<String> languages;

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
        super.init(request, response, storageNode, configNode);
        // if the languages have not been initialized yet
        if (getConfigValue("siteKey") == null) {
            initSiteKey(storageNode);
        }
        initLanguages();
    }

    @Override
    public void drawHtml(Writer out) throws IOException {
        log.debug("Languages are: " + this.getLanguages());
        if (languages != null && languages.size() > 0) {
            this.drawHtmlPre(out);
            for (int i = 0; i < languages.size(); i++) {
                String name = this.getName() + "_" + languages.get(i);
                String value = null;
                if (getStorageNode() != null) {
                    value = getStorageNode().getNodeData(name).getString();
                }
                EditWithLabel control = new EditWithLabel(name, value);
                control.setLabel(languages.get(i));
                control.setType(this.getConfigValue("type", PropertyType.TYPENAME_STRING));
                if (this.getConfigValue("saveInfo").equals("false")) {
                    control.setSaveInfo(false);
                }
                control.setCssClass(CssConstants.CSSCLASS_EDIT);
                control.setRows(this.getConfigValue("rows", "1"));
                control.setCssStyles("width", this.getConfigValue("width", "100%"));
                if (this.getConfigValue("onchange", null) != null) {
                    control.setEvent("onchange", this.getConfigValue("onchange"));
                }
                out.write(control.getHtml());
            }
            this.drawHtmlPost(out);
        } else {
            super.drawHtml(out);
        }
    }

    private void initSiteKey(Content storageNode) {
        if (storageNode != null) {
            try {
                Content rootDataNode = storageNode.getAncestor(1);
                setConfig("siteKey", rootDataNode.getName());
            } catch (PathNotFoundException ex) {
                log.error("Could not get level 1 node of " + storageNode.getHandle(), ex);
            } catch (AccessDeniedException ex) {
                log.error("Could not get level 1 node of " + storageNode.getHandle(), ex);
            } catch (RepositoryException ex) {
                log.error("Could not get level 1 node of " + storageNode.getHandle(), ex);
            }
        }
    }

    // @TODO: Move this to utility class
    private void initLanguages() {
        // set the languages for the site key by assuming that the languages are
        // defined at default site definition
        Site site = null;
        if (getConfigValue("siteKey") != null) {
            String siteKey = getConfigValue("siteKey");
            site = SiteManager.Factory.getInstance().getSite(getConfigValue("siteKey"));
        }
        if (site == null) {
            if (this.getStorageNode() != null) {
                site = STKUtil.getSite(this.getStorageNode());
            } else {
                // new node -> we need to look at the path!
                String path = MgnlContext.getParameter("mgnlPath");
                String repository = MgnlContext.getParameter("mgnlRepository");
                if (StringUtils.isNotBlank(path) && StringUtils.isNotBlank(repository)) {
                    Content parentNode = ContentUtil.getContent(repository, path);
                    if (parentNode != null) {
                        site = STKUtil.getSite(parentNode);
                    }
                }
            }
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

    public List<String> getLanguages() {
        return this.languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
}
