<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="urn:jsptld:cms-taglib"
    xmlns:cmsu="urn:jsptld:cms-util-taglib" 
    xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" 
	xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt" 
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">

	<div class="text">
		<!-- titel -->
		<cms:ifNotEmpty nodeDataName="title">
		<h2><cms:out nodeDataName="title" /></h2>
		</cms:ifNotEmpty>
		
		<!-- text -->
		<cms:out nodeDataName="text" />

	</div>
	<!-- empty lines -->
	<c:import url="/templates/shop/global/spacer.jsp" />	

</jsp:root>