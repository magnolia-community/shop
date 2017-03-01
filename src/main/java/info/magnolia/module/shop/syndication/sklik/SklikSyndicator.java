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

import static info.magnolia.module.rssaggregator.RSSAggregator.*;

import info.magnolia.cms.core.Content;
import info.magnolia.context.WebContext;
import info.magnolia.dam.api.Asset;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.link.LinkUtil;
import info.magnolia.module.rssaggregator.generator.AbstractSyndFeedGenerator;
import info.magnolia.module.rssaggregator.generator.Feed;
import info.magnolia.module.rssaggregator.generator.FeedGenerationException;
import info.magnolia.module.rssaggregator.util.ContentMapper;
import info.magnolia.module.shop.PriceCategoryManager;
import info.magnolia.module.shop.ShopProductConstants;
import info.magnolia.module.shop.accessors.ShopAccessor;
import info.magnolia.module.shop.accessors.ShopProductAccessor;
import info.magnolia.module.shop.exceptions.ShopConfigurationException;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.objectfactory.ComponentProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;

/**
 * Shop product syndicator for generating Sklik feeds.
 */
public class SklikSyndicator extends AbstractSyndFeedGenerator implements Cloneable {

    private static final Logger log = LoggerFactory.getLogger(SklikSyndicator.class);

    private String shopName;

    private Node shopRoot;

    private Node priceCategoryNode;

    private final ContentMapper<SyndEntry> mapper;

    @Inject
    public SklikSyndicator(final ComponentProvider componentProvider) {
        mapper = componentProvider.newInstance(FeedEntryMapper.class, this);
    }

    @Override
    public Feed generate() throws FeedGenerationException {
        try {
            SyndFeed syndFeed = newSyndFeed();
            syndFeed.setFeedType("sklik_1.0");
            syndFeed.setEntries(loadFeedEntries());
            setFeedInfo(syndFeed);

            String xml = syndFeedToXml(syndFeed);
            return new Feed(xml, DEFAULT_CONTENT_TYPE, DEFAULT_ENCODING);
        } catch (Exception e) {
            String message = String.format("Failed to generate Feed using generator '%s'", getClass().getName());
            log.error(message, e);
            throw new FeedGenerationException(message, e);
        }
    }

    @Override
    public List<SyndEntry> loadFeedEntries() {
        try {
            if (!StringUtils.isBlank(shopName)) {
                Content shopRootContent = ShopUtil.getShopRootByShopName(shopName);
                if (shopRootContent != null) {
                    shopRoot = shopRootContent.getJCRNode();
                    PriceCategoryManager priceCategoryManager = new ShopAccessor(shopName).getShopConfiguration().getPriceCategoryManager();

                    ((FeedEntryMapper) mapper).syndicator.setShopRoot(shopRoot);
                    ((FeedEntryMapper) mapper).syndicator.setPriceCategoryNode(priceCategoryManager.getPriceCategoryInUse());

                    Collection<Node> products = new ArrayList<Node>();
                    String allProductsQuery = "select * from shopProduct where jcr:path like '/" + shopName + "/%'";
                    products.addAll(ShopProductAccessor.getProductsBySQL(allProductsQuery));

                    List<SyndEntry> result = new ArrayList<SyndEntry>(products.size());
                    for (Node product : products) {
                        result.add(mapper.map(product));
                    }
                    return result;
                }
            }
        } catch (RepositoryException e) {
            log.error("Failed to generate feed from content. Returning empty list instead.", e);
        } catch (ShopConfigurationException e) {
            log.error("Failed to generate feed from content. Returning empty list instead.", e);
        }
        return new ArrayList<SyndEntry>();
    }

    @Override
    public void setFeedInfo(SyndFeed feed) {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private static class FeedEntryMapper implements ContentMapper<SyndEntry> {

        private final SklikSyndicator syndicator;
        private final SimpleTranslator i18n;
        private final DamTemplatingFunctions damTemplatingFunctions;
        private final Provider<WebContext> webContextProvider;

        @Inject
        public FeedEntryMapper(final SklikSyndicator syndicator, final DamTemplatingFunctions damTemplatingFunctions, final SimpleTranslator i18n, Provider<WebContext> webContextProvider) {
            this.syndicator = syndicator;
            this.i18n = i18n;
            this.damTemplatingFunctions = damTemplatingFunctions;
            this.webContextProvider = webContextProvider;
        }

        @Override
        public SyndEntry map(Node content) throws RepositoryException {
            SyndEntry entry = new SyndEntryImpl();
            final SklikModule sklikData = new SklikModuleImpl();
            // create absolute link
            Node detailPage = ShopUtil.getContentByTemplateCategorySubCategory(
                    syndicator.getShopRoot(), "feature", "product-detail");
            String absoluteLink = LinkUtil.createExternalLink(detailPage);
            String selector = ShopLinkUtil.createProductSelector(content);
            String extension = StringUtils.substringAfterLast(absoluteLink, ".");
            if (StringUtils.isNotEmpty(selector)) {
                selector = "~" + selector + "~";
            }
            String absoluteLinkWithSelector = StringUtils.substringBeforeLast(absoluteLink, extension) + selector + "." + extension;
            sklikData.setUrl(absoluteLinkWithSelector);

            // title
            String defaultTitle = String.format(i18n.translate("shop.shopSyndicator.syndEntry.defaultTitle"), content.getIdentifier());
            sklikData.setProductName(StringUtils.defaultIfEmpty(PropertyUtil.getString(content, ShopProductConstants.PROPERTY_TITLE), defaultTitle));

            // description
            if (content.hasProperty(ShopProductConstants.PROPERTY_DESCRIPTION)) {
                sklikData.setDescription((PropertyUtil.getString(content, ShopProductConstants.PROPERTY_DESCRIPTION)));
            }

            // price
            String priceCategoryUUID = syndicator.getPriceCategoryNode().getIdentifier();
            if (content.hasNode(ShopProductConstants.NODE_PRICES)) {
                NodeIterator prices = content.getNode(ShopProductConstants.NODE_PRICES).getNodes();
                while (prices.hasNext()) {
                    Node price = prices.nextNode();
                    if (priceCategoryUUID.equals(price.getProperty(ShopProductConstants.PROPERTY_PRICES_CATEGORY).getString())) {
                        if (price.hasProperty(ShopProductConstants.PROPERTY_PRICES_PRICE)) {
                            sklikData.setPriceVat(price.getProperty(ShopProductConstants.PROPERTY_PRICES_PRICE).getString());
                        }
                    }
                }
            }

            // image
            if (content.hasProperty(ShopProductConstants.PROPERTY_IMAGE)) {
                Asset imageAsset = damTemplatingFunctions.getAsset(content.getProperty(ShopProductConstants.PROPERTY_IMAGE).getString());
                if (imageAsset != null) {
                    String link = imageAsset.getLink();
                    if (!LinkUtil.isExternalLinkOrAnchor(link)) {
                        HttpServletRequest request = webContextProvider.get().getRequest();
                        String domain = StringUtils.substringBefore(request.getRequestURL().toString(), request.getRequestURI());
                        link = domain + link;
                    }
                    sklikData.setImageUrl(link);
                }
            }

            entry.getModules().add(sklikData);
            return entry;
        }

    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopRoot(Node shopRoot) {
        this.shopRoot = shopRoot;
    }

    public Node getShopRoot() {
        return shopRoot;
    }

    public Node getPriceCategoryNode() {
        return priceCategoryNode;
    }

    public void setPriceCategoryNode(Node priceCategoryNode) {
        this.priceCategoryNode = priceCategoryNode;
    }
}
