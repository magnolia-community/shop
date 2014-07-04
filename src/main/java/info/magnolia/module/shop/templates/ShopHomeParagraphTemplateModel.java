/**
 * This file Copyright (c) 2010-2013 Magnolia International
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
package info.magnolia.module.shop.templates;

import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.module.templatingkit.templates.pages.STKPage;
import info.magnolia.module.templatingkit.templates.pages.STKPageModel;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.templating.functions.TemplatingFunctions;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


/**
 * Generates shop basic page structure, one productcategory page, productsearchresult page,
 * keywordsearchresult (to use with ee only) page, productdetail, checkoutform.
 * @author tmiyar
 *
 */
public class ShopHomeParagraphTemplateModel extends STKPageModel<STKPage> {


    public ShopHomeParagraphTemplateModel(Node content, STKPage definition, RenderingModel<?> parent, STKTemplatingFunctions stkFunctions, TemplatingFunctions templatingFunctions, DamTemplatingFunctions damTemplatingFunctions) {
        super(content, definition, parent, stkFunctions, templatingFunctions, damTemplatingFunctions);
    }

    @Override
    public String execute() {
        createShopStructure(content);
        return super.execute();
    }

    protected void createShopStructure(Node contentInSystemContext) {
        Node contentNode = content;
        try {
        if(templatingFunctions.children(contentNode, MgnlNodeType.NT_PAGE).isEmpty()) {

                createShopPage(content, "sample-category", "shop:pages/shopProductCategory");
                createShopPage(content, "product-detail", "shop:pages/shopProductDetail");
                createShopPage(content, "product-search-result", "shop:pages/shopProductSearchResult");
                createShopPage(content, "keyword-search-result", "shop:pages/shopProductKeywordResult");

                Node shoppingCart = createShopPage(content, "shopping-cart", "shop:pages/shopShoppingCart");
                Node checkout = createShopPage(shoppingCart, "check-out", "shop:pages/shopCheckoutForm");
                createShopPage(shoppingCart, "confirmation", "shop:pages/shopConfirmationPage");
                createShopPage(checkout, "form-step", "shop:pages/shopFormStep");
                createShopPage(checkout, "confirm-order", "shop:pages/shopFormStepConfirmOrder");


        }
        } catch (AccessDeniedException e) {
            throw new RuntimeException("can't auto generate shop structure", e);
        } catch (RepositoryException e) {
            throw new RuntimeException("can't auto generate shop structure", e);
        }

    }

    private Node createShopPage(Node parent, String pageName, String templateName) throws AccessDeniedException,
    RepositoryException {
        Node page = NodeUtil.createPath(parent, pageName, MgnlNodeType.NT_PAGE, true);
        page.getNode("MetaData").setProperty("mgnl:template", templateName);
        return page;
    }
}
