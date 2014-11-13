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
package info.magnolia.module.shop.setup;


import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import info.magnolia.cms.exchange.ActivationManager;
import info.magnolia.cms.security.Realm;
import info.magnolia.cms.security.Role;
import info.magnolia.cms.security.RoleManager;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.SecuritySupportImpl;
import info.magnolia.cms.security.SystemUserManager;
import info.magnolia.cms.security.UserManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SystemContext;
import info.magnolia.module.ModuleManagementException;
import info.magnolia.module.ModuleVersionHandler;
import info.magnolia.module.ModuleVersionHandlerTestCase;
import info.magnolia.module.model.ModuleDefinition;
import info.magnolia.module.model.Version;
import info.magnolia.module.model.reader.BetwixtModuleDefinitionReader;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link ShopModuleVersionHandler}.
 */
public class ShopModuleVersionHandlerTest extends ModuleVersionHandlerTestCase {

    private Session config;
    private Session templates;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        config = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        templates = MgnlContext.getJCRSession("templates");
    }

    @Override
    protected String getModuleDescriptorPath() {
        return "/META-INF/magnolia/shop.xml";
    }

    @Override
    protected List<ModuleDefinition> getModuleDefinitionsForTests() throws ModuleManagementException {
        final ModuleDefinition core = new BetwixtModuleDefinitionReader().readFromResource("/META-INF/magnolia/core.xml");
        return Collections.singletonList(core);
    }

    @Override
    protected List<String> getModuleDescriptorPathsForTests() {
        return Arrays.asList("/META-INF/magnolia/core.xml");
    }

    @Override
    protected ModuleVersionHandler newModuleVersionHandlerForTests() {
        return new ShopModuleVersionHandler();
    }

    @Override
    protected String[] getExtraWorkspaces() {
        return new String[]{"dam", "resources", "templates"};
    }

    @Override
    protected String getExtraNodeTypes() {
        return "/mgnl-nodetypes/test-magnoliaAnddata-nodetypes.xml";
    }

    @Test
    public void testDoesNotInstallWhenMigratingFromTooOldVersion() throws Exception {
        try {
            // WHEN
            executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("1.1.1"));
        } catch (Throwable t) {
            // THEN
            assertTrue(t instanceof AssertionError);
            assertTrue(t.getMessage().contains("<stoppedConditionsNotMet>"));
        }
    }

    @Test
    public void testCleanInstall() throws Exception {
        // GIVEN
        Components.getComponent(SystemContext.class).getJCRSession(RepositoryConstants.USERS).getRootNode().addNode("system");
        Components.getComponent(SystemContext.class).getJCRSession(RepositoryConstants.USER_ROLES).getRootNode().addNode("superuser");
        ComponentsTestUtil.setInstance(ActivationManager.class, mock(ActivationManager.class));

        SecuritySupportImpl securitySupportImpl = new SecuritySupportImpl();
        RoleManager roleManager = mock(RoleManager.class);
        Role role = mock(Role.class);
        when(roleManager.getRole("superuser")).thenReturn(role);
        securitySupportImpl.setRoleManager(roleManager);
        Map<String, UserManager> userManagers = new HashMap<String, UserManager>();
        userManagers.put(Realm.REALM_SYSTEM.getName(), new SystemUserManager());
        securitySupportImpl.setUserManagers(userManagers);
        ComponentsTestUtil.setInstance(SecuritySupport.class, securitySupportImpl);

        this.setupConfigNode("/modules/standard-templating-kit/config/site/templates/availability/templates");
        this.setupConfigNode("/modules/multisite/config/sites/default/templates/availability/templates");
        this.setupConfigNode("/modules/ui-admincentral/config/appLauncherLayout/groups/edit");
        this.setupConfigNode("/modules/ui-admincentral/config/appLauncherLayout/groups/stk");
        this.setupConfigNode("/modules/ui-admincentral/config/appLauncherLayout/groups/sampleShop");
        this.setupConfigNode("/modules/ui-admincentral/config/appLauncherLayout/groups/shop");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(null);

        // THEN
        assertTrue(config.itemExists("/modules/standard-templating-kit/config/site/templates/availability/templates/shopCheckoutForm"));
        assertTrue(config.itemExists("/modules/multisite/config/sites/default/templates/availability/templates/shopCheckoutForm"));
    }

    @Test
    public void testUpdateTo210() throws Exception {
        // GIVEN
        setupNode("templates","/shop/pages");
        setupConfigNode("/modules/shop/templates/pages/shopHome/templateScript");
        setupConfigNode("/modules/shop/templates/pages/shopHome/areas/promos");
        setupConfigNode("/modules/shop/templates/pages/shopHome/navigation/vertical/startLevel");
        setupConfigNode("/modules/shop/templates/pages/shopHome/navigation/vertical/template");
        setupConfigNode("/modules/shop/templates/pages/shopHome/areas/main/areas/breadcrumb");


        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.0.1"));

        // THEN
        assertTrue(!templates.itemExists("/shop/pages"));
        assertTrue(!config.itemExists("/modules/shop/templates/pages/shopHome/templateScript"));
        assertTrue(!config.itemExists("/modules/shop/templates/pages/shopHome/areas/promos"));
        assertTrue(!config.itemExists("/modules/shop/templates/pages/shopHome/navigation/vertical/startLevel"));
        assertTrue(!config.itemExists("/modules/shop/templates/pages/shopHome/navigation/vertical/template"));
        assertTrue(!config.itemExists("/modules/shop/templates/pages/shopHome/areas/main/areas/breadcrumb"));
        assertTrue(config.itemExists("/modules/shop/templates/pages/shopHome/areas/sectionHeader"));
        assertTrue(config.itemExists("/modules/shop/templates/pages/shopFormStep/areas/promos"));
        assertTrue(config.itemExists("/modules/shop/templates/pages/shopCheckoutForm/areas/promos"));
        assertTrue(config.itemExists("/modules/shop/templates/pages/shopShoppingCart/areas/promos"));
        assertTrue(config.itemExists("/modules/shop/templates/pages/shopProductDetail/areas/promos"));
        assertTrue(config.itemExists("/modules/shop/templates/pages/shopProductCategory/areas/promos"));
        assertTrue(config.itemExists("/modules/shop/templates/pages/shopConfirmationPage/areas/promos"));
        assertTrue(config.itemExists("/modules/shop/templates/pages/shopProductSearchResult/areas/promos"));
        assertTrue(config.itemExists("/modules/shop/templates/pages/shopFormStepConfirmOrder/areas/promos"));
        assertTrue(config.itemExists("/modules/shop/templates/pages/shopProductKeywordResult/areas/promos"));
        assertTrue(config.itemExists("/modules/standard-templating-kit/config/themes/pop/cssFiles/shop"));
    }

    @Test
    public void testUpdateTo210AddSampleShopFolders() throws Exception {
        // GIVEN
        setupConfigNode("/modules/shop");
        setupNode(ShopRepositoryConstants.SHOPPING_CARTS, "/sampleShop/userSpecifiedNode");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.0.1"));

        // THEN
        assertTrue(MgnlContext.getJCRSession(ShopRepositoryConstants.SHOPPING_CARTS).itemExists("/sampleShop/userSpecifiedNode"));
        assertTrue(MgnlContext.getJCRSession(ShopRepositoryConstants.SHOP_SUPPLIERS).itemExists("/sampleShop"));
    }
}