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
package info.magnolia.shop.processors;

import info.magnolia.context.MgnlContext;

import java.util.GregorianCalendar;

/**
 * Created by will on 30.06.15.
 */
public class UsernameHierarchyStrategy extends FlatHierarchyStrategy {
    @Override
    public String getCartParentPath(String shopName) {
        String parentPath = super.getCartParentPath(shopName);
        String username = MgnlContext.getUser().getName();
        GregorianCalendar now = new GregorianCalendar();
        if (username.equals("anonymous") || username.length() == 1) {
            // user "anonymous" is on top level
            parentPath += "/" + username;
        } else {
            parentPath += "/" + username.substring(0,1);
            parentPath += "/" + username.substring(0,2);
            parentPath += "/" + username;
        }

        return parentPath;
    }

}