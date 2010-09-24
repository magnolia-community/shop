<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="urn:jsptld:cms-taglib"
	xmlns:cmsu="urn:jsptld:cms-util-taglib" 
	xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld"
	xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core"
	xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt" 
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">
	
	<div id="header">
		<a href="http://www.magnolia-cms.com" taget="_blank" id="logo"><img src="${pageContext.request.contextPath}/docroot/shop/images/magnolia_shop_logo.png" border="0" alt="Logo Magnolia eShop" /></a>
		<c:import url="/templates/shop/pages/inc/mainnav.jsp" />
	</div>

</jsp:root>