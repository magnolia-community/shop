/**
 * This file Copyright (c) 2015 Magnolia International
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
package info.magnolia.module.shop.syndication;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.i18n.DefaultMessagesManager;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.context.MgnlContext;
import info.magnolia.i18nsystem.LocaleProvider;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.i18nsystem.TranslationServiceImpl;
import info.magnolia.jcr.node2bean.Node2BeanProcessor;
import info.magnolia.jcr.node2bean.Node2BeanTransformer;
import info.magnolia.jcr.node2bean.TypeMapping;
import info.magnolia.jcr.node2bean.impl.Node2BeanProcessorImpl;
import info.magnolia.jcr.node2bean.impl.Node2BeanTransformerImpl;
import info.magnolia.jcr.node2bean.impl.TypeMappingImpl;
import info.magnolia.registry.RegistrationException;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.mock.MockContext;

import java.io.IOException;
import java.util.Locale;

import javax.jcr.RepositoryException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * Tests for {@link info.magnolia.module.shop.syndication.ShopSyndicator}.
 */
public class ShopSyndicatorTest {

    private SyndFeed feed;

    private ServerConfiguration serverConfiguration;
    private ShopSyndicator shopSyndicator;

    @Before
    public void setUp() throws RepositoryException, Content2BeanException, IOException, RegistrationException {
        feed = new SyndFeedImpl();
        MockContext ctx = new MockContext();
        ComponentsTestUtil.setImplementation(TypeMapping.class, TypeMappingImpl.class);
        ComponentsTestUtil.setImplementation(Node2BeanTransformer.class, Node2BeanTransformerImpl.class);
        ComponentsTestUtil.setImplementation(Node2BeanProcessor.class, Node2BeanProcessorImpl.class);
        ComponentsTestUtil.setImplementation(MessagesManager.class, DefaultMessagesManager.class);
        LocaleProvider lp = mock(LocaleProvider.class);
        when(lp.getLocale()).thenReturn(Locale.ENGLISH);
        serverConfiguration = mock(ServerConfiguration.class);
        SimpleTranslator i18n = new SimpleTranslator(new TranslationServiceImpl(), lp);

        MgnlContext.setInstance(ctx);

        shopSyndicator = new ShopSyndicator(serverConfiguration, null, i18n);
    }

    @After
    public void tearDown() {
        ComponentsTestUtil.clear();
        MgnlContext.setInstance(null);
    }

    @Test
    public void testFeedInfoWillNotFailOnNPEWhenFeedNodeIsNull() {
        // WHEN
        shopSyndicator.setFeedInfo(feed);

        // THEN
        // will not fail because of NPE
    }

    @Test
    public void testFeedInfoWillSetFeedLinkToDefaultBaseURLWhenFeedNodeIsNull() {
        // GIVEN
        when(serverConfiguration.getDefaultBaseUrl()).thenReturn("http://example.com");

        // WHEN
        shopSyndicator.setFeedInfo(feed);

        // THEN
        assertEquals("http://example.com", feed.getLink());
    }
}
