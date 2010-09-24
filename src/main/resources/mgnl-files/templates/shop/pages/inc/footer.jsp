<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="urn:jsptld:cms-taglib"
	xmlns:cmsu="urn:jsptld:cms-util-taglib" 
	xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld"
	xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core"
	xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt" 
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">
	
	<div id="footer">
		<div class="credits">
			<a href="http://www.magnolia-cms.com" target="_blank">Magnolia CMS</a> eShop by <a href="http://www.fastforward.ch" target="_blank">fastforward websolutions</a>
		</div>
		<ul class="footer_nav">
			<c:if test="${not empty imprintPagePath}">
				<li><a href="${pageContext.request.contextPath}${imprintPagePath}.html">Imprint</a></li>
			</c:if>
			<c:if test="${not empty contactPagePath}">
				<li class="last"><a href="${pageContext.request.contextPath}${contactPagePath}.html">Contact</a></li>
			</c:if>
		</ul>
		<div class="cleaner"><!-- --></div>
	</div>

</jsp:root>