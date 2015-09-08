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

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by will on 30.06.15.
 */
public class UserAndDateHierarchyStrategy extends UsernameHierarchyStrategy {
    @Override
    public String getCartParentPath(String shopName) {
        String parentPath = super.getCartParentPath(shopName);
        GregorianCalendar now = new GregorianCalendar();
        // @TODO: could be done with date formatting
        parentPath += "/" + now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // MONTH is zero-based in Calendar!
        if (month < 10) {
            parentPath += "/0" + month;
        } else {
            parentPath += "/" + month;
        }

        return parentPath;
    }
}
