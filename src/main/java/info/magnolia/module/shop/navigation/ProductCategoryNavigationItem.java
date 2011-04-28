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
package info.magnolia.module.shop.navigation;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.SelectorUtil;
import info.magnolia.module.shop.util.ShopUtil;


public class ProductCategoryNavigationItem {

  private Content content;
  private Content siteRoot;
  private String uuid;

  public ProductCategoryNavigationItem(Content content, Content siteRoot) {
      this.content = new I18nContentWrapper(content);
      this.siteRoot = siteRoot;
      this.uuid = SelectorUtil.getSelector(0);
  }
  
  public Content getContent() {
    return content;
  }

  public int getLevel(){
      try {
        //2 is shop name/ folder name of data module
        return  content.getLevel() - 2;
      } catch (PathNotFoundException e) {
      } catch (RepositoryException e) {
      }
      return 0;
  }
  
  public List<ProductCategoryNavigationItem> getItems() {
    List<ProductCategoryNavigationItem> items = new ArrayList<ProductCategoryNavigationItem>();
    for (Content child : content.getChildren("shopProductCategory")) {
      items.add(new ProductCategoryNavigationItem(child, siteRoot));         
    }
    return items;
  }
  
  public boolean isOpen(){
    
    if(StringUtils.isEmpty(uuid)) {
      return false;
    }
    final String selectedPath = ContentUtil.getContentByUUID("data", uuid).getHandle();
    final String currentHandle = this.content.getHandle();
    return selectedPath.startsWith(currentHandle + "/") || selectedPath.equals(currentHandle);
}

  public String getId(){
      return content.getName().toLowerCase();
  }

  public boolean isVisible(){
      return true;
  }

  public boolean isLeaf(){
      return content.hasChildren();
  }

  public boolean isSelected(){
      
      if(content.getUUID().equals(uuid)) {
          return true;
      } else {
          return false;
      }
  }

  public String getHref() {
      return ShopUtil.getCategoryLink(content, siteRoot);
  }

  public String getTitle() {
      return content.getTitle();
  }

  public String getNavigationTitle() {
      return content.getTitle();
  }
}