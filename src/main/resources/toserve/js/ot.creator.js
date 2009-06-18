/* 
 * File        : ot.creator.js
 * Author      : Bjarte Stien Karlsen
 * Copyright   : (c) 2009
 *               Do not use (or abuse) without permission
 */
window.status = 'Loading [ot.creator.js]';
window.ot = window.ot || { VERSION: '1.0' };

ot.MapCreator = function(options) {
	this.map = new ot.Map(options);
	that = this;

    $("#tabs").tabs();

		this.previewTile = "";

    $(".tiles").click(function(e) {
				var offset = $(this).offset();
				var actualY = e.pageY - offset.top;
				var actualX = e.pageX - offset.left;;
				var x = 1 + Math.floor(actualX / 32.0);
				var y = 1 + Math.floor(actualY / 32.0);
				that.previewTile =  this.id + y + "x" + x;

				$(".ui-selected").each(function() {
					var element = $(this);
					var id = element.attr("id");
					var t = that.map.grid[id];
					if(t.tile == that.previewTile){
								return this;
					}
					element.removeClass(t.tile);
					t.tile = that.previewTile;
					element.addClass(that.previewTile);
				});

				$("#save").show();
    });

		$("#save").click(function() {
				$("#ajax-loader").show();
			 $.ajax({
                type: "POST",
                url: "http://" + window.location.host + "/api/creator/" + that.map.options.id,
								data: { 'grid' : JSON.stringify(that.map.grid), 'rows' : that.map.options.rows, 'cols' : that.map.options.cols },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
									console.log(textStatus)
									console.log(errorThrown)
									$("#ajax-loader").hide();
                },
                success: function(msg){
									$("#save").hide();
									$("#ajax-loader").hide();
                }
       });
		});

    $("#addcol").click(function(){

				var numCols = that.map.addColumn();
        if(numCols == 2){
            $("#rmcol").removeAttr("disabled");
         }
				that.selectable();
				$("#save").show();
     });


    $("#addrow").click(function(){
				var numRows = that.map.addRow();
        if(numRows == 2){
            $("#rmrow").removeAttr("disabled");
         }
				that.selectable();
				$("#save").show();
     });

  	$("#rmcol").click(function() {
				
				var numCols = that.map.removeColumn();
        if(numCols == 1){
            $("#rmcol").attr("disabled", "true");
         }
				that.selectable();
				$("#save").show();
     });


    $("#rmrow").click(function() {
				var numRows = that.map.removeRow();
        if(numRows == 1){
            $("#rmrow").attr("disabled", "true");
         }
				that.selectable();
				$("#save").show();
     });

    $("#clear").click(function(e) {
        $(".cell").removeClass("ui-selected");
    });

		$("#inspector").dialog({ 
				position:  ['right','top'],
				width: 330,
				autoOpen: false
		});

		$("#inspect").click(function(e) {
			var i = $("#inspector");
			if(i.dialog('isOpen')){
				i.dialog("close");
			} else {
				i.dialog("open");
			}
		})
	
		$(".fg-button:not(.ui-state-disabled)").hover(function(){
					 $(this).addClass("ui-state-hover");
					 },
					 function(){
					 $(this).removeClass("ui-state-hover");
					 }
					 )
			 .mousedown(function(){
					 $(this).parents('.fg-buttonset-single:first').find(".fg-button.ui-state-active").removeClass("ui-state-active");
					 if( $(this).is('.ui-state-active.fg-button-toggleable, .fg-buttonset-multi .ui-state-active') ){ $(this).removeClass("ui-state-active"); }
					 else { $(this).addClass("ui-state-active"); }
					 })
			 .mouseup(function(){
					 if(! $(this).is('.fg-button-toggleable, .fg-buttonset-single .fg-button, .fg-buttonset-multi .fg-button') ){
					 $(this).removeClass("ui-state-active");
					 }
					 });
		

		$("#reset").click(function(e) {
		   $(".ui-selected").each(function() {
            if(this.nodeName == "div") {
                var id = $(this).attr("id");
								that.map.reset(id);
            }
			});
			 that.map.paint();
			 that.selectable();

		});

		//Lock/unlock all marked tiles
    $("#tile_activate_button").click(function() {
        var mode;
         if($("#tile_activate").attr("value") == "Enable") {
            mode = true;
            $("#tile_activate").attr("value", "Disable").switchClass("ui-icon-locked", "ui-icon-unlocked");
						$("#activate_text").text("Lock");
          } else {
            mode = false;
            $("#tile_activate").attr("value", "Enable").switchClass("ui-icon-unlocked", "ui-icon-locked");
						$("#activate_text").text("Unlock");
          }


        $(".ui-selected").each(function() {
						console.log(this);
            if(this.nodeName == "div") {
                var id = $(this).attr("id");
								that.map.grid[id].enabled = mode;
								if(mode === false) {
									$(this).addClass("disabled");
								}else {
									$(this).removeClass("disabled")
								}
            }
         });
		
			 	 $("#save").show();
      });

		that.selectable();
};


ot.MapCreator.prototype = {
	selectable: function() {
    $("#ot_map").selectable({
        selected: function(event, ui) {
            
						
            var id = $(ui.selected).attr("id");
            var cell = that.map.grid[id];

            $("#tile_desc").text(cell.desc);
            $("#tile_note").text(cell.note);

						$('#tile_desc').editable(function(value, settings) { 
							 $(".ui-selected").each(function() {

									if(this.nodeName === "div") {
											var id = $(this).attr("id");

											cell = that.map.grid[id];
											cell.desc = value;
									}
							 });

							$("#save").show();
							return(value);
						}, { 
								tooltip   : 'Click to add description...',
								height    : "22px",
						});

						$('#tile_note').editable(function(value, settings) { 
								$(".ui-selected").each(function() {
										if(this.nodeName === "div") {
												var id = $(this).attr("id");
												var cell = that.map.grid[id]
												cell.note = value;
												$("#" + id).html("<span class=\"note\">" + value + "</span>");
										}
								 });

							$("#save").show();
								return(value);
						}, { 
								tooltip   : 'Click to add note...',
								height    : "22px",
						});


						if(cell.enabled == false) {
							  $("#tile_activate").attr("value", "Enable").switchClass("ui-icon-unlocked", "ui-icon-locked");
								$("#activate_text").text("Unlock");
						} else {
						   $("#tile_activate").attr("value", "Disable").switchClass("ui-icon-locked", "ui-icon-unlocked");
								$("#activate_text").text("Lock");
					  }
			}

    });

	}

}
window.status = '';
