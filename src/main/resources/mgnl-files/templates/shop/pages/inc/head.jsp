<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="cms-taglib"
	xmlns:cmsu="cms-util-taglib" 
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">

	<jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />
	
	<!-- character encoding -->
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	
	<!-- page title -->
	<title>${pageProperties.title}</title>

	<!--  add magnolia css and js links -->
	<cms:links adminOnly="true" />
  
	<!-- css -->
	<link rel="stylesheet" href="${pageContext.request.contextPath}/docroot/shop/css/shop.css" type="text/css" />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/docroot/shop/css/prettyPhoto.css" type="text/css" />
	<c:out value="${ie6Crunches}" escapeXml="false" />
	
	<!-- js -->	
	<script src="${pageContext.request.contextPath}/docroot/shop/scripts/jquery-1.3.2.min.js" type="text/javascript" language="javascript">
		//
	</script>
	<script src="${pageContext.request.contextPath}/docroot/shop/scripts/shop.js" type="text/javascript" language="javascript">
		//
	</script>
	<!-- lightbox image gallery -->
	<script src="${pageContext.request.contextPath}/docroot/shop/scripts/plugins/jquery.prettyPhoto.js" type="text/javascript" charset="utf-8">
		//
	</script>

	<script type="text/javascript">
		$(document).ready(function() {
			$("a[rel^='prettyPhoto']").prettyPhoto({
				theme: 'dark_square' // light_rounded / dark_rounded / light_square / light_square
			});
		});
	</script>	
	
	<!-- search engine controll -->
	<meta name="robots" content="index, follow" />
	<meta name="revisit-after" content="5 days" />
	<c:if test="${not empty pageProperties.metaDescription}">
		<meta name="description" content="${pageProperties.metaDescription}" />
	</c:if>
</jsp:root>






	
	
