/**
 * This file Copyright (c) 2003-2009 Magnolia International
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
package info.magnolia.module.shop;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.data.setup.RegisterNodeTypeTask;

import info.magnolia.module.delta.Task;
import java.util.ArrayList;
import java.util.List;

/*
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
    installTasks.add(new RegisterNodeTypeTask("shopCart"));
    installTasks.add(new RegisterNodeTypeTask("shopCartItem"));
    installTasks.add(new RegisterNodeTypeTask("shopCurrency"));
    installTasks.add(new RegisterNodeTypeTask("shopCustomer"));
    installTasks.add(new RegisterNodeTypeTask("shopPriceCategory"));
    installTasks.add(new RegisterNodeTypeTask("shopProduct"));
    installTasks.add(new RegisterNodeTypeTask("shopProductCategory"));
    installTasks.add(new RegisterNodeTypeTask("shopProductPrice"));
    installTasks.add(new RegisterNodeTypeTask("shopTaxCategory"));
    installTasks.addAll(super.getBasicInstallTasks(installContext));
    return installTasks;
  }

  @Override
  protected List getExtraInstallTasks(InstallContext installContext) {
    final List installTasks = new ArrayList();
    // no exta install tasks needed for now
    return installTasks;
  }
}
