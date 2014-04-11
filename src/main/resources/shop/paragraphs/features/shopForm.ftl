[#assign shoppingCart = model.getShoppingCart()]

[#if (!shoppingCart?has_content || shoppingCart.getCartItemsCount() == 0) && !cmsfn.isEditMode() ]
    <p>${i18n['shoppingcart.empty']}</p>
[#else]
    [#include "/form/components/form.ftl"]
[/#if]