<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="urn:jsptld:cms-taglib"
	xmlns:cmsu="urn:jsptld:cms-util-taglib" 
	xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld"
	xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions" 
	xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt"
	xmlns:ffu="urn:jsptld:ff-util-taglib">
	<jsp:directive.page contentType="text/html; charset=UTF-8" />

	<c:import url="/templates/shop/pages/inc/pageSetup.jsp" />
	<c:import url="/templates/shop/pages/inc/shopPageSetup.jsp" />


	<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="${language}" lang="${language}">
		<head>
			<c:import url="/templates/shop/pages/inc/head.jsp" />
		</head>
		<body>
			<cms:mainBar dialog="shopPageProperties" />	
			<div id="panel">
				<c:import url="/templates/shop/pages/inc/header.jsp" />
				<div id="body">
					<div class="leftcol">
						<c:set var="paragraphs" value="shopProdCatNav" />
						<cms:contentNodeIterator contentNodeCollectionName="navColParagraphs" varStatus="navColStatus">
							<cms:editBar adminOnly="true" />
							<c:set var="paragraphIndex" value="${navColStatus.index}" scope="request" />
							<cms:includeTemplate />
						</cms:contentNodeIterator>
						<div style="height: 50px;"><!-- --></div>
						<cms:newBar contentNodeCollectionName="navColParagraphs" paragraph="${paragraphs}" />
					</div>
					<div class="centercol">
						<c:set var="paragraphs" value="shopText, shopProdCatBreadcrumb, shopProductListing, shopCart, shopCheckoutBreadcrumb, shopAddressForm, shopOrderValidation, shopOrderConfirmation" />
						<cms:contentNodeIterator contentNodeCollectionName="mainColParagraphs" varStatus="mainColStatus">
							<cms:editBar adminOnly="true" />
							<c:set var="paragraphIndex" value="${mainColStatus.index}" scope="request" />
							<cms:includeTemplate />
						</cms:contentNodeIterator>
						<div style="clear:both;">
							<cms:newBar contentNodeCollectionName="mainColParagraphs" paragraph="${paragraphs}" />
						</div>
					</div>
					<div class="rightcol">
						<c:set var="paragraphs" value="shopServiceCart, shopCheckoutSteps" />
						<cms:contentNodeIterator contentNodeCollectionName="serviceColParagraphs" varStatus="serviceColStatus">
							<cms:editBar adminOnly="true" />
							<c:set var="paragraphIndex" value="${serviceColStatus.index}" scope="request" />
							<cms:includeTemplate />
						</cms:contentNodeIterator>
						<cms:newBar contentNodeCollectionName="serviceColParagraphs" paragraph="${paragraphs}" />
						<div style="height: 50px;"><!-- --></div>

					</div>
					<div class="cleaner"><!-- --></div>
				</div>
				<c:import url="/templates/shop/pages/inc/footer.jsp" />
			</div>
			<div id="panel_end"><!-- --></div>
		</body>
	</html>
</jsp:root>
