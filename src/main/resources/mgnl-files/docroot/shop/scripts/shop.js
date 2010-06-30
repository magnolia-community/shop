function toggleCollapsibleParagraph(id) {
	if ($("#" + id).hasClass("open")) {
		$("#" + id).removeClass("open");
		$("#" + id).addClass("closed");
	} else {
		$("#" + id).removeClass("closed");
		$("#" + id).addClass("open");
	}
}

function showPrintDialog() {
	window.print();
}
