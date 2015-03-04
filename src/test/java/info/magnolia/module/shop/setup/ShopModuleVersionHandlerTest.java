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
package info.magnolia.module.shop.setup;

import static info.magnolia.test.hamcrest.NodeMatchers.*;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import info.magnolia.cms.exchange.ActivationManager;
import info.magnolia.cms.security.MgnlRoleManager;
import info.magnolia.cms.security.Permission;
import info.magnolia.cms.security.Realm;
import info.magnolia.cms.security.Role;
import info.magnolia.cms.security.RoleManager;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.SecuritySupportImpl;
import info.magnolia.cms.security.SystemUserManager;
import info.magnolia.cms.security.UserManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SystemContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.module.InstallContext;
import info.magnolia.module.ModuleVersionHandler;
import info.magnolia.module.ModuleVersionHandlerTestCase;
import info.magnolia.module.data.DataModule;
import info.magnolia.module.model.Version;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;

import java.util.Arrays;
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
    private Session userRoles;
    private SecuritySupportImpl securitySupportImpl;
    private RoleManager roleManager;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        config = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        templates = MgnlContext.getJCRSession("templates");
        userRoles = MgnlContext.getJCRSession(RepositoryConstants.USER_ROLES);
        securitySupportImpl = new SecuritySupportImpl();
        roleManager = mock(RoleManager.class);
        securitySupportImpl.setRoleManager(roleManager);
        ComponentsTestUtil.setInstance(SecuritySupport.class, securitySupportImpl);

    }

    @Override
    protected String getModuleDescriptorPath() {
        return "/META-INF/magnolia/shop.xml";
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
        setupNode("shops", "/sampleShop");
        setupNode(ShopRepositoryConstants.SHOPPING_CARTS, "/sampleShop/userSpecifiedNode");

        setupNode("templates", "/shop/pages");
        setupConfigNode("/modules/shop/templates/pages/shopHome/templateScript");
        setupConfigNode("/modules/shop/templates/pages/shopHome/areas/promos");
        setupConfigNode("/modules/shop/templates/pages/shopHome/navigation/vertical/startLevel");
        setupConfigNode("/modules/shop/templates/pages/shopHome/navigation/vertical/template");
        setupConfigNode("/modules/shop/templates/pages/shopHome/areas/main/areas/breadcrumb");
        setupConfigNode("/modules/shop/dialogs/createShop/form/tabs/system/fields/defaultPriceCategoryName");
        setupConfigNode("/modules/shop/fieldTypes");
        setupConfigNode("/modules/shop/apps/sampleShopProducts/subApps/browser");
        setupConfigNode("/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/categories/fields/productCategoryUUIDs");
        setupNode(ShopRepositoryConstants.SHOPS, "/sampleShop");

        ComponentsTestUtil.setInstance(SecuritySupport.class, securitySupportImpl);
        RoleManager roleManager = new MgnlRoleManager();
        securitySupportImpl.setRoleManager(roleManager);
        Role shopUserBase = roleManager.createRole("shop-user-base");
        roleManager.addPermission(shopUserBase, DataModule.WORKSPACE, "/", Permission.READ);
        roleManager.addPermission(shopUserBase, "dms", "/", Permission.READ);

        // WHEN
        InstallContext ctx = executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.0"));

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
        assertTrue(config.itemExists("/modules/shop/fieldTypes/priceCategorySelect"));
        assertEquals("info.magnolia.module.shop.app.field.definition.PriceCategoriesSelectFieldDefinition", config.getNode("/modules/shop/dialogs/createShop/form/tabs/system/fields/defaultPriceCategoryName").getProperty("class").getValue().getString());

        //testUpdateTo210AddSampleShopFolders
        assertTrue(MgnlContext.getJCRSession(ShopRepositoryConstants.SHOPPING_CARTS).itemExists("/sampleShop/userSpecifiedNode"));
        assertTrue(MgnlContext.getJCRSession(ShopRepositoryConstants.SHOP_SUPPLIERS).itemExists("/sampleShop"));

        assertThat(config.getNode("/modules/shop/apps/sampleShopProducts/subApps/browser"), hasNode("actions"));
        assertThat(config.getNode("/modules/shop/apps/sampleShopProducts/subApps/"), hasNode("detail"));
        assertThat(config.getNode("/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/categories/fields/productCategoryUUIDs"), hasProperty("sortOptions", false));
        assertThat(userRoles.getNode("/shop-user-base/"), not(hasNode("acl_data")));
        assertThat(userRoles.getNode("/shop-user-base/"), not(hasNode("acl_dms")));

        this.assertNoMessages(ctx);
    }

    @Test
    public void testShoppingCartsHaveFolderNodeTypeWithNamePropertyValueMgnlFolderAfterUpgrade() throws Exception {
        // GIVEN
        this.setupConfigNode("/modules/shop/dialogs/pages/shopSectionProperties/form/tabs/tabShop/fields/currentShop");
        this.setupConfigNode("/modules/shop/apps/shoppingCarts/subApps/browser/contentConnector");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.1.0"));

        // THEN
        assertTrue(config.propertyExists("/modules/shop/apps/shoppingCarts/subApps/browser/contentConnector/nodeTypes/folderNodeType/name"));
        assertEquals(config.getNode("/modules/shop/apps/shoppingCarts/subApps/browser/contentConnector/nodeTypes/folderNodeType").getProperty("name").getString(), NodeTypes.Folder.NAME);
    }

    @Test
    public void testShoppingCartsAreNotChangedAfterUpgradeIfNodeTypesExists() throws Exception {
        // GIVEN
        this.setupConfigNode("/modules/shop/dialogs/pages/shopSectionProperties/form/tabs/tabShop/fields/currentShop");
        this.setupConfigProperty("/modules/shop/apps/shoppingCarts/subApps/browser/contentConnector/nodeTypes/folderNodeType", "name", "testValue");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.1.0"));

        // THEN
        assertEquals(config.getNode("/modules/shop/apps/shoppingCarts/subApps/browser/contentConnector/nodeTypes/folderNodeType").getProperty("name").getString(), "testValue");
    }

    @Test
    public void testPropertiesNotExistAfterUpgradeTo211() throws Exception {
        // GIVEN
        this.setupConfigProperty("/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/product/fields/image", "label", "testValue");
        this.setupConfigProperty("/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/weightUnit/options/kg", "label", "testValue");
        this.setupConfigProperty("/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/weightUnit/options/lbs", "label", "testValue");
        this.setupConfigProperty("/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/sizeUnit/options/cm", "label", "testValue");
        this.setupConfigProperty("/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/sizeUnit/options/in", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/createPriceCategory/form/tabs/main/fields/taxIncluded/options/including", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/createPriceCategory/form/tabs/main/fields/taxIncluded/options/excluding", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/createShippingOption/form/tabs/main/fields/taxIncluded/options/including", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/createShippingOption/form/tabs/main/fields/taxIncluded/options/excluding", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/createSupplier/form/tabs/logo/fields/logoUUID_bak", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/createSupplier/form/tabs/logo/fields/terms", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/pages/shopSectionProperties/actions/commit", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/pages/shopSectionProperties/actions/cancel", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/extras/shopCatCloud/actions/commit", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/extras/shopCatCloud/actions/cancel", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/extras/shopCatCloud/form/tabs/tabMain/fields/catCloudTitle", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/extras/shopExtrasProduct/actions/commit", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/extras/shopExtrasProduct/actions/cancel", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/teasers/shopProductTeaser/actions/commit", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/teasers/shopProductTeaser/actions/cancel", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/teasers/shopProductTeaser/form/tabs/tabTeaser", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/teasers/shopProductTeaser/form/tabs/tabTeaser/fields/productUUID", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shoppingCarts/actions/commit", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shoppingCarts/actions/cancel", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shoppingCarts/form/tabs/mainTab/fields/customerReference", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shoppingCarts/form/tabs/mainTab/fields/priceCategoryReference", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shoppingCarts/form/tabs/shippingAddress/fields/shippingAddressSameAsBilling", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shoppingCarts/form/tabs/orderAddress/fields/orderSameAsBilling", "buttonLabel", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shoppingCarts/form/tabs/orderAddress/fields/orderSameAsBilling", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopProductList/actions/commit", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopProductList/actions/cancel", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopConfirmTerms/actions/commit", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopConfirmTerms/actions/cancel", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/title", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/checkboxLabel", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/checkboxLabel", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsDocumentUUID", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsDocumentUUID", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsPageUUID", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsPageUUID", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/mandatory", "buttonLabel", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/controlName", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopCheckDisableFields/actions/cancel", "label", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/controlName", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/legend", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/mandatory", "buttonLabel", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/controlLabel", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/controlValue", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/extras/shopExtrasProduct/form/tabs/tabTeaser/fields/productUUID", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/extras/shopExtrasProduct/form/tabs/tabTeaserOverwrite/fields/teaserTitle", "description", "testValue");
        this.setupConfigProperty("/modules/shop/dialogs/extras/shopExtrasProduct/form/tabs/tabTeaserOverwrite", "label", "testValue");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.1.0"));

        // THEN
        assertFalse(config.propertyExists("/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/product/fields/image/label"));
        assertFalse(config.propertyExists("/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/weightUnit/options/kg/label"));
        assertFalse(config.propertyExists("/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/weightUnit/options/lbs/label"));
        assertFalse(config.propertyExists("/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/sizeUnit/options/cm/label"));
        assertFalse(config.propertyExists("/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/sizeUnit/options/in/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/createPriceCategory/form/tabs/main/fields/taxIncluded/options/including/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/createPriceCategory/form/tabs/main/fields/taxIncluded/options/excluding/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/createShippingOption/form/tabs/main/fields/taxIncluded/options/including/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/createShippingOption/form/tabs/main/fields/taxIncluded/options/excluding/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/createSupplier/form/tabs/logo/fields/logoUUID_bak/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/createSupplier/form/tabs/logo/fields/terms/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/pages/shopSectionProperties/actions/commit/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/pages/shopSectionProperties/actions/cancel/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/extras/shopCatCloud/actions/commit/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/extras/shopCatCloud/actions/cancel/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/extras/shopCatCloud/form/tabs/tabMain/fields/catCloudTitle/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/extras/shopExtrasProduct/actions/commit/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/extras/shopExtrasProduct/actions/cancel/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/teasers/shopProductTeaser/actions/commit/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/teasers/shopProductTeaser/actions/cancel/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/teasers/shopProductTeaser/form/tabs/tabTeaser/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/teasers/shopProductTeaser/form/tabs/tabTeaser/fields/productUUID/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shoppingCarts/actions/commit/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shoppingCarts/actions/cancel/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shoppingCarts/form/tabs/mainTab/fields/customerReference/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shoppingCarts/form/tabs/mainTab/fields/priceCategoryReference/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shoppingCarts/form/tabs/shippingAddress/fields/shippingAddressSameAsBilling/description")); 
        assertFalse(config.propertyExists("/modules/shop/dialogs/shoppingCarts/form/tabs/orderAddress/fields/orderSameAsBilling/buttonLabel"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shoppingCarts/form/tabs/orderAddress/fields/orderSameAsBilling/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopProductList/actions/commit/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopConfirmTerms/actions/commit/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopConfirmTerms/actions/cancel/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/title/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/checkboxLabel/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/checkboxLabel/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsDocumentUUID/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsDocumentUUID/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsPageUUID/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsPageUUID/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/mandatory/buttonLabel"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/controlName/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopCheckDisableFields/actions/cancel/label"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/controlName/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/legend/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/mandatory/buttonLabel"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/controlLabel/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/controlValue/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/extras/shopExtrasProduct/form/tabs/tabTeaser/fields/productUUID/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/extras/shopExtrasProduct/form/tabs/tabTeaserOverwrite/fields/teaserTitle/description"));
        assertFalse(config.propertyExists("/modules/shop/dialogs/extras/shopExtrasProduct/form/tabs/tabTeaserOverwrite/label"));
    }

    @Test
    public void testPropertiesAreSetAfterUpgradeTo211() throws Exception {
        // GIVEN
        this.setupConfigNode("/modules/shop/dialogs/pages/shopSectionProperties/form/tabs/tabShop/fields/currentShop");
        this.setupConfigNode("/modules/shop/templates/pages/shopFormStep");
        this.setupConfigNode("/modules/shop/templates/pages/shopCheckoutForm");
        this.setupConfigNode("/modules/shop/templates/pages/shopFormStepConfirmOrder");
        this.setupConfigNode("/modules/shop/templates/components/features/shopForm");
        this.setupConfigNode("/modules/shop/templates/components/features/shopProductList");
        this.setupConfigNode("/modules/shop/templates/components/features/shopShoppingCart");
        this.setupConfigNode("/modules/shop/templates/components/features/shopProductDetail");
        this.setupConfigNode("/modules/shop/templates/components/features/shopProductCategory");
        this.setupConfigNode("/modules/shop/templates/components/features/shopConfirmationPage");
        this.setupConfigNode("/modules/shop/templates/components/features/form/shopConfirmTerms");
        this.setupConfigNode("/modules/shop/templates/components/features/shopProductSearchResult");
        this.setupConfigNode("/modules/shop/templates/components/features/shopFormStepConfirmOrder");
        this.setupConfigNode("/modules/shop/templates/components/features/shopProductKeywordResult");
        this.setupConfigProperty("/modules/shop/templates/components/features/form/shopCheckDisableFields", "modelClass", "info.magnolia.module.form.templates.components.FormFieldModel");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.1.0"));

        // THEN
        assertEquals("shop.templates.pages.shopFormStep.title", config.getProperty("/modules/shop/templates/pages/shopFormStep/title").getString());
        assertEquals("shop.templates.pages.shopCheckoutForm.title", config.getProperty("/modules/shop/templates/pages/shopCheckoutForm/title").getString());
        assertEquals("shop.templates.pages.shopFormStepConfirmOrder.title", config.getProperty("/modules/shop/templates/pages/shopFormStepConfirmOrder/title").getString());
        assertEquals("shop.templates.components.features.shopForm.description", config.getProperty("/modules/shop/templates/components/features/shopForm/description").getString());
        assertEquals("shop.templates.components.features.shopForm.title", config.getProperty("/modules/shop/templates/components/features/shopForm/title").getString());
        assertEquals("shop.templates.components.features.shopProductList.description", config.getProperty("/modules/shop/templates/components/features/shopProductList/description").getString());
        assertEquals("shop.templates.components.features.shopProductList.title", config.getProperty("/modules/shop/templates/components/features/shopProductList/title").getString());
        assertEquals("shop.templates.components.features.shopShoppingCart.description", config.getProperty("/modules/shop/templates/components/features/shopShoppingCart/description").getString());
        assertEquals("shop.templates.components.features.shopShoppingCart.title", config.getProperty("/modules/shop/templates/components/features/shopShoppingCart/title").getString());
        assertEquals("shop.templates.components.features.shopProductDetail.description", config.getProperty("/modules/shop/templates/components/features/shopProductDetail/description").getString());
        assertEquals("shop.templates.components.features.shopProductDetail.title", config.getProperty("/modules/shop/templates/components/features/shopProductDetail/title").getString());
        assertEquals("shop.templates.components.features.shopProductCategory.description", config.getProperty("/modules/shop/templates/components/features/shopProductCategory/description").getString());
        assertEquals("shop.templates.components.features.shopConfirmationPage.description", config.getProperty("/modules/shop/templates/components/features/shopConfirmationPage/description").getString());
        assertEquals("shop.templates.components.features.shopConfirmationPage.title", config.getProperty("/modules/shop/templates/components/features/shopConfirmationPage/title").getString());
        assertEquals("shop.templates.components.features.form.shopConfirmTerms.description", config.getProperty("/modules/shop/templates/components/features/form/shopConfirmTerms/description").getString());
        assertEquals("shop.templates.components.features.form.shopConfirmTerms.title", config.getProperty("/modules/shop/templates/components/features/form/shopConfirmTerms/title").getString());
        assertEquals("shop.templates.components.features.shopProductSearchResult.description", config.getProperty("/modules/shop/templates/components/features/shopProductSearchResult/description").getString());
        assertEquals("shop.templates.components.features.shopFormStepConfirmOrder.description", config.getProperty("/modules/shop/templates/components/features/shopFormStepConfirmOrder/description").getString());
        assertEquals("shop.templates.components.features.shopFormStepConfirmOrder.title", config.getProperty("/modules/shop/templates/components/features/shopFormStepConfirmOrder/title").getString());
        assertEquals("shop.templates.components.features.shopProductKeywordResult.description", config.getProperty("/modules/shop/templates/components/features/shopProductKeywordResult/description").getString());
        assertEquals("info.magnolia.module.shop.messages", config.getProperty("/modules/shop/templates/components/features/shopForm/i18nBasename").getString());
        assertThat(config.getNode("/modules/shop/templates/components/features/form/shopCheckDisableFields"), hasProperty("modelClass", "info.magnolia.module.shop.paragraphs.CheckDisableFieldsModel"));

    }

}
