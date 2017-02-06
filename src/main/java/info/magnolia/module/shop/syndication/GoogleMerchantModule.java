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
package info.magnolia.module.shop.syndication;


import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.module.Module;

/**
 * ROME module adding Google Merchant tags into feed.
 */
public interface GoogleMerchantModule extends Module, CopyFrom {

    String URI = "http://base.google.com/ns/1.0";
    String NS_PREFIX = "g";

    String ID_TAG_NAME = "id";
    String IMAGE_LINK_TAG_NAME = "image_link";
    String PRICE_TAG_NAME = "price";
    String CONDITION_TAG_NAME = "condition";
    String AVAILABILITY_TAG_NAME = "availability";


    String getId();
    void setId(final String id);

    String getImageLink();
    void setImageLink(final String imageLink);

    String getPrice();
    void setPrice(final String price);

    String getCondition();
    void setCondition(final String condition);

    void setAvailability(final String value);
    String getAvailability();
}
