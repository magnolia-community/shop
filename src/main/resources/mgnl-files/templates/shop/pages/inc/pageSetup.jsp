<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="urn:jsptld:cms-taglib"
	xmlns:cmsu="urn:jsptld:cms-util-taglib" 
	xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld"
	xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core"
	xmlns:fmt="urn:jsptld:http://java.sun.com/jstl/fmt" 
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">

	<jsp:directive.page import="info.magnolia.context.MgnlContext" />
	<jsp:directive.page import="info.magnolia.cms.core.Content" />
		
	<cms:adminOnly>
		<c:set var="admin" value="true" scope="request" />
	</cms:adminOnly>

	<!-- exposes the current node for use with jstl -->
	<cms:setNode var="pageProperties" scope="request" />

	<!-- get the links for service nav -->
	<cms:out nodeDataName="contactPage" uuidToLink="handle" var="contactPagePath" scope="request" inherit="true" />
	<cms:out nodeDataName="imprintPage" uuidToLink="handle" var="imprintPagePath" scope="request" inherit="true" />
	
	<jsp:scriptlet><![CDATA[
		// Ugly fix for http://jira.magnolia-cms.com/browse/MAGNOLIA-2831
		Content currentPage = MgnlContext.getAggregationState().getCurrentContent();
		while (!currentPage.getItemType().getSystemName().equals("mgnl:content") && currentPage.getLevel() > 0) {
			currentPage = currentPage.getParent();
		}
		if (currentPage != null && currentPage.getItemType().getSystemName().equals("mgnl:content")) {
			request.setAttribute("currentPage", currentPage);
		}
	]]></jsp:scriptlet>
		
	<jsp:text>
		<![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">]]>
	</jsp:text>

</jsp:root>
