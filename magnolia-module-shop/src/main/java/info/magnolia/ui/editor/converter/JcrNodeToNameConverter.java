/**
 * This file Copyright (c) 2010-2015 Magnolia International
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
package info.magnolia.ui.editor.converter;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import com.machinezoo.noexception.Exceptions;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import info.magnolia.jcr.RuntimeRepositoryException;
import info.magnolia.ui.datasource.jcr.JcrDatasource;
import org.apache.commons.lang3.StringUtils;

/**
 * Converter class for a linkField to save a node by it's name.
 *
 * @author atuor
 */
public class JcrNodeToNameConverter extends AbstractJcrConverter<Node> {

    @Inject
    public JcrNodeToNameConverter(JcrDatasource datasource) {
        super(datasource);
    }

    @Override
    public Result<String> convertToModel(Node value, ValueContext context) {
        return Result.of(() -> {
            if (value == null) {
                return null;
            }
            return Exceptions.wrap().get(value::getName);
        }, Throwable::getMessage);
    }

    @Override
    public Node convertToPresentation(String nodeName, ValueContext context) {
        if (StringUtils.isBlank(nodeName)) {
            return null;
        }
        nodeName = "/" + nodeName;
        try {
            return getNodeByPath(nodeName);
        } catch (PathNotFoundException e) {
            return new MissingNode(nodeName, Exceptions.wrap().get(this::getWorkspace));
        } catch (RepositoryException e) {
            throw new RuntimeRepositoryException(e);
        }
    }
}
