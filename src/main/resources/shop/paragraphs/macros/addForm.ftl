[#macro addForm product model]
<form method="post" enctype="multipart/form-data" >
	[#if optionSets?has_content]
		[#list optionSets as optionSet]
			[#assign options=model.getOptions(optionSet) /]
			[#if options?has_content]
				<div class="option">
					[#if optionSet.title?has_content]
						<label for="option_${optionSet.@name}">${optionSet.title}</label>
					[/#if]
					<select name="option_${optionSet.@uuid}" id="option_${optionSet.@name}">
						[#list options as option]
							<option value="${option.@uuid}">${option.title}</option>
						[/#list]
					</select>
				</div>
			[/#if]
		[/#list]
	[/#if]
	<input value="1" name="quantity" type="text"/>
	<input class="button" type="submit" value="Add to cart" />
	<input value="addToCart" name="command" type="hidden"/>
	<input value="${product.@uuid}" name="product" type="hidden"/>
</form>
[/#macro]