/**
 * This file Copyright (c) 2010-2011 Magnolia International
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
package info.magnolia.module.shop.search;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templating.MagnoliaTemplatingUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;

/**
 * Product list for the current offers if no productcategory selected yet.
 * @author tmiyar
 *
 */
public class DefaultProductListType extends AbstractProductListType {

    public DefaultProductListType(Content siteRoot, Content content) {
        super(siteRoot, content);
    }

    @Override
    protected String getPagerLink() {
        return MagnoliaTemplatingUtilities.getInstance().createLink(getContent());
    }

    @Override
    public List<Content> getResult() {
        List<Content> productList = new ArrayList<Content>();
        Content currentOffers = ContentUtil.getContent(getContent(), "currentOffers");
        if (currentOffers != null) {
          Collection<NodeData> offers = currentOffers.getNodeDataCollection();
          for (Iterator<NodeData> iterator = offers.iterator(); iterator.hasNext();) {
            NodeData producUuidNodeData = (NodeData) iterator.next();
            Content productNode = ContentUtil.getContentByUUID("data",
                producUuidNodeData.getString());
            try {
              if (productNode != null
                      && productNode.getItemType().getSystemName().equals("shopProduct")
                      && ShopUtil.getShopName().equals(productNode.getAncestor(2).getName())) {
                  productList.add(productNode);
              }
            } catch (RepositoryException e) {
  
            }
          } //end for
        } //end else
        return productList;
    }

    @Override
    public String getTitle() {
        return ShopUtil.getMessages().get("productList.currentOffers");
    }

    @Override
    public String getListType() {
        return "default";
    }

}
