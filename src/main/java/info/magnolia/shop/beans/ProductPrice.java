package info.magnolia.shop.beans;

import info.magnolia.context.MgnlContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by will on 08.09.15.
 */
public class ProductPrice {
    private static Logger log = LoggerFactory.getLogger(ProductPrice.class);

    private double price;
    private String tax;
    private String taxIncluded;
    private String currency;
    private String formatting;

    public String getPrice() {
        try {
            if (price >= 0 && StringUtils.isNotBlank(this.getFormatting())) {
                Locale locale = MgnlContext.getLocale();
                try {
                    locale = MgnlContext.getAggregationState().getLocale();
                } catch (IllegalStateException e) {
                    log.debug("nothing, will get the default context locale", e);
                }

                NumberFormat formatter = NumberFormat.getNumberInstance(locale);
                DecimalFormat df = (DecimalFormat) formatter;
                df.applyPattern(this.getFormatting());
                return df.format(price);
            }
        } catch (Exception e) {
            log.error("error reading price", e);
        }
        return "" + price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTaxIncluded() {
        return taxIncluded;
    }

    public void setTaxIncluded(String taxIncluded) {
        this.taxIncluded = taxIncluded;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFormatting() {
        return formatting;
    }

    public void setFormatting(String formatting) {
        this.formatting = formatting;
    }
}
