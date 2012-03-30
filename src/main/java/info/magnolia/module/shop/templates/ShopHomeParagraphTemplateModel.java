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
package info.magnolia.module.shop.templates;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.templating.functions.TemplatingFunctions;
import info.magnolia.module.templatingkit.functions.STKTemplatingFunctions;
import info.magnolia.module.templatingkit.templates.pages.STKPage;
import info.magnolia.module.templatingkit.templates.STKTemplateModel;
import info.magnolia.module.templatingkit.util.STKUtil;

/**
 * Generates shop basic page structure, one productcategory page, productsearchresult page,
 * keywordsearchresult (to use with ee only) page, productdetail, checkoutform.
 * @author tmiyar
 *
 */
public class ShopHomeParagraphTemplateModel extends STKTemplateModel<STKPage> {
    
    public ShopHomeParagraphTemplateModel(Node content, STKPage definition,
            RenderingModel<?> parent, STKTemplatingFunctions stkFunctions,
            TemplatingFunctions templatingFunctions) {
        super(content, definition, parent, stkFunctions, templatingFunctions);
    }

    public String execute() {
        try {
            STKUtil.doPrivileged(ContentUtil.asContent(content), new STKUtil.PrivilegedOperation(){
                @Override
                public void exec(Content contentInSystemContext) throws RepositoryException {
                    createShopStructure(contentInSystemContext);
                }
            });
        }
        catch (RepositoryException e) {
            throw new RuntimeException("can't auto generate the main or extra area", e);
        }
        return super.execute();
    }

    protected void createShopStructure(Content contentInSystemContext) {
        Content contentNode = ContentUtil.asContent(content);
        if(!contentNode.hasChildren()) {
            try {
                createShopPage(contentNode, "sample-category", "shopProductCategory");
                createShopPage(contentNode, "product-detail", "shopProductDetail");
                createShopPage(contentNode, "product-search-result", "shopProductSearchResult");
                createShopPage(contentNode, "keyword-search-result", "shopProductKeywordResult");
                
                Content shoppingCart = createShopPage(contentNode, "shopping-cart", "shopShoppingCart");
                Content checkout = createShopPage(shoppingCart, "check-out", "shopCheckoutForm");
                createShopPage(shoppingCart, "confirmation", "shopConfirmationPage");
                createShopPage(checkout, "form-step", "shopFormStep");
                createShopPage(checkout, "confirm-order", "shopFormStepConfirmOrder");
                
                content.save();
            } catch (AccessDeniedException e) {
                throw new RuntimeException("can't auto generate shop structure", e);
            } catch (RepositoryException e) {
                throw new RuntimeException("can't auto generate shop structure", e);
            }
        }
        
    }

    private Content createShopPage(Content parent, String pageName, String templateName) throws AccessDeniedException,
            RepositoryException {
        Content page = ContentUtil.getOrCreateContent(parent, pageName, ItemType.CONTENT, true);
        page.getMetaData().setTemplate(templateName);
        return page;
    }
}
