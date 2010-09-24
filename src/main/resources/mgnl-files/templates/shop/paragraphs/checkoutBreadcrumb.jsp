<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="urn:jsptld:cms-taglib"
    xmlns:cmsu="urn:jsptld:cms-util-taglib" 
    xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" 
	xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt" 
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">

	<div class="breadcrumb">
		<div style="float: left;"><cms:out nodeDataName="title" /></div>
		<cms:out nodeDataName="step" var="step" />
		<ul>
			<c:choose>
				<c:when test="${step eq 1}">
					<c:set var="cssClass" value="active" />
				</c:when>
				<c:otherwise>
					<c:set var="cssClass" value="" />
				</c:otherwise>
			</c:choose>
			<li class="${cssClass}"><a href="${pageContext.request.contextPath}${cartPagePath}.html">Shopping cart</a></li>
			<c:if test="${step gt 1}">
				<c:choose>
					<c:when test="${step eq 2}">
						<c:set var="cssClass" value="active" />
					</c:when>
					<c:otherwise>
						<c:set var="cssClass" value="" />
					</c:otherwise>
				</c:choose>
				<li class="${cssClass}"><a href="${pageContext.request.contextPath}${checkoutAddressPagePath}.html"> &amp;gt; Adresses</a></li>
			</c:if>
			<c:if test="${step gt 2}">
				<c:choose>
					<c:when test="${step eq 3}">
						<c:set var="cssClass" value="active" />
					</c:when>
					<c:otherwise>
						<c:set var="cssClass" value="" />
					</c:otherwise>
				</c:choose>
				<li class="${cssClass}"><a href="${pageContext.request.contextPath}${checkoutConfirmPagePath}.html"> &amp;gt; Check your order</a></li>
			</c:if>
			<c:if test="${step gt 3}">
				<c:choose>
					<c:when test="${step eq 4}">
						<c:set var="cssClass" value="active" />
					</c:when>
					<c:otherwise>
						<c:set var="cssClass" value="" />
					</c:otherwise>
				</c:choose>
				<li class="${cssClass}"><a href="${pageContext.request.contextPath}${checkoutConfirmPagePath}.html"> &amp;gt; Confirmation</a></li>
			</c:if>
		</ul>
		<div class="cleaner"><!-- --></div>
	</div>
		
</jsp:root>
