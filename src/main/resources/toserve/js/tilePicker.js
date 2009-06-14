$(document).ready(function(){
		$("#tabs").tabs();

		var lastClass = ""
		$(".tiles").click(function(e) {
			var offset = $(this).offset();
			var actualY = e.pageY - offset.top;
			var actualX = e.pageX - offset.left;;
			var x = 1 + Math.floor(actualX / 32.0);
			var y = 1 + Math.floor(actualY / 32.0);
			var id = this.id + y + "x" + x;
			if(lastClass != ""){
				$("#preview").removeClass(lastClass);
			}
			$("#preview").addClass(id);
			lastClass = id;
			console.log(id);
			});


		})

