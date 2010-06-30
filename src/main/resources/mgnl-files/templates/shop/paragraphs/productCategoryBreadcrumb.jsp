<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="cms-taglib"
	xmlns:cmsu="cms-util-taglib" 
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">

	<c:if test="${fn:length(breadCrumb) gt 0}">
		<div class="breadcrumb">
			<cms:ifNotEmpty nodeDataName="title">
				<div style="float: left;"><cms:out nodeDataName="title" /></div>
			</cms:ifNotEmpty>
			<ul>
				<c:forEach items="${breadCrumb}" var="currProductCategory">
					<!-- check if the current product category is in the bread crumb
					of the selected category - in this case highlight it! -->
					<c:choose>
						<c:when test="${empty selectedProduct and not empty selectedProductCategory and selectedProductCategory.handle eq currProductCategory.handle}">
							<c:set var="liClass" value="active" />
						</c:when>
						<c:when test="${not empty selectedProductCategory and fn:startsWith(selectedProductCategory.handle, currProductCategory.handle)}">
							<c:set var="liClass" value="path" />
						</c:when>
						<c:otherwise>
							<c:set var="liClass" value="" />
						</c:otherwise>
					</c:choose>
					<li class="${liClass}"><a href="${pageContext.request.contextPath}${shopPagePath}.html?prodCat=${currProductCategory.UUID}">/ <cms:out nodeDataName="title_${language}" contentNode="${currProductCategory}" /></a></li>
				</c:forEach>
				<c:if test="${not empty selectedProduct}">
					<li class="active"><a href="${pageContext.request.contextPath}${shopPagePath}.html?prodCat=${currProductCategory.UUID}&amp;product=${selectedProduct.UUID}">/ <cms:out nodeDataName="title_${language}" contentNode="${selectedProduct}" /></a></li>
				</c:if>
			</ul>
			<div class="cleaner"><!-- --></div>
		</div>
	</c:if>

</jsp:root>
