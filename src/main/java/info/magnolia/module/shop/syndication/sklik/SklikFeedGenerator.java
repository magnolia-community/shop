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

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.impl.BaseWireFeedGenerator;

/**
 * ROME wire feed generator for Sklik 1.0.
 */
public class SklikFeedGenerator extends BaseWireFeedGenerator {

    private static final Namespace NAMESPACE = Namespace.getNamespace(SklikModule.URI);

    private static final String SHOP_ELEMENT = "SHOP";
    private static final String SHOPITEM_ELEMENT = "SHOPITEM";

    public SklikFeedGenerator() {
        this("sklik_1.0");
    }

    protected SklikFeedGenerator(String type) {
        super(type);
    }

    protected Namespace getFeedNamespace() {
        return NAMESPACE;
    }

    @Override
    public Document generate(WireFeed feed) throws FeedException {
        Channel channel = (Channel) feed;
        Element root = new Element(SHOP_ELEMENT, getFeedNamespace());
        generateModuleNamespaceDefs(root);
        generateFeedModules(feed.getModules(), root);
        generateForeignMarkup(root, (List) feed.getForeignMarkup());
        addEntries(channel, root);
        purgeUnusedNamespaceDeclarations(root);
        return new Document(root);
    }

    protected void addEntries(Channel feed, Element parent) throws FeedException {
        List items = feed.getItems();
        for (int i = 0; i < items.size(); i++) {
            addEntry((Item) items.get(i), parent);
        }
    }

    protected void addEntry(Item entry, Element parent) throws FeedException {
        Element eEntry = new Element(SHOPITEM_ELEMENT, getFeedNamespace());
        generateItemModules(entry.getModules(), eEntry);
        parent.addContent(eEntry);
    }
}