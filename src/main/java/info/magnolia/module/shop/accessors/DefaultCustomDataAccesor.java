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
package info.magnolia.module.shop.accessors;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.jcr.wrapper.I18nNodeWrapper;

import java.util.Collection;

import javax.jcr.Node;

import org.apache.jackrabbit.util.ISO9075;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CustomData interface implementation.
 * @author tmiyar
 *
 */
public abstract class DefaultCustomDataAccesor implements CustomDataAccesor {

    private Node node;
    private String name;

    protected static Logger log = LoggerFactory.getLogger(DefaultCustomDataAccesor.class);

    public DefaultCustomDataAccesor(String name) throws Exception {
        node = this.getNode(name);
        this.name = name;

    }
    protected Node getNodeByName(String rootPath, String nodeType, String name) throws Exception {
        String xpath = "/jcr:root" + rootPath + "//" + ISO9075.encode(name);
        Collection<Content> nodeCollection = QueryUtil.query("data", xpath, "xpath", nodeType);

        if(!nodeCollection.isEmpty()) {
            return new I18nNodeWrapper(nodeCollection.iterator().next().getJCRNode());
        }
        throw new Exception(name + "of type=" + nodeType + "not found in path=" + rootPath);
    }

    public Node getNode() {
        return node;
    }

    public String getName() {
        return name;
    }

    protected abstract Node getNode(String name) throws Exception;


}
