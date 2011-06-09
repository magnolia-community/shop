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
package info.magnolia.module.shop.util;

import info.magnolia.cms.core.Content;
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
     * Enum for the diferent product lists.
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
        String selectorName = SelectorUtil.getSelector(2);
        if(StringUtils.isEmpty(selectorName)) {
            selectorName = SelectorUtil.getSelector(0);
        }
        
        if(StringUtils.isNotEmpty(selectorName) && selectorName.equalsIgnoreCase(paramType.name())) {
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
    
  public static String getCategoryLink(Content category, Content siteRoot) {
    Content shopRootPage = ShopUtil.getShopRoot(siteRoot);
    String selector = ParamType.CATEGORY.name() + "." +category.getName();
    return ShopLinkUtil.createLinkFromContentWithSelectors(shopRootPage, selector);
  }
  
  public static String getProductListSearchLink(Content siteRoot) {
      String link = "";
      String selector = "" + ParamType.SEARCH;
      Content productListPage = ShopUtil.getShopRoot(siteRoot);
      if(productListPage != null) {
          link = ShopLinkUtil.createLinkFromContentWithSelectors(productListPage, selector);
      }
      
      return link;
  
  }
  
  public static String createLinkFromContentWithSelectors(Content content, String selector) {
      String link = MagnoliaTemplatingUtilities.getInstance().createLink(content);
      String extension = StringUtils.substringAfterLast(link, ".");
      if(StringUtils.isNotEmpty(selector)) {
          selector += ".";
      }
      String linkWithSelectors = StringUtils.substringBeforeLast(link, extension) + selector + extension;
      return linkWithSelectors;
  }
  
  public static String getProductDetailPageLink(Content product, Content siteRoot)
  throws RepositoryException {
  if (product != null) {
    Content detailPage = STKUtil.getContentByTemplateCategorySubCategory(
        siteRoot, "feature", "productDetail");
    
    String categoryUUID = getSelectedCategoryUUID();
    String selector = ParamType.PRODUCT + "." + product.getName();
    
    if (StringUtils.isNotEmpty(categoryUUID)) {
        Content category = ContentUtil.getContentByUUID("data", categoryUUID);
        selector = ParamType.CATEGORY + "." + category.getName() 
            + "." + ParamType.PRODUCT + "."  + product.getName();
    } 
    return ShopLinkUtil.createLinkFromContentWithSelectors(detailPage, selector);
  }
  return "";
  }
  
  public static String getSelectedCategoryUUID() {
      String name = ShopLinkUtil.getParamValue(ParamType.CATEGORY);
      if(StringUtils.isNotEmpty(name)) {
        Content category = ShopUtil.getProductCategoryNode(name);
        if(category != null) {
          return category.getUUID();
         
        }
      }
      return "";
    }

}
