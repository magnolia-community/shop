/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.magnolia.module.shop.dialog;

import info.magnolia.cms.gui.control.Edit;

/**
 *
 * @author will
 */
public class GridEdit extends Edit {

    public GridEdit(String name, String value) {
        super(name, value);
        this.setEvent("onChange", "DynamicTable.persist();", true);
    }

    @Override
    public String getHtmlSaveInfo() {
        return "";
    }
}
