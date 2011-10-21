/**
 * This file Copyright (c) 2003-2011 Magnolia International
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
package info.magnolia.module.shop.dialog;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.gui.control.Button;
import info.magnolia.cms.gui.control.ButtonSet;
import info.magnolia.cms.gui.control.ControlImpl;
import info.magnolia.cms.gui.control.Hidden;
import info.magnolia.cms.gui.dialog.DialogBox;
import info.magnolia.cms.gui.dialog.DialogButtonSet;
import info.magnolia.cms.gui.dialog.DialogControl;
import info.magnolia.cms.gui.dialog.DialogControlImpl;
import info.magnolia.cms.gui.dialog.DialogFactory;
import info.magnolia.cms.gui.dialog.UUIDDialogControl;
import info.magnolia.cms.gui.misc.CssConstants;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.shop.ShopModule;
import info.magnolia.module.templatingkit.dam.DAMHandler;
import info.magnolia.module.templatingkit.dam.DAMSupport;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a copy from DialogDAM because of MGNLSTK-769 and the method i need to override is private
 * 
 * This dialog is loaded when control=dam is selected. It's configuration is complemented
 * with the controls defined for each (DAMSupport) handler
 * Once rendered this subcontrols will have as name: "dam control name + handler control name"
 * i.e. dam control name "image" handler control name "dmsUUID"
 * @author tmiyar
 *
 */
public class ShopDialogDAM extends DialogControlImpl implements MultiLanguageDialogControl {

    private static final String ORIGINAL_NAME = "originalName";
    private static final Logger log = LoggerFactory.getLogger(ShopDialogDAM.class);
    private DialogBox box;
    private Collection<DAMHandler> handlers = null;
    private List<String> languages;

    public void init(HttpServletRequest request, HttpServletResponse response,
            Content storageNode, Content configNode) throws RepositoryException {

        // First, try to set the languages by looking at the path of the storage
        // node
        if (storageNode != null) {
            DialogLanguagesUtil.initSiteKey(this, storageNode);
        } else {
            String mgnlPath = request.getParameter("mgnlPath");
            if (mgnlPath.startsWith("/")) {
                // this should be the case
                mgnlPath = mgnlPath.substring(1);
            }
            mgnlPath = StringUtils.substringBefore(mgnlPath, "/");
            setConfig("siteKey", mgnlPath);
        }
        super.init(request, response, storageNode, configNode);
        // if the languages have not been initialized yet
        if (getConfigValue("siteKey") == null) {
            DialogLanguagesUtil.initSiteKey(this, storageNode);
        }
        languages = DialogLanguagesUtil.initLanguages(this);
        this.handlers = getDamSupport().getHandlers().values();
        loadSubs();
    }

    public void drawHtmlPreSubs(String language, Writer out) throws IOException {
        if(handlers.size() > 1) {
            drawRadio(language, out);
        }
        else if(handlers.size() == 1){
            Hidden control = new Hidden(getName() + getLanguageSuffix(language), handlers.iterator().next().getName());
            out.write(control.getHtml());
        }
    }

    protected void drawSubs(String language, Writer out) throws IOException {
        String aName = this.getName();
        
        String locale = getLanguageSuffix(language);

        for(DAMHandler handler: handlers) {

            if(handlers.size() > 1) {
                String style = "style=\"display:none;\"";
                out.write("<div id=\"" + aName + locale + "_" + handler.getName() + "_dam_div\" " + style + " >");

                out.write("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout:fixed\" >");
                out.write("<col width=\"200\" /><col />");

            }
            Iterator it = handler.getControls().iterator();

            while (it.hasNext()) {
                Content controlNodeConfig = (Content) it.next();
                // since the sub name is constructed as controlName+handlerName, we need to use uni18ned name
                // to construct sub name and attach locale afterwards
                DialogControlImpl sub = this.getSub(aName + controlNodeConfig.getName() + locale);
                if (sub == null) {
                    sub = this.getSub(aName + controlNodeConfig.getName() );
                }
                sub.drawHtml(out);
            }

            if(handlers.size() > 1) {
                out.write("</table></div>");
            }
        }
    }

    private void loadSubs() {
        if (languages != null && languages.size() > 0) {
            for (int i = 0; i < languages.size(); i++) {
                for(DAMHandler handler: handlers) {
        
                    for (Object c : handler.getControls()) {
                        Content controlNodeConfig = (Content) c;
                        try {
                            DialogControl control = DialogFactory.loadDialog(this.getRequest(), this.getResponse(), this.getStorageNode(),
                                    controlNodeConfig);
                            String name = ((DialogControlImpl) control).getName() + getLanguageSuffix(languages.get(i));
                            ((DialogControlImpl) control).setName(this.getName() + name);
        
                            this.addSub((DialogControlImpl) control);
        
                        } catch (RepositoryException e) {
                            // ignore
                            log.debug(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }

    public void drawHtmlPostSubs(String language, Writer out) throws IOException {
        if(box != null) {
            out.append("<script type=\"text/javascript\"> ");
            out.append("mgnl.dam.DAMDialog.onSelectionChanged('" + this.getName() +getLanguageSuffix(language) + "','" + this.getValue() + "');");
            out.append("</script>");
            box.drawHtmlPost(out);
        }
    }

    private DAMSupport getDamSupport() {
        try {
            return ShopModule.getInstance().getDamSupport();
        } catch (Exception e) {
            throw new RuntimeException("Can't get shop damsupport to use", e);
        }
    }

    private List<Button> getDamRadioOptions(String language) {
        List<Button> selectOptions = new ArrayList<Button>();
        for(DAMHandler handler: handlers) {
            Button option = new Button(this.getName() +getLanguageSuffix(language), handler.getName());
            option.setLabel(this.getMessage(handler.getDamSelectorOptionLabel()));
            option.setOnclick("mgnl.dam.DAMDialog.onSelectionChanged('" + this.getName() +getLanguageSuffix(language) + "','" + handler.getName() + "');");
            selectOptions.add(option);
        }
        return selectOptions;
    }

    public void drawRadio(String language, Writer out) throws IOException {

        box = new DialogButtonSet();
        try {
            box.init(this.getRequest(), this.getResponse(), null, null);
        } catch (RepositoryException e) {
            //ignore
        }
        box.setLabel(this.getMessage(this.getLabel()) + " ("+ language +")" );
        box.setSaveInfo(true);
        //init value for the different languages
        this.setValue(null);
        ButtonSet control;
        // radio
        control = new ButtonSet(this.getName() +getLanguageSuffix(language), this.getValue(language));

        control.setButtonType(ControlImpl.BUTTONTYPE_RADIO);

        control.setCssClass(this.getConfigValue("cssClass", CssConstants.CSSCLASS_BUTTONSETBUTTON));

        control.setSaveInfo(true);

        control.setType(PropertyType.TYPENAME_STRING);
        

        control.setButtonHtmlPre("<tr><td class=\"" + CssConstants.CSSCLASS_BUTTONSETBUTTON + "\">");
        control.setButtonHtmlInter("</td><td class=\"" + CssConstants.CSSCLASS_BUTTONSETLABEL + "\">");
        control.setButtonHtmlPost("</td></tr>");
        List<Button> selectOptions = getDamRadioOptions(language);
        int cols = selectOptions.size();
        if (cols > 1) {
            control.setHtmlPre(control.getHtmlPre() + "<tr>");
            control.setHtmlPost("</tr>" + control.getHtmlPost());
            int item = 1;
            int itemsPerCol = (int) Math.ceil(selectOptions.size() / ((double) cols));
            for (Object selectOption : selectOptions) {
                Button b = (Button) selectOption;
                if (item == 1) {
                    b.setHtmlPre("<td><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" >"
                            + control.getButtonHtmlPre());
                }
                if (item == itemsPerCol) {
                    b.setHtmlPost(control.getButtonHtmlPost() + "</table></td><td class=\""
                            + CssConstants.CSSCLASS_BUTTONSETINTERCOL
                            + "\"></td>");
                    item = 1;
                } else {
                    item++;
                }
            }
            // very last button: close table
            int lastIndex = selectOptions.size() - 1;
            // avoid ArrayIndexOutOfBoundsException, but should not happen
            if (lastIndex > -1) {
                ((Button) selectOptions.get(lastIndex)).setHtmlPost(control.getButtonHtmlPost() + "</table>");
            }
        }

        int width = 100;
        if(cols < 5) {
            width = cols * 20;
        }
        control.setHtmlPre("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"" + width + "%\">");

        control.setButtons(selectOptions);
        box.drawHtmlPre(out);
        out.write(control.getHtml());

    }

    @Override
    public void setName(String s) {
        // on name update made due to i18n, relay updated names also to all subs
        if (!StringUtils.isBlank(s)) {
            String originalName = super.getName();
            String locale = StringUtils.substringAfter(s, originalName);
            if (!StringUtils.isBlank(originalName) && locale.startsWith("_")) {
                // keep the original name for later
                this.setConfig(ORIGINAL_NAME, originalName);
                // also update all subs since they have been set all by now
                List<DialogControlImpl> subs = this.getSubs();
                for (DialogControlImpl sub: subs) {
                    sub.setName(sub.getName()+locale);
                }
            }
        }
        super.setName(s);
    }

    @Override
    public void drawHtml(Writer out) throws IOException {
        
        if (languages != null && languages.size() > 0) {
            for (int i = 0; i < languages.size(); i++) {
                this.drawHtmlPreSubs(languages.get(i), out);
                this.drawSubs(languages.get(i), out);
                this.drawHtmlPostSubs(languages.get(i), out);
            }
        }
    }
    
    public String getValue(String language) {
        if (this.value == null) {
            if (this.getStorageNode() != null) {
                this.value = readValue(language);
                if(this instanceof UUIDDialogControl){
                    String repository = ((UUIDDialogControl)this).getRepository();
                    this.value = ContentUtil.uuid2path(repository, this.value);
                }
            }
            
            if (MgnlContext.getParameter(this.getName()) != null) {
                this.value = MgnlContext.getParameter(this.getName());
            }

            if (this.value == null && StringUtils.isNotEmpty(getConfigValue(DEFAULT_VALUE_PROPERTY))) {
                return this.getMessage(this.getConfigValue(DEFAULT_VALUE_PROPERTY));
            }

            if (this.value == null) {
                this.value = StringUtils.EMPTY;
            }
        }
        return this.value;
    }
    
    protected String readValue(String language) {
        try {
            if(!this.getStorageNode().hasNodeData(this.getName() + getLanguageSuffix(language))){
                return null;
            }
        }
        catch (RepositoryException e) {
            log.error("can't read nodedata [" + this.getName() + "]", e);
            return null;
        }
        return this.getStorageNode().getNodeData(this.getName() + getLanguageSuffix(language)).getString();
    }

    public List<String> getLanguages() {
        return this.languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public String getLanguageSuffix(String language) {
        return DialogLanguagesUtil.getLanguageSuffix(this, language);
    }
    
}
