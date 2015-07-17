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
package info.magnolia.module.shop.beans;

import java.util.ArrayList;
import java.util.Map;

/**
 * Shoping cart.
 * @author will
 */
public interface ShoppingCart {
    public int addToShoppingCart(String productUUID, int quantity, Map<String, CartItemOption> options);
    public int addToShoppingCart(String productUUID, int quantity);

    public void updateItemByName(String name, int quantity);

    /**
     * Removes item for provided product from cart.
     *
     * @param productUUID
     * @deprecated Deprecated since v.2.3.0. Use {@link #removeItemByName(String)}
     */
    @Deprecated
    public void removeFromShoppingCart(String productUUID);
    public void removeItemByName(String itemName);

    public ArrayList<ShoppingCartItem> getCartItems();

    public int getCartItemsCount();

    public String getLanguage();

    public void setLanguage(String language);

    public void updateCartData(Map<String, Object> parameters );

    /**
     * Returns the next available item name. As shopping cart items are not saved before the cart is being processed,
     * there is no id to identify them within the cart. addToShoppingCart() will now get the next available id and
     * assign it to the new item. The id will be used as the item's "name".
     *
     * @return Next available item name.
     */
    public String getNextItemName();
}
