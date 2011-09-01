/**
 * This file Copyright (c) 2009-2011 Magnolia International
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

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.exchange.ActivationManager;
import info.magnolia.cms.security.MgnlRoleManager;
import info.magnolia.cms.security.MgnlUserManager;
import info.magnolia.cms.security.Role;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.SecuritySupportImpl;
import info.magnolia.cms.security.User;
import info.magnolia.cms.util.FactoryUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.ModuleManagementException;
import info.magnolia.module.ModuleVersionHandler;
import info.magnolia.module.ModuleVersionHandlerTestCase;
import info.magnolia.module.exchangesimple.DefaultActivationManager;

import javax.jcr.RepositoryException;
import javax.security.auth.Subject;

/**
 * Test creation of shop menu
 * @author tmiyar
 *
 */
public class ShopModuleVersionHandlerTest extends ModuleVersionHandlerTestCase {
        
    
    protected String getModuleDescriptorPath() {
            return "/META-INF/magnolia/shop.xml";
        }

        protected void setUp() throws Exception {
            super.setUp();
            final SecuritySupportImpl securitySupport = new SecuritySupportImpl();
            // this, because of AddContactUserRoleTask
            securitySupport.addUserManager("system", new MgnlUserManager() {
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
                public Role getRole(String name) {
                    if ("superuser".equals(name)) {
                        return new Role() {
                            public String getName() {
                                return "superuser";
                            }

                            public void addPermission(String repository, String path, long permission) {
                                //
                            }

                            public void removePermission(String repository, String path) {
                                //
                            }

                            public void removePermission(String repository, String path, long permission) {
                                //
                            }
                        };
                    }
                    return super.getRole(name);
                }
            });
            FactoryUtil.setInstance(SecuritySupport.class, securitySupport);

            // due to SetupModuleRepositoriesTask#subscribeRepository
            FactoryUtil.setInstance(ActivationManager.class, new DefaultActivationManager());
        }

        protected ModuleVersionHandler newModuleVersionHandlerForTests() {
            return new ShopModuleVersionHandler();
        }

        /**
         * 
         * Test the installation process, there must be a new menu shops, new templates, data types...
         */
        public void testInstallation() throws ModuleManagementException, RepositoryException {

            final HierarchyManager hm = MgnlContext.getHierarchyManager(ContentRepository.CONFIG);
            // menu configuration
            setupConfigNode("/modules/adminInterface/config/menu",ItemType.CONTENT);
            setupConfigNode("/modules/data/config/types",ItemType.CONTENT);
            setupConfigNode("/modules/adminInterface/config/menu/data",ItemType.CONTENTNODE);
            setupConfigNode("/modules/adminInterface/config/menu/sampleShop",ItemType.CONTENTNODE);
            setupConfigNode("/modules/standard-templating-kit/config/site/templates/availability/templates",ItemType.CONTENTNODE);
            setupConfigNode("/modules/standard-templating-kit/templates",ItemType.CONTENTNODE);

            executeUpdatesAsIfTheCurrentlyInstalledVersionWas(null);
            //check menu
            assertEquals("shops", hm.getContent("/modules/adminInterface/config/menu/shops").getName());
            //check  datatypes
         
        }

        protected String[] getExtraWorkspaces() {
            return new String[]{"data", "dms", "resources", "templates"};
        }
        
        protected String getExtraNodeTypes() {
            return "/mgnl-nodetypes/test-magnoliaAnddata-nodetypes.xml";
        }

    }