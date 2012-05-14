
[#if content.termsDocumentUUID?has_content]
	[#assign linkURL = cmsfn.createLink("dms", content.termsDocumentUUID) /]
[/#if]
[#if content.termsPageUUID?has_content]
	[#assign linkURL = cmsfn.createLink("website", content.termsPageUUID) /]
[/#if]

[#if linkURL?has_content]
	[#assign linkStart = "<a href=\"" + linkURL + "\" target=\"_blank\">" /]
	[#assign linkEnd = "</a>" /]
	<div ${model.style!} >
		[#if content.title?has_content]
			<label for="${content.controlName}">
				<span>
				[#if !model.isValid()]
					<em>${i18n['form.error.field']}</em>
				[/#if]
				${cmsfn.encode(content).title!}
				[#if content.mandatory]
					<dfn title="required">${model.requiredSymbol!}</dfn>
				[/#if]
				</span>
			</label>
		[/#if]
		<div class="terms">
			<input type="checkbox" name="${content.controlName}" id="${content.controlName}" value="true" />
			<label for="${content.controlName}">
				${content.checkboxLabel?replace("[link_start]", linkStart)?replace("[link_end]", linkEnd)}
				[#if content.mandatory && !(content.title?has_content)]
					<dfn title="required">${model.requiredSymbol!}</dfn>
				[/#if]
			</label>
		</div>
	</div>
[#elseif cmsfn.editMode]
	<div ${model.style!} >
	</div>
[/#if]