<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="urn:jsptld:cms-taglib"
    xmlns:cmsu="urn:jsptld:cms-util-taglib" 
    xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" 
	xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt" 
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:ffu="urn:jsptld:ff-util-taglib">

	<div class="shoppingcart">
		<cms:ifNotEmpty nodeDataName="title">
			<h3><cms:out nodeDataName="title" /></h3>
		</cms:ifNotEmpty>
		<c:choose>
			<c:when test="${not empty shoppingCart and shoppingCart.cartItemsCount gt 0}">
				<c:set var="total" value="0" />
				<ul class="shoppingcart_small">
					<c:forEach items="${shoppingCart.cartItems}" var="currentCartItem">
						<c:set var="imagesUUID" value="" />
						<c:set var="productImagesList" value="" />
						<cms:out nodeDataName="imagesUUID" contentNode="${currentCartItem.product}" var="imagesUUID" />
						<c:if test="${not empty imagesUUID}">
							<ffu:dmsFileList uuid="${imagesUUID}" repository="dms" var="productImagesList" />
						</c:if>
						<li>
							<c:choose>
								<c:when test="${fn:length(productImagesList) gt 0}">
									<img src="${pageContext.request.contextPath}/dms${productImagesList[0].link}" alt="" border="0" class="productImage" />
								</c:when>
								<c:otherwise>
									<img src="${pageContext.request.contextPath}/docroot/shop/images/box.gif" alt="Kein Bild" border="0" class="productImage" />
								</c:otherwise>
							</c:choose>
							<div class="text">
								<h4><c:out value="${currentCartItem.quantity}" /><jsp:text> </jsp:text><cms:out nodeDataName="title_${language}" contentNode="${currentCartItem.product}" /></h4>
							</div>
							<div class="cleaner"><!-- --></div>
						</li>
					</c:forEach>
				</ul>
				<div class="total text">
					<cms:out nodeDataName="currencyUUID" contentNode="${priceCategory}" var="currencyUUID" />
					Total: <cms:out nodeDataName="name" uuid="${currencyUUID}" repository="data" /><jsp:text> </jsp:text><fmt:formatNumber pattern="0.00" value="${shoppingCart.grossTotal}" /><br />
					<c:choose>
						<c:when test="${shoppingCart.taxIncluded}">
							incl. VAT
						</c:when>
						<c:otherwise>
							excl. VAT
						</c:otherwise>
					</c:choose>
				</div>
				<cms:out nodeDataName="checkoutPage" uuidToLink="handle" var="checkoutPagePath" />
				<a href="${pageContext.request.contextPath}${cartPagePath}.html" class="cart button">Show cart</a>
			</c:when>
			<c:otherwise>
				<div class="text">Your shopping cart is empty.</div>
			</c:otherwise>
		</c:choose>
	</div>

		
</jsp:root>
