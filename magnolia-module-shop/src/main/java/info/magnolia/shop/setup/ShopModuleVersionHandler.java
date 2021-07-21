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
package info.magnolia.shop.setup;

import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.Delta;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.RemoveNodeTask;
import info.magnolia.module.delta.SetPropertyTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.delta.ValueOfPropertyDelegateTask;
import info.magnolia.objectfactory.Components;

import javax.jcr.ImportUUIDBehavior;
import java.util.ArrayList;
import java.util.List;

/**
 * Shop version handler.
 */
public class ShopModuleVersionHandler extends DefaultModuleVersionHandler {

    private final MagnoliaConfigurationProperties properties = Components.getComponent(MagnoliaConfigurationProperties.class);
    private static final String MAGNOLIA_BOOTSTRAP_SAMPLES = "magnolia.bootstrap.samples";

    public ShopModuleVersionHandler() {
        register(deltaFor301());
        register(deltaFor4_0_0());
    }

    private Delta deltaFor4_0_0() {
        DeltaBuilder builder = DeltaBuilder.update("4.0.0", "Bootstrap tasks for shop 4.0.0");

        builder.addTask(new ValueOfPropertyDelegateTask(
                "Check if defaultShoppingCart node uses ClassDescriptor",
                "config/modules/ocm/config/classDescriptors/defaultShoppingCart",
                "class",
                "org.apache.jackrabbit.ocm.mapper.model.ClassDescriptor",
                false,
                new SetPropertyTask("Change ClassDescriptor to ProxyClassDescriptor",
                        "config/modules/ocm/config/classDescrpitors/psShoppingCart",
                        "class",
                        "ch.fastforward.magnolia.ocm.beans.ProxyClassDescriptor")
        ));
        builder.addTask(new ValueOfPropertyDelegateTask(
                "Check if defaultShoppingCartItem node uses ClassDescriptor",
                "config/modules/ocm/config/classDescriptors/defaultShoppingCartItem",
                "class",
                "org.apache.jackrabbit.ocm.mapper.model.ClassDescriptor",
                false,
                new SetPropertyTask("Change ClassDescriptor to ProxyClassDescriptor",
                        "config/modules/ocm/config/classDescrpitors/psShoppingCart",
                        "class",
                        "ch.fastforward.magnolia.ocm.beans.ProxyClassDescriptor")
        ));
        builder.addTask(new ValueOfPropertyDelegateTask(
                "Check if defaultShoppingCartItemOption node uses ClassDescriptor",
                "config/modules/ocm/config/classDescriptors/defaultShoppingCartItemOption",
                "class",
                "org.apache.jackrabbit.ocm.mapper.model.ClassDescriptor",
                false,
                new SetPropertyTask("Change ClassDescriptor to ProxyClassDescriptor",
                        "config/modules/ocm/config/classDescrpitors/psShoppingCart",
                        "class",
                        "ch.fastforward.magnolia.ocm.beans.ProxyClassDescriptor")
        ));
        return builder;
    }

    private Delta deltaFor301() {
        DeltaBuilder builder = DeltaBuilder.update("3.0.1", "Bootstrap tasks for shop 3.0.1");

        addFileBootstrapTasks(builder, new String[] {
                "/mgnl-bootstrap/shop/config.modules.shop.fieldTypes.xml",
                "/mgnl-bootstrap/shop/ocm/config.modules.ocm.config.classDescriptors.defaultShoppingCart.xml",
                "/mgnl-bootstrap/shop/ocm/config.modules.ocm.config.classDescriptors.defaultShoppingCartItem.xml",
                "/mgnl-bootstrap/shop/ocm/config.modules.ocm.config.classDescriptors.defaultShoppingCartItemOption.xml",
                "/mgnl-bootstrap/shop_default/config.modules.shop.apps.shop.subApps.browser.actions.xml",
                "/mgnl-bootstrap/shop_workflow/config.modules.shop.apps.shop.subApps.browser.actions.xml"
        });

        if (properties.hasProperty(MAGNOLIA_BOOTSTRAP_SAMPLES) && properties.getProperty(MAGNOLIA_BOOTSTRAP_SAMPLES).equalsIgnoreCase("true")) {
            addFileBootstrapTasks(builder, new String[] {
                    "/mgnl-bootstrap-samples/shop/shops.sampleShop.xml"
            });
        }

        builder.addTask(new RemoveNodeTask("Removing legacy node", "/modules/publishing-core/config/receivers/magnoliaPublic8080/workspaces.xml"));
        builder.addTask(new RemoveNodeTask("Removing legacy node", "/modules/shop/apps"));
        builder.addTask(new RemoveNodeTask("Removing legacy node", "/modules/shop/dialogs"));
        return builder;
    }

    @Override
    protected List<Task> getExtraInstallTasks(InstallContext installContext) {
        final List<Task> installTasks = new ArrayList<>();
        return installTasks;
    }

    private void addFileBootstrapTasks(DeltaBuilder builder, String[] files) {
        for (String file : files) {
            BootstrapSingleResource task = new BootstrapSingleResource("Bootstrap file",
                    "Imports an updated configuration XML file", file, ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);
            builder.addTask(task);
        }
    }
}
