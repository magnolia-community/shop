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
package info.magnolia.module.shop.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.jcr.RepositoryException;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.shop.navigation.ProductCategoryNavigationModel;
import info.magnolia.module.shop.util.CustomDataUtil;
import info.magnolia.module.shop.util.ShopLinkImpl;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.shop.util.ShopLinkUtil.ParamType;
import info.magnolia.module.templating.RenderingModel;
import info.magnolia.module.templatingkit.navigation.Link;
import info.magnolia.module.templatingkit.navigation.LinkImpl;
import info.magnolia.module.templatingkit.templates.STKTemplate;
import info.magnolia.module.templatingkit.templates.SingletonParagraphTemplateModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets the extended breadcrumb and navigation for shop items.
 * @author tmiyar
 *
 */
public class ShopSingletonParagraphTemplateModel extends SingletonParagraphTemplateModel {

    private static Logger log = LoggerFactory.getLogger(ShopSingletonParagraphTemplateModel.class);
    private String currentShopName = "";

    public ShopSingletonParagraphTemplateModel(Content content,
            STKTemplate definition, RenderingModel parent) {
        super(content, definition, parent);
    }

  public ProductCategoryNavigationModel getProductCategoryNavigation() {
    return new ProductCategoryNavigationModel(getCurrentShopName());
  }

    @Override
    public String execute() {
        MgnlContext.setAttribute(ShopUtil.ATTRIBUTE_SHOPNAME, getCurrentShopName(),
                Context.SESSION_SCOPE);
        ShopUtil.setShoppingCartInSession();
        return super.execute();
    }

  protected String getCurrentShopName() {
    if (StringUtils.isEmpty(currentShopName)) {
      Content shopRoot = ShopUtil.getShopRoot();
      if (shopRoot != null) {
        currentShopName = NodeDataUtil.getString(shopRoot, "currentShop");
      }
    }
    return currentShopName;
  }

  public Collection<Link> getBreadcrumb() throws RepositoryException {
    List<Link> items = new ArrayList<Link>();

    // add family
    String name = ShopLinkUtil.getParamValue(ParamType.CATEGORY);
    if (StringUtils.isNotEmpty(name)) {
      Content dataNode;
        try {
            dataNode = CustomDataUtil.getProductCategoryNode(name);
        
            if (dataNode != null) {
                Content node = new I18nContentWrapper(dataNode);
                Content shopNode = CustomDataUtil.getShopNode(ShopUtil.getShopName());
           
                while (node.getLevel() > shopNode.getLevel()) {
                  items.add(new ShopLinkImpl(node));
                  node = node.getParent();
                } //while
        
            }//if
        } catch (Exception e) {
            //do nothing
        }
    }//if
    
    // add website nodes
    Content root = getSiteRoot();
    Content current = content;
    while (current.getLevel() >= root.getLevel()) {
      // only the nodes that are not hidden in navigation
      if (!NodeDataUtil.getBoolean(current, "hideInNav", false)) {
        items.add(new LinkImpl(current));
      }
      if (current.getLevel() == 0) {
        break;
      }
      current = current.getParent();
    }

    Collections.reverse(items);
    return items;
  }

  public String getCategoryLink(Content category) {
    return ShopLinkUtil.getProductCategoryLink(category);
  }

}
