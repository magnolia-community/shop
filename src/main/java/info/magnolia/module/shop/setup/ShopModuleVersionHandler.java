/**
 * This file Copyright (c) 2003-2011 Magnolia International
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

import static info.magnolia.nodebuilder.Ops.addProperty;
import static info.magnolia.nodebuilder.Ops.getNode;
import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.admininterface.setup.AddMainMenuItemTask;
import info.magnolia.module.admininterface.setup.AddSubMenuItemTask;
import info.magnolia.module.data.setup.RegisterNodeTypeTask;

import info.magnolia.module.delta.AddRoleToUserTask;
import info.magnolia.module.delta.IsAuthorInstanceDelegateTask;
import info.magnolia.module.delta.IsInstallSamplesTask;
import info.magnolia.module.delta.IsModuleInstalledOrRegistered;
import info.magnolia.module.delta.OrderNodeBeforeTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.inplacetemplating.setup.TemplatesInstallTask;
import info.magnolia.module.resources.setup.InstallResourcesTask;
import info.magnolia.module.templatingkit.resources.STKResourceModel;
import info.magnolia.module.templatingkit.setup.UpdateAllSiteDefinitions;
import info.magnolia.nodebuilder.NodeBuilder;
import info.magnolia.nodebuilder.task.ErrorHandling;
import info.magnolia.nodebuilder.task.NodeBuilderTask;
import info.magnolia.nodebuilder.task.TaskLogErrorHandler;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import static info.magnolia.nodebuilder.Ops.*;

/**
 * This class is used to handle installation and updates of your module.
 */
public class ShopModuleVersionHandler extends DefaultModuleVersionHandler {

  public ShopModuleVersionHandler() {
    // nothing to do here
  }

  /**
   * 
   * @param installContext
   * @return a list with all the RegisterNodeTypeTask objects needed for the
   *         shop node types
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
    installTasks.add(new RegisterNodeTypeTask("shopProduct"));
    installTasks.add(new RegisterNodeTypeMultipleTask("shopProductCategory"));
    installTasks.add(new RegisterNodeTypeTask("shopProductOptions"));
    installTasks.add(new RegisterNodeTypeTask("shopProductOption"));
    installTasks.add(new RegisterNodeTypeTask("shopTaxCategories"));
    installTasks.add(new RegisterNodeTypeTask("shopTaxCategory"));
    installTasks.addAll(super.getBasicInstallTasks(installContext));
    return installTasks;
  }

  @Override
  protected List getExtraInstallTasks(InstallContext installContext) {
    final List installTasks = new ArrayList();
    installTasks.addAll(super.getExtraInstallTasks(installContext));
    installTasks.add(new TemplatesInstallTask("/shop/.*\\.ftl", true));
    installTasks.add(new AddMainMenuItemTask("shops", "menu.shops", "info.magnolia.module.shop.messages", "MgnlAdminCentral.showTree('shop')", "/.resources/icons/24/shoppingcart.gif", "data"));
    installTasks.add(new AddSubMenuItemTask("shops", "shopProduct", "menu.products", "info.magnolia.module.shop.messages", "MgnlAdminCentral.showTree('shopProduct')", "/.resources/icons/16/dot.gif"));
    installTasks.add(new AddSubMenuItemTask("shops", "shopProductCategory", "menu.productCategories", "info.magnolia.module.shop.messages", "MgnlAdminCentral.showTree('shopProductCategory')", "/.resources/icons/16/dot.gif"));
    installTasks.add(new AddSubMenuItemTask("shops", "shoppingCarts", "menu.carts", "info.magnolia.module.shop.messages", "MgnlAdminCentral.showTree('shoppingCarts')", "/.resources/icons/16/dot.gif"));
    installTasks.add(new IsInstallSamplesTask("","", new OrderNodeBeforeTask("","", ContentRepository.CONFIG, "/modules/adminInterface/config/menu/sampleShop", "shops")));
    installTasks.add(new IsInstallSamplesTask("","", new OrderNodeBeforeTask("","", ContentRepository.CONFIG, "/modules/adminInterface/config/menu/bikeShop", "shops")));
    installTasks.add(new InstallResourcesTask("/templating-kit/themes/pop/css/shop.css", "processedCss", STKResourceModel.class.getName()));
    installTasks.add(new UpdateAllSiteDefinitions("Add new templates to Availability", "") {
      protected void updateSiteDefinition(InstallContext ctx, Content siteDefinition) throws RepositoryException, TaskExecutionException {
          new NodeBuilder(new TaskLogErrorHandler(ctx), siteDefinition,
              getNode("templates/availability/templates").then(
                  addNode("shopCheckoutForm", ItemType.CONTENTNODE).then(
                      addProperty("name", "shopCheckoutForm")),
                  addNode("shopConfirmationPage", ItemType.CONTENTNODE).then(
                      addProperty("name", "shopConfirmationPage")),
                  addNode("shopFormStep", ItemType.CONTENTNODE).then(
                      addProperty("name", "shopFormStep")),
                  addNode("shopFormStepConfirmOrder", ItemType.CONTENTNODE).then(
                      addProperty("name", "shopFormStepConfirmOrder")),
                  addNode("shopHome", ItemType.CONTENTNODE).then(
                      addProperty("name", "shopHome")),
                  addNode("shopProductDetail", ItemType.CONTENTNODE).then(
                      addProperty("name", "shopProductDetail")),
                  addNode("shopShoppingCart", ItemType.CONTENTNODE).then(
                      addProperty("name", "shopShoppingCart"))
              )).exec();
      }
    });
    
    installTasks.add(new IsModuleInstalledOrRegistered("Keywords for product Categores", 
        "Adds control to product categories dialog for asigning keywords.", "extended-templating-kit", 
        new NodeBuilderTask("","", ErrorHandling.strict, "config",
            getNode("modules/data/dialogs/shopProductCategory/mainTab").then(
                addNode("tags", ItemType.CONTENTNODE).then(
                    addProperty("controlType", "categorizationUUIDMultiSelect"),
                    addProperty("saveHandler", "info.magnolia.module.categorization.controls.CategorizationSaveHandler"),
                    addProperty("tree", "category"),
                    addProperty("type", "String"),
                    addProperty("i18n", "true"),
                    addProperty("i18nBasename", "info.magnolia.module.shop.messages"),
                    addProperty("label", "dialogs.generic.tabCategorization.categories.label"),
                    addProperty("description", "dialogs.generic.tabCategorization.categories.description")))
            )));
    installTasks.add(new IsModuleInstalledOrRegistered("Set multilanguage on categorization", 
            "Display name in the site defined languages.", "categorization", 
            new NodeBuilderTask("","", ErrorHandling.strict, "config",
                getNode("modules/data/dialogs/category/mainTab/displayName").then(
                    setProperty("controlType", "shopMultiLanguageEdit")
                ))));
    installTasks.add(new IsAuthorInstanceDelegateTask("Shop role for anonymous user", "This role to anonymous users will be added just on public instances.", null, 
            new AddRoleToUserTask("", "anonymous", "shop-user-base")));
    return installTasks;
  }
}
