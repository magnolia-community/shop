/**
 * This file Copyright (c) 2003-2009 Magnolia International
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
package info.magnolia.module.shop;

import info.magnolia.module.mail.MailModule;
import info.magnolia.module.mail.MgnlMailFactory;
import info.magnolia.module.mail.templates.MgnlEmail;
import info.magnolia.cms.i18n.MessagesUtil;
import info.magnolia.context.MgnlContext;
import javax.mail.MessagingException;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author gjoseph
 * @version $Revision: $ ($Author: $)
 */
public class ShopMailUtil {

  private static Logger log = LoggerFactory.getLogger(ShopMailUtil.class);

  private static String i18nBasename = "info.magnolia.module.shop.messages";

  public static void mail(String fromEmail, String toEmail, String subjectKey, String emailTemplate, Map templateValues)
      throws MessagingException, Exception {
    if (StringUtils.isEmpty(toEmail)) {
      throw new RuntimeException("Can't send email to user without an email");
    }

    if (templateValues == null) {
      templateValues = new HashMap();
    }
    templateValues.put("to", toEmail);

    final String subject = MessagesUtil.get(subjectKey, i18nBasename);
    final MgnlMailFactory mailFactory = MailModule.getInstance().getFactory();
    // try {

    final MgnlEmail mail = mailFactory.getEmailFromTemplate(emailTemplate, templateValues);

    mail.setFrom(fromEmail);
    mail.setToList(toEmail);
    // mail.setBccList(toName)
    mail.setSubject(subject);
    mail.setBodyFromResourceFile();

    mailFactory.getEmailHandler().prepareAndSendMail(mail);
    // } catch (Exception e) {
    // throw new RuntimeException(e); // TODO
    // }
  }

  protected static String buildAddress(String name, String email) {
    if (StringUtils.isEmpty(email)) {
      throw new IllegalArgumentException("No email provided, can't build address.");
    }
    if (StringUtils.isNotEmpty(name)) {
      return '"' + name + "\" <" + email + '>';
    } else {
      return email;
    }
  }

  protected static Locale determineLocale() {
    if (MgnlContext.hasInstance()) {
      return MgnlContext.getLocale();
    } else {
      return Locale.getDefault();
    }
  }
}