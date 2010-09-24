<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="urn:jsptld:cms-taglib"
    xmlns:cmsu="urn:jsptld:cms-util-taglib" 
    xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" 
	xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt" 
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">

	<cms:out nodeDataName="step" var="step" />
	<ul class="checkout_steps">
		<c:choose>
			<c:when test="${step eq 1}">
				<c:set var="cssClass" value="active" />
			</c:when>
			<c:otherwise>
				<c:set var="cssClass" value="" />
			</c:otherwise>
		</c:choose>
		<li class="${cssClass}">
			<div>
				<c:choose>
					<c:when test="${step lt 4}">
						<a href="${pageContext.request.contextPath}${cartPagePath}.html">Shopping cart</a>
					</c:when>
					<c:otherwise>
						Shopping cart
					</c:otherwise>
				</c:choose>
			</div>
		</li>
		<c:choose>
			<c:when test="${step eq 2}">
				<c:set var="cssClass" value="active" />
			</c:when>
			<c:otherwise>
				<c:set var="cssClass" value="" />
			</c:otherwise>
		</c:choose>
		<li class="${cssClass}">
			<div>
				<c:choose>
					<c:when test="${step gt 1 and step lt 4}">
						<a href="${pageContext.request.contextPath}${cartPagePath}.html">Addresses</a>
					</c:when>
					<c:otherwise>
						Addresses
					</c:otherwise>
				</c:choose>
			</div>
		</li>
		<c:choose>
			<c:when test="${step eq 3}">
				<c:set var="cssClass" value="active" />
			</c:when>
			<c:otherwise>
				<c:set var="cssClass" value="" />
			</c:otherwise>
		</c:choose>
		<li class="${cssClass}">
			<div>
				<c:choose>
					<c:when test="${step eq 3}">
						<a href="${pageContext.request.contextPath}${cartPagePath}.html">Check your order</a>
					</c:when>
					<c:otherwise>
						Check your order
					</c:otherwise>
				</c:choose>
			</div>
		</li>
		<c:choose>
			<c:when test="${step eq 4}">
				<c:set var="cssClass" value="active" />
			</c:when>
			<c:otherwise>
				<c:set var="cssClass" value="" />
			</c:otherwise>
		</c:choose>
		<li class="${cssClass} last">
			<div>
				Confirmation
			</div>
		</li>
	</ul>
		
</jsp:root>
