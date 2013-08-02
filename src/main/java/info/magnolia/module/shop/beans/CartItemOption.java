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
package info.magnolia.module.shop.beans;

import ch.fastforward.magnolia.ocm.beans.OCMBean;
import java.io.Serializable;

/**
 * Product option bean.
 * @author will
 */
public class CartItemOption extends OCMBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private String title;
    private String optionSetUUID;
    private String valueUUID;
    private String valueName;
    private String valueTitle;

    /**
     * @return the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the valueUUID.
     */
    public String getValueUUID() {
        return valueUUID;
    }

    /**
     * @param valueUUID the valueUUID to set.
     */
    public void setValueUUID(String valueUUID) {
        this.valueUUID = valueUUID;
    }

    /**
     * @return the valueName.
     */
    public String getValueName() {
        return valueName;
    }

    /**
     * @param valueName the valueName to set.
     */
    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    /**
     * @return the valueTitle.
     */
    public String getValueTitle() {
        return valueTitle;
    }

    /**
     * @param valueTitle the valueTitle to set.
     */
    public void setValueTitle(String valueTitle) {
        this.valueTitle = valueTitle;
    }

    /**
     * @return the optionSetUUID.
     */
    public String getOptionSetUUID() {
        return optionSetUUID;
    }

    /**
     * @param optionSetUUID the optionSetUUID to set.
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
