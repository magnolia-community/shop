package info.magnolia.shop.beans;

import ch.fastforward.magnolia.ocm.beans.OCMBean;
import info.magnolia.shop.beans.ProductPrice;
import info.magnolia.shop.util.ShopUtil;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Created by will on 08.09.15.
 */
public class Product extends OCMBean {

    public static final String PROPERTY_NAME_TITLE = "title";
    public static final String PROPERTY_NAME_PRODUCT_DESCRIPTION_1 = "productDescription1";
    public static final String PROPERTY_NAME_PRODUCT_DESCRIPTION_2 = "productDescription2";

    private String title;
    private String productDescription1;
    private String productDescription2;
    private ProductPrice price;

    public Product(Node productNode) throws RepositoryException {
        // TODO: deprecate setUuid() in favor of setIdentifier() in OCMBean
        this.setUuid(productNode.getIdentifier());
        this.setPath(productNode.getPath());
        // TODO: OCMBean should set this automatically with setPath()
        this.setName(productNode.getName());

        // wrap with I18nNodeWrapper
        productNode = ShopUtil.wrapWithI18n(productNode);

        // marshall the properties
        if (productNode.hasProperty(PROPERTY_NAME_TITLE)) {
            setTitle(productNode.getProperty(PROPERTY_NAME_TITLE).getString());
        }
        if (productNode.hasProperty(PROPERTY_NAME_PRODUCT_DESCRIPTION_1)) {
            setProductDescription1(productNode.getProperty(PROPERTY_NAME_PRODUCT_DESCRIPTION_1).getString());
        }
        if (productNode.hasProperty(PROPERTY_NAME_PRODUCT_DESCRIPTION_2)) {
            setProductDescription2(productNode.getProperty(PROPERTY_NAME_PRODUCT_DESCRIPTION_2).getString());
        }

        // init price
        setPrice(ShopUtil.getProductPriceBean(productNode));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProductDescription1() {
        return productDescription1;
    }

    public void setProductDescription1(String productDescription1) {
        this.productDescription1 = productDescription1;
    }

    public String getProductDescription2() {
        return productDescription2;
    }

    public void setProductDescription2(String productDescription2) {
        this.productDescription2 = productDescription2;
    }

    public ProductPrice getPrice() {
        return price;
    }

    public void setPrice(ProductPrice price) {
        this.price = price;
    }
}
