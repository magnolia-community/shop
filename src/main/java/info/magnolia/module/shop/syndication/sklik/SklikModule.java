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

import com.sun.syndication.feed.CopyFrom;
import com.sun.syndication.feed.module.Module;

/**
 * ROME module specifying Sklik tags.
 */
public interface SklikModule extends Module, CopyFrom {

    // namespace URI
    String URI = "http://www.zbozi.cz/ns/offer/1.0";

    String PRODUCT_NAME = "PRODUCTNAME";
    String DESCRIPTION = "DESCRIPTION";
    String URL = "URL";
    String PRICE_VAT = "PRICE_VAT";
    String DELIVERY_DATE = "DELIVERY_DATE";
    String IMGURL = "IMGURL";

    String getProductName();

    void setProductName(final String productName);

    String getDescription();

    void setDescription(final String description);

    String getUrl();

    void setUrl(final String url);

    String getPriceVat();

    void setPriceVat(final String priceVat);

    String getDeliveryDate();

    void setDeliveryDate(final String deliveryDate);

    String getImageUrl();

    void setImageUrl(final String imageUrl);
}
