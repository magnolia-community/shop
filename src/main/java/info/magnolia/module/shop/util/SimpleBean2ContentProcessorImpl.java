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
package info.magnolia.module.shop.util;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.content2bean.Bean2ContentProcessor;
import info.magnolia.content2bean.Bean2ContentTransformer;
import info.magnolia.content2bean.Content2BeanException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean to content.
 * @author will
 */
public class SimpleBean2ContentProcessorImpl implements Bean2ContentProcessor {

    private Logger log = LoggerFactory.getLogger(SimpleBean2ContentProcessorImpl.class);

    @Override
    public Content toContent(Content arg0, Object arg1, boolean arg2, Bean2ContentTransformer arg3) {
        log.error("Not supported yet.");
        return null;
    }

    @Override
    public Content setNodeDatas(Content node, Object bean, Bean2ContentTransformer transformer) throws Content2BeanException {
        if (node != null && bean != null) {
            if (transformer != null) {
                log.error("No transformer supported yet");
            }
            Map data;
            if (bean instanceof Collection) {
                // "convert" collection to map
                data = new HashMap();
                int i = 0;
                Iterator values = ((Collection) bean).iterator();
                while (values.hasNext()) {
                    data.put("" + i, values.next());
                    i++;
                }
            } else if (bean instanceof Map) {
                data = (Map) bean;
            } else {
                // @todo make sure this works!
                data = toMap(bean);
            }
            // @todo Reorganize this by separating simple values (nodeData) from
            // complex values so you can first set all node datas, then save and
            // look at the complex values
            String key;
            Object value;
            NodeData nd;
            for (Iterator iter = data.keySet().iterator(); iter.hasNext();) {
                key = (String) iter.next();
                value = data.get(key);
                if (value != null) {
//                    log.debug("Value for key \"" + key + "\": " + value.getClass().getName());
                    // check if the value can be stored in a nodeData or if its
                    // a complex value which needs to be stored in a node
                    if (value instanceof Collection || value instanceof Map) {
                        try {
                            // remove any node datas for that name
                            if (node.getNodeData(key).isExist()) {
                                node.deleteNodeData(key);
                                node.getParent().save();
                            }
                            // get or create a content node for that name
                            // but first, the node needs to be saved...
                            node.getParent().save();
                            Content cn = ContentUtil.getOrCreateContent(node, key, ItemType.CONTENTNODE, true);
//                            node.getParent().save();
                            // if the contentNode already existed, clear out its data
                            Collection childNodes = cn.getChildren();
                            if (childNodes != null && !childNodes.isEmpty()) {
                                Iterator<Content> childIter = childNodes.iterator();
                                while (childIter.hasNext()) {
                                    childIter.next().delete();
                                }
                            }
                            Collection nodeDatas = cn.getNodeDataCollection();
                            if (nodeDatas != null && !nodeDatas.isEmpty()) {
                                Iterator<NodeData> nodeDataIter = nodeDatas.iterator();
                                while (nodeDataIter.hasNext()) {
                                    cn.deleteNodeData(nodeDataIter.next().getName());
                                }
                            }
                            node.save();
                            // now fill in the new data
                            setNodeDatas(cn, value, transformer);
                        } catch (PathNotFoundException ex) {
                            log.error("Could not prepare subnode " + key + " in node " + node.getHandle() + " for data.", ex);
                        } catch (RepositoryException ex) {
                            log.error("Could not prepare subnode " + key + " in node " + node.getHandle() + " for data.", ex);
                        }
                    } else {
                        try {
                            // remove any old subnode with that name
                            if (ContentUtil.getContent(node, key) != null) {
                                node.delete(key);
                                node.getParent().save();
                            }
                            // create a new nodeData
                            nd = NodeDataUtil.getOrCreate(node, key);
                            // set the correct value
                            if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof BigInteger) {
                                nd.setValue(((Number) value).longValue());
                            } else if (value instanceof Float || value instanceof Double || value instanceof BigDecimal) {
                                nd.setValue(((Number) value).doubleValue());
                            } else if (value instanceof Calendar) {
                                nd.setValue((Calendar) value);
                            } else if (value instanceof Date) {
                                GregorianCalendar cal = new GregorianCalendar();
                                cal.setTime((Date) value);
                                nd.setValue(cal);
                            } else if (value instanceof Boolean) {
                                nd.setValue(((Boolean) value).booleanValue());
                            } else {
                                nd.setValue(value.toString());
                            }
                        } catch (AccessDeniedException ex) {
                            log.error("Could not set simple value " + value + " in node " + node.getHandle(), ex);
                        } catch (RepositoryException ex) {
                            log.error("Could not set simple value " + value + " in node " + node.getHandle(), ex);
                        }
                    }
                } else {
                    log.debug("Value for key \"" + key + "\" is null!");
//                            if (node.getNodeData(name).isExist()) {
//                                node.deleteNodeData(name);
//                            }
                }
            }
            try {
                node.updateMetaData();
                node.getParent().save();
            } catch (AccessDeniedException ex) {
                log.error("Could not save node " + node.getHandle() + " after updating it.", ex);
            } catch (RepositoryException ex) {
                log.error("Could not save node " + node.getHandle() + " after updating it.", ex);
            }
        } else {
            log.error("Either node or bean empty!");
        }
        return node;
    }

    static private Map toMap(Object bean) throws Content2BeanException {
        try {
            return BeanUtils.describe(bean);
        } catch (Exception e) {
            throw new Content2BeanException("can't read properties from bean", e);
        }
    }
}
