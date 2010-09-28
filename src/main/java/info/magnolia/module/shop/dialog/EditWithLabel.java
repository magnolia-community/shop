/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.magnolia.module.shop.dialog;

import info.magnolia.cms.gui.control.ControlImpl;
import info.magnolia.cms.gui.control.Edit;

/**
 *
 * @author will
 */
public class EditWithLabel extends Edit {

    public EditWithLabel(String name, String value) {
        super(name, value);
    }

    @Override
    public String getHtml() {
        StringBuffer html = new StringBuffer();
        String id = this.getId();
        if (id == null) {
            id = this.getName();
        }
        if (getLabel() != null) {
            html.append("<label for=\"" + id + "\" style=\"display: block;\">" + getLabel() + "</label>");
        }
        if (this.getRows().equals("1")) { //$NON-NLS-1$
            html.append("<input type=\"text\""); //$NON-NLS-1$
            html.append(" name=\"" + this.getName() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            html.append(" id=\"" + id + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            html.append(" value=\"" + ControlImpl.escapeHTML(this.getValue()) + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            html.append(getHtmlEvents());
            html.append(this.getHtmlCssClass());
            html.append(this.getHtmlCssStyles());
            html.append(" />"); //$NON-NLS-1$
        } else {
            html.append("<textarea"); //$NON-NLS-1$
            html.append(" name=\"" + this.getName() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            html.append(" id=\"" + id + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            html.append(" rows=\"" + this.getRows() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            html.append(" cols=\"100\""); //$NON-NLS-2$
            html.append(this.getHtmlCssClass());
            html.append(this.getHtmlCssStyles());
            html.append(getHtmlEvents());
            html.append(">"); //$NON-NLS-1$
            html.append(this.getValue());
            html.append("</textarea>"); //$NON-NLS-1$
        }
        if (this.getSaveInfo()) {
            html.append(this.getHtmlSaveInfo());
        }
        return html.toString();
    }
}
