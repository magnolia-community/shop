<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="urn:jsptld:cms-taglib"
    xmlns:cmsu="urn:jsptld:cms-util-taglib" 
	xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld"
    xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" 
	xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt" 
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">

	<c:set var="queryString" value="/jcr:root/${shopDataRootPath}/productcategories/*" />
<!--	<c:if test="${admin}">
		<div class="admin">
			shop data root path: <c:out value="${shopDataRootPath}" /><br />
			<c:out value="${queryString}" />
		</div>
	</c:if>-->
	<cms:query repository="data" query="${queryString}" type="xpath" nodeType="shopProductCategory" var="topLevelProductCategores" />
	<c:if test="${fn:length(topLevelProductCategores) gt 0}">
		<ul class="category_nav">
			<c:forEach items="${topLevelProductCategores}" var="currProductCategory" >
				<!-- check if the current product category is in the bread crumb
				of the selected category - in this case highlight it! -->
				<c:choose>
					<c:when test="${not empty selectedProductCategory and selectedProductCategory.handle eq currProductCategory.handle}">
						<c:set var="liClass" value="active" />
					</c:when>
					<c:when test="${not empty selectedProductCategory and fn:startsWith(selectedProductCategory.handle, currProductCategory.handle)}">
						<c:set var="liClass" value="path" />
					</c:when>
					<c:otherwise>
						<c:set var="liClass" value="" />
					</c:otherwise>
				</c:choose>
				<li class="${liClass}">
				<a href="${pageContext.request.contextPath}${shopPagePath}.html?prodCat=${currProductCategory.UUID}"><cms:out nodeDataName="title_${language}" contentNode="${currProductCategory}" /></a></li>
				<c:if test="${liClass eq 'active' or liClass eq 'path'}">
					<!-- get second level product categories -->
					<cms:query repository="data" query="/jcr:root${currProductCategory.handle}/*" type="xpath" nodeType="shopProductCategory" var="secondLevelProductCategores" />
					<c:if test="${fn:length(secondLevelProductCategores) gt 1}">
						<ul>
							<c:forEach items="${secondLevelProductCategores}" begin="1" var="currProductCategory" >
								<!-- check if the current product category is in the bread crumb
								of the selected category - in this case highlight it! -->
								<c:choose>
									<c:when test="${not empty selectedProductCategory and selectedProductCategory.handle eq currProductCategory.handle}">
										<c:set var="liClass" value="active" />
									</c:when>
									<c:when test="${not empty selectedProductCategory and fn:startsWith(selectedProductCategory.handle, currProductCategory.handle)}">
										<c:set var="liClass" value="path" />
									</c:when>
									<c:otherwise>
										<c:set var="liClass" value="" />
									</c:otherwise>
								</c:choose>
								<li class="${liClass}"><a href="${pageContext.request.contextPath}${shopPagePath}.html?prodCat=${currProductCategory.UUID}"><cms:out nodeDataName="title_${language}" contentNode="${currProductCategory}" /></a></li>
							</c:forEach>
						</ul>
					</c:if>
				</c:if>
			</c:forEach>
		</ul>
		<c:if test="${admin}">
			<div class="admin">
				<i>Test-Infos:</i><br />
				<b>Preiskategorie:</b><br /><cms:out nodeDataName="title_de" contentNode="${priceCategory}" /><br />
				<b>Sprache:</b><br />
				<c:out value="${language}" />								
			</div>
		</c:if>
	</c:if>

</jsp:root>
									