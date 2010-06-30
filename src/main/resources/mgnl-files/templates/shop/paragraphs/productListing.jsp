<jsp:root version="1.2" 
	xmlns:jsp="http://java.sun.com/JSP/Page" 
	xmlns:cms="urn:jsptld:cms-taglib"
    xmlns:cmsu="urn:jsptld:cms-util-taglib" 
	xmlns:cmsfn="http://www.magnolia.info/tlds/cmsfn-taglib.tld"
    xmlns:c="urn:jsptld:http://java.sun.com/jsp/jstl/core" 
	xmlns:fmt="urn:jsptld:http://java.sun.com/jsp/jstl/fmt" 
	xmlns:fn="http://java.sun.com/jsp/jstl/functions"
	xmlns:ffu="urn:jsptld:ff-util-taglib">

	<c:choose>
		<c:when test="${not empty selectedProductCategory}">
			<c:set var="queryString" value="/jcr:root/${shopRootDataPath}/products//element(*,shopProduct)[jcr:contains(productCategoryUUIDs/., '${selectedProductCategory.UUID}')]" />
			<cms:query repository="data" query="${queryString}" type="xpath" nodeType="shopProduct" var="products" />
			<c:choose>
				<c:when test="${empty selectedProduct}">
					<h1><cms:out nodeDataName="title_${language}" contentNode="${selectedProductCategory}" /></h1>
					<c:choose>
						<c:when test="${fn:length(products) gt 0}">
							<div class="text"><c:out value="${fn:length(products)}" /> products found</div>
							<ul class="productlisting">
								<c:forEach items="${products}" var="currProduct">
									<!-- look for product images -->
									<cms:out nodeDataName="imagesUUID" contentNode="${currProduct}" var="imagesUUID" />
									<c:if test="${not empty imagesUUID}">
										<ffu:dmsFileList uuid="${imagesUUID}" repository="dms" var="productImagesList" />
									</c:if>
									<li>
										<c:choose>
											<c:when test="${fn:length(productImagesList) gt 0}">
												<img src="${pageContext.request.contextPath}/dms${productImagesList[0].link}" alt="" border="0" class="productImage" />
											</c:when>
											<c:otherwise>
												<img src="${pageContext.request.contextPath}/docroot/shop/images/box.gif" alt="Kein Bild" border="0" class="productImage" />
											</c:otherwise>
										</c:choose>
										<div class="text">
											<h2><cms:out nodeDataName="title_${language}" contentNode="${currProduct}" /></h2>
											<cms:out nodeDataName="productDescription1_de" contentNode="${currProduct}" />
											<div class="more pngtransparency"><a href="${pageContext.request.contextPath}${shopPagePath}?prodCat=${selectedProductCategory.UUID}&amp;product=${currProduct.UUID}" class="more">More info</a></div>
											<cms:out nodeDataName="currencyUUID" contentNode="${priceCategory}" var="currencyUUID" />
											<c:set var="queryString" value="//*[@jcr:uuid='${currProduct.UUID}']/prices/element(*,mgnl:contentNode)[@priceCategoryUUID = '${priceCategory.UUID}']" />
											<cms:query repository="data" query="${queryString}" type="xpath" nodeType="mgnl:contentNode" var="prices" />
											<div class="fixed_info">
												<c:choose>
													<c:when test="${fn:length(prices) gt 0}">
														<div class="price">
															<cms:out nodeDataName="name" uuid="${currencyUUID}" repository="data" /><jsp:text> </jsp:text><cms:out nodeDataName="price" contentNode="${prices[0]}" var="price" />
															<fmt:formatNumber value="${price}" pattern="#.00" type="currency" />
															<cms:out nodeDataName="taxIncluded" contentNode="${priceCategory}" var="taxIncluded" />
															<cms:out nodeDataName="taxCategoryUUID" contentNode="${currProduct}" var="taxCategoryUUID" /> 																<div class="tax">
																<c:choose>
																	<c:when test="${taxIncluded}">
																		incl.
																	</c:when>
																	<c:otherwise>
																		excl.
																	</c:otherwise>
																</c:choose>
																<cms:out nodeDataName="tax" uuid="${taxCategoryUUID}" repository="data" />% VAT
															</div>
															<form action="${pageContext.request.contextPath}/.magnolia/pages/shop.html" method="POST" class="add_to_cart">
																<input type="text" name="quantity" value="1" class="quantity" /><a href="#" onclick="this.parentNode.submit();" class="add_to_cart button">Add to cart</a>
																<input type="hidden" name="command" value="addToCart" />
																<input type="hidden" name="shopKey" value="${shopKey}" />
																<input type="hidden" name="product" value="${currProduct.UUID}" />
																<div class="cleaner"><!-- --></div>
															</form>
														</div>
													</c:when>
													<c:otherwise>
														No price available
													</c:otherwise>
												</c:choose>
											</div>
										</div>
									</li>
								</c:forEach>
							</ul>
						</c:when>
						<c:otherwise>
							<div class="text">Sorry! No products found in this product category.</div>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<h1><cms:out nodeDataName="title_${language}" contentNode="${selectedProduct}" /></h1>
					<div class="text"><c:out value="${fn:length(products)}" /> products found in category <cms:out nodeDataName="title_${language}" contentNode="${selectedProductCategory}" /></div>
					<div class="productdetails">
						<!-- look for product images -->
						<cms:out nodeDataName="imagesUUID" contentNode="${selectedProduct}" var="imagesUUID" />
						<c:if test="${not empty imagesUUID}">
							<ffu:dmsFileList uuid="${imagesUUID}" repository="dms" var="productImagesList" />
						</c:if>
						<c:choose>
							<c:when test="${fn:length(productImagesList) gt 0}">
								<img src="${pageContext.request.contextPath}/dms${productImagesList[0].link}" alt="" border="0" class="productImage" />
							</c:when>
							<c:otherwise>
								<img src="${pageContext.request.contextPath}/docroot/shop/images/box.gif" alt="Kein Bild" border="0" class="productImage" />
							</c:otherwise>
						</c:choose>
						<div class="description">
							<div class="text">
								<h2><cms:out nodeDataName="title_${language}" contentNode="${selectedProduct}" /></h2>
								<div class="productnumber">Product#: <cms:out nodeDataName="name" contentNode="${selectedProduct}" /><br />&amp;nbsp;</div>
								<cms:out nodeDataName="productDescription1_de" contentNode="${selectedProduct}" />
								<cms:ifNotEmpty nodeDataName="productDescription2_de" contentNode="${selectedProduct}">
									<div><cms:out nodeDataName="productDescription2_de" contentNode="${selectedProduct}" /></div>
								</cms:ifNotEmpty>
							</div>
							<cms:out nodeDataName="currencyUUID" contentNode="${priceCategory}" var="currencyUUID" />
							<c:set var="queryString" value="//*[@jcr:uuid='${selectedProduct.UUID}']/prices/element(*,mgnl:contentNode)[@priceCategoryUUID = '${priceCategory.UUID}']" />
							<cms:query repository="data" query="${queryString}" type="xpath" nodeType="mgnl:contentNode" var="prices" />
							<c:choose>
								<c:when test="${fn:length(prices) gt 0}">
									<div class="price">
										<cms:out nodeDataName="name" uuid="${currencyUUID}" repository="data" /><jsp:text> </jsp:text><cms:out nodeDataName="price" contentNode="${prices[0]}" var="price" />
										<fmt:formatNumber value="${price}" pattern="#.00" type="currency" />
										<cms:out nodeDataName="taxIncluded" contentNode="${priceCategory}" var="taxIncluded" />
										<cms:out nodeDataName="taxCategoryUUID" contentNode="${selectedProduct}" var="taxCategoryUUID" /> 																<div class="tax">
											<c:choose>
												<c:when test="${taxIncluded}">
													incl.
												</c:when>
												<c:otherwise>
													excl.
												</c:otherwise>
											</c:choose>
											<cms:out nodeDataName="tax" uuid="${taxCategoryUUID}" repository="data" />% VAT, plus shipping &amp; handling
										</div>
									</div>
									<form action="${pageContext.request.contextPath}/.magnolia/pages/shop.html" method="POST" class="add_to_cart">
										<input type="text" name="quantity" value="1" class="quantity" /><a href="#" onclick="this.parentNode.submit();" class="add_to_cart">In Warenkorb</a>
										<input type="hidden" name="command" value="addToCart" />
										<input type="hidden" name="shopKey" value="${shopKey}" />
										<input type="hidden" name="product" value="${selectedProduct.UUID}" />
										<div class="cleaner"><!-- --></div>
									</form>
								</c:when>
								<c:otherwise>
									No price available
								</c:otherwise>
							</c:choose>
						</div>
						<div class="cleaner"><!-- --></div>
						<c:if test="${fn:length(productImagesList) gt 1}">
							<div class="gallery">
								<c:forEach items="${productImagesList}" var="currImage" begin="1">
									<a href="${pageContext.request.contextPath}/dms${currImage.link}" class="gallery" rel="prettyPhoto[${selectedProduct.UUID}];options={animate: false, slideshowDelay: 5}" title=""><img class="thumb" src="${pageContext.request.contextPath}/dms${currImage.link}" alt="" /></a>
								</c:forEach>
								<div class="cleaner"><!-- --></div>
							</div>
						</c:if>
					</div>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<h1>Current offers</h1>
			...
		</c:otherwise>
	</c:choose>

</jsp:root>