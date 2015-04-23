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

import static info.magnolia.jcr.nodebuilder.Ops.*;

import info.magnolia.cms.security.Permission;
import info.magnolia.dam.app.setup.migration.ChangeWebsiteDmsReferenceToDamMigrationTask;
import info.magnolia.dam.app.setup.migration.CleanContentForDamMigrationTask;
import info.magnolia.dam.app.setup.migration.MoveDataWorkspaceToDamMigrationTask;
import info.magnolia.dam.app.setup.migration.MoveUploadedContentToDamMigrationTask;
import info.magnolia.jcr.nodebuilder.task.ErrorHandling;
import info.magnolia.jcr.nodebuilder.task.NodeBuilderTask;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AddPermissionTask;
import info.magnolia.module.delta.AddRoleToUserTask;
import info.magnolia.module.delta.ArrayDelegateTask;
import info.magnolia.module.delta.BootstrapSingleModuleResource;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.CheckAndModifyPropertyValueTask;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.IsAuthorInstanceDelegateTask;
import info.magnolia.module.delta.IsInstallSamplesTask;
import info.magnolia.module.delta.IsModuleInstalledOrRegistered;
import info.magnolia.module.delta.ModuleDependencyBootstrapTask;
import info.magnolia.module.delta.NewPropertyTask;
import info.magnolia.module.delta.NodeExistsDelegateTask;
import info.magnolia.module.delta.OrderNodeAfterTask;
import info.magnolia.module.delta.OrderNodeBeforeTask;
import info.magnolia.module.delta.PartialBootstrapTask;
import info.magnolia.module.delta.PropertyExistsDelegateTask;
import info.magnolia.module.delta.RemoveNodeTask;
import info.magnolia.module.delta.RemoveNodesTask;
import info.magnolia.module.delta.RemovePermissionTask;
import info.magnolia.module.delta.RemovePropertyTask;
import info.magnolia.module.delta.SetPropertyTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.ValueOfPropertyDelegateTask;
import info.magnolia.module.form.setup.ChangeValidationToMultiValuedPropertyTask;
import info.magnolia.module.inplacetemplating.setup.TemplatesInstallTask;
import info.magnolia.module.resources.setup.InstallResourcesTask;
import info.magnolia.module.shop.ShopRepositoryConstants;
import info.magnolia.module.templatingkit.resources.STKResourceModel;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.ui.contentapp.setup.for5_3.ContentAppMigrationTask;
import info.magnolia.ui.dialog.setup.DialogMigrationTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to handle installation and updates of your module.
 */
public class ShopModuleVersionHandler extends DefaultModuleVersionHandler {

    public ShopModuleVersionHandler() {

        register(DeltaBuilder.checkPrecondition("1.1.2", ""));

        register(DeltaBuilder.update("2.0.0", "")
                .addTask(new MoveDataWorkspaceToDamMigrationTask("Migration task: Migrate DMS content to DAM", "Migrate DMS to DAM", Arrays.asList("/sampleShop"), null, "dms"))
                .addTask(new ChangeWebsiteDmsReferenceToDamMigrationTask("Images to DAM migration", "Migrates image references from DMS to DAM", "website", Arrays.asList("/demo-features/modules/sample-shop")))
                .addTask(new MoveUploadedContentToDamMigrationTask("Migration task: Migrate Uploaded content to DAM repository", "", "website",
                        Arrays.asList("/demo-features/modules/sample-shop"), "/shop_uploaded"))
                .addTask(new CleanContentForDamMigrationTask("Migration task: Clean Content repository", "", "website", Arrays.asList("/demo-features/modules/sample-shop")))
                .addTask(new ChangeWebsiteDmsReferenceToDamMigrationTask("DMS product image references to DAM migration", "Migrates product image references from DMS to DAM", "data", Arrays.asList("/shopProducts")))
                .addTask(new MoveUploadedContentToDamMigrationTask("Uploaded product images to DAM migration", "Migrates uploaded shop product images to DAM", "data", Arrays.asList("/shopProducts"), "/shop_uploaded"))
                .addTask(new RemoveNodesTask("Remove dialogs", "Remove old dialogs from data module.", "config", Arrays.asList(
                        new String[]{"/modules/data/dialogs/shop", "/modules/data/dialogs/shopCountry", "/modules/data/dialogs/shopProduct", "/modules/data/dialogs/shopSupplier",
                                "/modules/data/dialogs/shopCurrency", "/modules/data/dialogs/shopCountries", "/modules/data/dialogs/shopCurrencies", "/modules/data/dialogs/shopTaxCategory",
                                "/modules/data/dialogs/shopTaxCategories", "/modules/data/dialogs/shopPriceCategory", "/modules/data/dialogs/shopPriceCategories", "/modules/data/dialogs/shopProductOption",
                                "/modules/data/dialogs/shopProductOptions", "/modules/data/dialogs/shopShippingOption", "/modules/data/dialogs/shopShippingOptions"}), true))
                .addTask(new RemoveNodesTask("Remove nodes", "Remove unused config, trees and controls.", "config", Arrays.asList(
                        new String[]{"/modules/shop/config", "/modules/shop/controls", "/modules/shop/trees"}), true))
                .addTask(new DialogMigrationTask("Migration task", "Migration of shop dialogs.", "shop"))
                .addTask(new MoveShopProductsToWorkspace("Migrate products", "Migrate products to the new workspace", "/shopProducts", "/", "shopProducts"))
                .addTask(new MoveShopsToWorkspace("Migrate shops", "Migrate shops to the new workspace", "/shops", "/", "shops"))
                .addTask(new MoveShopSuppliersToWorkspace("Migrate suppliers", "Migrate suppliers to the new workspace", "/shopSuppliers", "/", "shopSuppliers"))
                .addTask(new BootstrapSingleResource("Install apps", "Install apps.", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml"))
                .addTask(new BootstrapSingleResource("Install field types", "Install field types.", "/mgnl-bootstrap/shop/config.modules.shop.fieldTypes.xml"))
                .addTask(new ArrayDelegateTask("Install dialogs", "Install new dialogs.",
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/config.modules.shop.dialogs.xml", "/dialogs/createShop"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/config.modules.shop.dialogs.xml", "/dialogs/createCurrency"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/config.modules.shop.dialogs.xml", "/dialogs/createTaxCategory"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/config.modules.shop.dialogs.xml", "/dialogs/createPriceCategory"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/config.modules.shop.dialogs.xml", "/dialogs/createShopProductOptions"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/config.modules.shop.dialogs.xml", "/dialogs/createShopProductOption"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/config.modules.shop.dialogs.xml", "/dialogs/createShippingOption"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/config.modules.shop.dialogs.xml", "/dialogs/createCountry"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/config.modules.shop.dialogs.xml", "/dialogs/createSupplier"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/config.modules.shop.dialogs.xml", "/dialogs/editShop")

                ))
                .addTask(new IsModuleInstalledOrRegistered("Keywords for product Categories",
                        "Adds control to product categories dialog for asigning keywords.", "categorization",
                        new NodeBuilderTask("", "", ErrorHandling.strict, "config",
                                getNode("modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs").then(
                                        addNode("tags", NodeTypes.ContentNode.NAME).then(
                                                addNode("fields", NodeTypes.ContentNode.NAME).then(
                                                        addNode("categories", NodeTypes.ContentNode.NAME).then(
                                                                addNode("field", NodeTypes.ContentNode.NAME).then(
                                                                        addNode("identifierToPathConverter", NodeTypes.ContentNode.NAME).then(
                                                                                addProperty("class", "info.magnolia.ui.form.field.converter.BaseIdentifierToPathConverter")),
                                                                        addProperty("appName", "categories"),
                                                                        addProperty("buttonSelectNewLabel", "field.link.select.new"),
                                                                        addProperty("buttonSelectOtherLabel", "field.link.select.another"),
                                                                        addProperty("class", "info.magnolia.ui.form.field.definition.LinkFieldDefinition"),
                                                                        addProperty("fieldEditable", "true"),
                                                                        addProperty("targetWorkspace", "category")),
                                                                addProperty("buttonSelectAddLabel", "field.link.select.add"),
                                                                addProperty("class", "info.magnolia.ui.form.field.definition.MultiValueFieldDefinition"),
                                                                addProperty("identifier", "true"),
                                                                addProperty("transformerClass", "info.magnolia.ui.form.field.transformer.multi.MultiValueSubChildrenNodeTransformer"))))))))
                .addTask(new BootstrapSingleResource("Register apps", "Register shop apps to the appLauncher.", "/mgnl-bootstrap/shop/config.modules.ui-admincentral.config.appLauncherLayout.groups.shop.xml"))
                .addTask(new CreateAppsForExistingShops("Create apps", "Create shop apps for alredy existing shops."))
        );

        register(DeltaBuilder.update("2.0.1", "")
                .addTask(new ArrayDelegateTask("Rename permission nodes to match fresh install",
                        new RemovePermissionTask("", "shop-user-base", ShopRepositoryConstants.SHOP_PRODUCTS, "/", Permission.READ),
                        new AddPermissionTask("", "shop-user-base", ShopRepositoryConstants.SHOP_PRODUCTS, "/*", Permission.READ, false),
                        new RemovePermissionTask("", "shop-user-base", ShopRepositoryConstants.SHOPPING_CARTS, "/", Permission.READ),
                        new AddPermissionTask("", "shop-user-base", ShopRepositoryConstants.SHOPPING_CARTS, "/*", Permission.READ, false),
                        new RemovePermissionTask("", "shop-user-base", ShopRepositoryConstants.SHOPS, "/", Permission.READ),
                        new AddPermissionTask("", "shop-user-base", ShopRepositoryConstants.SHOPS, "/*", Permission.READ, false)
                ))
                .addTask(new BootstrapSingleModuleResource("config.modules.shop.fieldTypes.availableShops.xml"))
                .addTask(new NodeExistsDelegateTask("", "/modules/shop/dialogs/pages/shopSectionProperties/form/tabs/tabShop/fields/currentShop",
                        new CheckAndModifyPropertyValueTask("/modules/shop/dialogs/pages/shopSectionProperties/form/tabs/tabShop/fields/currentShop", "class", "info.magnolia.ui.form.field.definition.SelectFieldDefinition", "info.magnolia.module.shop.app.field.definition.AvailableShopsSelectFieldDefinition"))
                )
                .addTask(new ArrayDelegateTask("Disable view of checkout form when shopping cart is empty",
                        new BootstrapSingleResource("", "", "/mgnl-bootstrap/shop/paragraphs/config.modules.shop.templates.components.features.shopFormStep.xml"),
                        new NodeExistsDelegateTask("", "/modules/shop/templates/components/features/shopForm",
                                new ArrayDelegateTask("",
                                        new CheckAndModifyPropertyValueTask("", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopForm", "i18nBasename", "info.magnolia.module.form.messages", "info.magnolia.module.shop.messages"),
                                        new CheckAndModifyPropertyValueTask("", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopForm", "templateScript", "/form/components/form.ftl", "/shop/paragraphs/features/shopForm.ftl")
                                )),
                        new NodeExistsDelegateTask("", "/modules/shop/templates/pages/shopFormStep/areas/main/areas/content/autoGeneration/content/singleton",
                                new CheckAndModifyPropertyValueTask("", "", RepositoryConstants.CONFIG, "/modules/shop/templates/pages/shopFormStep/areas/main/areas/content/autoGeneration/content/singleton", "templateId", "form:components/formStep", "shop:components/features/shopFormStep")
                        ),
                        new NodeExistsDelegateTask("", "/demo-features/modules/sample-shop/shopping-cart/checkout-form/billing-address/content/singleton",
                                new CheckAndModifyPropertyValueTask("", "", RepositoryConstants.WEBSITE, "/demo-features/modules/sample-shop/shopping-cart/checkout-form/billing-address/content/singleton", "mgnl:template", "form:components/formStep", "shop:components/features/shopFormStep")
                        ),
                        new TemplatesInstallTask("/shop/.*\\.ftl", true)
                ))
        );

        register(DeltaBuilder.update("2.1.0", "")
                .addTask(new ContentAppMigrationTask("/modules/shop"))
                .addTask(new RemoveNodesTask("Remove nodes from config", "Remove not needed nodes.", "config", Arrays.asList(
                        new String[]{"/modules/shop/templates/pages/shopHome/templateScript", "/modules/shop/templates/pages/shopHome/areas/promos", "/modules/shop/templates/pages/shopHome/navigation/vertical/startLevel", "/modules/shop/templates/pages/shopHome/navigation/vertical/template", "/modules/shop/templates/pages/shopHome/areas/main/areas/breadcrumb"}), true))
                .addTask(new RemoveNodesTask("Remove nodes from templates", "Remove not needed nodes.", "templates", Arrays.asList(new String[]{"/shop/pages"}), true))
                .addTask(new PartialBootstrapTask("Add sectionHeader node with right properties", "", "/mgnl-bootstrap/shop/templates/config.modules.shop.templates.pages.shopHome.xml", "/shopHome/areas/sectionHeader"))
                .addTask(new ArrayDelegateTask("Add promos with enable=false for subpages",
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/templates/config.modules.shop.templates.pages.shopFormStep.xml", "/shopFormStep/areas/promos"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/templates/config.modules.shop.templates.pages.shopCheckoutForm.xml", "/shopCheckoutForm/areas/promos"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/templates/config.modules.shop.templates.pages.shopShoppingCart.xml", "/shopShoppingCart/areas/promos"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/templates/config.modules.shop.templates.pages.shopProductDetail.xml", "/shopProductDetail/areas/promos"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/templates/config.modules.shop.templates.pages.shopProductCategory.xml", "/shopProductCategory/areas/promos"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/templates/config.modules.shop.templates.pages.shopConfirmationPage.xml", "/shopConfirmationPage/areas/promos"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/templates/config.modules.shop.templates.pages.shopProductSearchResult.xml", "/shopProductSearchResult/areas/promos"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/templates/config.modules.shop.templates.pages.shopFormStepConfirmOrder.xml", "/shopFormStepConfirmOrder/areas/promos"),
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/templates/config.modules.shop.templates.pages.shopProductKeywordResult.xml", "/shopProductKeywordResult/areas/promos")))
                .addTask(new BootstrapSingleResource("Add shop css to themes", "", "/mgnl-bootstrap/shop/config.modules.standard-templating-kit.config.themes.pop.cssFiles.shop.xml"))
                .addTask(new ArrayDelegateTask("Add rootFolders 'sampleShop' to sampleSuppliers and sampleShopingCarts if not there yet",
                        new NodeExistsDelegateTask("", "", ShopRepositoryConstants.SHOPPING_CARTS, "/sampleShop", null,
                                new BootstrapSingleResource("Bootstrap shoppingCarts", "", "/mgnl-bootstrap-samples/shop/shoppingCarts.sampleShop.xml")),
                        new NodeExistsDelegateTask("", "", ShopRepositoryConstants.SHOP_SUPPLIERS, "/sampleShop", null,
                                new BootstrapSingleResource("Bootstrap shopSuppliers", "", "/mgnl-bootstrap-samples/shop/shopSuppliers.sampleShop.xml"))))
                .addTask(new ArrayDelegateTask("Add default price category select field.",
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap/shop/config.modules.shop.fieldTypes.xml", "/fieldTypes/priceCategorySelect"),
                        new SetPropertyTask(RepositoryConstants.CONFIG, "/modules/shop/dialogs/createShop/form/tabs/system/fields/defaultPriceCategoryName", "class", "info.magnolia.module.shop.app.field.definition.PriceCategoriesSelectFieldDefinition")
                ))
                .addTask(new ArrayDelegateTask("Migrate old configuration",
                        new PartialBootstrapTask("", "", "/mgnl-bootstrap-samples/shop/config.modules.shop.apps.sampleShopProducts.xml", "/sampleShopProducts/subApps/"),
                        new NodeExistsDelegateTask("", "/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/categories/fields/productCategoryUUIDs",
                                new NewPropertyTask("", "/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/categories/fields/productCategoryUUIDs", "sortOptions", false)),
                        new RemoveNodeTask("", "", RepositoryConstants.USER_ROLES, "/shop-user-base/acl_data"),
                        new RemoveNodeTask("", "", RepositoryConstants.USER_ROLES, "/shop-user-base/acl_dms")
                ))
                .addTask(new ArrayDelegateTask("Extract new templates",
                        new TemplatesInstallTask("/shop/.*\\.ftl", true)
                )));

        register(DeltaBuilder.update("2.2", "")
                .addTask(new NodeExistsDelegateTask("Setting name property of folderNodeType in shoppingCarts", "Setting name property in folderNodeType to value mgnl:folder in shoppingCarts contentConnector.", RepositoryConstants.CONFIG, "/modules/shop/apps/shoppingCarts/subApps/browser/contentConnector/nodeTypes", null,
                        new PartialBootstrapTask("Setting folderNodeType in shoppingCarts", "Setting name property in folderNodeType to mgnl:folder in shoppingCarts contentConnector.", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shoppingCarts/subApps/browser/contentConnector/nodeTypes")))
                .addTask(new ArrayDelegateTask("Remove properties",
                        new PropertyExistsDelegateTask("Remove label of the image field", "", RepositoryConstants.CONFIG, "/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/product/fields/image", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/product/fields/image", "label")),
                        new PropertyExistsDelegateTask("Remove label of the kg field", "", RepositoryConstants.CONFIG, "/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/weightUnit/options/kg", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/weightUnit/options/kg", "label")),
                        new PropertyExistsDelegateTask("Remove label of the lbs field", "", RepositoryConstants.CONFIG, "/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/weightUnit/options/lbs", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/weightUnit/options/lbs", "label")),
                        new PropertyExistsDelegateTask("Remove label of the cm field", "", RepositoryConstants.CONFIG, "/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/sizeUnit/options/cm", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/sizeUnit/options/cm", "label")),
                        new PropertyExistsDelegateTask("Remove label of the in field", "", RepositoryConstants.CONFIG, "/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/sizeUnit/options/in", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/weightSize/fields/sizeUnit/options/in", "label")),
                        new PropertyExistsDelegateTask("Remove label of the including field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/createPriceCategory/form/tabs/main/fields/taxIncluded/options/including", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/createPriceCategory/form/tabs/main/fields/taxIncluded/options/including", "label")),
                        new PropertyExistsDelegateTask("Remove label of the excluding field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/createPriceCategory/form/tabs/main/fields/taxIncluded/options/excluding", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/createPriceCategory/form/tabs/main/fields/taxIncluded/options/excluding", "label")),
                        new PropertyExistsDelegateTask("Remove label of the including field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/createShippingOption/form/tabs/main/fields/taxIncluded/options/including", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/createShippingOption/form/tabs/main/fields/taxIncluded/options/including", "label")),
                        new PropertyExistsDelegateTask("Remove label of the excluding field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/createShippingOption/form/tabs/main/fields/taxIncluded/options/excluding", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/createShippingOption/form/tabs/main/fields/taxIncluded/options/excluding", "label")),
                        new PropertyExistsDelegateTask("Remove description of the logoUUID_bak field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/createSupplier/form/tabs/logo/fields/logoUUID_bak", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/createSupplier/form/tabs/logo/fields/logoUUID_bak", "description")),
                        new PropertyExistsDelegateTask("Remove description of the terms field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/createSupplier/form/tabs/logo/fields/terms", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/createSupplier/form/tabs/logo/fields/terms", "description")),
                        new PropertyExistsDelegateTask("Remove label of the commit button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/pages/shopSectionProperties/actions/commit", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/pages/shopSectionProperties/actions/commit", "label")),
                        new PropertyExistsDelegateTask("Remove label of the cancel button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/pages/shopSectionProperties/actions/cancel", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/pages/shopSectionProperties/actions/cancel", "label")),
                        new PropertyExistsDelegateTask("Remove label of the commit button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopCatCloud/actions/commit", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopCatCloud/actions/commit", "label")),
                        new PropertyExistsDelegateTask("Remove label of the cancel button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopCatCloud/actions/cancel", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopCatCloud/actions/cancel", "label")),
                        new PropertyExistsDelegateTask("Remove description of the catCloudTitle field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopCatCloud/form/tabs/tabMain/fields/catCloudTitle", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopCatCloud/form/tabs/tabMain/fields/catCloudTitle", "description")),
                        new PropertyExistsDelegateTask("Remove label of the commit button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopExtrasProduct/actions/commit", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopExtrasProduct/actions/commit", "label")),
                        new PropertyExistsDelegateTask("Remove label of the cancel button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopExtrasProduct/actions/cancel", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopExtrasProduct/actions/cancel", "label")),
                        new PropertyExistsDelegateTask("Remove label of the commit button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/teasers/shopProductTeaser/actions/commit", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/teasers/shopProductTeaser/actions/commit", "label")),
                        new PropertyExistsDelegateTask("Remove label of the cancel button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/teasers/shopProductTeaser/actions/cancel", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/teasers/shopProductTeaser/actions/cancel", "label")),
                        new PropertyExistsDelegateTask("Remove label of the tabTeaser tab", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/teasers/shopProductTeaser/form/tabs/tabTeaser", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/teasers/shopProductTeaser/form/tabs/tabTeaser", "label")),
                        new PropertyExistsDelegateTask("Remove description of the productUUID field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/teasers/shopProductTeaser/form/tabs/tabTeaser/fields/productUUID", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/teasers/shopProductTeaser/form/tabs/tabTeaser/fields/productUUID", "description")),
                        new PropertyExistsDelegateTask("Remove label of the commit button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/actions/commit", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/actions/commit", "label")),
                        new PropertyExistsDelegateTask("Remove label of the cancel button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/actions/cancel", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/actions/cancel", "label")),
                        new PropertyExistsDelegateTask("Remove description of the customerReference field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/form/tabs/mainTab/fields/customerReference", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/form/tabs/mainTab/fields/customerReference", "description")),
                        new PropertyExistsDelegateTask("Remove description of the priceCategoryReference field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/form/tabs/mainTab/fields/priceCategoryReference", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/form/tabs/mainTab/fields/priceCategoryReference", "description")),
                        new PropertyExistsDelegateTask("Remove description of the shippingAddressSameAsBilling field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/form/tabs/shippingAddress/fields/shippingAddressSameAsBilling", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/form/tabs/shippingAddress/fields/shippingAddressSameAsBilling", "description")),
                        new PropertyExistsDelegateTask("Remove buttonLabel of the shippingAddressSameAsBilling", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/form/tabs/shippingAddress/fields/shippingAddressSameAsBilling", "buttonLabel", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/form/tabs/shippingAddress/fields/shippingAddressSameAsBilling", "buttonLabel")),
                        new PropertyExistsDelegateTask("Remove buttonLabel of the orderSameAsBilling", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/form/tabs/orderAddress/fields/orderSameAsBilling", "buttonLabel", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/form/tabs/orderAddress/fields/orderSameAsBilling", "buttonLabel")),
                        new PropertyExistsDelegateTask("Remove description of the orderSameAsBilling field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/form/tabs/orderAddress/fields/orderSameAsBilling", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shoppingCarts/form/tabs/orderAddress/fields/orderSameAsBilling", "description")),
                        new PropertyExistsDelegateTask("Remove label of the commit button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopProductList/actions/commit", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopProductList/actions/commit", "label")),
                        new PropertyExistsDelegateTask("Remove label of the cancel button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopProductList/actions/cancel", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopProductList/actions/cancel", "label")),
                        new PropertyExistsDelegateTask("Remove label of the commit button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/actions/commit", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/actions/commit", "label")),
                        new PropertyExistsDelegateTask("Remove label of the cancel button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/actions/cancel", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/actions/cancel", "label")),
                        new PropertyExistsDelegateTask("Remove description of the title field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/title", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/title", "description")),
                        new PropertyExistsDelegateTask("Remove description of the checkboxLabel field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/checkboxLabel", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/checkboxLabel", "description")),
                        new PropertyExistsDelegateTask("Remove label of the checkboxLabel field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/checkboxLabel", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/checkboxLabel", "label")),
                        new PropertyExistsDelegateTask("Remove description of the termsDocumentUUID field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsDocumentUUID", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsDocumentUUID", "description")),
                        new PropertyExistsDelegateTask("Remove label of the termsDocumentUUID field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsDocumentUUID", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsDocumentUUID", "label")),
                        new PropertyExistsDelegateTask("Remove description of the termsPageUUID field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsPageUUID", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsPageUUID", "description")),
                        new PropertyExistsDelegateTask("Remove label of the termsDocumentUUID field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsPageUUID", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/termsPageUUID", "label")),
                        new PropertyExistsDelegateTask("Remove buttonLabel of the mandatory field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/mandatory", "buttonLabel", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/mandatory", "buttonLabel")),
                        new PropertyExistsDelegateTask("Remove description of the controlName field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/controlName", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopConfirmTerms/form/tabs/tabMain/fields/controlName", "description")),
                        new PropertyExistsDelegateTask("Remove label of the commit button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/actions/commit", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/actions/commit", "label")),
                        new PropertyExistsDelegateTask("Remove label of the cancel button", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/actions/cancel", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/actions/cancel", "label")),
                        new PropertyExistsDelegateTask("Remove description of the controlName field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/controlName", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/controlName", "description")),
                        new PropertyExistsDelegateTask("Remove description of the legend field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/legend", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/legend", "description")),
                        new PropertyExistsDelegateTask("Remove buttonLabel of the mandatory field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/mandatory", "buttonLabel", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/mandatory", "buttonLabel")),
                        new PropertyExistsDelegateTask("Remove description of the controlLabel field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/controlLabel", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/controlLabel", "description")),
                        new PropertyExistsDelegateTask("Remove description of the controlValue field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/controlValue", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/shopCheckDisableFields/form/tabs/tabMain/fields/controlValue", "description")),
                        new PropertyExistsDelegateTask("Remove description of the productUUID field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopExtrasProduct/form/tabs/tabTeaser/fields/productUUID", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopExtrasProduct/form/tabs/tabTeaser/fields/productUUID", "description")),
                        new PropertyExistsDelegateTask("Remove description of the teaserTitle field", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopExtrasProduct/form/tabs/tabTeaserOverwrite/fields/teaserTitle", "description", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopExtrasProduct/form/tabs/tabTeaserOverwrite/fields/teaserTitle", "description")),
                        new PropertyExistsDelegateTask("Remove label of the tabTeaserOverwrite tab", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopExtrasProduct/form/tabs/tabTeaserOverwrite", "label", new RemovePropertyTask("", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopExtrasProduct/form/tabs/tabTeaserOverwrite", "label")),
                        new PropertyExistsDelegateTask("Remove dialog property of the /modules/shop/templates/pages/shopHome/areas/sectionHeader node", "/modules/shop/templates/pages/shopHome/areas/sectionHeader", "dialog", new RemovePropertyTask("", "/modules/shop/templates/pages/shopHome/areas/sectionHeader", "dialog")),
                        new PropertyExistsDelegateTask("Remove modelClass property of the /modules/shop/templates/pages/shopHome/areas/sectionHeader node", "/modules/shop/templates/pages/shopHome/areas/sectionHeader", "modelClass", new RemovePropertyTask("", "/modules/shop/templates/pages/shopHome/areas/sectionHeader", "modelClass")),
                        new PropertyExistsDelegateTask("Remove templateScript property of the /modules/shop/templates/pages/shopHome/areas/sectionHeader node", "/modules/shop/templates/pages/shopHome/areas/sectionHeader", "templateScript", new RemovePropertyTask("", "/modules/shop/templates/pages/shopHome/areas/sectionHeader", "templateScript")),
                        new PropertyExistsDelegateTask("Remove type property of the /modules/shop/templates/pages/shopHome/areas/sectionHeader node", "/modules/shop/templates/pages/shopHome/areas/sectionHeader", "type", new RemovePropertyTask("", "/modules/shop/templates/pages/shopHome/areas/sectionHeader", "type")),
                        new PropertyExistsDelegateTask("Remove dialog property of the /modules/shop/templates/pages/shopShoppingCart/areas/sectionHeader node", "/modules/shop/templates/pages/shopShoppingCart/areas/sectionHeader", "dialog", new RemovePropertyTask("", "/modules/shop/templates/pages/shopShoppingCart/areas/sectionHeader", "dialog")),
                        new PropertyExistsDelegateTask("Remove type property of the /modules/shop/templates/pages/shopShoppingCart/areas/sectionHeader node", "/modules/shop/templates/pages/shopShoppingCart/areas/sectionHeader", "type", new RemovePropertyTask("", "/modules/shop/templates/pages/shopShoppingCart/areas/sectionHeader", "type")),
                        new PropertyExistsDelegateTask("Remove modelClass property of the /modules/shop/templates/pages/shopShoppingCart/areas/sectionHeader node", "/modules/shop/templates/pages/shopShoppingCart/areas/sectionHeader", "modelClass", new RemovePropertyTask("", "/modules/shop/templates/pages/shopShoppingCart/areas/sectionHeader", "modelClass")),
                        new PropertyExistsDelegateTask("Remove dialog property of the /modules/shop/templates/pages/shopProductDetail/areas/sectionHeader node", "/modules/shop/templates/pages/shopProductDetail/areas/sectionHeader", "dialog", new RemovePropertyTask("", "/modules/shop/templates/pages/shopProductDetail/areas/sectionHeader", "dialog")),
                        new PropertyExistsDelegateTask("Remove modelClass property of the /modules/shop/templates/pages/shopProductDetail/areas/sectionHeader node", "/modules/shop/templates/pages/shopProductDetail/areas/sectionHeader", "modelClass", new RemovePropertyTask("", "/modules/shop/templates/pages/shopProductDetail/areas/sectionHeader", "modelClass")),
                        new PropertyExistsDelegateTask("Remove type property of the /modules/shop/templates/pages/shopProductDetail/areas/sectionHeader node", "/modules/shop/templates/pages/shopProductDetail/areas/sectionHeader", "type", new RemovePropertyTask("", "/modules/shop/templates/pages/shopProductDetail/areas/sectionHeader", "type")),
                        new PropertyExistsDelegateTask("Remove dialog property of the /modules/shop/templates/pages/shopConfirmationPage/areas/sectionHeader node", "/modules/shop/templates/pages/shopConfirmationPage/areas/sectionHeader", "dialog", new RemovePropertyTask("", "/modules/shop/templates/pages/shopConfirmationPage/areas/sectionHeader", "dialog")),
                        new PropertyExistsDelegateTask("Remove modelClass property of the /modules/shop/templates/pages/shopConfirmationPage/areas/sectionHeader node", "/modules/shop/templates/pages/shopConfirmationPage/areas/sectionHeader", "modelClass", new RemovePropertyTask("", "/modules/shop/templates/pages/shopConfirmationPage/areas/sectionHeader", "modelClass")),
                        new PropertyExistsDelegateTask("Remove type property of the /modules/shop/templates/pages/shopConfirmationPage/areas/sectionHeader node", "/modules/shop/templates/pages/shopConfirmationPage/areas/sectionHeader", "type", new RemovePropertyTask("", "/modules/shop/templates/pages/shopConfirmationPage/areas/sectionHeader", "type")),
                        new PropertyExistsDelegateTask("Remove dialog property of the /modules/shop/templates/pages/shopFormStep/areas/sectionHeader node", "/modules/shop/templates/pages/shopFormStep/areas/sectionHeader", "dialog", new RemovePropertyTask("", "/modules/shop/templates/pages/shopFormStep/areas/sectionHeader", "dialog")),
                        new PropertyExistsDelegateTask("Remove modelClass property of the /modules/shop/templates/pages/shopFormStep/areas/sectionHeader node", "/modules/shop/templates/pages/shopFormStep/areas/sectionHeader", "modelClass", new RemovePropertyTask("", "/modules/shop/templates/pages/shopFormStep/areas/sectionHeader", "modelClass")),
                        new PropertyExistsDelegateTask("Remove type property of the /modules/shop/templates/pages/shopFormStep/areas/sectionHeader node", "/modules/shop/templates/pages/shopFormStep/areas/sectionHeader", "type", new RemovePropertyTask("", "/modules/shop/templates/pages/shopFormStep/areas/sectionHeader", "type")),
                        new PropertyExistsDelegateTask("Remove dialog property of the /modules/shop/templates/pages/shopCheckoutForm/areas/sectionHeader node", "/modules/shop/templates/pages/shopCheckoutForm/areas/sectionHeader", "dialog", new RemovePropertyTask("", "/modules/shop/templates/pages/shopCheckoutForm/areas/sectionHeader", "dialog")),
                        new PropertyExistsDelegateTask("Remove modelClass property of the /modules/shop/templates/pages/shopCheckoutForm/areas/sectionHeader node", "/modules/shop/templates/pages/shopCheckoutForm/areas/sectionHeader", "modelClass", new RemovePropertyTask("", "/modules/shop/templates/pages/shopCheckoutForm/areas/sectionHeader", "modelClass")),
                        new PropertyExistsDelegateTask("Remove type property of the /modules/shop/templates/pages/shopCheckoutForm/areas/sectionHeader node", "/modules/shop/templates/pages/shopCheckoutForm/areas/sectionHeader", "type", new RemovePropertyTask("", "/modules/shop/templates/pages/shopCheckoutForm/areas/sectionHeader", "type")),
                        new PropertyExistsDelegateTask("Remove dialog property of the /modules/shop/templates/components/extras/shopExtrasCart node", "/modules/shop/templates/components/extras/shopExtrasCart", "dialog", new RemovePropertyTask("", "/modules/shop/templates/components/extras/shopExtrasCart", "dialog")),
                        new PropertyExistsDelegateTask("Remove dialog property of the /modules/shop/templates/components/extras/shopExtrasProductSearch node", "/modules/shop/templates/components/extras/shopExtrasProductSearch", "dialog", new RemovePropertyTask("", "/modules/shop/templates/components/extras/shopExtrasProductSearch", "dialog")),
                        new PropertyExistsDelegateTask("Remove dialog property of the /modules/shop/templates/components/features/shopConfirmationPage node", "/modules/shop/templates/components/features/shopConfirmationPage", "dialog", new RemovePropertyTask("", "/modules/shop/templates/components/features/shopConfirmationPage", "dialog")),
                        new PropertyExistsDelegateTask("Remove dialogName property of the /modules/shop/apps/shop/subApps/browser/actions/edit node", "/modules/shop/apps/shop/subApps/browser/actions/edit", "dialogName", new RemovePropertyTask("", "/modules/shop/apps/shop/subApps/browser/actions/edit", "dialogName"))))
                .addTask(new ArrayDelegateTask("Set properties",
                        new ChangeValidationToMultiValuedPropertyTask("Change validation fields of form field to multi-valued property", Arrays.asList("form:components/formEdit")),
                        new NodeExistsDelegateTask("Set title property if /modules/shop/templates/pages/shopFormStep node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/pages/shopFormStep", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/pages/shopFormStep", "title", "shop.templates.pages.shopFormStep.title")),
                        new NodeExistsDelegateTask("Set title property if /modules/shop/templates/pages/shopCheckoutForm node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/pages/shopCheckoutForm", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/pages/shopCheckoutForm", "title", "shop.templates.pages.shopCheckoutForm.title")),
                        new NodeExistsDelegateTask("Set title property if /modules/shop/templates/pages/shopFormStepConfirmOrder node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/pages/shopFormStepConfirmOrder", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/pages/shopFormStepConfirmOrder", "title", "shop.templates.pages.shopFormStepConfirmOrder.title")),
                        new NodeExistsDelegateTask("Set description property if /modules/shop/templates/components/features/shopForm node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopForm", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopForm", "description", "shop.templates.components.features.shopForm.description")),
                        new NodeExistsDelegateTask("Set title property if /modules/shop/templates/components/features/shopForm node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopForm", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopForm", "title", "shop.templates.components.features.shopForm.title")),
                        new NodeExistsDelegateTask("Set description property if /modules/shop/templates/components/features/shopProductList node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductList", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductList", "description", "shop.templates.components.features.shopProductList.description")),
                        new NodeExistsDelegateTask("Set title property if /modules/shop/templates/components/features/shopProductList node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductList", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductList", "title", "shop.templates.components.features.shopProductList.title")),
                        new NodeExistsDelegateTask("Set description property if /modules/shop/templates/components/features/shopShoppingCart node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopShoppingCart", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopShoppingCart", "description", "shop.templates.components.features.shopShoppingCart.description")),
                        new NodeExistsDelegateTask("Set title property if /modules/shop/templates/components/features/shopShoppingCart node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopShoppingCart", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopShoppingCart", "title", "shop.templates.components.features.shopShoppingCart.title")),
                        new NodeExistsDelegateTask("Set description property if /modules/shop/templates/components/features/shopProductDetail node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductDetail", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductDetail", "description", "shop.templates.components.features.shopProductDetail.description")),
                        new NodeExistsDelegateTask("Set title property if /modules/shop/templates/components/features/shopProductDetail node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductDetail", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductDetail", "title", "shop.templates.components.features.shopProductDetail.title")),
                        new NodeExistsDelegateTask("Set description property if /modules/shop/templates/components/features/shopProductCategory node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductCategory", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductCategory", "description", "shop.templates.components.features.shopProductCategory.description")),
                        new NodeExistsDelegateTask("Set description property if /modules/shop/templates/components/features/form/shopConfirmTerms node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/form/shopConfirmTerms", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/form/shopConfirmTerms", "description", "shop.templates.components.features.form.shopConfirmTerms.description")),
                        new NodeExistsDelegateTask("Set description property if /modules/shop/templates/components/features/shopConfirmationPage node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopConfirmationPage", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopConfirmationPage", "description", "shop.templates.components.features.shopConfirmationPage.description")),
                        new NodeExistsDelegateTask("Set title property if /modules/shop/templates/components/features/shopConfirmationPage node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopConfirmationPage", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopConfirmationPage", "title", "shop.templates.components.features.shopConfirmationPage.title")),
                        new NodeExistsDelegateTask("Set title property if /modules/shop/templates/components/features/form/shopConfirmTerms node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/form/shopConfirmTerms", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/form/shopConfirmTerms", "title", "shop.templates.components.features.form.shopConfirmTerms.title")),
                        new NodeExistsDelegateTask("Set description property if /modules/shop/templates/components/features/shopProductSearchResult node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductSearchResult", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductSearchResult", "description", "shop.templates.components.features.shopProductSearchResult.description")),
                        new NodeExistsDelegateTask("Set description property if /modules/shop/templates/components/features/shopFormStepConfirmOrder node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopFormStepConfirmOrder", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopFormStepConfirmOrder", "description", "shop.templates.components.features.shopFormStepConfirmOrder.description")),
                        new NodeExistsDelegateTask("Set title property if /modules/shop/templates/components/features/shopFormStepConfirmOrder node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopFormStepConfirmOrder", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopFormStepConfirmOrder", "title", "shop.templates.components.features.shopFormStepConfirmOrder.title")),
                        new NodeExistsDelegateTask("Set description property if /modules/shop/templates/components/features/shopProductKeywordResult node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductKeywordResult", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopProductKeywordResult", "description", "shop.templates.components.features.shopProductKeywordResult.description")),
                        new NodeExistsDelegateTask("Set i18nBasename property if /modules/shop/templates/components/features/shopForm node exists", "", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopForm", new SetPropertyTask("", RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/shopForm", "i18nBasename", "info.magnolia.module.shop.messages")),
                        new NodeExistsDelegateTask("Set modelClass property in /modules/shop/templates/components/features/form/shopCheckDisableFields node", "/modules/shop/templates/components/features/form/shopCheckDisableFields", new SetPropertyTask(RepositoryConstants.CONFIG, "/modules/shop/templates/components/features/form/shopCheckDisableFields", "modelClass", "info.magnolia.module.shop.paragraphs.CheckDisableFieldsModel")),
                        new NodeExistsDelegateTask("Set editable property in /modules/shop/templates/pages/shopHome/areas/sectionHeader node", "/modules/shop/templates/pages/shopHome/areas/sectionHeader", new SetPropertyTask(RepositoryConstants.CONFIG, "/modules/shop/templates/pages/shopHome/areas/sectionHeader", "editable", "true")),
                        new NodeExistsDelegateTask("Set dialog property in /modules/shop/templates/pages/shopHome/areas/main/areas/intro node", "/modules/shop/templates/pages/shopHome/areas/main/areas/intro", new CheckAndModifyPropertyValueTask("/modules/shop/templates/pages/shopHome/areas/main/areas/intro", "dialog", "standard-templating-kit:pages/section/stkSectionHeader", "standard-templating-kit:pages/section/stkSectionIntro")),
                        new NodeExistsDelegateTask("Set dialog property in /modules/shop/templates/pages/shopShoppingCart/areas/main/areas/intro node", "/modules/shop/templates/pages/shopShoppingCart/areas/main/areas/intro", new CheckAndModifyPropertyValueTask("/modules/shop/templates/pages/shopShoppingCart/areas/main/areas/intro", "dialog", "standard-templating-kit:pages/section/stkSectionHeader", "standard-templating-kit:pages/section/stkSectionIntro")),
                        new NodeExistsDelegateTask("Set dialog property in /modules/shop/templates/pages/shopProductDetail/areas/main/areas/intro node", "/modules/shop/templates/pages/shopProductDetail/areas/main/areas/intro", new CheckAndModifyPropertyValueTask("/modules/shop/templates/pages/shopProductDetail/areas/main/areas/intro", "dialog", "standard-templating-kit:pages/section/stkSectionHeader", "standard-templating-kit:pages/section/stkSectionIntro")),
                        new NodeExistsDelegateTask("Set dialog property in /modules/shop/templates/pages/shopProductCategory/areas/main/areas/intro node", "/modules/shop/templates/pages/shopProductCategory/areas/main/areas/intro", new CheckAndModifyPropertyValueTask("/modules/shop/templates/pages/shopProductCategory/areas/main/areas/intro", "dialog", "standard-templating-kit:pages/section/stkSectionHeader", "standard-templating-kit:pages/section/stkSectionIntro")),
                        new NodeExistsDelegateTask("Set dialog property in /modules/shop/templates/pages/shopConfirmationPage/areas/main/areas/intro node", "/modules/shop/templates/pages/shopConfirmationPage/areas/main/areas/intro", new CheckAndModifyPropertyValueTask("/modules/shop/templates/pages/shopConfirmationPage/areas/main/areas/intro", "dialog", "standard-templating-kit:pages/section/stkSectionHeader", "standard-templating-kit:pages/section/stkSectionIntro")),
                        new NodeExistsDelegateTask("Set dialog property in /modules/shop/templates/pages/shopProductSearchResult/areas/main/areas/intro node", "/modules/shop/templates/pages/shopProductSearchResult/areas/main/areas/intro", new CheckAndModifyPropertyValueTask("/modules/shop/templates/pages/shopProductSearchResult/areas/main/areas/intro", "dialog", "standard-templating-kit:pages/section/stkSectionHeader", "standard-templating-kit:pages/section/stkSectionIntro")),
                        new NodeExistsDelegateTask("Set dialog property in /modules/shop/templates/pages/shopProductKeywordResult/areas/main/areas/intro node", "/modules/shop/templates/pages/shopProductKeywordResult/areas/main/areas/intro", new CheckAndModifyPropertyValueTask("/modules/shop/templates/pages/shopProductKeywordResult/areas/main/areas/intro", "dialog", "standard-templating-kit:pages/section/stkSectionHeader", "standard-templating-kit:pages/section/stkSectionIntro"))))
                .addTask(new ArrayDelegateTask("Remove nodes",
                        new NodeExistsDelegateTask("Remove unused /modules/shop/templates/pages/shopHome/navigation/metaNavigation node", "/modules/shop/templates/pages/shopHome/navigation/metaNavigation", new RemoveNodeTask("Remove unused metaNavigation node", "/modules/shop/templates/pages/shopHome/navigation/metaNavigation")),
                        new NodeExistsDelegateTask("Remove unused /modules/shop/templates/pages/shopShoppingCart/navigation/metaNavigation node", "/modules/shop/templates/pages/shopShoppingCart/navigation/metaNavigation", new RemoveNodeTask("Remove unused metaNavigation node", "/modules/shop/templates/pages/shopShoppingCart/navigation/metaNavigation")),
                        new NodeExistsDelegateTask("Remove unused /modules/shop/templates/pages/shopProductDetail/navigation/metaNavigation node", "/modules/shop/templates/pages/shopProductDetail/navigation/metaNavigation", new RemoveNodeTask("Remove unused metaNavigation node", "/modules/shop/templates/pages/shopProductDetail/navigation/metaNavigation")),
                        new NodeExistsDelegateTask("Remove unused /modules/shop/templates/pages/shopProductCategory/navigation/metaNavigation node", "/modules/shop/templates/pages/shopProductCategory/navigation/metaNavigation", new RemoveNodeTask("Remove unused metaNavigation node", "/modules/shop/templates/pages/shopProductCategory/navigation/metaNavigation")),
                        new NodeExistsDelegateTask("Remove unused /modules/shop/templates/pages/shopConfirmationPage/navigation/metaNavigation node", "/modules/shop/templates/pages/shopConfirmationPage/navigation/metaNavigation", new RemoveNodeTask("Remove unused metaNavigation node", "/modules/shop/templates/pages/shopConfirmationPage/navigation/metaNavigation")),
                        new NodeExistsDelegateTask("Remove unused /modules/shop/templates/pages/shopProductSearchResult/navigation/metaNavigation node", "/modules/shop/templates/pages/shopProductSearchResult/navigation/metaNavigation", new RemoveNodeTask("Remove unused metaNavigation node", "/modules/shop/templates/pages/shopProductSearchResult/navigation/metaNavigation")),
                        new NodeExistsDelegateTask("Remove unused /modules/shop/templates/pages/shopProductKeywordResult/navigation/metaNavigation node", "/modules/shop/templates/pages/shopProductKeywordResult/navigation/metaNavigation", new RemoveNodeTask("Remove unused metaNavigation node", "/modules/shop/templates/pages/shopProductKeywordResult/navigation/metaNavigation")),
                        new NodeExistsDelegateTask("Remove unused /modules/shop/dialogs/shopEmpty node", "/modules/shop/dialogs/shopEmpty", new RemoveNodeTask("Remove unused shopEmpty node", "/modules/shop/dialogs/shopEmpty"))))
                .addTask(new NodeExistsDelegateTask("Configure deletedCountries node in shop/apps/shop/subApps/browser/actionbar/sections node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/deletedCountries", null,
                        new PartialBootstrapTask("Configure deletedCountries node in shop/apps/shop/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shop/subApps/browser/actionbar/sections/deletedCountries")))
                .addTask(new NodeExistsDelegateTask("Configure deletedCountry node in shop/apps/shop/subApps/browser/actionbar/sections node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/deletedCountry", null,
                        new PartialBootstrapTask("Configure deletedCountry node in shop/apps/shop/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shop/subApps/browser/actionbar/sections/deletedCountry")))
                .addTask(new NodeExistsDelegateTask("Configure deletedCurrencies node in shop/apps/shop/subApps/browser/actionbar/sections node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/deletedCurrencies", null,
                        new PartialBootstrapTask("Configure deletedCurrencies node in shop/apps/shop/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shop/subApps/browser/actionbar/sections/deletedCurrencies")))
                .addTask(new NodeExistsDelegateTask("Configure deletedCurrency node in shop/apps/shop/subApps/browser/actionbar/sections node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/deletedCurrency", null,
                        new PartialBootstrapTask("Configure deletedCurrency node in shop/apps/shop/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shop/subApps/browser/actionbar/sections/deletedCurrency")))
                .addTask(new NodeExistsDelegateTask("Configure deletedPriceCategories node in shop/apps/shop/subApps/browser/actionbar/sections node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/deletedPriceCategories", null,
                        new PartialBootstrapTask("Configure deletedPriceCategories node in shop/apps/shop/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shop/subApps/browser/actionbar/sections/deletedPriceCategories")))
                .addTask(new NodeExistsDelegateTask("Configure deletedPriceCategory node in shop/apps/shop/subApps/browser/actionbar/sections node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/deletedPriceCategory", null,
                        new PartialBootstrapTask("Configure deletedPriceCategory node in shop/apps/shop/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shop/subApps/browser/actionbar/sections/deletedPriceCategory")))
                .addTask(new NodeExistsDelegateTask("Configure deletedShippingOption node in shop/apps/shop/subApps/browser/actionbar/sections node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/deletedShippingOption", null,
                        new PartialBootstrapTask("Configure deletedShippingOption node in shop/apps/shop/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shop/subApps/browser/actionbar/sections/deletedShippingOption")))
                .addTask(new NodeExistsDelegateTask("Configure deletedShippingOptions node in shop/apps/shop/subApps/browser/actionbar/sections node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/deletedShippingOptions", null,
                        new PartialBootstrapTask("Configure deletedShippingOptions node in shop/apps/shop/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shop/subApps/browser/actionbar/sections/deletedShippingOptions")))
                .addTask(new NodeExistsDelegateTask("Configure deletedShop node in shop/apps/shop/subApps/browser/actionbar/sections node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/deletedShop", null,
                        new PartialBootstrapTask("Configure deletedShop node in shop/apps/shop/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shop/subApps/browser/actionbar/sections/deletedShop")))
                .addTask(new NodeExistsDelegateTask("Configure deletedTaxCategories node in shop/apps/shop/subApps/browser/actionbar/sections node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/deletedTaxCategories", null,
                        new PartialBootstrapTask("Configure deletedTaxCategories node in shop/apps/shop/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shop/subApps/browser/actionbar/sections/deletedTaxCategories")))
                .addTask(new NodeExistsDelegateTask("Configure deletedTaxCategory node in shop/apps/shop/subApps/browser/actionbar/sections node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/deletedTaxCategory", null,
                        new PartialBootstrapTask("Configure deletedTaxCategory node in shop/apps/shop/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shop/subApps/browser/actionbar/sections/deletedTaxCategory")))
                .addTask(new NodeExistsDelegateTask("Configure addFolder action in shop/apps/shoppingCarts/subApps/browser/actions node", "/modules/shop/apps/shoppingCarts/subApps/browser/actions/addFolder", null,
                        new PartialBootstrapTask("Configure addFolder action in shop/apps/shoppingCarts/subApps/browser/actions node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shoppingCarts/subApps/browser/actions/addFolder")))
                .addTask(new NodeExistsDelegateTask("Configure deletedFolder node in shop/apps/shopProducts/subApps/browser/actionbar/sections node", "/modules/shop/apps/shopProducts/subApps/browser/actionbar/sections/deletedFolder", null,
                        new PartialBootstrapTask("Configure deletedFolder node in shop/apps/shopProducts/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actionbar/sections/deletedFolder")))
                .addTask(new NodeExistsDelegateTask("Configure deletedShopProduct node in shop/apps/shopProducts/subApps/browser/actionbar/sections node", "/modules/shop/apps/shopProducts/subApps/browser/actionbar/sections/deletedShopProduct", null,
                        new PartialBootstrapTask("Configure deletedShopProduct node in shop/apps/shopProducts/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actionbar/sections/deletedShopProduct")))
                .addTask(new NodeExistsDelegateTask("Configure deletedShopProductOption node in shop/apps/shopProducts/subApps/browser/actionbar/sections node", "/modules/shop/apps/shopProducts/subApps/browser/actionbar/sections/deletedShopProductOption", null,
                        new PartialBootstrapTask("Configure deletedShopProductOption node in shop/apps/shopProducts/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actionbar/sections/deletedShopProductOption")))
                .addTask(new NodeExistsDelegateTask("Configure deletedShopProductOptions node in shop/apps/shopProducts/subApps/browser/actionbar/sections node", "/modules/shop/apps/shopProducts/subApps/browser/actionbar/sections/deletedShopProductOptions", null,
                        new PartialBootstrapTask("Configure deletedShopProductOptions node in shop/apps/shopProducts/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actionbar/sections/deletedShopProductOptions")))
                .addTask(new NodeExistsDelegateTask("Configure rules node in shop/apps/shopProducts/subApps/browser/actionbar/sections/folder/availability node", "/modules/shop/apps/shopProducts/subApps/browser/actionbar/sections/folder/availability/rules", null,
                        new PartialBootstrapTask("Configure rules node in shop/apps/shopProducts/subApps/browser/actionbar/sections/folder/availability node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actionbar/sections/folder/availability/rules")))
                .addTask(new NodeExistsDelegateTask("Configure previousVersionActions node in shop/apps/shopProducts/subApps/browser/actionbar/sections/folder/groups node", "/modules/shop/apps/shopProducts/subApps/browser/actionbar/sections/folder/groups/previousVersionActions", null,
                        new PartialBootstrapTask("Configure previousVersionActions node in shop/apps/shopProducts/subApps/browser/actionbar/sections/folder/groups node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actionbar/sections/folder/groups/previousVersionActions")))
                .addTask(new NodeExistsDelegateTask("Configure rules node in shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProduct/availability node", "/modules/shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProduct/availability/rules", null,
                        new PartialBootstrapTask("Configure rules node in shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProduct/availability node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actionbar/sections/shopProduct/availability/rules")))
                .addTask(new NodeExistsDelegateTask("Configure previousVersionActions node in shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProduct/groups node", "/modules/shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProduct/groups/previousVersionActions", null,
                        new PartialBootstrapTask("Configure previousVersionActions node in shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProduct/groups node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actionbar/sections/shopProduct/groups/previousVersionActions")))
                .addTask(new NodeExistsDelegateTask("Configure rules node in shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOption/availability node", "/modules/shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOption/availability/rules", null,
                        new PartialBootstrapTask("Configure rules node in shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOption/availability node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOption/availability/rules")))
                .addTask(new NodeExistsDelegateTask("Configure previousVersionActions node in shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOption/groups node", "/modules/shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOption/groups/previousVersionActions", null,
                        new PartialBootstrapTask("Configure previousVersionActions node in shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOption/groups node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOption/groups/previousVersionActions")))
                .addTask(new NodeExistsDelegateTask("Configure activate action in /modules/shop/apps/shopProducts/subApps/browser/actions node", "/modules/shop/apps/shopProducts/subApps/browser/actions/activate", new ValueOfPropertyDelegateTask("", "/modules/shop/apps/shopProducts/subApps/browser/actions/activate", "class", "info.magnolia.ui.framework.action.ActivationActionDefinition", false,
                        new PartialBootstrapTask("", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actions/activate"))))
                .addTask(new NodeExistsDelegateTask("Configure deactivate action in /modules/shop/apps/shopProducts/subApps/browser/actions node", "/modules/shop/apps/shopProducts/subApps/browser/actions/deactivate", new ValueOfPropertyDelegateTask("", "/modules/shop/apps/shopProducts/subApps/browser/actions/deactivate", "class", "info.magnolia.ui.framework.action.DeactivationActionDefinition", false,
                        new PartialBootstrapTask("", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actions/deactivate"))))
                .addTask(new NodeExistsDelegateTask("Configure activateDeletion node in shop/apps/shopProducts/subApps/browser/actions node", "/modules/shop/apps/shopProducts/subApps/browser/actions/activateDeletion", null,
                        new PartialBootstrapTask("Configure activateDeletion node in shop/apps/shopProducts/subApps/browser/actions node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actions/activateDeletion")))
                .addTask(new NodeExistsDelegateTask("Configure restorePreviousVersion action in shop/apps/shopProducts/subApps/browser/actions node", "/modules/shop/apps/shopProducts/subApps/browser/actions/restorePreviousVersion", null,
                        new PartialBootstrapTask("Configure restorePreviousVersion action in shop/apps/shopProducts/subApps/browser/actions node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actions/restorePreviousVersion")))
                .addTask(new NodeExistsDelegateTask("Configure rules node in shop/apps/shopProducts/subApps/browser/actions/editShopProductOption/availability node", "/modules/shop/apps/shopProducts/subApps/browser/actions/editShopProductOption/availability/rules", null,
                        new PartialBootstrapTask("Configure rules node in shop/apps/shopProducts/subApps/browser/actions/editShopProductOption/availability node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actions/editShopProductOption/availability/rules")))
                .addTask(new NodeExistsDelegateTask("Configure rules node in shop/apps/shopProducts/subApps/browser/actions/editShopProductOptions/availability node", "/modules/shop/apps/shopProducts/subApps/browser/actions/editShopProductOptions/availability/rules", null,
                        new PartialBootstrapTask("Configure rules node in shop/apps/shopProducts/subApps/browser/actions/editShopProductOptions/availability node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actions/editShopProductOptions/availability/rules")))
                .addTask(new NodeExistsDelegateTask("Configure deletedFolder node in shop/apps/shopSuppliers/subApps/browser/actionbar/sections node", "/modules/shop/apps/shopSuppliers/subApps/browser/actionbar/sections/deletedFolder", null,
                        new PartialBootstrapTask("Configure deletedFolder node in shop/apps/shopSuppliers/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopSuppliers/subApps/browser/actionbar/sections/deletedFolder")))
                .addTask(new NodeExistsDelegateTask("Configure deletedSupplier node in shop/apps/shopSuppliers/subApps/browser/actionbar/sections node", "/modules/shop/apps/shopSuppliers/subApps/browser/actionbar/sections/deletedSupplier", null,
                        new PartialBootstrapTask("Configure deletedSupplier node in shop/apps/shopSuppliers/subApps/browser/actionbar/sections node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopSuppliers/subApps/browser/actionbar/sections/deletedSupplier")))
                .addTask(new NodeExistsDelegateTask("Configure rules node in shop/apps/shopSuppliers/subApps/browser/actionbar/sections/folder/availability node", "/modules/shop/apps/shopSuppliers/subApps/browser/actionbar/sections/folder/availability/rules", null,
                        new PartialBootstrapTask("Configure rules node in shop/apps/shopSuppliers/subApps/browser/actionbar/sections/folder/availability node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopSuppliers/subApps/browser/actionbar/sections/folder/availability/rules")))
                .addTask(new NodeExistsDelegateTask("Configure previousVersionActions node in shop/apps/shopSuppliers/subApps/browser/actionbar/sections/folder/groups node", "/modules/shop/apps/shopSuppliers/subApps/browser/actionbar/sections/folder/groups/previousVersionActions", null,
                        new PartialBootstrapTask("Configure previousVersionActions node in shop/apps/shopSuppliers/subApps/browser/actionbar/sections/folder/groups node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopSuppliers/subApps/browser/actionbar/sections/folder/groups/previousVersionActions")))
                .addTask(new NodeExistsDelegateTask("Configure rules node in shop/apps/shopSuppliers/subApps/browser/actionbar/sections/supplier/availability node", "/modules/shop/apps/shopSuppliers/subApps/browser/actionbar/sections/supplier/availability/rules", null,
                        new PartialBootstrapTask("Configure rules node in shop/apps/shopSuppliers/subApps/browser/actionbar/sections/supplier/availability node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopSuppliers/subApps/browser/actionbar/sections/supplier/availability")))
                .addTask(new NodeExistsDelegateTask("Configure previousVersionActions node in shop/apps/shopSuppliers/subApps/browser/actionbar/sections/supplier/groups node", "/modules/shop/apps/shopSuppliers/subApps/browser/actionbar/sections/supplier/groups/previousVersionActions", null,
                        new PartialBootstrapTask("Configure previousVersionActions node in shop/apps/shopSuppliers/subApps/browser/actionbar/sections/supplier/groups node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopSuppliers/subApps/browser/actionbar/sections/supplier/groups/previousVersionActions")))
                .addTask(new NodeExistsDelegateTask("Configure activate action in /modules/shop/apps/shopSuppliers/subApps/browser/actions node", "/modules/shop/apps/shopSuppliers/subApps/browser/actions/activate", new ValueOfPropertyDelegateTask("", "/modules/shop/apps/shopSuppliers/subApps/browser/actions/activate", "class", "info.magnolia.ui.framework.action.ActivationActionDefinition", false,
                        new PartialBootstrapTask("", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopSuppliers/subApps/browser/actions/activate"))))
                .addTask(new NodeExistsDelegateTask("Configure deactivate action in /modules/shop/apps/shopSuppliers/subApps/browser/actions node", "/modules/shop/apps/shopSuppliers/subApps/browser/actions/deactivate", new ValueOfPropertyDelegateTask("", "/modules/shop/apps/shopSuppliers/subApps/browser/actions/deactivate", "class", "info.magnolia.ui.framework.action.DeactivationActionDefinition", false,
                        new PartialBootstrapTask("", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopSuppliers/subApps/browser/actions/deactivate"))))
                .addTask(new NodeExistsDelegateTask("Configure activateDeletion action in shop/apps/shopSuppliers/subApps/browser/actions node", "/modules/shop/apps/shopSuppliers/subApps/browser/actions/activateDeletion", null,
                        new PartialBootstrapTask("Configure activateDeletion action in shop/apps/shopSuppliers/subApps/browser/actions node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopSuppliers/subApps/browser/actions/activateDeletion")))
                .addTask(new NodeExistsDelegateTask("Configure restorePreviousVersion action in shop/apps/shopSuppliers/subApps/browser/actions node", "/modules/shop/apps/shopSuppliers/subApps/browser/actions/restorePreviousVersion", null,
                        new PartialBootstrapTask("Configure restorePreviousVersion action in shop/apps/shopSuppliers/subApps/browser/actions node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopSuppliers/subApps/browser/actions/restorePreviousVersion")))
                .addTask(new NodeExistsDelegateTask("Configure label property in /modules/shop/apps/shopSuppliers/subApps/browser/actionbar/sections/folder node", "/modules/shop/apps/shopSuppliers/subApps/browser/actionbar/sections/folder", new PropertyExistsDelegateTask("", "/modules/shop/apps/shopSuppliers/subApps/browser/actionbar/sections/folder", "label", null,
                        new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shopSuppliers/subApps/browser/actionbar/sections/folder", addProperty("label", "shopSuppliers.browser.actionbar.sections.folder.label")))))
                .addTask(new NodeExistsDelegateTask("Configure rules node in shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOptions/availability node", "/modules/shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOptions/availability/rules", null,
                        new PartialBootstrapTask("Configure rules node in shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOptions/availability node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOptions/availability/rules")))
                .addTask(new NodeExistsDelegateTask("Configure previousVersionActions node in shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOptions/groups node", "/modules/shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOptions/groups/previousVersionActions", null,
                        new PartialBootstrapTask("Configure previousVersionActions node in shop/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOptions/groups node", "/mgnl-bootstrap/shop/config.modules.shop.apps.xml", "/apps/shopProducts/subApps/browser/actionbar/sections/shopProductOptions/groups/previousVersionActions")))
                .addTask(new NodeExistsDelegateTask("Configure implementationClass property in /modules/shop/apps/shop/subApps/browser/actionbar/sections/country/availability/rules/IsNotDeletedRule node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/country/availability", new PropertyExistsDelegateTask("", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/country/availability/rules/IsNotDeletedRule", "implementationClass", null,
                        new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/country/availability", getOrAddNode("rules", NodeTypes.ContentNode.NAME).then(getOrAddNode("IsNotDeletedRule", NodeTypes.ContentNode.NAME).then(addProperty("implementationClass", "info.magnolia.ui.framework.availability.IsNotDeletedRule")))))))
                .addTask(new NodeExistsDelegateTask("Configure implementationClass property in /modules/shop/apps/shop/subApps/browser/actionbar/sections/currencies/availability/rules/IsNotDeletedRule node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/currencies/availability", new PropertyExistsDelegateTask("", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/currencies/availability/rules/IsNotDeletedRule", "implementationClass", null,
                        new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/currencies/availability", getOrAddNode("rules", NodeTypes.ContentNode.NAME).then(getOrAddNode("IsNotDeletedRule", NodeTypes.ContentNode.NAME).then(addProperty("implementationClass", "info.magnolia.ui.framework.availability.IsNotDeletedRule")))))))
                .addTask(new NodeExistsDelegateTask("Configure implementationClass property in /modules/shop/apps/shop/subApps/browser/actionbar/sections/priceCategories/availability/rules/IsNotDeletedRule node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/priceCategories/availability", new PropertyExistsDelegateTask("", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/priceCategories/availability/rules/IsNotDeletedRule", "implementationClass", null,
                        new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/priceCategories/availability", getOrAddNode("rules", NodeTypes.ContentNode.NAME).then(getOrAddNode("IsNotDeletedRule", NodeTypes.ContentNode.NAME).then(addProperty("implementationClass", "info.magnolia.ui.framework.availability.IsNotDeletedRule")))))))
                .addTask(new NodeExistsDelegateTask("Configure implementationClass property in /modules/shop/apps/shop/subApps/browser/actionbar/sections/priceCategory/availability/rules/IsNotDeletedRule node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/priceCategory/availability", new PropertyExistsDelegateTask("", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/priceCategory/availability/rules/IsNotDeletedRule", "implementationClass", null,
                        new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/priceCategory/availability", getOrAddNode("rules", NodeTypes.ContentNode.NAME).then(getOrAddNode("IsNotDeletedRule", NodeTypes.ContentNode.NAME).then(addProperty("implementationClass", "info.magnolia.ui.framework.availability.IsNotDeletedRule")))))))
                .addTask(new NodeExistsDelegateTask("Configure implementationClass property in /modules/shop/apps/shop/subApps/browser/actionbar/sections/shippingOption/availability/rules/IsNotDeletedRule node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/shippingOption/availability", new PropertyExistsDelegateTask("", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/shippingOption/availability/rules/IsNotDeletedRule", "implementationClass", null,
                        new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/shippingOption/availability", getOrAddNode("rules", NodeTypes.ContentNode.NAME).then(getOrAddNode("IsNotDeletedRule", NodeTypes.ContentNode.NAME).then(addProperty("implementationClass", "info.magnolia.ui.framework.availability.IsNotDeletedRule")))))))
                .addTask(new NodeExistsDelegateTask("Configure implementationClass property in /modules/shop/apps/shop/subApps/browser/actionbar/sections/shop/availability/rules/IsNotDeletedRule node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/shop/availability", new PropertyExistsDelegateTask("", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/shop/availability/rules/IsNotDeletedRule", "implementationClass", null,
                        new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/shop/availability", getOrAddNode("rules", NodeTypes.ContentNode.NAME).then(getOrAddNode("IsNotDeletedRule", NodeTypes.ContentNode.NAME).then(addProperty("implementationClass", "info.magnolia.ui.framework.availability.IsNotDeletedRule")))))))
                .addTask(new NodeExistsDelegateTask("Configure implementationClass property in /modules/shop/apps/shop/subApps/browser/actionbar/sections/taxCategories/availability/rules/IsNotDeletedRule node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/taxCategories/availability", new PropertyExistsDelegateTask("", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/taxCategories/availability/rules/IsNotDeletedRule", "implementationClass", null,
                        new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/taxCategories/availability", getOrAddNode("rules", NodeTypes.ContentNode.NAME).then(getOrAddNode("IsNotDeletedRule", NodeTypes.ContentNode.NAME).then(addProperty("implementationClass", "info.magnolia.ui.framework.availability.IsNotDeletedRule")))))))
                .addTask(new NodeExistsDelegateTask("Configure implementationClass property in /modules/shop/apps/shop/subApps/browser/actionbar/sections/taxCategory/availability/rules/IsNotDeletedRule node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/taxCategory/availability", new PropertyExistsDelegateTask("", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/taxCategory/availability/rules/IsNotDeletedRule", "implementationClass", null,
                        new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/taxCategory/availability", getOrAddNode("rules", NodeTypes.ContentNode.NAME).then(getOrAddNode("IsNotDeletedRule", NodeTypes.ContentNode.NAME).then(addProperty("implementationClass", "info.magnolia.ui.framework.availability.IsNotDeletedRule")))))))
                .addTask(new NodeExistsDelegateTask("Configure previousVersionActions/items/restorePreviousVersion node in /shop/apps/shop/subApps/browser/actionbar/sections/currencies/groups node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/currencies/groups", new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/currencies/groups",
                        getOrAddNode("previousVersionActions", NodeTypes.ContentNode.NAME).then(getOrAddNode("items", NodeTypes.ContentNode.NAME).then(getOrAddNode("restorePreviousVersion", NodeTypes.ContentNode.NAME))))))
                .addTask(new NodeExistsDelegateTask("Configure implementationClass property in /modules/shop/apps/shop/subApps/browser/actionbar/sections/currency/availability/rules/IsNotDeletedRule node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/currency/availability", new PropertyExistsDelegateTask("", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/currency/availability/rules/IsNotDeletedRule", "implementationClass", null,
                        new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/currency/availability", getOrAddNode("rules", NodeTypes.ContentNode.NAME).then(getOrAddNode("IsNotDeletedRule", NodeTypes.ContentNode.NAME).then(addProperty("implementationClass", "info.magnolia.ui.framework.availability.IsNotDeletedRule")))))))
                .addTask(new NodeExistsDelegateTask("Configure previousVersionActions/items/restorePreviousVersion node in /shop/apps/shop/subApps/browser/actionbar/sections/currency/groups node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/currency/groups", new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/currency/groups",
                        getOrAddNode("previousVersionActions", NodeTypes.ContentNode.NAME).then(getOrAddNode("items", NodeTypes.ContentNode.NAME).then(getOrAddNode("restorePreviousVersion", NodeTypes.ContentNode.NAME))))))
                .addTask(new NodeExistsDelegateTask("Configure previousVersionActions/items/restorePreviousVersion node in /shop/apps/shop/subApps/browser/actionbar/sections/shop/groups node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/shop/groups", new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/shop/groups",
                        getOrAddNode("previousVersionActions", NodeTypes.ContentNode.NAME).then(getOrAddNode("items", NodeTypes.ContentNode.NAME).then(getOrAddNode("restorePreviousVersion", NodeTypes.ContentNode.NAME))))))
                .addTask(new NodeExistsDelegateTask("Configure previousVersionActions/items/restorePreviousVersion node in /shop/apps/shop/subApps/browser/actionbar/sections/taxCategories/groups node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/taxCategories/groups", new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/taxCategories/groups",
                        getOrAddNode("previousVersionActions", NodeTypes.ContentNode.NAME).then(getOrAddNode("items", NodeTypes.ContentNode.NAME).then(getOrAddNode("restorePreviousVersion", NodeTypes.ContentNode.NAME))))))
                .addTask(new NodeExistsDelegateTask("Configure previousVersionActions/items/restorePreviousVersion node in /shop/apps/shop/subApps/browser/actionbar/sections/taxCategory/groups node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/taxCategory/groups", new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/taxCategory/groups",
                        getOrAddNode("previousVersionActions", NodeTypes.ContentNode.NAME).then(getOrAddNode("items", NodeTypes.ContentNode.NAME).then(getOrAddNode("restorePreviousVersion", NodeTypes.ContentNode.NAME))))))
                .addTask(new NodeExistsDelegateTask("Configure previousVersionActions/items/restorePreviousVersion node in /shop/apps/shop/subApps/browser/actionbar/sections/priceCategories/groups node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/priceCategories/groups", new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/priceCategories/groups",
                        getOrAddNode("previousVersionActions", NodeTypes.ContentNode.NAME).then(getOrAddNode("items", NodeTypes.ContentNode.NAME).then(getOrAddNode("restorePreviousVersion", NodeTypes.ContentNode.NAME))))))
                .addTask(new NodeExistsDelegateTask("Configure previousVersionActions/items/restorePreviousVersion nodes in /shop/apps/shop/subApps/browser/actionbar/sections/priceCategory/groups node", "/modules/shop/apps/shop/subApps/browser/actionbar/sections/priceCategory/groups", new NodeBuilderTask("", "", ErrorHandling.strict, RepositoryConstants.CONFIG, "/modules/shop/apps/shop/subApps/browser/actionbar/sections/priceCategory/groups",
                        getOrAddNode("previousVersionActions", NodeTypes.ContentNode.NAME).then(getOrAddNode("items", NodeTypes.ContentNode.NAME).then(getOrAddNode("restorePreviousVersion", NodeTypes.ContentNode.NAME))))))
                .addTask(new IsModuleInstalledOrRegistered("Configure actions and commands if workflow module is installed or is not installed.", "workflow",
                        new ArrayDelegateTask("Configure actions and commands if workflow module is installed.",
                                new NodeExistsDelegateTask("Configure commands in shop catalog in shop node", "/modules/shop/commands", null,
                                        new PartialBootstrapTask("Configure commands in shop catalog in shop node", "/mgnl-bootstrap/shop_workflow/config.modules.shop.commands.xml", "/commands/shop")),
                                new NodeExistsDelegateTask("Configure activateDeletion action in shop/apps/shop/subApps/browser/actions node", "/modules/shop/apps/shop/subApps/browser/actions/activateDeletion", null,
                                        new PartialBootstrapTask("Configure activateDeletion action in shop/apps/shop/subApps/browser/actions node", "/mgnl-bootstrap/shop_workflow/config.modules.shop.apps.shop.subApps.browser.actions.xml", "/actions/activateDeletion")),
                                new NodeExistsDelegateTask("Configure restorePreviousVersion action in shop/apps/shop/subApps/browser/actions node", "/modules/shop/apps/shop/subApps/browser/actions/restorePreviousVersion", null,
                                        new PartialBootstrapTask("Configure restorePreviousVersion action in shop/apps/shop/subApps/browser/actions node", "/mgnl-bootstrap/shop_workflow/config.modules.shop.apps.shop.subApps.browser.actions.xml", "/actions/restorePreviousVersion")),
                                new NodeExistsDelegateTask("Configure activate action in /modules/shop/apps/shop/subApps/browser/actions node", "/modules/shop/apps/shop/subApps/browser/actions/activate",
                                        new ValueOfPropertyDelegateTask("", "/modules/shop/apps/shop/subApps/browser/actions/activate", "class", "info.magnolia.ui.framework.action.ActivationActionDefinition", false,
                                                new PartialBootstrapTask("", "/mgnl-bootstrap/shop_workflow/config.modules.shop.apps.shop.subApps.browser.actions.xml", "/actions/activate"))),
                                new NodeExistsDelegateTask("Configure deactivate action in /modules/shop/apps/shop/subApps/browser/actions node", "/modules/shop/apps/shop/subApps/browser/actions/deactivate",
                                        new ValueOfPropertyDelegateTask("", "/modules/shop/apps/shop/subApps/browser/actions/deactivate", "class", "info.magnolia.ui.framework.action.DeactivationActionDefinition", false,
                                                new PartialBootstrapTask("", "/mgnl-bootstrap/shop_workflow/config.modules.shop.apps.shop.subApps.browser.actions.xml", "/actions/deactivate")))),
                        new ArrayDelegateTask("Configure actions and commands if workflow module is not installed.",
                                new NodeExistsDelegateTask("Configure commands in shop catalog in shop node", "/modules/shop/commands", null,
                                        new PartialBootstrapTask("Configure commands in shop catalog in shop node", "/mgnl-bootstrap/shop_default/config.modules.shop.commands.xml", "/commands/shop")),
                                new NodeExistsDelegateTask("Configure activateDeletion action in shop/apps/shop/subApps/browser/actions node", "/modules/shop/apps/shop/subApps/browser/actions/activateDeletion", null,
                                        new PartialBootstrapTask("Configure activateDeletion action in shop/apps/shop/subApps/browser/actions node", "/mgnl-bootstrap/shop_default/config.modules.shop.apps.shop.subApps.browser.actions.xml", "/actions/activateDeletion")),
                                new NodeExistsDelegateTask("Configure restorePreviousVersion action in shop/apps/shop/subApps/browser/actions node", "/modules/shop/apps/shop/subApps/browser/actions/restorePreviousVersion", null,
                                        new PartialBootstrapTask("Configure restorePreviousVersion action in shop/apps/shop/subApps/browser/actions node", "/mgnl-bootstrap/shop_default/config.modules.shop.apps.shop.subApps.browser.actions.xml", "/actions/restorePreviousVersion")),
                                new NodeExistsDelegateTask("Configure activate action in /modules/shop/apps/shop/subApps/browser/actions node", "/modules/shop/apps/shop/subApps/browser/actions/activate",
                                        new ValueOfPropertyDelegateTask("", "/modules/shop/apps/shop/subApps/browser/actions/activate", "class", "info.magnolia.ui.framework.action.ActivationActionDefinition", false,
                                                new PartialBootstrapTask("", "/mgnl-bootstrap/shop_default/config.modules.shop.apps.shop.subApps.browser.actions.xml", "/actions/activate"))),
                                new NodeExistsDelegateTask("Configure deactivate action in /modules/shop/apps/shop/subApps/browser/actions node", "/modules/shop/apps/shop/subApps/browser/actions/deactivate",
                                        new ValueOfPropertyDelegateTask("", "/modules/shop/apps/shop/subApps/browser/actions/deactivate", "class", "info.magnolia.ui.framework.action.DeactivationActionDefinition", false,
                                                new PartialBootstrapTask("", "/mgnl-bootstrap/shop_default/config.modules.shop.apps.shop.subApps.browser.actions.xml", "/actions/deactivate")))
                        )
                ))
                .addTask(new RemoveNodesTask("Remove multiple section from action bars", RepositoryConstants.CONFIG, Arrays.asList("/modules/shop/apps/shop/subApps/browser/actionbar/sections/multiple", "/modules/shop/apps/shopProducts/subApps/browser/actionbar/sections/multiple", "/modules/shop/apps/shopSuppliers/subApps/browser/actionbar/sections/multiple"), false))
                .addTask(new RegisterShopNodeTypeTask("shops", "shop", true))
                .addTask(new RegisterShopNodeTypeTask("shops", "shopCurrencies", true))
                .addTask(new RegisterShopNodeTypeTask("shops", "shopCurrency", true))
                .addTask(new RegisterShopNodeTypeTask("shops", "shopPriceCategories", true))
                .addTask(new RegisterShopNodeTypeTask("shops", "shopPriceCategory", true))
                .addTask(new RegisterShopNodeTypeTask("shops", "shopTaxCategories", true))
                .addTask(new RegisterShopNodeTypeTask("shops", "shopTaxCategory", true))
                .addTask(new RegisterShopNodeTypeTask("shops", "shopCountries", true))
                .addTask(new RegisterShopNodeTypeTask("shops", "shopCountry", true))
                .addTask(new RegisterShopNodeTypeTask("shops", "shopShippingOptions", true))
                .addTask(new RegisterShopNodeTypeTask("shops", "shopShippingOption", true))
                .addTask(new RegisterShopNodeTypeTask("shopProducts", "shopProduct", true))
                .addTask(new RegisterShopNodeTypeTask("shopProducts", "shopProductOptions", true))
                .addTask(new RegisterShopNodeTypeTask("shopProducts", "shopProductOption", true))
                .addTask(new RegisterShopNodeTypeTask("shopSuppliers", "shopSupplier", true))
        );
    }

    @Override
    protected List<Task> getExtraInstallTasks(InstallContext installContext) {
        final List<Task> installTasks = new ArrayList<Task>();
        installTasks.addAll(super.getExtraInstallTasks(installContext));
        installTasks.add(new TemplatesInstallTask("/shop/.*\\.ftl", true));

        installTasks.add(new InstallResourcesTask("/templating-kit/themes/pop/css/shop.css", "resources:processedCss", false, STKResourceModel.class.getName()));
        installTasks.add(new NodeExistsDelegateTask("Install shop templates", "Install shop templates under Site Configuration", "config", "/modules/standard-templating-kit/config/site/", new NodeBuilderTask("", "", ErrorHandling.strict, "config", "/modules/standard-templating-kit/config/site/",
                getNode("templates/availability/templates").then(
                        addNode("shopCheckoutForm", NodeTypes.ContentNode.NAME).then(
                                addProperty("id", "shop:pages/shopCheckoutForm")),
                        addNode("shopConfirmationPage", NodeTypes.ContentNode.NAME).then(
                                addProperty("id", "shop:pages/shopConfirmationPage")),
                        addNode("shopFormStep", NodeTypes.ContentNode.NAME).then(
                                addProperty("id", "shop:pages/shopFormStep")),
                        addNode("shopFormStepConfirmOrder", NodeTypes.ContentNode.NAME).then(
                                addProperty("id", "shop:pages/shopFormStepConfirmOrder")),
                        addNode("shopHome", NodeTypes.ContentNode.NAME).then(
                                addProperty("id", "shop:pages/shopHome")),
                        addNode("shopProductCategory", NodeTypes.ContentNode.NAME).then(
                                addProperty("id", "shop:pages/shopProductCategory")),
                        addNode("shopProductKeywordResult", NodeTypes.ContentNode.NAME).then(
                                addProperty("id", "shop:pages/shopProductKeywordResult")),
                        addNode("shopProductSearchResult", NodeTypes.ContentNode.NAME).then(
                                addProperty("id", "shop:pages/shopProductSearchResult")),
                        addNode("shopProductDetail", NodeTypes.ContentNode.NAME).then(
                                addProperty("id", "shop:pages/shopProductDetail")),
                        addNode("shopShoppingCart", NodeTypes.ContentNode.NAME).then(
                                addProperty("id", "shop:pages/shopShoppingCart"))))));

        installTasks.add(new NodeExistsDelegateTask("Install shop templates", "Install shop templates under default Site Definition", "config", "/modules/multisite/config/sites/default/",
                new NodeBuilderTask("", "", ErrorHandling.strict, "config", "/modules/multisite/config/sites/default/",
                        getNode("templates/availability/templates").then(
                                addNode("shopCheckoutForm", NodeTypes.ContentNode.NAME).then(
                                        addProperty("id", "shop:pages/shopCheckoutForm")),
                                addNode("shopConfirmationPage", NodeTypes.ContentNode.NAME).then(
                                        addProperty("id", "shop:pages/shopConfirmationPage")),
                                addNode("shopFormStep", NodeTypes.ContentNode.NAME).then(
                                        addProperty("id", "shop:pages/shopFormStep")),
                                addNode("shopFormStepConfirmOrder", NodeTypes.ContentNode.NAME).then(
                                        addProperty("id", "shop:pages/shopFormStepConfirmOrder")),
                                addNode("shopHome", NodeTypes.ContentNode.NAME).then(
                                        addProperty("id", "shop:pages/shopHome")),
                                addNode("shopProductCategory", NodeTypes.ContentNode.NAME).then(
                                        addProperty("id", "shop:pages/shopProductCategory")),
                                addNode("shopProductKeywordResult", NodeTypes.ContentNode.NAME).then(
                                        addProperty("id", "shop:pages/shopProductKeywordResult")),
                                addNode("shopProductSearchResult", NodeTypes.ContentNode.NAME).then(
                                        addProperty("id", "shop:pages/shopProductSearchResult")),
                                addNode("shopProductDetail", NodeTypes.ContentNode.NAME).then(
                                        addProperty("id", "shop:pages/shopProductDetail")),
                                addNode("shopShoppingCart", NodeTypes.ContentNode.NAME).then(
                                        addProperty("id", "shop:pages/shopShoppingCart"))))));

        installTasks.add(new IsModuleInstalledOrRegistered("Keywords for product Categories",
                "Adds control to product categories dialog for assigning keywords.", "categorization",
                new NodeBuilderTask("", "", ErrorHandling.strict, "config",
                        getNode("modules/shop/apps/shopProducts/subApps/detail/editor/form/tabs/categories/fields").then(
                                addNode("categories", NodeTypes.ContentNode.NAME).then(
                                        addNode("field", NodeTypes.ContentNode.NAME).then(
                                                addNode("identifierToPathConverter", NodeTypes.ContentNode.NAME).then(
                                                        addProperty("class", "info.magnolia.ui.form.field.converter.BaseIdentifierToPathConverter")),
                                                addProperty("appName", "categories"),
                                                addProperty("buttonSelectNewLabel", "field.link.select.new"),
                                                addProperty("buttonSelectOtherLabel", "field.link.select.another"),
                                                addProperty("class", "info.magnolia.ui.form.field.definition.LinkFieldDefinition"),
                                                addProperty("fieldEditable", "true"),
                                                addProperty("targetWorkspace", "category")),
                                        addProperty("buttonSelectAddLabel", "field.link.select.add"),
                                        addProperty("class", "info.magnolia.ui.form.field.definition.MultiValueFieldDefinition"),
                                        addProperty("identifier", "true"),
                                        addProperty("transformerClass", "info.magnolia.ui.form.field.transformer.multi.MultiValueSubChildrenNodeTransformer"))))));

        installTasks.add(new IsModuleInstalledOrRegistered("Add Keyword extras paragraph",
                "Adds an autogenerated keyword paragraph in the extras area of ProductCategory template.", "categorization",
                new NodeBuilderTask("", "", ErrorHandling.strict, "config",
                        getNode("modules/shop/templates/pages/shopProductCategory/areas/extras/areas/extras1/autoGeneration/content").then(
                                addNode("extrasItem2", NodeTypes.ContentNode.NAME).then(
                                        addProperty("catCloudTitle", "Keywords"),
                                        addProperty("nodeType", "mgnl:component"),
                                        addProperty("templateId", "shop:components/extras/shopExtrasTagCloud"))))));
        installTasks.add(new IsAuthorInstanceDelegateTask("Shop role for anonymous user", "This role to anonymous users will be added just on public instances.", null,
                new AddRoleToUserTask("", "anonymous", "shop-user-base")));

        installTasks.add(new IsInstallSamplesTask("Install demo-project sample content ", "", new ModuleDependencyBootstrapTask("demo-project")));

        installTasks.add(new OrderNodeAfterTask("Order node", "Order entry in appLauncher", "config", "/modules/ui-admincentral/config/appLauncherLayout/groups/sampleShop", "edit"));
        installTasks.add(new OrderNodeBeforeTask("Order node", "Order entry in appLauncher", "config", "/modules/ui-admincentral/config/appLauncherLayout/groups/shop", "stk"));

        installTasks.add(new IsModuleInstalledOrRegistered("Bootstrap actions and commands if workflow module is installed or is not installed.", "workflow",
                new ArrayDelegateTask("Bootstrap actions and commands if workflow module is installed.",
                        new BootstrapSingleResource("","","/mgnl-bootstrap/shop_workflow/config.modules.shop.apps.shop.subApps.browser.actions.xml"),
                        new BootstrapSingleResource("","","/mgnl-bootstrap/shop_workflow/config.modules.shop.commands.xml")),
                new ArrayDelegateTask("Bootstrap actions and commands if workflow module is not installed.",
                        new BootstrapSingleResource("","","/mgnl-bootstrap/shop_default/config.modules.shop.apps.shop.subApps.browser.actions.xml"),
                        new BootstrapSingleResource("","","/mgnl-bootstrap/shop_default/config.modules.shop.commands.xml"))
        ));

        return installTasks;
    }
}
