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

import com.sun.syndication.feed.module.ModuleImpl;

/**
 * ROME module implementation of @GoogleMerchantModule.
 */
public class GoogleMerchantModuleImpl extends ModuleImpl implements GoogleMerchantModule {

    private String _id;
    private String _imageLink;
    private String _price = "0.0";
    private String _condition = "new";
    private String _availability = "in stock";


    public GoogleMerchantModuleImpl() {
        super(GoogleMerchantModule.class, URI);
    }

    @Override
    public String getImageLink() {
        return _imageLink;
    }

    @Override
    public void setImageLink(final String imageLink) {
        _imageLink = imageLink;
    }

    @Override
    public String getPrice() {
        return _price;
    }

    @Override
    public void setPrice(final String price) {
        _price = price;
    }

    @Override
    public String getCondition() {
        return _condition;
    }

    @Override
    public void setCondition(final String condition) {
        _condition = condition;
    }

    @Override
    public String getId() {
        return _id;
    }

    @Override
    public void setId(final String id) {
        _id = id;
    }

    @Override
    public void copyFrom(final Object other) {
        if (!(other instanceof GoogleMerchantModule)) {
            throw new IllegalArgumentException("Expected other to be of class " + GoogleMerchantModule.class.getSimpleName() + " but was " + other.getClass().getSimpleName());
        }
        final GoogleMerchantModule otherModule = (GoogleMerchantModule) other;
        setId(otherModule.getId());
        setImageLink(otherModule.getImageLink());
        setPrice(otherModule.getPrice());
        setCondition(otherModule.getCondition());
    }

    @Override
    public Class<?> getInterface() {
        return GoogleMerchantModule.class;
    }

    @Override
    public String getAvailability() {
        return _availability;
    }

    @Override
    public void setAvailability(final String availability) {
        _availability = availability;
    }
}