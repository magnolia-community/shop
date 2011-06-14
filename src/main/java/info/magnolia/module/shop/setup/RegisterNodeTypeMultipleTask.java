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
package info.magnolia.module.shop.setup;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.module.InstallContext;
import info.magnolia.module.data.DataModule;
import info.magnolia.module.data.TypeDefinition;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

/**
 * Register a new node type with property multiple set to true so we can
 * use categorization on data types.
 * @author tmiyar
 *
 */
public class RegisterNodeTypeMultipleTask extends AbstractRepositoryTask {

    private String typeName;
    /**
     * Node type template for registering node types for the single types.
     */
    protected static final String NODE_TYPE_DEFINITION =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<nodeTypes"
        + " xmlns:rep=\"internal\""
        + " xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\""
        + " xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\""
        + " xmlns:mgnl=\"http://www.magnolia-cms.com/jcr/mgnl\""
        + " xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
        + "<nodeType name=\"{0}\" isMixin=\"false\" hasOrderableChildNodes=\"true\" primaryItemName=\"\">"
        + "<supertypes>"
        + "<supertype>dataItem</supertype>"
        + "</supertypes>"
        + "<propertyDefinition name=\"*\" requiredType=\"undefined\" autoCreated=\"false\" mandatory=\"false\" onParentVersion=\"COPY\" protected=\"false\" multiple=\"true\"/>"
        + "</nodeType>"
        + "</nodeTypes>";

    public static final MessageFormat NODE_TYPE_DEF_TEMPLATE = new MessageFormat(NODE_TYPE_DEFINITION);
    /**
     * Registers new data type during module installation.
     * @param typeName Name of the registered type.
     */
    public RegisterNodeTypeMultipleTask(String typeName) {
        super("Register node type", "Registers the '" + typeName + "' node type.");
        this.typeName = typeName;
    }

    protected void doExecute(InstallContext installContext)
            throws RepositoryException, TaskExecutionException {
        TypeDefinition type = new TypeDefinition();
        type.setName(typeName);
            String nodeTypes = null;
            try {
                nodeTypes = type.getNodeTypes();
                // default value
                if (StringUtils.isEmpty(nodeTypes)) {
                    nodeTypes = NODE_TYPE_DEF_TEMPLATE.format(new String[] { type.getName() });
                }
                ContentRepository.getRepositoryProvider(DataModule.getRepository()).registerNodeTypes(new ByteArrayInputStream(nodeTypes.getBytes()));
            } catch (RepositoryException e) {
                log.error("Failed to register node type: {}", nodeTypes);
                throw new RuntimeException(e);
            }
    }
}

