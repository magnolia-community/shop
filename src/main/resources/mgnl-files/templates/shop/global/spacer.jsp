<jsp:root version="1.2"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:cms="urn:jsptld:cms-taglib"
	xmlns:cmsu="urn:jsptld:cms-util-taglib"
	xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core">
	<jsp:directive.page contentType="text/html; charset=UTF-8" session="false" />

	<!-- empty lines -->
	<cms:out nodeDataName="spacer" var="spacer" />
	<c:choose>
		<c:when test="${empty spacer or spacer eq 0}">
			<div class="cleaner"><!-- --></div>
		</c:when>
		<c:otherwise>
			<div style="clear: both; height: ${spacer}em;"><!-- --></div>
		</c:otherwise>
	</c:choose>

</jsp:root>
