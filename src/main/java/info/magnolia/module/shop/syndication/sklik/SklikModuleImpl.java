/**
 * This file Copyright (c) 2015 Magnolia International
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
package info.magnolia.module.shop.syndication.sklik;

import com.sun.syndication.feed.module.ModuleImpl;

/**
 * ROME module implementation of @SklikModule.
 */
public class SklikModuleImpl extends ModuleImpl implements SklikModule {

    private String productName = "";
    private String description = "";
    private String url = "";
    private String priceVat = "0.0";
    private String deliveryDate = "1";


    public SklikModuleImpl() {
        super(SklikModule.class, URI);
    }

    @Override
    public void copyFrom(final Object other) {
        if (!(other instanceof SklikModule)) {
            throw new IllegalArgumentException("Expected other to be of class " + SklikModule.class.getName() + " but was " + other.getClass().getName());
        }
        final SklikModule otherModule = (SklikModule) other;
        setProductName(otherModule.getProductName());
        setDescription(otherModule.getDescription());
        setUrl(otherModule.getUrl());
        setPriceVat(otherModule.getPriceVat());
        setDeliveryDate(otherModule.getDeliveryDate());
    }

    @Override
    public Class<?> getInterface() {
        return SklikModule.class;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getPriceVat() {
        return priceVat;
    }

    @Override
    public void setPriceVat(String priceVat) {
        this.priceVat = priceVat;
    }

    @Override
    public String getDeliveryDate() {
        return deliveryDate;
    }

    @Override
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}