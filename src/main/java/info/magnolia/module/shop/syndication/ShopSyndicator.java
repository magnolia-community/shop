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

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.dam.api.Asset;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.link.Link;
import info.magnolia.link.LinkException;
import info.magnolia.link.LinkTransformerManager;
import info.magnolia.link.LinkUtil;
import info.magnolia.module.rssaggregator.generator.AbstractSyndFeedGenerator;
import info.magnolia.module.rssaggregator.generator.FeedGenerationException;
import info.magnolia.module.rssaggregator.util.ContentMapper;
import info.magnolia.module.shop.PriceCategoryManager;
import info.magnolia.module.shop.ShopProductConstants;
import info.magnolia.module.shop.accessors.ShopAccessor;
import info.magnolia.module.shop.accessors.ShopProductAccessor;
import info.magnolia.module.shop.exceptions.ShopConfigurationException;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;

/**
 * Shop product syndicator.
 */
public class ShopSyndicator extends AbstractSyndFeedGenerator implements Cloneable {

    private static final Logger log = LoggerFactory.getLogger(ShopSyndicator.class);

    private String shopName;

    private Node shopRoot;

    private Node priceCategoryNode;

    private final ContentMapper<SyndEntry> MAPPER;

    private ServerConfiguration serverConfiguration;

    private SimpleTranslator simpleTranslator;

    private DamTemplatingFunctions damTemplatingFunctions;

    @Inject
    public ShopSyndicator(ServerConfiguration serverConfiguration, DamTemplatingFunctions damTemplatingFunctions, SimpleTranslator simpleTranslator) {
        this.serverConfiguration = serverConfiguration;
        this.simpleTranslator = simpleTranslator;
        this.damTemplatingFunctions = damTemplatingFunctions;
        MAPPER = new FeedEntryMapper(this, damTemplatingFunctions, simpleTranslator);
    }

    @Override
    public List<SyndEntry> loadFeedEntries() {
        try {
            if (!StringUtils.isBlank(getShopName())) {
                shopRoot = ShopUtil.getShopRootByShopName(shopName).getJCRNode();
                PriceCategoryManager priceCategoryManager = new ShopAccessor(getShopName()).getShopConfiguration().getPriceCategoryManager();

                ((FeedEntryMapper) MAPPER).syndicator.setShopRoot(shopRoot);
                ((FeedEntryMapper) MAPPER).syndicator.setPriceCategoryNode(priceCategoryManager.getPriceCategoryInUse());

                Collection<Node> products = new ArrayList<Node>();
                String allProductsQuery = "select * from shopProduct where jcr:path like '/" + shopName + "/%'";
                products.addAll(ShopProductAccessor.getProductsBySQL(allProductsQuery));

                List<SyndEntry> result = new ArrayList<SyndEntry>(products.size());
                for (Node product : products) {
                    result.add(MAPPER.map(product));
                }
                return result;
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
        try {
            feed.setTitle(PropertyUtil.getString(shopRoot, ShopProductConstants.PROPERTY_TITLE));
            feed.setLink(StringUtils.defaultIfEmpty(LinkUtil.createExternalLink(shopRoot), serverConfiguration.getDefaultBaseUrl()));
            feed.setDescription("");
        } catch (Exception e) {
            throw new FeedGenerationException("Failed to retrieve feed description from " + getShopName(), e);
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private static class FeedEntryMapper implements ContentMapper<SyndEntry> {

        private ShopSyndicator syndicator;

        private final SimpleTranslator i18n;

        private final DamTemplatingFunctions damTemplatingFunctions;

        @Inject
        public FeedEntryMapper(ShopSyndicator syndicator, DamTemplatingFunctions damTemplatingFunctions, SimpleTranslator i18n) {
            this.syndicator = syndicator;
            this.damTemplatingFunctions = damTemplatingFunctions;
            this.i18n = i18n;
        }

        @Override
        public SyndEntry map(Node content) throws RepositoryException {
            SyndEntry entry = new SyndEntryImpl();
            // create absolute link
            Node detailPage = ShopUtil.getContentByTemplateCategorySubCategory(
                    syndicator.getShopRoot(), "feature", "product-detail");
            String absoluteLink = LinkUtil.createExternalLink(detailPage);
            String selector = ShopLinkUtil.createProductSelector(content);
            String extension = StringUtils.substringAfterLast(absoluteLink, ".");
            if(StringUtils.isNotEmpty(selector)) {
                selector = "~" + selector + "~";
            }
            String absoluteLinkWithSelector = StringUtils.substringBeforeLast(absoluteLink, extension) + selector + "." + extension;
            entry.setLink(absoluteLinkWithSelector);

            // title
            String defaultTitle = String.format(i18n.translate("shop.shopSyndicator.syndEntry.defaultTitle"), content.getIdentifier());
            entry.setTitle(StringUtils.defaultIfEmpty(PropertyUtil.getString(content, ShopProductConstants.PROPERTY_TITLE), defaultTitle));

            // description
            if (content.hasProperty(ShopProductConstants.PROPERTY_DESCRIPTION)) {
                SyndContent description = new SyndContentImpl();
                description.setType("text/html");
                description.setValue(PropertyUtil.getString(content, ShopProductConstants.PROPERTY_DESCRIPTION));
                entry.setDescription(description);
            }

            // Google Merchant namespace
            final GoogleMerchantModule merchantData = new GoogleMerchantModuleImpl();
            // g:id
            merchantData.setId(content.getIdentifier());

            // g:price
            String priceCategoryUUID = syndicator.getPriceCategoryNode().getIdentifier();
            String currencyUUID = syndicator.getPriceCategoryNode().getProperty("currencyUUID").getString();
            String currency = ShopUtil.getCurrencyByUUID(currencyUUID).getProperty(ShopProductConstants.PROPERTY_TITLE).getString();

            if (content.hasNode(ShopProductConstants.NODE_PRICES)) {
                NodeIterator prices = content.getNode(ShopProductConstants.NODE_PRICES).getNodes();
                while (prices.hasNext()) { 
                    Node price = prices.nextNode();
                    if (priceCategoryUUID.equals(price.getProperty(ShopProductConstants.PROPERTY_PRICES_CATEGORY).getString())) {
                        if (price.hasProperty(ShopProductConstants.PROPERTY_PRICES_PRICE)) {
                            merchantData.setPrice(price.getProperty(ShopProductConstants.PROPERTY_PRICES_PRICE).getString() + " " + currency);
                        }
                    }
                }
            } else {
                merchantData.setPrice("0.0 " + currency);
            }

            // g:image_link
            if (content.hasProperty(ShopProductConstants.PROPERTY_IMAGE)) {
                try {
                    Asset imageAsset = damTemplatingFunctions.getAsset(content.getProperty(ShopProductConstants.PROPERTY_IMAGE).getString());
                    Link imageAssetLink = LinkUtil.createLinkInstance("dam", imageAsset.getItemKey().getAssetId());
                    merchantData.setImageLink(LinkTransformerManager.getInstance().getCompleteUrl().transform(imageAssetLink));
                } catch (LinkException e) {
                    log.debug("Couldn't create external link to asset with key " + content.getProperty(ShopProductConstants.PROPERTY_IMAGE).getString());
                }
            }

            entry.getModules().add(merchantData);

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
