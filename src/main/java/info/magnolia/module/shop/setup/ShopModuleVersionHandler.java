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

import static info.magnolia.nodebuilder.Ops.*;

import info.magnolia.dam.setup.migration.ChangeWebsiteDmsReferenceToDamMigrationTask;
import info.magnolia.dam.setup.migration.CleanContentForDamMigrationTask;
import info.magnolia.dam.setup.migration.MoveDataWorkspaceToDamMigrationTask;
import info.magnolia.dam.setup.migration.MoveUploadedContentToDamMigrationTask;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.module.InstallContext;
import info.magnolia.module.admininterface.setup.SimpleContentVersionHandler;
import info.magnolia.module.data.setup.RegisterNodeTypeTask;
import info.magnolia.module.delta.AddRoleToUserTask;
import info.magnolia.module.delta.ArrayDelegateTask;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.IsAuthorInstanceDelegateTask;
import info.magnolia.module.delta.IsInstallSamplesTask;
import info.magnolia.module.delta.IsModuleInstalledOrRegistered;
import info.magnolia.module.delta.ModuleDependencyBootstrapTask;
import info.magnolia.module.delta.NodeExistsDelegateTask;
import info.magnolia.module.delta.OrderNodeAfterTask;
import info.magnolia.module.delta.OrderNodeBeforeTask;
import info.magnolia.module.delta.PartialBootstrapTask;
import info.magnolia.module.delta.RemoveNodesTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.inplacetemplating.setup.TemplatesInstallTask;
import info.magnolia.module.resources.setup.InstallResourcesTask;
import info.magnolia.module.templatingkit.resources.STKResourceModel;
import info.magnolia.nodebuilder.task.ErrorHandling;
import info.magnolia.nodebuilder.task.NodeBuilderTask;
import info.magnolia.ui.dialog.setup.DialogMigrationTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to handle installation and updates of your module.
 */
public class ShopModuleVersionHandler extends SimpleContentVersionHandler {

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

    }

    /**
     *
     * @param installContext
     * @return a list with all the RegisterNodeTypeTask objects needed for the
     * shop node types
     */
    @Override
    protected List<Task> getBasicInstallTasks(InstallContext installContext) {
        final List<Task> installTasks = new ArrayList<Task>();
        // make sure we register the type before doing anything else
        installTasks.add(new RegisterNodeTypeTask("category"));
        installTasks.add(new RegisterNodeTypeTask("shop"));
        installTasks.add(new RegisterNodeTypeTask("shopCurrencies"));
        installTasks.add(new RegisterNodeTypeTask("shopCurrency"));
        installTasks.add(new RegisterNodeTypeTask("shopPriceCategories"));
        installTasks.add(new RegisterNodeTypeTask("shopPriceCategory"));
        installTasks.add(new RegisterNodeTypeMultipleTask("shopProduct"));
        installTasks.add(new RegisterNodeTypeTask("shopProductOptions"));
        installTasks.add(new RegisterNodeTypeTask("shopProductOption"));
        installTasks.add(new RegisterNodeTypeTask("shopTaxCategories"));
        installTasks.add(new RegisterNodeTypeTask("shopTaxCategory"));
        installTasks.add(new RegisterNodeTypeTask("shopSupplier"));
        installTasks.add(new RegisterNodeTypeTask("shopCountries"));
        installTasks.add(new RegisterNodeTypeTask("shopCountry"));
        installTasks.add(new RegisterNodeTypeTask("shopShippingOptions"));
        installTasks.add(new RegisterNodeTypeTask("shopShippingOption"));
        installTasks.addAll(super.getBasicInstallTasks(installContext));
        return installTasks;
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
                addProperty("transformerClass", "info.magnolia.ui.form.field.transformer.multi.MultiValueSubChildrenNodeTransformer"))))))));

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

        return installTasks;
    }
}
