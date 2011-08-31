/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.magnolia.module.shop.paragraphs;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.shop.util.ShopLinkUtil;
import info.magnolia.module.shop.util.ShopLinkUtil.ParamType;
import info.magnolia.module.shop.util.ShopUtil;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;
import info.magnolia.module.templatingkit.paragraphs.InternalTeaserModel;
import info.magnolia.module.templatingkit.templates.STKTemplateModel;
import info.magnolia.module.templatingkit.util.STKUtil;
import java.util.logging.Level;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author will
 */
public class ProductTeaserModel extends InternalTeaserModel {

    private static Logger log = LoggerFactory.getLogger(ProductTeaserModel.class);
    private Content siteRoot = null;

    public ProductTeaserModel(Content content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);
        RenderingModel currParent = parent;
        while (!(currParent instanceof STKTemplateModel)) {
            currParent = currParent.getParent();
        }
        siteRoot = ((STKTemplateModel) currParent).getSiteRoot();
    }

    @Override
    public Content getTarget() {
        Content shopRoot = null;
        try {
            shopRoot = STKUtil.getContentByTemplateCategorySubCategory(siteRoot, "feature", "productDetail");
        } catch (RepositoryException ex) {
            log.error("Could not get shopHome page", ex);
        }
        if (shopRoot != null) {
            return STKUtil.wrap(shopRoot);
        } else {
            return null;

        }
    }

    public Content getProduct() {
        String productUUID = NodeDataUtil.getString(content, "productUUID");
        if (StringUtils.isNotBlank(productUUID)) {
            return ContentUtil.getContentByUUID("data", productUUID);
        }
        return null;
    }

    public String getProductDetailPageLink() throws RepositoryException {
        String categoryUUID = NodeDataUtil.getString(content, "productCategoryUUID");
        Content product = this.getProduct();
        if (product != null) {
            Content detailPage = STKUtil.getContentByTemplateCategorySubCategory(
                    siteRoot, "feature", "productDetail");

            String selector = ParamType.PRODUCT + "." + product.getName();

            if (StringUtils.isNotEmpty(categoryUUID)) {
                Content category = ContentUtil.getContentByUUID("data", categoryUUID);
                selector = ParamType.CATEGORY + "." + category.getName()
                        + "." + ParamType.PRODUCT + "." + product.getName();
            }
            return ShopLinkUtil.createLinkFromContentWithSelectors(detailPage, selector);
        }
        return "";
    }
}
