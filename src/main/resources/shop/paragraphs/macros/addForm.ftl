[#macro addForm product model]
<form method="post" enctype="multipart/form-data" >
	<input value="1" name="quantity" type="text"/>
	<input class="button" type="submit" value="Add to cart" />
	<input value="addToCart" name="command" type="hidden"/>
	<input value="${product.@uuid}" name="product" type="hidden"/>
</form>
[/#macro]