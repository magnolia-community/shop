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

import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.objectfactory.Components;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.content2bean.Bean2ContentProcessor;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.module.admininterface.FieldSaveHandler;
import info.magnolia.module.shop.util.SimpleBean2ContentProcessorImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Save handler.
 * @author will
 */
public class DialogProductPricesSaveHandler implements FieldSaveHandler {

  private static Logger log = LoggerFactory.getLogger(DialogProductPricesSaveHandler.class);

  public void save(Content parentNode, Content configNode, java.lang.String name, MultipartForm form, int type,
      int valueType, int isRichEditValue, int encoding) throws RepositoryException, AccessDeniedException {
    // get all price categories
    String priceCategoryNodeType = "shopPriceCategory";
    if (configNode.getNodeData("priceCategoryNodeType").isExist()) {
      priceCategoryNodeType = configNode.getNodeData("priceCategoryNodeType").getString();
    }
    String priceCategoryPath = "/shop/pricecategories";
    if (configNode.getNodeData("priceCategoryPath").isExist()) {
      priceCategoryPath = configNode.getNodeData("priceCategoryPath").getString();
    }
    if (priceCategoryPath.endsWith("/")) {
      priceCategoryPath = StringUtils.substringBeforeLast(priceCategoryPath, "/");
    }
    String queryString = "SELECT * from " + priceCategoryNodeType + " WHERE jcr:path LIKE '" + priceCategoryPath
        + "/%'";
    Collection priceCategories = QueryUtil.query("data", queryString, "sql", priceCategoryNodeType);

    // look for prices for each category
    String price, priceCategoryUUID;
    ArrayList prices = new ArrayList();
    HashMap currPrice;
    Double priceValue;
    for (int i = 0; i < priceCategories.size(); i++) {
      currPrice = new HashMap();
      price = form.getParameter(name + "_price_" + i);
      if (StringUtils.isNotBlank(price)) {
        // TODO: maybe one should clear out any illegal character like
        // grouping characters etc. (i.e. anything other than 0-9 and .)
        priceValue = new Double(price);
        currPrice.put("price", priceValue);
      }
      priceCategoryUUID = form.getParameter(name + "_priceCategoryUUID_" + i);
      currPrice.put("priceCategoryUUID", priceCategoryUUID);
      prices.add(currPrice);
    }

    try {
      log.debug("data: " + prices);
      Bean2ContentProcessor b2cp = (Bean2ContentProcessor) Components
          .getSingleton(SimpleBean2ContentProcessorImpl.class);
      parentNode.getParent().save();
      Content node = ContentUtil.getOrCreateContent(parentNode, name, ItemType.CONTENTNODE, true);
      b2cp.setNodeDatas(node, prices, null);
    } catch (Content2BeanException ex) {
      log.error("Could not set data in node " + parentNode.getHandle(), ex);
    }
  }
}
