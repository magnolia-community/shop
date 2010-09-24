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
			<c:if test="${not empty errors}">
				<div class="text error"><c:out value="${errors}" /></div>
			</c:if>
			<div class="text order_addresses">
				<div style="float: left; width: 50%;">
					<h2>Billing address</h2>
					<c:if test="${not empty shoppingCart.billingAddressCompany}">
						<div><c:out value="${shoppingCart.billingAddressCompany}" /></div>
					</c:if>
					<c:if test="${not empty shoppingCart.billingAddressFirstname or not empty shoppingCart.billingAddressLastname}">
						<div>
							<c:out value="${shoppingCart.billingAddressFirstname}" />
							<c:if test="${not empty shoppingCart.billingAddressFirstname and not empty shoppingCart.billingAddressLastname}"><jsp:text> </jsp:text></c:if>
							<c:out value="${shoppingCart.billingAddressLastname}" />
						</div>
					</c:if>
					<c:if test="${not empty shoppingCart.billingAddressCity}">
						<div>
							<c:if test="${not empty shoppingCart.billingAddressZip}">
								<c:out value="${shoppingCart.billingAddressZip}" /><jsp:text> </jsp:text>
							</c:if>
							<c:out value="${shoppingCart.billingAddressCity}" />
						</div>
					</c:if>
					<div>&amp;nbsp;</div>
					<c:if test="${not empty shoppingCart.billingAddressPhone}">
						<div><c:out value="${shoppingCart.billingAddressPhone}" /></div>
					</c:if>
					<c:if test="${not empty shoppingCart.billingAddressMail}">
						<div><c:out value="${shoppingCart.billingAddressMail}" /></div>
					</c:if>
				</div>
				<div style="float: right; width: 50%;">
					<h2>Shipping address</h2>
					<c:if test="${not empty shoppingCart.shippingAddressCompany}">
						<div><c:out value="${shoppingCart.shippingAddressCompany}" /></div>
					</c:if>
					<c:if test="${not empty shoppingCart.shippingAddressFirstname or not empty shoppingCart.shippingAddressLastname}">
						<div>
							<c:out value="${shoppingCart.shippingAddressFirstname}" />
							<c:if test="${not empty shoppingCart.shippingAddressFirstname and not empty shoppingCart.shippingAddressLastname}"><jsp:text> </jsp:text></c:if>
							<c:out value="${shoppingCart.shippingAddressLastname}" />
						</div>
					</c:if>
					<c:if test="${not empty shoppingCart.shippingAddressCity}">
						<div>
							<c:if test="${not empty shoppingCart.shippingAddressZip}">
								<c:out value="${shoppingCart.shippingAddressZip}" /><jsp:text> </jsp:text>
							</c:if>
							<c:out value="${shoppingCart.shippingAddressCity}" />
						</div>
					</c:if>
					<div>&amp;nbsp;</div>
					<c:if test="${not empty shoppingCart.shippingAddressPhone}">
						<div><c:out value="${shoppingCart.shippingAddressPhone}" /></div>
					</c:if>
					<c:if test="${not empty shoppingCart.shippingAddressMail}">
						<div><c:out value="${shoppingCart.shippingAddressMail}" /></div>
					</c:if>
				</div>
				<div class="cleaner"><!-- --></div>
			</div>
			<table class="shoppingcart" cellpadding="0" cellspacing="0">
				<tr>
					<th colspan="2">Product</th>
					<th>Quantity</th>
					<th>Unit price</th>
					<th>Total</th>
				</tr>
				<c:forEach items="${shoppingCart.cartItems}" var="currentCartItem">
					<c:set var="imagesUUID" value="" />
					<c:set var="productImagesList" value="" scope="request" />
					<cms:out nodeDataName="imagesUUID" contentNode="${currentCartItem.product}" var="imagesUUID" />
					<c:if test="${not empty imagesUUID}">
						<c:import url="/templates/shop/global/documentList.jsp">
							<c:param name="dmsNodeUUID" value="${imagesUUID}" />
							<c:param name="varName" value="productImagesList" />
						</c:import>
					</c:if>
					<tr>
						<td>
							<c:choose>
								<c:when test="${fn:length(productImagesList) gt 0}">
									<img src="${pageContext.request.contextPath}/dms${productImagesList[0].link}" alt="" border="0" class="productImage" />
								</c:when>
								<c:otherwise>
									<img src="${pageContext.request.contextPath}/docroot/shop/images/box.gif" alt="Kein Bild" border="0" class="productImage" />
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<h4><c:out value="${currentCartItem.productTitle}" /></h4>
							<c:if test="${not empty currentCartItem.productSubTitle}">
								<h5><c:out value="${currentCartItem.productSubTitle}" /></h5>
							</c:if>
							(Product#: <c:out value="${currentCartItem.productNumber}" />)
						</td>
						<td class="quantity">
							<c:out value="${currentCartItem.quantity}" />
						</td>
						<td class="unitprice"><fmt:formatNumber value="${currentCartItem.unitPrice}" pattern="0.00" /></td>
						<td class="total">
							<fmt:formatNumber value="${currentCartItem.itemTotal}" pattern="0.00" />
							<div class="tax">
								(<c:choose>
									<c:when test="${shoppingCart.taxIncluded}">inkl.</c:when>
									<c:otherwise>exkl.</c:otherwise>
								</c:choose>
								<jsp:text> </jsp:text><fmt:formatNumber value="${currentCartItem.itemTax}" pattern="0.00" /> VAT)
							</div>
						</td>
					</tr>
				</c:forEach>
				<tr class="total">
					<td colspan="4">
						<cms:out nodeDataName="currencyUUID" uuid="${shoppingCart.priceCategoryUUID}" repository="data" var="currencyUUID" />
						Total products in <cms:out nodeDataName="name" uuid="${currencyUUID}" repository="data" />
						excl. VAT
					</td>
					<td>
						<fmt:formatNumber value="${shoppingCart.grossTotalExclTax}" pattern="0.00" />
					</td>
				</tr>
				<tr class="total">
					<td colspan="4">
						Shipping &amp; handling
					</td>
					<td>
						<fmt:formatNumber value="0" pattern="0.00" />
					</td>
				</tr>
				<tr class="total">
					<td colspan="4">
						VAT
					</td>
					<td>
						<fmt:formatNumber value="${shoppingCart.itemTaxTotal}" pattern="0.00" />
					</td>
				</tr>
				<tr class="total">
					<td colspan="4">
						Grand total in <cms:out nodeDataName="name" uuid="${currencyUUID}" repository="data" />
						incl. VAT
					</td>
					<td>
						<fmt:formatNumber value="${shoppingCart.grossTotalInclTax}" pattern="0.00" />
					</td>
				</tr>
			</table>
			<form name="gtc_form" action="${pageContext.request.contextPath}/.magnolia/pages/shop">
				<input type="hidden" name="command" value="saveAndConfirmOrder" />
				<input type="hidden" name="update" value="gtc" />
				<input type="hidden" name="targetPage" value="${checkoutConfirmationPagePath}.html" />
				<input type="hidden" name="shopKey" value="${shopKey}" />
				<div class="text">
					<input type="checkbox"  name="acceptedGTC" value="true" id="accepted_gtc" /> <label for="accepted_gtc" style="float: none;">I agree with the <a href="#">general terms &amp; conditions</a>.</label>
				</div>
				<div style="margin-bottom: 1em;">
					<a href="javascript:document.gtc_form.submit();" class="button button_continue">Continue</a>
					<div class="cleaner"><!-- --></div>
				</div>
			</form>
		</c:when>
		<c:otherwise>
			<div class="text">Your shopping cart is empty. Please select from the product categories on the left.</div>
		</c:otherwise>
	</c:choose>

</jsp:root>
