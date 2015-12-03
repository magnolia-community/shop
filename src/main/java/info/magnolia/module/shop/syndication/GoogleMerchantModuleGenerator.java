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

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleGenerator;

/**
 * ROME module generator for @GoogleMerchantModule.
 */
public class GoogleMerchantModuleGenerator implements ModuleGenerator {

    private static final Namespace NAMESPACE = Namespace.getNamespace(GoogleMerchantModule.NS_PREFIX, GoogleMerchantModule.URI);
    private static final Set<Namespace> NAMESPACES = Collections.singleton(NAMESPACE);

    @Override
    public void generate(final Module module, final Element element) {
        final GoogleMerchantModule myModule = (GoogleMerchantModule) module;
        addIfNotBlank(element, myModule.getCondition(), GoogleMerchantModule.CONDITION_TAG_NAME);
        addIfNotBlank(element, myModule.getImageLink(), GoogleMerchantModule.IMAGE_LINK_TAG_NAME);
        addIfNotBlank(element, myModule.getAvailability(), GoogleMerchantModule.AVAILABILITY_TAG_NAME);
        addIfNotNull(element, myModule.getId(), GoogleMerchantModule.ID_TAG_NAME);
        addIfNotNull(element, myModule.getPrice(), GoogleMerchantModule.PRICE_TAG_NAME);
    }

    private void addIfNotNull(final Element element, final Object value, final String tag) {
        if (value != null) {
            add(element, String.valueOf(value), tag);
        }
    }

    private void addIfNotBlank(final Element element, final String value, final String tag) {
        if (!StringUtils.isBlank(value))  {
            add(element, value, tag);
        }
    }

    private void add(final Element element, final String value, final String tag) {
        final Element myElement = new Element(tag, NAMESPACE);
        myElement.setText(value);
        element.addContent(myElement);
    }

    @Override
    public String getNamespaceUri() {
        return GoogleMerchantModule.URI;
    }

    @Override
    public Set<?> getNamespaces() {
        return NAMESPACES;
    }
}