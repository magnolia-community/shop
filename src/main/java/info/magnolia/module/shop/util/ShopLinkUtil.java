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
package info.magnolia.module.shop.util;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.SelectorUtil;
import info.magnolia.module.templating.MagnoliaTemplatingUtilities;
import info.magnolia.module.templatingkit.util.STKUtil;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

/**
 * Util class for creating shop links.
 * @author tmiyar
 *
 */
public class ShopLinkUtil {
    
    /**
     * Enum for the different product lists.
     * @author tmiyar
     *
     */
    public static enum ParamType {
        SEARCH,  
        CATEGORY,
        PRODUCT,
        TAG,
    }
  
    public static boolean isParamOfType(ParamType paramType) {
        String selector = SelectorUtil.getSelector();
        
        if(StringUtils.isNotEmpty(selector) && StringUtils.contains(selector, paramType.name())) {
            return true;
        }
        return false;
    }
    
    public static String getParamValue(ParamType paramType) {
        String[] selectors = StringUtils.split(SelectorUtil.getSelector(), ".");
        int index = 0;
        for (int i = 0; i < selectors.length; i++) {
          String selector = selectors[i];
          if(selector.equalsIgnoreCase(paramType.name())) {
              index = i + 1;
              break;
          }
        }
        if(selectors.length > index) {
            return selectors[index];
        } else {
            return "";
        }
    }
    
  public static String getProductCategoryLink(Content category) {
      Content shopRootPage = ShopUtil.getShopRoot();
      String selector = createProductCategorySelector(category);
      return ShopLinkUtil.createLinkFromContentWithSelectors(shopRootPage, selector);
  }

  public static String createProductCategorySelector(Content category) {
      return StringUtils.replace(category.getTitle(), " ", "-") + "." + ParamType.CATEGORY.name() + "." +category.getName();
  }
  
  public static String getProductListSearchLink(Content siteRoot) {
      String link = "";
      String selector = createSearchSelector();
      Content productListPage = ShopUtil.getShopRoot();
      if(productListPage != null) {
          link = ShopLinkUtil.createLinkFromContentWithSelectors(productListPage, selector);
      }
      
      return link;
  
  }

  private static String createSearchSelector() {
      return ParamType.SEARCH.name();
  }
  
  public static String createLinkFromContentWithSelectors(Content content, String selector) {
      String link = MagnoliaTemplatingUtilities.getInstance().createLink(content);
      String extension = StringUtils.substringAfterLast(link, ".");
      if(StringUtils.isNotEmpty(selector)) {
          selector += ".";
      }
      return StringUtils.substringBeforeLast(link, extension) + selector + extension;
  }
  
  public static String getProductDetailPageLink(Content product, Content siteRoot)
  throws RepositoryException {
  if (product != null) {
    Content detailPage = STKUtil.getContentByTemplateCategorySubCategory(
        siteRoot, "feature", "productDetail");
    
    String categoryUUID = getSelectedCategoryUUID();
    String selector = createProductSelector(product);
    
    if (StringUtils.isNotEmpty(categoryUUID)) {
        Content category = new I18nContentWrapper(ContentUtil.getContentByUUID("data", categoryUUID));
        selector = createProductAndProductCategorySelector(
                product, category);
    } 
    return ShopLinkUtil.createLinkFromContentWithSelectors(detailPage, selector);
  }
  return "";
  }

  public static String createProductAndProductCategorySelector(Content product,
        Content category) {
      return createProductCategorySelector(category)
            + "." + createProductSelector(product);
}

public static String createProductSelector(Content product) {
    return StringUtils.replace(product.getTitle(), " ", "-") + "." +ParamType.PRODUCT.name() + "." + product.getName();
}
  
  public static String getSelectedCategoryUUID() {
      String name = ShopLinkUtil.getParamValue(ParamType.CATEGORY);
      if(StringUtils.isNotEmpty(name)) {
        Content category = null;
        try {
            category = CustomDataUtil.getProductCategoryNode(name);
            return category.getUUID();
        } catch (Exception e) {
            //Item not found
        }
      }
      return "";
    }

}
