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
package info.magnolia.shop.beans;

import ch.fastforward.magnolia.ocm.beans.OCMBean;
import info.magnolia.shop.util.ShopUtil;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * POJO for product.
 */
public class Product extends OCMBean {

    public static final String PROPERTY_NAME_TITLE = "title";
    public static final String PROPERTY_NAME_PRODUCT_DESCRIPTION_1 = "productDescription1";
    public static final String PROPERTY_NAME_PRODUCT_DESCRIPTION_2 = "productDescription2";

    private String title;
    private String productDescription1;
    private String productDescription2;
    private ProductPrice price;

    public Product(Node productNode) throws RepositoryException {
        // TODO: deprecate setUuid() in favor of setIdentifier() in OCMBean
        this.setUuid(productNode.getIdentifier());
        this.setPath(productNode.getPath());
        // TODO: OCMBean should set this automatically with setPath()
        this.setName(productNode.getName());

        // wrap with I18nNodeWrapper
        productNode = ShopUtil.wrapWithI18n(productNode);

        // marshall the properties
        if (productNode.hasProperty(PROPERTY_NAME_TITLE)) {
            setTitle(productNode.getProperty(PROPERTY_NAME_TITLE).getString());
        }
        if (productNode.hasProperty(PROPERTY_NAME_PRODUCT_DESCRIPTION_1)) {
            setProductDescription1(productNode.getProperty(PROPERTY_NAME_PRODUCT_DESCRIPTION_1).getString());
        }
        if (productNode.hasProperty(PROPERTY_NAME_PRODUCT_DESCRIPTION_2)) {
            setProductDescription2(productNode.getProperty(PROPERTY_NAME_PRODUCT_DESCRIPTION_2).getString());
        }

        // init price
        setPrice(ShopUtil.getProductPriceBean(productNode));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProductDescription1() {
        return productDescription1;
    }

    public void setProductDescription1(String productDescription1) {
        this.productDescription1 = productDescription1;
    }

    public String getProductDescription2() {
        return productDescription2;
    }

    public void setProductDescription2(String productDescription2) {
        this.productDescription2 = productDescription2;
    }

    public ProductPrice getPrice() {
        return price;
    }

    public void setPrice(ProductPrice price) {
        this.price = price;
    }
}
