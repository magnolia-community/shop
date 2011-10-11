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
import info.magnolia.cms.gui.dialog.DialogControlImpl;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.templatingkit.sites.Site;
import info.magnolia.module.templatingkit.sites.SiteManager;
import info.magnolia.module.templatingkit.util.STKUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * will read the languaes from the configuration to be able to display dialogs from
 * data repository in the defined languajes.
 * @author tmiyar
 *
 */
public class DialogLanguagesUtil {
    private static Logger log = LoggerFactory.getLogger(DialogLanguagesUtil.class);
    
    public static void initSiteKey(DialogControlImpl controlImpl, Content storageNode) {
        if (storageNode != null) {
            try {
                Content rootDataNode = storageNode.getAncestor(1);
                controlImpl.setConfig("siteKey", rootDataNode.getName());
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
    public static ArrayList<String> initLanguages(DialogControlImpl controlImpl) {
        // set the languages for the site key by assuming that the languages are
        // defined at default site definition
        Site site = null;
        if (controlImpl.getConfigValue("siteKey") != null) {
            String siteKey = controlImpl.getConfigValue("siteKey");
            site = SiteManager.Factory.getInstance().getSite(siteKey);
        }
        if (site == null) {
            if (controlImpl.getStorageNode() != null) {
                site = STKUtil.getSite(controlImpl.getStorageNode());
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
        return languageList;
    }
}