/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package info.magnolia.module.shop.beans;

import info.magnolia.module.ocm.beans.OCMBean;
import java.io.Serializable;

/**
 *
 * @author will
 */
public class CartItemOption extends OCMBean implements Serializable {
    private String title;
    private String optionSetUUID;
    private String valueUUID;
    private String valueName;
    private String valueTitle;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the valueUUID
     */
    public String getValueUUID() {
        return valueUUID;
    }

    /**
     * @param valueUUID the valueUUID to set
     */
    public void setValueUUID(String valueUUID) {
        this.valueUUID = valueUUID;
    }

    /**
     * @return the valueName
     */
    public String getValueName() {
        return valueName;
    }

    /**
     * @param valueName the valueName to set
     */
    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    /**
     * @return the valueTitle
     */
    public String getValueTitle() {
        return valueTitle;
    }

    /**
     * @param valueTitle the valueTitle to set
     */
    public void setValueTitle(String valueTitle) {
        this.valueTitle = valueTitle;
    }

    /**
     * @return the optionSetUUID
     */
    public String getOptionSetUUID() {
        return optionSetUUID;
    }

    /**
     * @param optionSetUUID the optionSetUUID to set
     */
    public void setOptionSetUUID(String optionSetUUID) {
        this.optionSetUUID = optionSetUUID;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof CartItemOption && this.valueUUID != null) {
            CartItemOption cio = (CartItemOption) o;
            return valueUUID.equals(cio.getValueUUID());
        }
        return false;
    }

}
