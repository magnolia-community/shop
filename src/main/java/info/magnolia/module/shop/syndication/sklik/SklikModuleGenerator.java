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

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;

import com.rometools.rome.feed.module.Module;
import com.rometools.rome.io.ModuleGenerator;

/**
 * ROME module generator for @SklikModule.
 */
public class SklikModuleGenerator implements ModuleGenerator {

    private static final Namespace NAMESPACE = Namespace.getNamespace(SklikModule.URI);
    private static final Set<Namespace> NAMESPACES = Collections.singleton(NAMESPACE);

    @Override
    public void generate(final Module module, final Element element) {
        final SklikModule myModule = (SklikModule) module;
        addIfNotBlank(element, myModule.getProductName(), SklikModule.PRODUCT_NAME);
        addIfNotBlank(element, myModule.getDescription(), SklikModule.DESCRIPTION);
        addIfNotBlank(element, myModule.getUrl(), SklikModule.URL);
        addIfNotBlank(element, myModule.getImageUrl(), SklikModule.IMGURL);
        addIfNotNull(element, myModule.getPriceVat(), SklikModule.PRICE_VAT);
        addIfNotNull(element, myModule.getDeliveryDate(), SklikModule.DELIVERY_DATE);
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
        return SklikModule.URI;
    }

    @Override
    public Set<Namespace> getNamespaces() {
        return NAMESPACES;
    }
}