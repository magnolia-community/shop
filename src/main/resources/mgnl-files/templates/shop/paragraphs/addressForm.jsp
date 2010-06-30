<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="urn:jsptld:cms-taglib"
    xmlns:cmsu="urn:jsptld:cms-util-taglib" 
    xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" 
	xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt" 
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">

	<cms:ifNotEmpty nodeDataName="title">
		<h1><cms:out nodeDataName="title" /></h1>
	</cms:ifNotEmpty>
	<c:choose>
		<c:when test="${not empty shoppingCart and shoppingCart.cartItemsCount gt 0}">
			<div class="text"><c:out value="${shoppingCart.cartItemsCount}" /> products in the shopping cart</div>
			<form name="address_form" class="standard_form" action="${pageContext.request.contextPath}/.magnolia/pages/shop" method="POST">
				<input type="hidden" name="shopKey" value="${shopKey}" />
				<input type="hidden" name="command" value="updateCart" />
				<input type="hidden" name="targetPage" value="${checkoutConfirmPagePath}.html" />
				<h2>Billing address</h2>
				<div class="billing">
					<input type="hidden" name="update" value="billingAddress" />
					<div class="form_element">
						<label for="billing_address_company">Company:</label>
						<div class="input_element">
							<input type="text" class="text" name="billingAddressCompany" value="${shoppingCart.billingAddressCompany}" id="billing_address_company" />
						</div>
					</div>
					<div class="form_element">
						<label for="billing_address_firstname">Firstname:</label>
						<div class="input_element">
							<input type="text" class="text" name="billingAddressFirstname" value="${shoppingCart.billingAddressFirstname}" id="billing_address_firstname" />
						</div>
					</div>
					<div class="form_element">
						<label for="billing_address_lastname">Lastname:</label>
						<div class="input_element">
							<input type="text" class="text" name="billingAddressLastname" value="${shoppingCart.billingAddressLastname}" id="billing_address_lastname" />
						</div>
					</div>
					<div class="form_element">
						<label for="billing_address_street">Address:</label>
						<div class="input_element">
							<input type="text" class="text" name="billingAddressStreet" value="${shoppingCart.billingAddressStreet}" id="billing_address_street" />
						</div>
					</div>
					<div class="form_element">
						<label for="billing_address_zip">Zip:</label>
						<div class="input_element">
							<input type="text" class="text" name="billingAddressZip" value="${shoppingCart.billingAddressZip}" id="billing_address_zip" style="width: 60px;" />
						</div>
					</div>
					<div class="form_element">
						<label for="billing_address_city">City:</label>
						<div class="input_element">
							<input type="text" class="text" name="billingAddressCity" value="${shoppingCart.billingAddressCity}" id="billing_address_city" />
						</div>
					</div>
					<div class="form_element">
						<label for="billing_address_phone">Phone:</label>
						<div class="input_element">
							<input type="text" class="text" name="billingAddressPhone" value="${shoppingCart.billingAddressPhone}" id="billing_address_phone" />
						</div>
					</div>
					<div class="form_element">
						<label for="billing_address_mail">Email:</label>
						<div class="input_element">
							<input type="text" class="text" name="billingAddressMail" value="${shoppingCart.billingAddressMail}" id="billing_address_mail" />
						</div>
					</div>
				</div>
				<h2>Shipping address</h2>
				<input type="checkbox"  name="shippingSameAsBilling" value="true" id="shipping_same_as_billing" onchange="toggleShippingAddress();" /> <label for="shipping_same_as_billing" style="float: none;">Shipping address same as billing address</label>
				<div id="shipping_address" style="display: none;">
					<input type="hidden" name="update" value="shippingAddress" />
					<div class="form_element">
						<label for="shipping_address_company">Company:</label>
						<div class="input_element">
							<input type="text" class="text" name="shippingAddressCompany" value="${shoppingCart.shippingAddressCompany}" id="shipping_address_company" />
						</div>
					</div>
					<div class="form_element">
						<label for="shipping_address_firstname">Firstname:</label>
						<div class="input_element">
							<input type="text" class="text" name="shippingAddressFirstname" value="${shoppingCart.shippingAddressFirstname}" id="shipping_address_firstname" />
						</div>
					</div>
					<div class="form_element">
						<label for="shipping_address_lastname">Lastname:</label>
						<div class="input_element">
							<input type="text" class="text" name="shippingAddressLastname" value="${shoppingCart.shippingAddressLastname}" id="shipping_address_lastname" />
						</div>
					</div>
					<div class="form_element">
						<label for="shipping_address_street">Address:</label>
						<div class="input_element">
							<input type="text" class="text" name="shippingAddressStreet" value="${shoppingCart.shippingAddressStreet}" id="shipping_address_street" />
						</div>
					</div>
					<div class="form_element">
						<label for="shipping_address_zip">Zip:</label>
						<div class="input_element">
							<input type="text" class="text" name="shippingAddressZip" value="${shoppingCart.shippingAddressZip}" id="shipping_address_zip" style="width: 60px;" />
						</div>
					</div>
					<div class="form_element">
						<label for="shipping_address_city">City:</label>
						<div class="input_element">
							<input type="text" class="text" name="shippingAddressCity" value="${shoppingCart.shippingAddressCity}" id="shipping_address_city" />
						</div>
					</div>
					<div class="form_element">
						<label for="shipping_address_phone">Phone:</label>
						<div class="input_element">
							<input type="text" class="text" name="shippingAddressPhone" value="${shoppingCart.shippingAddressPhone}" id="shipping_address_phone" />
						</div>
					</div>
					<div class="form_element">
						<label for="shipping_address_mail">Email:</label>
						<div class="input_element">
							<input type="text" class="text" name="shippingAddressMail" value="${shoppingCart.shippingAddressMail}" id="shipping_address_mail" />
						</div>
					</div>
				</div>
				<script type="text/javascript">
$(document).ready(function() {
toggleShippingAddress();
});

function toggleShippingAddress() {
if ($("#shipping_same_as_billing").attr("checked")) {
$("#shipping_address input").attr("disabled", "disabled");
$("#shipping_address").hide('normal');
} else {
$("#shipping_address").show('normal');
$("#shipping_address input").removeAttr("disabled");
}
}
				</script>
			</form>
				<div style="margin: 1em 0;">
					<a href="javascript:document.address_form.submit();" class="button button_continue">Continue</a>
					<div class="cleaner"><!-- --></div>
				</div>
		</c:when>
		<c:otherwise>
			<div class="text">Your shopping cart is empty. Please select from the product categories on the left.</div>
		</c:otherwise>
	</c:choose>

</jsp:root>
