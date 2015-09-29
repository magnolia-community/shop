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
package info.magnolia.shop.rest.beans;

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.wrapper.I18nNodeWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by will on 16.07.15.
 */
public class Product {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private Node node;
//    private TemplateProductPriceBean price;
    private List<String> images;

    public Product(Node node, String shopName) {
        if (!NodeUtil.isWrappedWith(node, I18nNodeWrapper.class)) {
            node = new I18nNodeWrapper(node);
        }
        this.node = node;
//        this.price = ShopUtil.getProductPriceBean(node, shopName);
        initImages(node);
    }

    private void initImages(Node node) {
        images = new ArrayList<>();
        String imageUUID = PropertyUtil.getString(node, "image", null);
        if (imageUUID != null) {
            images.add(imageUUID);
        }
    }

    public String getUuid() {
        try {
            return node.getIdentifier();
        } catch (RepositoryException e) {
            log.error("Could not get uuid of node " + node, e);
        }
        return null;
    }

    public void setUuid(String uuid) {

    }

    public String getProductNumber() {
        try {
            return node.getName();
        } catch (RepositoryException e) {
            log.error("Could not get name of node " + node, e);
        }
        return null;
    }

    public void setProductNumber(String prodNo) {

    }

    public String getTitle() {
        return PropertyUtil.getString(node, "title", "untitled");
    }

    public void setTitle(String title) {
    }


    public String getProductDescription1() {
        return PropertyUtil.getString(node, "productDescription1");
    }

    public void setProductDescription1(String desc) {

    }

    public String getProductDescription2() {
        return PropertyUtil.getString(node, "productDescription2");
    }

    public void setProductDescription2() {}

    public List<String> getImages() {
        return images;
    }

    public void setImages(Collection<String> imgs) {

    }

//    public TemplateProductPriceBean getPrice() {
//        return price;
//    }
//
//    public void setPrice(TemplateProductPriceBean price) {
//
//    }

    public long getMaxQuantityPerOrder() {
        Long maxQantityPerOrder = PropertyUtil.getLong(node, "maxQuantityPerOrder");
        if (maxQantityPerOrder == null) {
            return -1;
        } else {
            return maxQantityPerOrder.longValue();
        }
    }

    public void setMaxQuantityPerOrder() {

    }

}
