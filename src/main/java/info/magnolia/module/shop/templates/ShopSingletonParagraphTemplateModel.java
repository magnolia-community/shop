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
package info.magnolia.module.shop.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.jcr.RepositoryException;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.cms.util.SelectorUtil;
import info.magnolia.context.Context;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.shop.navigation.ProductCategoryNavigationModel;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templating.RenderingModel;
import info.magnolia.module.templatingkit.navigation.LinkImpl;
import info.magnolia.module.templatingkit.templates.STKTemplate;
import info.magnolia.module.templatingkit.templates.SingletonParagraphTemplateModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShopSingletonParagraphTemplateModel extends
    SingletonParagraphTemplateModel {

  private static Logger log = LoggerFactory
      .getLogger(ShopSingletonParagraphTemplateModel.class);
  private String currentShopName = "";

  public ShopSingletonParagraphTemplateModel(Content content,
      STKTemplate definition, RenderingModel parent) {
    super(content, definition, parent);
  }

  public ProductCategoryNavigationModel getProductCategoryNavigation() {
    return new ProductCategoryNavigationModel(this.getSiteRoot(),
        getCurrentShop());
  }

  @Override
  public String execute() {
    MgnlContext.setAttribute("shopName", getCurrentShop(),
        Context.SESSION_SCOPE);
    ShopUtil.setShoppingCart();
    return super.execute();
  }

  protected String getCurrentShop() {
    if (StringUtils.isEmpty(currentShopName)) {
      Content shopRoot = ShopUtil.getShopRoot(this.getSiteRoot());
      if (shopRoot != null) {
        currentShopName = NodeDataUtil.getString(shopRoot, "currentShop");
      }
    }
    return currentShopName;
  }

  public Collection getBreadcrumb() throws RepositoryException {
    List items = new ArrayList();

    // add categories
    String name = SelectorUtil.getSelector(0);
    if (StringUtils.isNotEmpty(name)) {
      // Check it is not a product
      Content dataNode = ShopUtil.getProductCategoryNode(name);
      if (dataNode != null) {
        Content node = new I18nContentWrapper(dataNode);
        while (node.getLevel() > 2) {
          items.add(node);
          node = node.getParent();
        }

      }
    }

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
    return ShopUtil.getCategoryLink(category, this.getSiteRoot());
  }

}
