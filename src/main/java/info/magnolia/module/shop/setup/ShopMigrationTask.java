/**
 * This file Copyright (c) 2013 Magnolia International
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

import info.magnolia.migration.task.AbstractSTKRelatedModuleMigrationTask;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.ArrayDelegateTask;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.CheckAndModifyPropertyValueTask;
import info.magnolia.module.delta.RemoveNodeTask;
import info.magnolia.module.delta.RenameNodesTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.templatingkit.migration.util.MigrationUtil;

import java.util.Arrays;
import java.util.List;

import javax.jcr.RepositoryException;

/**
 * Migration of Shop Module.
 */
public class ShopMigrationTask extends AbstractSTKRelatedModuleMigrationTask {

    public ShopMigrationTask(String taskName, String taskDescription, String moduleName, boolean disposeObservation, List<String> siteDefinition) {
        super(taskName, taskDescription, moduleName, disposeObservation, siteDefinition);
    }

    @Override
    protected void executeExtraMigrationTask(InstallContext installContext) throws TaskExecutionException {
        installContext.warn("Please fix categories of products after shop migration if you use sample-shop!");
        // do cleanup of unused nodes in SHOP 1.1
        ArrayDelegateTask task = new ArrayDelegateTask("Cleanup of nodes that are not used in shop 1.1 anymore.",
                new RemoveNodeTask("Remove node", "Remove shopGrid node.", "config", "/modules/shop/controls/shopGrid"),
                new RemoveNodeTask("Remove node", "Remove shopGridDate node.", "config", "/modules/shop/controls/shopGridDate"),
                new RemoveNodeTask("Remove node", "Remove shopGridEdit node.", "config", "/modules/shop/controls/shopGridEdit"),
                new RemoveNodeTask("Remove node", "Remove shopGridQuerySelect node.", "config", "/modules/shop/controls/shopGridQuerySelect"),
                new RemoveNodeTask("Remove node", "Remove shopCountrySelection component.", "config", "/modules/shop/templates/components/features/form/shopCountrySelection"),
                new RemoveNodeTask("Remove node", "Remove shopCountrySelection dialog node.", "config", "/modules/shop/dialogs/shopCountrySelection"),
                new RemoveNodeTask("Remove node", "Remove control in shopCheckDisableFields tab.", "config", "/modules/shop/dialogs/shopCheckDisableFields/tabMain/default"),
                new RemoveNodeTask("Remove node", "Remove form validator shopCountry.", "config", "/modules/form/config/validators/shopCountry"),
                new RemoveNodeTask("Remove node", "Remove tab weightSizeTab from shopProduct.", "config", "/modules/data/dialogs/shopProduct/weightSizeTab"),
                new RemoveNodeTask("Remove node", "Remove control supplierUUID from shopProduct/mainTab.", "config", "/modules/data/dialogs/shopProduct/mainTab/supplierUUID"),
                new RemoveNodeTask("Remove node", "Remove 'Suppliers' item from data menu.", "config", "/modules/adminInterface/config/menu/data/shopSupplier"),
                new RemoveNodeTask("Remove node", "Remove shopSupplier tree node.", "config", "/modules/data/trees/shopSupplier"),
                new RemoveNodeTask("Remove node", "Remove shopCountries tree node.", "config", "/modules/data/trees/shopCountries"),
                new RemoveNodeTask("Remove node", "Remove shopCountry tree node.", "config", "/modules/data/trees/shopCountry"),
                new RemoveNodeTask("Remove node", "Remove shopSupplier dialog node.", "config", "/modules/data/dialogs/shopSupplier"),
                new RemoveNodeTask("Remove node", "Remove shopCountries dialog node.", "config", "/modules/data/dialogs/shopCountries"),
                new RemoveNodeTask("Remove node", "Remove shopCountry dialog node.", "config", "/modules/data/dialogs/shopCountry"),
                new RemoveNodeTask("Remove node", "Remove shopSupplier data type.", "config", "/modules/data/config/types/shopSupplier"),
                new RemoveNodeTask("Remove node", "Remove shopCountries dialogs from shop type.", "config", "/modules/data/config/types/shop/shopCountries"),
                new RemoveNodeTask("Remove node", "Remove shopShippingOptions dialog from shop type.", "config", "/modules/data/config/types/shop/shopShippingOptions")
                );
        task.execute(installContext);

        // do cleanup of ocm shopping cart fields
        task = new ArrayDelegateTask("Cleanup of OCM shopping cart properties.");
        String[] fields = new String[] { "paymentDate", "paymentType", "paymentID", "shippingOptionUUID", "shippingOptionTitle", "shippingCost", "shippingCostTaxRate",
                "shippingCostTaxIncluded", "orderAddressCompany", "orderAddressCompany2", "orderAddressFirstname", "orderAddressLastname",
                "orderAddressSex", "orderAddressTitle", "orderAddressStreet", "orderAddressStreet2", "orderAddressZip", "orderAddressCity",
                "orderAddressState", "orderAddressCountry", "orderAddressPhone", "orderAddressMobile", "orderAddressMail", "billingAddressCompany2",
                "billingAddressSex", "billingAddressTitle", "billingAddressStreet2", "billingAddressState", "billingAddressCountry", "billingAddressMobile",
                "shippingAddressCompany2", "shippingAddressSex", "shippingAddressTitle", "shippingAddressStreet2", "shippingAddressState", "shippingAddressCountry",
                "shippingAddressMobile" };
        for (String field : fields) {
            task.addTask(new RemoveNodeTask("Remove node", "Remove unused field from shopping cart ocm.", "config", "/modules/ocm/config/classDescriptors/defaultShoppingCart/fieldDescriptors/" + field));
        }
        task.execute(installContext);

        // rename shopping cart nodes in ocm config
        task = new ArrayDelegateTask("Rename shopping cart nodes in ocm config.",
                new RenameNodesTask("Rename node", "Rename defaultShoppingCart to testShoppingCart.", "config", "/modules/ocm/config/classDescriptors", "defaultShoppingCart", "testShoppingCart", "mgnl:contentNode"),
                new RenameNodesTask("Rename node", "Rename defaultShoppingCartItem to testShoppingCartItem.", "config", "/modules/ocm/config/classDescriptors", "defaultShoppingCartItem", "testShoppingCartItem", "mgnl:contentNode"),
                new RenameNodesTask("Rename node", "Rename defaultShoppingCartItemOption to testShoppingCartItemOption.", "config", "/modules/ocm/config/classDescriptors", "defaultShoppingCartItemOption", "testShoppingCartItemOption", "mgnl:contentNode"),
                new RenameNodesTask("Rename node", "Rename acceptedGTC to termsAccepted", "config", "/modules/ocm/config/classDescriptors/testShoppingCart", "acceptedGTC", "termsAccepted", "mgnl:contentNode"),
                new CheckAndModifyPropertyValueTask("Modify property", "Modify fieldName property of termsAccepted node", "config", "/modules/ocm/config/classDescriptors/testShoppingCart", "nextBeanID", "176", "101"),
                new CheckAndModifyPropertyValueTask("Modify property", "Modify fieldName property of termsAccepted node", "config", "/modules/ocm/config/classDescriptors/testShoppingCart/fieldDescriptors/termsAccepted", "fieldName", "acceptedGTC", "cartDiscountRate"),
                new CheckAndModifyPropertyValueTask("Modify property", "Modify fieldName property of termsAccepted node", "config", "/modules/ocm/config/classDescriptors/testShoppingCart/fieldDescriptors/termsAccepted", "jcrName", "acceptedGTC", "cartDiscountRate"),
                new CheckAndModifyPropertyValueTask("Modify property", "Modify fieldName property of termsAccepted node", "config", "/modules/ocm/config/classDescriptors/testShoppingCart/fieldDescriptors/cartDiscountRate", "fieldName", "cartDiscountRate", "termsAccepted"),
                new CheckAndModifyPropertyValueTask("Modify property", "Modify fieldName property of termsAccepted node", "config", "/modules/ocm/config/classDescriptors/testShoppingCart/fieldDescriptors/cartDiscountRate", "jcrName", "cartDiscountRate", "termsAccepted")
                );
        task.execute(installContext);

        // install css
        new BootstrapSingleResource("Bootstrap theme.", "Install css theme for shop module.", "/mgnl-bootstrap/shop/config.modules.standard-templating-kit.config.themes.pop.cssFiles.shop.xml").execute(installContext);

        // install images
        new BootstrapSingleResource("Bootstrap images.", "Install images for shop module", "/mgnl-bootstrap/shop/resources.templating-kit.themes.pop.img.shop.xml").execute(installContext);

        // migrate forms
        try {
            MigrationUtil.transformForm(installContext.getJCRSession("config"), "/modules/shop/templates/components/features/shopForm", Arrays.asList("formGroupFields"), getPersistentMapService().getComponentsMap(), "/form/generic/listArea.ftl", "availableComponents", "fieldsets");
            MigrationUtil.transformForm(installContext.getJCRSession("config"), "/modules/shop/templates/components/features/shopFormStepConfirmOrder", Arrays.asList("formGroupFields"), getPersistentMapService().getComponentsMap(), "/form/generic/listArea.ftl", "availableComponents", "fieldsets");
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void executeRenameAndChangeId(InstallContext installContext) throws TaskExecutionException {
        // do nothing
    }

}
