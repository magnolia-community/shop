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
package info.magnolia.module.shop.paragraphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.core.search.Query;
import info.magnolia.cms.i18n.I18nContentWrapper;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.cms.util.QueryUtil;
import info.magnolia.cms.util.SelectorUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.dms.beans.Document;
import info.magnolia.module.shop.ShopConfiguration;
import info.magnolia.module.shop.ShopModule;
import info.magnolia.module.shop.beans.ShoppingCart;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templating.MagnoliaTemplatingUtilities;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;
import info.magnolia.module.templatingkit.navigation.LinkImpl;
import info.magnolia.module.templatingkit.paragraphs.ImageGalleryParagraphModel;
import info.magnolia.module.templatingkit.templates.STKTemplateModel;
import info.magnolia.module.templatingkit.util.STKUtil;

/**
 * Shop paragraph model, used on productdetail and productlist paragraphs.
 * 
 * @author tmiyar
 * 
 */
public class ShopParagraphModel extends ImageGalleryParagraphModel {

  private static Logger log = LoggerFactory.getLogger(ShopParagraphModel.class);

  private Content siteRoot;

  public ShopParagraphModel(Content content, RenderableDefinition definition,
      RenderingModel parent) {
    super(content, definition, parent);
    this.siteRoot = ((STKTemplateModel) parent).getSiteRoot();
  }

  public ShoppingCart getShoppingCart() {
    return ShopUtil.getShoppingCart();
  }

  protected Content getSiteRoot() {
    return siteRoot;
  }

  @Override
  public String execute() {
    String command = MgnlContext.getParameter("command");

    if (StringUtils.isNotEmpty(command)) {
      if (StringUtils.equals(command, "addToCart")) {
        addToCart();
        return "";
      }
    }

    return "";
  }

  private void addToCart() {
    String quantityString = MgnlContext.getParameter("quantity");
    int quantity = 1;
    try {
      quantity = (new Integer(quantityString)).intValue();
      if (quantity <= 0) {
        quantity = 1;
      }
    } catch (NumberFormatException nfe) {
      // TODO: log error? qunatity will be set to 1
    }
    String product = MgnlContext.getParameter("product");
    if (StringUtils.isBlank(product)) {
      log
          .error("Cannot add item to cart because no \"product\" parameter was found in the request");
    } else {
      ShoppingCart cart = getShoppingCart();
      int success = cart.addToShoppingCart(product, quantity);
      if (success <= 0) {
        log.error("Cannot add item to cart because no product for " + product
            + " could be found");

      }
    }
  }

  /**
   * Returns current offers product list or selected category product list,
   * depending if there is a category selected or not.
   * 
   * @return
   */
  public List<Content> getProductList() {

    String productCategory = getSelectedCategoryUUID();

    if (StringUtils.isNotEmpty(productCategory)) {
      String xpath = "/jcr:root/"
          + ShopUtil.getShopName()
          + "/products//element(*,shopProduct)[jcr:contains(productCategoryUUIDs/., '"
          + productCategory + "')]";
      List<Content> productList = (List<Content>) QueryUtil.query("data", xpath, Query.XPATH);
      List<Content> i18nProductList = new ArrayList<Content>();
      for (Content content : productList) {
        i18nProductList.add(new I18nContentWrapper(content));
      }
      return i18nProductList;
    } else {
      Content currentOffers = ContentUtil.getContent(content, "currentOffers");
      List<Content> productList = new ArrayList<Content>();
      if (currentOffers != null) {
        Collection offers = currentOffers.getNodeDataCollection();
        for (Iterator iterator = offers.iterator(); iterator.hasNext();) {
          NodeData productPathNodeData = (NodeData) iterator.next();
          Content productNode = ContentUtil.getContent("data",
              productPathNodeData.getString());
          try {
            if (productNode != null
                && productNode.getItemType().getSystemName().equals(
                    "shopProduct")) {
              productList.add(new I18nContentWrapper(productNode));
            }
          } catch (RepositoryException e) {

          }

        }
        return productList;
      }
    }
    return null;
  }

  /**
   * Gets the category selected from the url, using selector.
   * 
   */
  public Content getCategory() {
    String productCategoryUUID = getSelectedCategoryUUID();
    if (StringUtils.isNotEmpty(productCategoryUUID)) {
      Content node = ContentUtil.getContentByUUID("data", productCategoryUUID);
      if (!node.isNodeType("shopProduct")) {
        return new I18nContentWrapper(node);
      }
    }
    return null;
  }

  public String getSelectedCategoryUUID() {
    return SelectorUtil.getSelector(0);
  }

  /**
   * Gets product selected from the url, using selector.
   * 
   */
  public Content getProduct() {
    String productUUID = SelectorUtil.getSelector(1);
    // if is displaying current offers there is no category selected
    if (StringUtils.isEmpty(productUUID)) {
      productUUID = SelectorUtil.getSelector(0);
    }

    if (StringUtils.isNotEmpty(productUUID)) {
      return new I18nContentWrapper(ContentUtil.getContentByUUID("data", productUUID));
    }
    return null;
  }

  /**
   * Gets the link for the product detail page.
   * 
   * @throws RepositoryException
   */
  public String getProductDetailPageLink(Content product)
      throws RepositoryException {
    if (product != null) {
      Content detailPage = STKUtil.getContentByTemplateCategorySubCategory(
          siteRoot, "feature", "productDetail");
      String categoryUUID = getSelectedCategoryUUID();
      if (StringUtils.isEmpty(categoryUUID)) {
        return MagnoliaTemplatingUtilities.getInstance().createLink(detailPage)
            .replace(".html", "." + product.getUUID() + ".html");
      } else {
        return MagnoliaTemplatingUtilities.getInstance().createLink(detailPage)
            .replace(".html",
                "." + categoryUUID + "." + product.getUUID() + ".html");
      }
    }
    return "";
  }

  public TemplateProductPriceBean getProductPriceBean(Content product) {
    Content priceCategory = getShopPriceCategory();
    String productPrice = getProductPriceByCategory(product, priceCategory
        .getUUID());
    Content currency = getCurrencyByUUID(NodeDataUtil.getString(priceCategory,
        "currencyUUID"));
    Content tax = getTaxByUUID(NodeDataUtil.getString(product,
        "taxCategoryUUID"));

    TemplateProductPriceBean bean = new TemplateProductPriceBean();
    bean.setPrice(productPrice);
    bean.setCurrency(NodeDataUtil.getString(currency, "title"));
    boolean taxIncluded = NodeDataUtil.getBoolean(priceCategory, "taxIncluded",
        false);
    if (taxIncluded) {
      bean.setTaxIncluded(ShopUtil.getMessages().get("tax.included"));
    } else {
      bean.setTaxIncluded(ShopUtil.getMessages().get("tax.no.included"));
    }

    bean.setTax(NodeDataUtil.getString(tax, "tax"));
    return bean;
  }

  public Content getTaxByUUID(String uuid) {
    return new I18nContentWrapper(ContentUtil.getContentByUUID("data", uuid));
  }

  public Content getCurrencyByUUID(String uuid) {
    return new I18nContentWrapper(ContentUtil.getContentByUUID("data", uuid));
  }

  protected String getProductPriceByCategory(Content product,
      String priceCategoryUUID) {
    Content pricesNode = ContentUtil.getContent(product, "prices");
    if (pricesNode.hasChildren()) {
      for (Iterator iterator = pricesNode.getChildren().iterator(); iterator
          .hasNext();) {
        Content price = new I18nContentWrapper((Content) iterator.next());
        if (NodeDataUtil.getString(price, "priceCategoryUUID").equals(
            priceCategoryUUID)) {
          return NodeDataUtil.getString(price, "price");
        }
      }
    }
    return null;
  }

  public Content getShopPriceCategory() {
    ShopConfiguration shopConfiguration = ShopModule.getInstance()
        .getCurrentShopConfiguration(ShopUtil.getShopName());
    if (shopConfiguration != null) {
      return shopConfiguration.getPriceCategoryManager()
          .getPriceCategoryInUse();
    }
    return null;

  }

  /**
   * returns the link to the shopping cart page based on template
   * category-subcategory
   */
  public String getShoppingCartLink() {
    Content shoppingCartPage;
    try {
      shoppingCartPage = STKUtil.getContentByTemplateCategorySubCategory(
          siteRoot, "feature", "shopShoppingCart");
      return new LinkImpl(shoppingCartPage).getHref();
    } catch (RepositoryException e) {
      log.error("Cant get shopping cart page", e);
    }
    return "";
  }

  /**
   * get images folder items
   */
  protected List<String> getKeys() {
    Content product = getProduct();
    Content dmsFolder = STKUtil.getReferencedContent(product, "imageDmsUUID",
        "dms");
    if (dmsFolder == null) {
      return new ArrayList();
    }
    List<String> keys = new ArrayList();
    try {
      dmsFolder = dmsFolder.getParent();

      Collection children = dmsFolder.getChildren(ItemType.CONTENTNODE);

      for (Iterator iterator = children.iterator(); iterator.hasNext();) {
        Content imageNode = (Content) iterator.next();
        if (showImage(new Document(imageNode))) {
          keys.add(imageNode.getUUID());
        }
      }

    } catch (Exception e) {

    }
    return keys;
  }

}