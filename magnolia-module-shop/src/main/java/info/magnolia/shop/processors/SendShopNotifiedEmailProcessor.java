/**
 * This file Copyright (c) 2010-2015 Magnolia International
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
package info.magnolia.shop.processors;

import info.magnolia.context.MgnlContext;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.form.processors.AbstractEMailFormProcessor;
import info.magnolia.module.form.processors.FormProcessorFailedException;
import info.magnolia.shop.ShopRepositoryConstants;
import info.magnolia.shop.beans.DefaultShoppingCartImpl;
import info.magnolia.shop.util.ShopUtil;
import info.magnolia.ui.api.message.Message;
import info.magnolia.ui.api.message.MessageType;
import info.magnolia.ui.framework.message.MessagesManager;

import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Default processor to send email about new order was placed to the shop administrator.
 */
public class SendShopNotifiedEmailProcessor extends AbstractEMailFormProcessor {

    private final static String ORDER_NOTIFIED_SUBJECT_CONTENT = "shop.order.notified.subject.content";
    private final static String ORDER_NOTIFIED_MESSAGE_CONTENT = "shop.order.notified.message.content";

    private static Logger log = LoggerFactory.getLogger(SendShopNotifiedEmailProcessor.class);

    @Inject
    private MessagesManager manager;

    @Inject
    private SimpleTranslator i18n;

    @Override
    public void internalProcess(Node content, Map<String, Object> params) throws FormProcessorFailedException {
        try {
            // add current shopping cart to the parameters map
            DefaultShoppingCartImpl cart = (DefaultShoppingCartImpl) ShopUtil.getPreviousShoppingCart(ShopUtil.getShopName());
            if (cart == null) {
                throw new FormProcessorFailedException("cart.not.found");
            }

            String orderId = cart.getName();
            String subject = i18n.translate(ORDER_NOTIFIED_SUBJECT_CONTENT, orderId);
            String body = i18n.translate(ORDER_NOTIFIED_MESSAGE_CONTENT, orderId);

            // send notified messages
            sendNotifiedMessage(subject, body);

            // send notified mail
            sendNotifiedEmail(subject, body, params);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FormProcessorFailedException("Error while sending notified your order info.");
        }
    }

    private void sendNotifiedEmail(String subject, String body, Map<String, Object> params) throws FormProcessorFailedException {
        try {
            Session shopsSession = MgnlContext.getJCRSession(ShopRepositoryConstants.SHOPS);
            String to = PropertyUtil.getString(shopsSession.getRootNode().getNode(ShopUtil.getShopName()), "email", StringUtils.EMPTY);
            sendMail(body, StringUtils.EMPTY, subject, to, "text/html", params);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FormProcessorFailedException("Error while sending notified email.");
        }
    }

    private void sendNotifiedMessage(String subject, String body) {
        Message message = new Message();
        message.setSubject(subject);
        message.setMessage(body);
        message.setType(MessageType.INFO);
        manager.sendLocalMessage(message);
    }
}
