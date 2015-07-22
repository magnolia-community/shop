/**
 * This file Copyright (c) 2013-2015 Magnolia International
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
package info.magnolia.module.shop.setup;

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.shop.ShopNodeTypes;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.repository.RepositoryConstants;

import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.io.IOUtils;

/**
 * RefactorPackageNameTask class specific for updating to 2.3.0. This migration task scan all component definitions, search for modelClass references to '..paragraphs' package then update it with '..components' name.
 */
public class RefactorPackageNameTask extends AbstractRepositoryTask {

    public static final String SQL2 = "select * from [nt:base] where %s like '%s%%'";
    public static final String MODEL_CLASS_PROPERTY_NAME = "modelClass";
    public static final String OLD_PACKAGE_PATH = "info.magnolia.module.shop.paragraphs.";
    public static final String NEW_PACKAGE_PATH = "info.magnolia.module.shop.components.";

    public static final String V_2_3_0_INVOICE_RESOURCE_PATH = "/info/magnolia/module/shop/invoice.html";

    public RefactorPackageNameTask() {
        super("Refactor package '..paragraphs' to '..components'.", "Task specific for updating to 2.3.0.");
    }

    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException {
        Session session = installContext.getJCRSession(RepositoryConstants.CONFIG);
        QueryManager manager = session.getWorkspace().getQueryManager();
        Query query = manager.createQuery(String.format(SQL2, MODEL_CLASS_PROPERTY_NAME, OLD_PACKAGE_PATH), javax.jcr.query.Query.JCR_SQL2);
        NodeIterator ni = query.execute().getNodes();
        while (ni.hasNext()) {
            Node node = ni.nextNode();
            String oldClassPath = PropertyUtil.getString(node, MODEL_CLASS_PROPERTY_NAME);
            PropertyUtil.setProperty(node, MODEL_CLASS_PROPERTY_NAME, oldClassPath.replace(OLD_PACKAGE_PATH, NEW_PACKAGE_PATH));
        }
        session.save();

        Session shopsSession = installContext.getJCRSession(ShopRepositoryConstants.SHOPS);
        try {
            String ftlTemplate = IOUtils.toString(this.getClass().getResourceAsStream(V_2_3_0_INVOICE_RESOURCE_PATH));
            for (Node shop : NodeUtil.getNodes(shopsSession.getRootNode(), ShopNodeTypes.SHOP)) {
                PropertyUtil.setProperty(shop, ShopUtil.CONFIGURED_INVOICE_TEMPLATE_PROPERTY_NAME, ftlTemplate);
            }
            shopsSession.save();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
