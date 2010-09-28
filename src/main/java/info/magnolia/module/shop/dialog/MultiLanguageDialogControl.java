/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package info.magnolia.module.shop.dialog;

import info.magnolia.cms.gui.dialog.DialogControl;
import java.util.List;

/**
 *
 * @author will
 */
public interface MultiLanguageDialogControl extends DialogControl {
    public List<String> getLanguages();
    public void setLanguages(List<String> languages);
}
