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


import static org.mockito.Mockito.mock;

import info.magnolia.cms.exchange.ActivationManager;
import info.magnolia.cms.security.MgnlRoleManager;
import info.magnolia.cms.security.MgnlUserManager;
import info.magnolia.cms.security.Role;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.SecuritySupportImpl;
import info.magnolia.cms.security.User;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SingleJCRSessionSystemContext;
import info.magnolia.context.SystemContext;
import info.magnolia.migration.reporting.DefaultReportingService;
import info.magnolia.migration.reporting.ReportingService;
import info.magnolia.module.ModuleManagementException;
import info.magnolia.module.ModuleVersionHandler;
import info.magnolia.module.ModuleVersionHandlerTestCase;
import info.magnolia.module.model.ModuleDefinition;
import info.magnolia.module.model.reader.BetwixtModuleDefinitionReader;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.security.auth.Subject;

import org.junit.Test;

/**
 * Test creation of shop menu.
 * @author tmiyar
 */
public class ShopModuleVersionHandlerTest extends ModuleVersionHandlerTestCase {

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
    public void setUp() throws Exception {
        super.setUp();
        ComponentsTestUtil.setImplementation(SystemContext.class, SingleJCRSessionSystemContext.class);
        ComponentsTestUtil.setImplementation(ReportingService.class, DefaultReportingService.class);

        final Node superuser = Components.getComponent(SystemContext.class).getJCRSession(RepositoryConstants.USER_ROLES).getRootNode().addNode("superuser");
        final Session config = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        config.getRootNode().addNode("server").setProperty("admin", "true");
        config.getRootNode().addNode("modules").addNode("adminInterface").addNode("config").addNode("menu").addNode("data");

        final SecuritySupportImpl securitySupport = new SecuritySupportImpl();
        // this, because of AddContactUserRoleTask
        securitySupport.addUserManager("system", new MgnlUserManager() {
            @Override
            public User getUser(String name) {
                if ("anonymous".equals(name)) {
                    // wooooh ugly hack - to return a DummyUser
                    return getUser((Subject) null);
                }
                return super.getUser(name);
            }
        });
        // this, because of SetupModuleRepositoriesTask.grantRepositoryToSuperuser
        securitySupport.setRoleManager(new MgnlRoleManager() {
            @Override
            public Role getRole(String name) {
                if ("superuser".equals(name)) {
                    return new Role() {
                        @Override
                        public String getName() {
                            return "superuser";
                        }

                        @Override
                        public void addPermission(String repository, String path, long permission) {
                        }

                        @Override
                        public void removePermission(String repository, String path) {
                        }

                        @Override
                        public void removePermission(String repository, String path, long permission) {
                        }

                        @Override
                        public String getId() {
                            // return "superuser"
                            final String id = null;
                            try {
                                return superuser.getIdentifier();
                            } catch (RepositoryException e) {
                            }
                            return id;
                        }
                    };
                }
                return super.getRole(name);
            }
        });

        ComponentsTestUtil.setInstance(SecuritySupport.class, securitySupport);
        // due to SetupModuleRepositoriesTask#subscribeRepository
        ComponentsTestUtil.setInstance(ActivationManager.class, mock(ActivationManager.class));
    }

    @Override
    protected ModuleVersionHandler newModuleVersionHandlerForTests() {
        return new ShopModuleVersionHandler();
    }

    /**
     * Test the installation process, there must be a new menu shops, new templates, data types...
     */
    @Test
    public void testInstallation() throws Exception {
        // GIVEN menu configuration
//        setupConfigNode("/modules/adminInterface/config/menu");
//        setupConfigNode("/modules/data/config/types");
//        setupConfigNode("/modules/adminInterface/config/menu/data");
//        setupConfigNode("/modules/adminInterface/config/menu/sampleShop");
//        setupConfigNode("/modules/standard-templating-kit/config/site/templates/availability/templates");
//        setupConfigNode("/modules/standard-templating-kit/templates");
//
//        setupConfigNode("/modules/ocm/config/classDescriptors/testShoppingCart/fieldDescriptors/termsAccepted");
//        setupConfigNode("/modules/ocm/config/classDescriptors/testShoppingCart/fieldDescriptors/cartDiscountRate/");
//        setupConfigNode("/modules/shop/templates/components/features/form");
//
//        Session session = Components.getComponent(SystemContext.class).getJCRSession(RepositoryConstants.CONFIG);
//        // MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
//        session.getRootNode().addNode("modules").addNode("ocm").addNode("config").addNode("classDescriptors", MgnlNodeType.NT_CONTENTNODE);
//
//        // WHEN
//        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(null);

        // THEN
        // check menu
        // assertTrue(session.getRootNode().hasNode("modules/adminInterface/config/menu/shops/shoppingCarts"));

        // check data types
//        Node nodeTypes = session.getRootNode().getNode("modules/data/config/types");

        // FIXME doesn't work with maven:
        // assertTrue(nodeTypes.hasNode("shop"));
        // assertTrue(nodeTypes.hasNode("shop/shopCurrencies"));
        // assertTrue(nodeTypes.hasNode("shop/shopCurrencies/shopCurrency"));
        // assertTrue(nodeTypes.hasNode("shop/shopPriceCategories"));
        // assertTrue(nodeTypes.hasNode("shop/shopPriceCategories/shopPriceCategory"));
        // assertTrue(nodeTypes.hasNode("shop/shopTaxCategories"));
        // assertTrue(nodeTypes.hasNode("shop/shopTaxCategories/shopTaxCategory"));
        // assertTrue(nodeTypes.hasNode("shop/shopCountries"));
        // assertTrue(nodeTypes.hasNode("shop/shopCountries/shopCountry"));
        // assertTrue(nodeTypes.hasNode("shop/shopShippingOptions"));
        // assertTrue(nodeTypes.hasNode("shop/shopShippingOptions/shopShippingOption"));
        //
        // assertTrue(nodeTypes.hasNode("shopProduct"));
        // assertTrue(nodeTypes.hasNode("shopProduct/shopProductOptions"));
        // assertTrue(nodeTypes.hasNode("shopProduct/shopProductOptions/shopProductOption"));
        //
        // assertTrue(nodeTypes.hasNode("shopSupplier"));
    }

    @Override
    protected String[] getExtraWorkspaces() {
        return new String[] { "data", "dms", "resources", "templates" };
    }

    @Override
    protected String getExtraNodeTypes() {
        return "/mgnl-nodetypes/test-magnoliaAnddata-nodetypes.xml";
    }

}