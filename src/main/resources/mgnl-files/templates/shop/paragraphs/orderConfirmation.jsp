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
		<c:when test="${not empty lastCartUUID}">
			<div class="text">The order has been saved. Your order number is: <b><cms:out nodeDataName="name" uuid="${lastCartUUID}" repository="data" /></b></div>
		</c:when>
		<c:otherwise>
			<div class="text">No order found!</div>
		</c:otherwise>
	</c:choose>

</jsp:root>
