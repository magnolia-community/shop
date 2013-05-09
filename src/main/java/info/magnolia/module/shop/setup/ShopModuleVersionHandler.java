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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.module.InstallContext;
import info.magnolia.module.admininterface.setup.AddMainMenuItemTask;
import info.magnolia.module.admininterface.setup.AddSubMenuItemTask;
import info.magnolia.module.admininterface.setup.SimpleContentVersionHandler;
import info.magnolia.module.data.setup.RegisterNodeTypeTask;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.AddRoleToUserTask;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.CheckAndModifyPropertyValueTask;
import info.magnolia.module.delta.CreateNodePathTask;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.IsAuthorInstanceDelegateTask;
import info.magnolia.module.delta.IsInstallSamplesTask;
import info.magnolia.module.delta.IsModuleInstalledOrRegistered;
import info.magnolia.module.delta.ModuleDependencyBootstrapTask;
import info.magnolia.module.delta.NodeExistsDelegateTask;
import info.magnolia.module.delta.OrderNodeBeforeTask;
import info.magnolia.module.delta.RemoveNodeTask;
import info.magnolia.module.delta.SetPropertyTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.inplacetemplating.setup.TemplatesInstallTask;
import info.magnolia.module.resources.setup.InstallResourcesTask;
import info.magnolia.module.templatingkit.resources.STKResourceModel;
import info.magnolia.nodebuilder.NodeBuilder;
import info.magnolia.nodebuilder.task.ErrorHandling;
import info.magnolia.nodebuilder.task.NodeBuilderTask;
import info.magnolia.nodebuilder.task.TaskLogErrorHandler;
import info.magnolia.repository.RepositoryConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.RepositoryException;

/**
 * This class is used to handle installation and updates of your module.
 */
public class ShopModuleVersionHandler extends SimpleContentVersionHandler {

    public ShopModuleVersionHandler() {

        register(DeltaBuilder.update("1.1", "").addTask(new ShopMigrationTask("Migration task: migrate Shop configuration", "Migrate configuration of templates, dialogs and site definitions", "shop", false, new ArrayList<String>())) // This task fails when trying to install on 4.5 as all theme images have been moved to the resources repository!
                //                .addTask (new AbstractTask("Register new DMS Images", "Import all bootstrap files containing the new DMS images.") {
                //
                //                    public void execute(InstallContext ctx) throws TaskExecutionException {
                //                        try {
                //                            ctx.getJCRSession("dms").importXML("/templating-kit/pop/img", getClass().getResourceAsStream("/mgnl-bootstrap/shop/resources.templating-kit.themes.pop.img.shop.xml"), ImportUUIDBehavior.IMPORT_UUID_COLLISION_REMOVE_EXISTING);
                //                        }
                //                        catch (RepositoryException e) {
                //                            throw new TaskExecutionException("Can not bootstrap new DMS Images: ",e);
                //                        }
                //                        catch (IOException e) {
                //                            throw new TaskExecutionException("Can not bootstrap new DMS Images: ",e);
                //                        }
                //                    }
                //                })
                .addTask(new CheckAndModifyPropertyValueTask("Add Shop Form Model", "", RepositoryConstants.CONFIG,
                "/modules/shop/templates/components/features/shopForm", "modelClass",
                "info.magnolia.module.form.templates.components.FormModel", "info.magnolia.module.shop.paragraphs.ShopFormModel")).addTask(new RemoveNodeTask("Remove obsolete extras tabLinkList", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/extras/shopExtrasProduct/tabLinkList")).addTask(new RemoveNodeTask("Remove obsolete teasers tabLinkList", "", RepositoryConstants.CONFIG, "/modules/shop/dialogs/teasers/shopProductTeaser/tabLinkList")));

        register(DeltaBuilder.update("1.1.1", "").addTask(new NodeExistsDelegateTask("Check is data workflow is installed", "", RepositoryConstants.CONFIG, "/modules/data/commands/data/activate/startFlow", new SetPropertyTask("Fix activation Product prices and categories", RepositoryConstants.CONFIG,
                "/modules/data/commands/data/activate/startFlow", "itemTypes", "dataItemNode, mgnl:contentNode, shop, shopCurrencies, "
                + "shopCurrency, shopPriceCategories, shopPriceCategory, shopProduct, shopProductOptions, "
                + "shopProductOption, shopTaxCategories, shopTaxCategory"))).addTask(new IsModuleInstalledOrRegistered("Bootstrap new sample-shop", "", "demo-project",
                new AbstractTask("Register new sample-shop", "Import all bootstrap files of new sample-shop.") {

                    public void execute(InstallContext ctx) throws TaskExecutionException {
                        try {
                            ctx.getJCRSession("website").importXML("/demo-features/modules", getClass().getResourceAsStream("/info/magnolia/module/shop/setup/demo-project/website.demo-features.modules.sample-shop.xml"), ImportUUIDBehavior.IMPORT_UUID_COLLISION_REMOVE_EXISTING);
                        } catch (RepositoryException e) {
                            throw new TaskExecutionException("Can not bootstrap new sample-shop: ", e);
                        } catch (IOException e) {
                            throw new TaskExecutionException("Can not bootstrap new sample-shop: ", e);
                        }
                    }
                })));
        register(DeltaBuilder.update("1.1.2", "Moving features from Shop module for Magnolia 4.4 to the version for Magnolia 4.5")
                // register node types
                .addTask(new RegisterNodeTypeTask("shopSupplier"))
                .addTask(new RegisterNodeTypeTask("shopCountries"))
                .addTask(new RegisterNodeTypeTask("shopCountry"))
                .addTask(new RegisterNodeTypeTask("shopShippingOptions"))
                .addTask(new RegisterNodeTypeTask("shopShippingOption"))
                // data-types
                .addTask(new BootstrapSingleResource("Install", "shopCountries data type", "/mgnl-bootstrap/shop/data-types/config.modules.data.config.types.shop.shopCountries.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                .addTask(new BootstrapSingleResource("Install", "shopSupplier data type", "/mgnl-bootstrap/shop/data-types/config.modules.data.config.types.shopSupplier.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                .addTask(new BootstrapSingleResource("Install", "shopShippingOptions data type", "/mgnl-bootstrap/shop/data-types/config.modules.data.config.types.shop.shopShippingOptions.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                // data-dialogs
                .addTask(new BootstrapSingleResource("Install", "shopCountries dialog", "/mgnl-bootstrap/shop/data-dialogs/config.modules.data.dialogs.shopCountries.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                .addTask(new BootstrapSingleResource("Install", "shopCountry dialog", "/mgnl-bootstrap/shop/data-dialogs/config.modules.data.dialogs.shopCountry.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                .addTask(new BootstrapSingleResource("Install", "shopSupplier dialog", "/mgnl-bootstrap/shop/data-dialogs/config.modules.data.dialogs.shopSupplier.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                .addTask(new BootstrapSingleResource("Install", "supplier tab in product dialog", "/mgnl-bootstrap/shop/data-dialogs/config.modules.data.dialogs.shopProduct.mainTab.supplierUUID.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                .addTask(new BootstrapSingleResource("Install", "size + weight tab in product dialog", "/mgnl-bootstrap/shop/data-dialogs/config.modules.data.dialogs.shopProduct.weightSizeTab.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                .addTask(new BootstrapSingleResource("Install", "shopShippingOptions dialog", "/mgnl-bootstrap/shop/data-dialogs/config.modules.data.dialogs.shopShippingOptions.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                .addTask(new BootstrapSingleResource("Install", "shopShippingOption dialog", "/mgnl-bootstrap/shop/data-dialogs/config.modules.data.dialogs.shopShippingOption.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                // data-trees
                .addTask(new BootstrapSingleResource("Install", "shopSupplier tree", "/mgnl-bootstrap/shop/data-trees/config.modules.data.trees.shopSupplier.xml", ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                // other
                .addTask(new NodeExistsDelegateTask("Check supplier root node", "Looks in the data repository for a \"shopSuppliers\" node on the top level", "data", "/shopSuppliers", null,
                new CreateNodePathTask("Create node",
                "Creates a \"shopSuppliers\" node in the data repository on the top level", "data", "/shopSuppliers",
                new ItemType("dataFolder"))))
                // ocm move
                .addTask(new BootstrapSingleResource("Bootstrap", "Installing ocm class descriptor for ",
                        "/mgnl-bootstrap/shop/ocm/config.modules.ocm.config.classDescriptors.defaultShoppingCart.xml",
                        ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                .addTask(new BootstrapSingleResource("Bootstrap", "Installing ocm class descriptor for ",
                        "/mgnl-bootstrap/shop/ocm/config.modules.ocm.config.classDescriptors.defaultShoppingCartItem.xml",
                        ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                .addTask(new BootstrapSingleResource("Bootstrap", "Installing ocm class descriptor for ",
                        "/mgnl-bootstrap/shop/ocm/config.modules.ocm.config.classDescriptors.defaultShoppingCartItemOption.xml",
                        ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                // additional controls
                .addTask(new BootstrapSingleResource("Bootstrap", "Installing the shopGrid control",
                        "/mgnl-bootstrap/shop/controls/config.modules.shop.controls.shopGrid.xml",
                        ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                .addTask(new BootstrapSingleResource("Bootstrap", "Installing the shopGridDate control",
                        "/mgnl-bootstrap/shop/controls/config.modules.shop.controls.shopGridDate.xml",
                        ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                .addTask(new BootstrapSingleResource("Bootstrap", "Installing the shopGridEdit control",
                        "/mgnl-bootstrap/shop/controls/config.modules.shop.controls.shopGridEdit.xml",
                        ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
                .addTask(new BootstrapSingleResource("Bootstrap", "Installing the shopGridQuerySelect control",
                        "/mgnl-bootstrap/shop/controls/config.modules.shop.controls.shopGridQuerySelect.xml",
                        ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING))
           );

    }

    /**
     *
     * @param installContext
     * @return a list with all the RegisterNodeTypeTask objects needed for the
     * shop node types
     */
    @Override
    protected List getBasicInstallTasks(InstallContext installContext) {
        final List<Task> installTasks = new ArrayList<Task>();
        // make sure we register the type before doing anything else
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
    protected List getExtraInstallTasks(InstallContext installContext) {
        final List installTasks = new ArrayList();
        installTasks.addAll(super.getExtraInstallTasks(installContext));
        installTasks.add(new TemplatesInstallTask("/shop/.*\\.ftl", true));
        installTasks.add(new AddMainMenuItemTask("shops", "menu.shops", "info.magnolia.module.shop.messages", "MgnlAdminCentral.showTree('shop')", "/.resources/icons/24/shoppingcart.gif", "data"));
        installTasks.add(new AddSubMenuItemTask("shops", "shoppingCarts", "menu.carts", "info.magnolia.module.shop.messages", "MgnlAdminCentral.showTree('shoppingCarts')", "/.resources/icons/16/dot.gif"));
        installTasks.add(new IsInstallSamplesTask("", "", new OrderNodeBeforeTask("", "", RepositoryConstants.CONFIG, "/modules/adminInterface/config/menu/sampleShop", "shops")));
        installTasks.add(new InstallResourcesTask("/templating-kit/themes/pop/css/shop.css", "resources:processedCss", STKResourceModel.class.getName()));
        installTasks.add(new UpdateAllSiteDefinitions("Add new templates to Availability", "") {

            protected void updateSiteDefinition(InstallContext ctx, Content siteDefinition) throws RepositoryException, TaskExecutionException {
                new NodeBuilder(new TaskLogErrorHandler(ctx), siteDefinition,
                        getNode("templates/availability/templates").then(
                        addNode("shopCheckoutForm", MgnlNodeType.NT_CONTENTNODE).then(
                        addProperty("id", "shop:pages/shopCheckoutForm")),
                        addNode("shopConfirmationPage", MgnlNodeType.NT_CONTENTNODE).then(
                        addProperty("id", "shop:pages/shopConfirmationPage")),
                        addNode("shopFormStep", MgnlNodeType.NT_CONTENTNODE).then(
                        addProperty("id", "shop:pages/shopFormStep")),
                        addNode("shopFormStepConfirmOrder", MgnlNodeType.NT_CONTENTNODE).then(
                        addProperty("id", "shop:pages/shopFormStepConfirmOrder")),
                        addNode("shopHome", MgnlNodeType.NT_CONTENTNODE).then(
                        addProperty("id", "shop:pages/shopHome")),
                        addNode("shopProductCategory", MgnlNodeType.NT_CONTENTNODE).then(
                        addProperty("id", "shop:pages/shopProductCategory")),
                        addNode("shopProductKeywordResult", MgnlNodeType.NT_CONTENTNODE).then(
                        addProperty("id", "shop:pages/shopProductKeywordResult")),
                        addNode("shopProductSearchResult", MgnlNodeType.NT_CONTENTNODE).then(
                        addProperty("id", "shop:pages/shopProductSearchResult")),
                        addNode("shopProductDetail", MgnlNodeType.NT_CONTENTNODE).then(
                        addProperty("id", "shop:pages/shopProductDetail")),
                        addNode("shopShoppingCart", MgnlNodeType.NT_CONTENTNODE).then(
                        addProperty("id", "shop:pages/shopShoppingCart")))).exec();
            }
        });

        installTasks.add(new IsModuleInstalledOrRegistered("Keywords for product Categories",
                "Adds control to product categories dialog for asigning keywords.", "categorization",
                new NodeBuilderTask("", "", ErrorHandling.strict, "config",
                getNode("modules/data/dialogs/shopProduct").then(
                addNode("tagsTab", MgnlNodeType.NT_CONTENTNODE).then(
                addProperty("controlType", "tab"),
                addProperty("label", "dialogs.generic.tabCategorization.categories.label"),
                addNode("tags", MgnlNodeType.NT_CONTENTNODE).then(
                addProperty("controlType", "categorizationUUIDMultiSelect"),
                addProperty("saveHandler", "info.magnolia.module.categorization.controls.CategorizationSaveHandler"),
                addProperty("tree", "category"),
                addProperty("type", "String"),
                addProperty("i18n", "true"),
                addProperty("i18nBasename", "info.magnolia.module.shop.messages"),
                addProperty("label", "dialogs.generic.tabCategorization.categories.label"),
                addProperty("description", "dialogs.generic.tabCategorization.categories.description")))))));
        installTasks.add(new IsModuleInstalledOrRegistered("Set multilanguage on categorization",
                "Display name in the site defined languages.", "categorization",
                new NodeBuilderTask("", "", ErrorHandling.strict, "config",
                getNode("modules/data/dialogs/category/mainTab/displayName").then(
                setProperty("controlType", "shopMultiLanguageEdit")))));

        installTasks.add(new IsModuleInstalledOrRegistered("Add Keyword extras paragraph",
                "Adds an autogenerated keyword paragraph in the extras area of ProductCategory template.", "categorization",
                new NodeBuilderTask("", "", ErrorHandling.strict, "config",
                getNode("modules/shop/templates/pages/shopProductCategory/areas/extras/areas/extras1/autoGeneration/content").then(
                addNode("extrasItem2", MgnlNodeType.NT_CONTENTNODE).then(
                addProperty("catCloudTitle", "Keywords"),
                addProperty("nodeType", "mgnl:component"),
                addProperty("templateId", "shop:components/extras/shopExtrasTagCloud"))))));
        installTasks.add(new IsAuthorInstanceDelegateTask("Shop role for anonymous user", "This role to anonymous users will be added just on public instances.", null,
                new AddRoleToUserTask("", "anonymous", "shop-user-base")));

        installTasks.add(new IsInstallSamplesTask("Install demo-project sample content ", "", new ModuleDependencyBootstrapTask("demo-project")));
        installTasks.add(new NodeExistsDelegateTask("Check is data workflow is installed", "", RepositoryConstants.CONFIG, "/modules/data/commands/data/activate/startFlow", new SetPropertyTask("Fix activation Product prices and categories", RepositoryConstants.CONFIG,
                "/modules/data/commands/data/activate/startFlow", "itemTypes", "dataItemNode, mgnl:contentNode, shop, shopCurrencies, "
                + "shopCurrency, shopPriceCategories, shopPriceCategory, shopProduct, shopProductOptions, "
                + "shopProductOption, shopTaxCategories, shopTaxCategory")));
        installTasks.add(new NodeExistsDelegateTask("Check is data workflow is installed", "", RepositoryConstants.CONFIG, "/modules/data/commands/data/deactivate/startFlow", new SetPropertyTask("Fix activation Product prices and categories", RepositoryConstants.CONFIG,
                "/modules/data/commands/data/deactivate/startFlow", "itemTypes", "dataItemNode, mgnl:contentNode, shop, shopCurrencies, "
                + "shopCurrency, shopPriceCategories, shopPriceCategory, shopProduct, shopProductOptions, "
                + "shopProductOption, shopTaxCategories, shopTaxCategory")));

        return installTasks;
    }
}
