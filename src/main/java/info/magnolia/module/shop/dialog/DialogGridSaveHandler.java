/**
 * This file Copyright (c) 2008-2011 Magnolia International
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

import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.gui.control.ControlImpl;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.FactoryUtil;
import info.magnolia.content2bean.Bean2ContentProcessor;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.module.admininterface.FieldSaveHandler;
import info.magnolia.module.shop.util.SimpleBean2ContentProcessorImpl;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Save handler for the gird dialog.
 * @author Will Scheidegger
 */
public class DialogGridSaveHandler implements FieldSaveHandler {

    private static Logger log = LoggerFactory.getLogger(DialogGridSaveHandler.class);

    /**
     * 
     * @param parentNode
     * @param configNode
     * @param name
     * @param form
     * @param type
     * @param valueType
     * @param isRichEditValue
     * @param encoding
     * @throws RepositoryException
     * @throws AccessDeniedException
     */
    public void save(Content parentNode, Content configNode, java.lang.String name,
            MultipartForm form, int type, int valueType, int isRichEditValue,
            int encoding) throws RepositoryException, AccessDeniedException {
        // 0. get a DialogControl instance for this DialogGrid
        WebContext wc = MgnlContext.getWebContext();
//        DialogGrid control = (DialogGrid) DialogFactory.getDialogControlInstanceByName(wc.getRequest(), wc.getResponse(), parentNode, configNode, configNode.getNodeData("controlType").getString());
        // 1. get JSON string from form
        String json = form.getParameter(name + "Persisted");
        if (StringUtils.isNotBlank(json)) {
            // 2. convert it to a JSON object
            JSONArray jsonArray = JSONArray.fromObject(json);
            Collection data = JSONArray.toList(jsonArray, HashMap.class);
            // convert values according to the used controls
            Map row;
            Iterator rows = data.iterator();
            Content gridControlsNode = ContentUtil.getContent(configNode, "gridControls");
            if (gridControlsNode != null) {
                Collection gridControlNodes = gridControlsNode.getChildren(ItemType.CONTENTNODE);
                Iterator gcnIter = gridControlNodes.iterator();
                Content gridControlNode;
                while (rows.hasNext()) {
                    gcnIter = gridControlNodes.iterator();
                    row = (Map) rows.next();
                    while (gcnIter.hasNext()) {
                        gridControlNode = (Content) gcnIter.next();
                        if (row.get(gridControlNode.getName()) != null) {
                            //
                            int gridControlType = PropertyType.STRING;
                            int gridControlValueType = ControlImpl.VALUETYPE_SINGLE;
                            int gridControlIsRichEditValue = 0;
                            int gridControlEncoding = ControlImpl.ENCODING_NO;
                            String[] values = {StringUtils.EMPTY};
                            if (gridControlNode.getNodeData("type").isExist()) {
                                String typeString = gridControlNode.getNodeData("type").getString();
                                if (typeString.equalsIgnoreCase("LONG")) {
                                    gridControlType = PropertyType.LONG;
                                } else if (typeString.equalsIgnoreCase("DOUBLE")) {
                                    gridControlType = PropertyType.DOUBLE;
                                } else if (typeString.equalsIgnoreCase("DATE")) {
                                    gridControlType = PropertyType.DATE;
                                } else if (typeString.equalsIgnoreCase("BOOLEAN")) {
                                    gridControlType = PropertyType.BOOLEAN;
                                } else if (typeString.equalsIgnoreCase("BINARY")) {
                                    gridControlType = PropertyType.BINARY;
                                }
                            }
                            /*                                if (info.length >= 3) {
                            valueType = Integer.valueOf(info[2]).intValue();
                            }
                            if (info.length >= 4) {
                            isRichEditValue = Integer.valueOf(info[3]).intValue();
                            }
                            if (info.length >= 5) {
                            encoding = Integer.valueOf(info[4]).intValue();
                            }*/
                        }
                    }
                }
            }
            try {
                log.debug("data: " + data);
                Bean2ContentProcessor b2cp = (Bean2ContentProcessor) FactoryUtil.getSingleton(SimpleBean2ContentProcessorImpl.class);
                Content node = ContentUtil.getContent(parentNode, name); //parentNode.getContent(name);
                if (node != null) {
                    // delete the old content node
                    // @TODO: backup instead of delete might be a good idea
                    node.delete();
                }
                node = ContentUtil.getOrCreateContent(parentNode, name, ItemType.CONTENTNODE, false);
                b2cp.setNodeDatas(node, data, null);
            } catch (Content2BeanException ex) {
                log.error("Could not set data in node", ex);
            }

            /*            Collection data = new ArrayList();
            for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONDynaBean) {
            Map map = ((JSONDynaBean) value).JSONArray.toList(jsonArray);
            }
            }*/
//List output = (List) JSONSerializer.toJava( jsonArray );
//            log.debug("json object for " + name + ": " + jsonObject.)
/*            log.debug(name + " data collection: " + data);
            // 3. save in corresponding subnode
            Content node = ContentUtil.getOrCreateContent(parentNode, name, ItemType.CONTENTNODE, true);
            if (node != null) {
            try {
            Bean2ContentProcessor b2cp = (Bean2ContentProcessor) FactoryUtil.getSingleton(SimpleBean2ContentProcessorImpl.class);
            b2cp.setNodeDatas(node, data, null);
            } catch (Content2BeanException ex) {
            log.error("Could not set data in node " + node.getHandle(), ex);
            }
            }*/
            /*            Collection data = new ArrayList();
            JSONArray jsonArr = new JSONArray(json);
            for (int i=0; i<jsonArr.length(); i++) {
            }
            data = JSONArray.toList(jsonArr);
            log.debug(name + " data collection: " + data);
            // 3. save in corresponding subnode
            Content node = ContentUtil.getOrCreateContent(parentNode, name, ItemType.CONTENTNODE, true);
            if (node != null) {
            try {
            Bean2ContentProcessor b2cp = (Bean2ContentProcessor) FactoryUtil.getSingleton(SimpleBean2ContentProcessorImpl.class);
            b2cp.setNodeDatas(node, data, null);
            } catch (Content2BeanException ex) {
            log.error("Could not set data in node " + node.getHandle(), ex);
            }
            }*/
            /*            Collection data = new ArrayList();
            for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONDynaBean) {
            Map map = ((JSONDynaBean) value).JSONArray.toList(jsonArray);
            }
            }*/
//List output = (List) JSONSerializer.toJava( jsonArray );
//            log.debug("json object for " + name + ": " + jsonObject.)
/*            log.debug(name + " data collection: " + data);
            // 3. save in corresponding subnode
            Content node = ContentUtil.getOrCreateContent(parentNode, name, ItemType.CONTENTNODE, true);
            if (node != null) {
            try {
            Bean2ContentProcessor b2cp = (Bean2ContentProcessor) FactoryUtil.getSingleton(SimpleBean2ContentProcessorImpl.class);
            b2cp.setNodeDatas(node, data, null);
            } catch (Content2BeanException ex) {
            log.error("Could not set data in node " + node.getHandle(), ex);
            }
            }*/

            /*            Collection data = new ArrayList();
            JSONArray jsonArr = new JSONArray(json);
            for (int i=0; i<jsonArr.length(); i++) {
            }
            data = JSONArray.toList(jsonArr);

            log.debug(name + " data collection: " + data);
            // 3. save in corresponding subnode
            Content node = ContentUtil.getOrCreateContent(parentNode, name, ItemType.CONTENTNODE, true);
            if (node != null) {
            try {
            Bean2ContentProcessor b2cp = (Bean2ContentProcessor) FactoryUtil.getSingleton(SimpleBean2ContentProcessorImpl.class);
            b2cp.setNodeDatas(node, data, null);
            } catch (Content2BeanException ex) {
            log.error("Could not set data in node " + node.getHandle(), ex);
            }
            }*/
        } else {
            log.debug("No " + name + "Persisted parameter value found... deleting existing subnodes?");
        }
    }
}
