[#macro shoppingCartTable shoppingCart type]
  [#if !shoppingCart?has_content || shoppingCart.getCartItemsCount() == 0]
      <p>${i18n['shoppingcart.empty']}</p>
  [#else]
    [#assign currencyTitle = model.currencyTitle!]
    [#assign currencyFormatting = model.currencyFormatting]
      <table cellspacing="1" cellpadding="1" border="0" width="100%" class="shopping_cart">
      <thead>
        <th id="prod-name-col-width">${i18n['table.product']}</th>
        <th class="quantity">${i18n['table.quantity']}</th>
        <th class="unitprice">${i18n['table.unitprice']}</th>
        <th class="itemtotal">${i18n['table.total']}</th>
        [#if type=="cart"]
            <th></th>
      [/#if]
      </thead>
    <tbody>
        [#list shoppingCart.getCartItems() as product]
          [#assign productLink = model.getProductDetailPageLink(product.product)!]
          <tr>
            [#if productLink?has_content]
              <td>
                <a href="${productLink}">${product.productTitle!product.@name!"--"}</a>
              [#if product.options?has_content]
                  [#assign keys = product.options?keys]
                [#list keys as key]
                  [#assign option = cmsfn.contentByIdentifier("shopProducts", product.options[key].valueUUID)!]
                    [#if option?has_content]
                        <span class="option">
                        [#assign option = cmsfn.asContentMap(option) /]
                        [#assign optionSet = option?parent]
                        <span class="label">${optionSet.title}:</span> <span class="value">${option.title}</span>[#if key_has_next], [/#if]</span>
                    [/#if]
                [/#list]
              [/#if]
              </td>
            [#else]
              <td>
                ${product.productTitle}
              [#if product.options?has_content]
                  [#assign keys = product.options?keys]
                [#list keys as key]
                  <span class="option">[#assign option = cmsfn.getContentByUUID("shopProducts", product.options[key].valueUUID)!]
                  [#assign option = cmsfn.i18n(option) /]
                  [#assign optionSet = cmsfn.i18n(option?parent)]
                  <span class="label">${optionSet.title}:</span> <span class="value">${option.title}</span>[#if key_has_next], [/#if]</span>
                [/#list]
              [/#if]
            </td>
            [/#if]
            <td class="quantity">${product.quantity}</td>
            <td class="unitprice">${product.unitPrice?string(currencyFormatting!)} ${currencyTitle!}</td>
            <td class="itemtotal">${product.itemTotal?string(currencyFormatting!)} ${currencyTitle!}</td>
            [#if type=="cart"]
              <td>
                <a class="product-add-more" href="${model.getCommandLink('add', product.productUUID, product_index)}"><img class="shopAddRemove" src="${ctx.contextPath}/.resources/images/add.gif" /></a>
                <a class="product-subtract" href="${model.getCommandLink('subtract', product.productUUID, product_index)}"><img class="shopAddRemove" src="${ctx.contextPath}/.resources/images/remove.gif" /></a>
                <a class="product-removeall" href="${model.getCommandLink('removeall', product.productUUID, product_index)}"><img class="shopAddRemove" src="${ctx.contextPath}/.resources/images/removeAll.gif" /></a>
              </td>
            [/#if]
          </tr>
        [/#list]
        <tr>
        <td></td>
        <td></td>
        <td class="subtotal">${i18n['shoppingcart.subtotal']}</td>
        <td class="total_excl_tax">${(shoppingCart.grossTotalExclTax!0)?string(currencyFormatting!)} ${currencyTitle!}</td>
        [#if type=="cart"]<td></td>[/#if]
      </tr>
        <tr>
        <td></td>
        <td></td>
        <td class="vat">${i18n['vat']}</td>
        <td class="tax">${(shoppingCart.itemTaxTotal!0)?string(currencyFormatting!)} ${currencyTitle!}</td>
        [#if type=="cart"]<td></td>[/#if]
      </tr>
      <tr>
        <td></td>
        <td></td>
        <td class="total">${i18n['shoppingCart.total']}</td>
        <td class="total_incl_tax">${(shoppingCart.grossTotalInclTax!0)?string(currencyFormatting!)} ${currencyTitle}</td>
        [#if type=="cart"]<td></td>[/#if]
      </tr>
      </tbody>
      </table>
  [/#if]
[/#macro]