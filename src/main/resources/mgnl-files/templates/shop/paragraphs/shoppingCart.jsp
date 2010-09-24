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
			<div class="text"><c:out value="${shoppingCart.cartItemsCount}" /> products in shopping cart</div>
			<form name="cart_form" action="${pageContext.request.contextPath}/.magnolia/pages/shop">
				<input type="hidden" name="command" value="updateCartItemsQuantityByProductUUIDs" />
				<input type="hidden" name="shopKey" value="${shopKey}" />
				<table class="shoppingcart" cellpadding="0" cellspacing="0">
					<tr>
						<th colspan="2">Product</th>
						<th>Quantity</th>
						<th>Unit price</th>
						<th>Total</th>
						<th><!-- --></th>
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
								<div class="productnumber">(Product#: <c:out value="${currentCartItem.productNumber}" />)</div>
							</td>
							<td class="quantity">
								<input type="text" name="quantity_${currentCartItem.productUUID}" value="${currentCartItem.quantity}" />
							</td>
							<td class="unitprice"><fmt:formatNumber value="${currentCartItem.unitPrice}" pattern="0.00" /></td>
							<td class="total">
								<fmt:formatNumber value="${currentCartItem.itemTotal}" pattern="0.00" />
								<div class="tax">
									(<c:choose>
										<c:when test="${shoppingCart.taxIncluded}">incl.</c:when>
										<c:otherwise>exkl.</c:otherwise>
									</c:choose>
									<jsp:text> </jsp:text><fmt:formatNumber value="${currentCartItem.itemTax}" pattern="0.00" /> VAT)
								</div>
							</td>
							<td><a href="${pageContext.request.contextPath}/.magnolia/pages/shop?shopKey=${shopKey}&amp;command=removeFromCart&amp;product=${currentCartItem.productUUID}" title="Artikel aus Warenkorb löschen"><img src="${pageContext.request.contextPath}/docroot/shop/images/Trash_32x32.png" alt="Artikel aus Warenkorb löschen" border="0" /></a></td>
						</tr>
					</c:forEach>
					<tr class="total">
						<td colspan="3">
							<a href="javascript:document.cart_form.submit();" class="refresh button">Update cart</a>
						</td>
						<td colspan="3">
							&amp;nbsp;
						</td>
					</tr>
					<tr class="total">
						<td colspan="4">
							<cms:out nodeDataName="currencyUUID" uuid="${shoppingCart.priceCategoryUUID}" repository="data" var="currencyUUID" />
							Total products in <cms:out nodeDataName="name" uuid="${currencyUUID}" repository="data" />
							excl. VAT
						</td>
						<td>
							<fmt:formatNumber value="${shoppingCart.grossTotalExclTax}" pattern="0.00" />
						</td>
						<td><!-- --></td>
					</tr>
					<tr class="total">
						<td colspan="4">
							Shipping &amp; handling
						</td>
						<td>
							<fmt:formatNumber value="0" pattern="0.00" />
						</td>
						<td><!-- --></td>
					</tr>
					<tr class="total">
						<td colspan="4">
							VAT
						</td>
						<td>
							<fmt:formatNumber value="${shoppingCart.itemTaxTotal}" pattern="0.00" />
						</td>
						<td><!-- --></td>
					</tr>
					<tr class="total">
						<td colspan="4">
							Grand total
						</td>
						<td>
							<fmt:formatNumber value="${shoppingCart.grossTotalInclTax}" pattern="0.00" />
						</td>
						<td><!-- --></td>
					</tr>
				</table>
				<div style="margin-bottom: 1em;">
					<a href="${pageContext.request.contextPath}${checkoutAddressPagePath}.html" class="button button_continue">Continue</a>
					<div class="cleaner"><!-- --></div>
				</div>
			</form>
		</c:when>
		<c:otherwise>
			<div class="text">Your shopping cart is empty. Please select from the product categories on the left.</div>
		</c:otherwise>
	</c:choose>

		
</jsp:root>
