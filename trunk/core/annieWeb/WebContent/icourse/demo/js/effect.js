function hover(){
	$("#contact").hover(function(){
			$(".edge").stop().fadeIn();
		}, function() {
			$(".edge").stop().fadeOut();
	});
}