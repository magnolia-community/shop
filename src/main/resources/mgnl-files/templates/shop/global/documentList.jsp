<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="urn:jsptld:cms-taglib"
	xmlns:cmsu="urn:jsptld:cms-util-taglib" 
	xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld"
	xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core"
	xmlns:fmt="urn:jsptld:http://java.sun.com/jstl/fmt" 
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">
	<jsp:directive.page import="java.util.Iterator" />
	<jsp:directive.page import="java.util.ArrayList" />
	<jsp:directive.page import="java.util.Collection" />
	<jsp:directive.page import="info.magnolia.cms.core.Content" />
	<jsp:directive.page import="info.magnolia.module.dms.beans.Document" />
		
	<!-- Ugly hack to get rid of a dependency - replace with propper imaging module use in stk template! -->
	<c:if test="${not empty param['dmsNodeUUID'] and not empty param['varName']}">
		<!-- Does anyone know how to access non-request param values in scriptlets? Well, neither does Google so since this is a temporary fix we'll just store the variable name in a page context attribute... -->
		<c:set var="varName" value="${param['varName']}" />
		<!-- get the dms node - folder or file -->
		<c:set var="queryString">/jcr:root//*[@jcr:uuid = '${param['dmsNodeUUID']}']</c:set>
		<cms:query repository="dms" query="${queryString}" type="xpath" nodeType="nt:base" var="matching" />
		<c:if test="${fn:length(matching) gt 0}">
			<!-- Since we searched by the uuid the count is either 0 or 1. If something is found, make sure it's a file. If a folder was provided, get all files from that folder. -->
			<cms:out nodeDataName="type" contentNode="${matching[0]}" var="type" />
			<c:if test="${type eq 'folder'}">
				<!-- we're not interested in the folder, but in all images of that folder -->
				<c:set var="queryString">/jcr:root//*[@jcr:uuid = '${param['dmsNodeUUID']}']/element(*,mgnl:contentNode)[@type]</c:set>
				<cms:query repository="dms" query="${queryString}" type="xpath" nodeType="nt:base" var="matching" />
			</c:if>
			<!-- now convert the dms nodes to DMS Document objects and store them in a request variable -->
			<jsp:scriptlet><![CDATA[
			
Iterator dmsNodesIter = ((Collection) pageContext.getAttribute("matching")).iterator();
ArrayList docsList = new ArrayList();
while (dmsNodesIter.hasNext()) {
	docsList.add(new Document((Content) dmsNodesIter.next()));
}
request.setAttribute((String) pageContext.getAttribute("varName"), docsList);
request.setAttribute("foo", "bar");

			]]></jsp:scriptlet>
		</c:if>
	</c:if>

</jsp:root>
