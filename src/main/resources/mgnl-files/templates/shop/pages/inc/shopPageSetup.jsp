<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="urn:jsptld:cms-taglib"
	xmlns:cmsu="urn:jsptld:cms-util-taglib" 
	xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld"
	xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core"
	xmlns:fmt="urn:jsptld:http://java.sun.com/jstl/fmt" 
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">
	
	<jsp:directive.page import="info.magnolia.cms.link.LinkHelper" />
	<jsp:directive.page import="info.magnolia.context.MgnlContext" />
	<jsp:directive.page import="info.magnolia.cms.security.User" />
	<jsp:directive.page import="info.magnolia.cms.core.Content" />
	<jsp:directive.page import="info.magnolia.cms.util.ContentUtil" />
	<jsp:directive.page import="info.magnolia.cms.util.NodeDataUtil" />
	<jsp:directive.page import="java.util.Date" />
	<jsp:directive.page import="java.util.ArrayList" />
	<jsp:directive.page import="org.apache.commons.lang.StringUtils" />
	<jsp:directive.page import="info.magnolia.module.shop.beans.DefaultShoppingCart" />
	
	<jsp:declaration><![CDATA[
		private static final String shopName = "sampleShop";
		private String languageKey = "en";
		private String priceCategoryName = "standard-price";
		
	]]></jsp:declaration>

	<jsp:scriptlet><![CDATA[		
		// 1. get the configuration
		Content shopConfigNode = ContentUtil.getContent("config", "/modules/shop/config/shops/"+shopName);
		request.setAttribute("shopConfig", shopConfigNode);
		String shopRootDataPath = NodeDataUtil.getString(shopConfigNode, "shopDataRootPath");
		request.setAttribute("shopDataRootPath", shopRootDataPath);
		request.setAttribute("shopKey", shopName);

		// 2. get the price category
		String priceCategoryPath = shopRootDataPath + "/pricecategories/" + priceCategoryName;
		System.out.println("priceCategoryPath: "+priceCategoryPath);
		Content priceCategory = ContentUtil.getContent("data", priceCategoryPath);
		System.out.println("priceCategory: "+priceCategory);
		if (priceCategory != null) {
			request.setAttribute("priceCategory", priceCategory);
		}

		// 3. get the shopping cart		
		DefaultShoppingCart cart = (DefaultShoppingCart) session.getAttribute("shoppingCart");
		if (cart == null) {
			cart = new DefaultShoppingCart(priceCategory);
			cart.setLanguage(languageKey);
			session.setAttribute("shoppingCart", cart);
		}
		
		// 4. get the product category
		String productCategoryUUID = request.getParameter("prodCat");
		Content selectedProductCategory = null;
		if (StringUtils.isNotBlank(productCategoryUUID)) {
			selectedProductCategory = ContentUtil.getContentByUUID("data", productCategoryUUID);
			request.setAttribute("selectedProductCategory", selectedProductCategory);
		}

		// 5. get the product
		Content selectedProduct = null;
		String productUUID = request.getParameter("product");
		if (StringUtils.isNotBlank(productUUID)) {
			selectedProduct = ContentUtil.getContentByUUID("data", productUUID);
		}
		request.setAttribute("selectedProduct", selectedProduct);
		
		// 6. prepare the category breadcrumb
		ArrayList breadCrumb = new ArrayList();
		if (selectedProductCategory != null) {
			Content helperCategory = selectedProductCategory;
			while (helperCategory.getHandle().startsWith(shopRootDataPath + "/productcategories/") && helperCategory.getLevel() > 1) {
				breadCrumb.add(0, helperCategory);
				helperCategory = helperCategory.getParent();
			}
		}
		request.setAttribute("breadCrumb", breadCrumb);		

		request.setAttribute("language", languageKey);
	]]></jsp:scriptlet>

	<!-- get the links for service nav -->
	<cms:out nodeDataName="shopPage" uuidToLink="handle" var="shopPagePath" scope="request" inherit="true" />
	<cms:out nodeDataName="cartPage" uuidToLink="handle" var="cartPagePath" scope="request" inherit="true" />
	<cms:out nodeDataName="checkoutAddressPage" uuidToLink="handle" var="checkoutAddressPagePath" scope="request" inherit="true" />
	<cms:out nodeDataName="checkoutConfirmPage" uuidToLink="handle" var="checkoutConfirmPagePath" scope="request" inherit="true" />
	<cms:out nodeDataName="checkoutConfirmationPage" uuidToLink="handle" var="checkoutConfirmationPagePath" scope="request" inherit="true" />

</jsp:root>
