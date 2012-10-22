[#macro addForm product model]
<form method="post" enctype="multipart/form-data" >
	[#if optionSets?has_content]
		[#list optionSets as optionSet]
			[#assign options=model.getOptions(optionSet) /]
			[#if options?has_content]
				<div class="option">
					[#if optionSet.title?has_content]
						<label for="option_${cmsfn.asContentMap(optionSet).@name}">${cmsfn.asContentMap(optionSet).title}</label>
					[/#if]
					<select name="option_${cmsfn.asContentMap(optionSet).@uuid}" id="option_${cmsfn.asContentMap(optionSet).@name}">
						[#list options as option]
							<option value="${cmsfn.asContentMap(option).@uuid}">${cmsfn.asContentMap(option).title}</option>
						[/#list]
					</select>
				</div>
			[/#if]
		[/#list]
	[/#if]
	<input value="1" name="quantity" type="text"/>
	<input class="button" type="submit" value="${i18n['add.to.cart']}" />
	<input value="addToCart" name="command" type="hidden"/>
	<input value="${product.@uuid}" name="product" type="hidden"/>
</form>
[/#macro]