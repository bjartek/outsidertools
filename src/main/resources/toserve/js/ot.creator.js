/* 
 * File        : ot.creator.js
 * Author      : Bjarte Stien Karlsen
 * Copyright   : (c) 2009
 *               Do not use (or abuse) without permission
 */
window.status = 'Loading [ot.creator.js]';
window.ot = window.ot || { VERSION: '1.0' };

ot.MapCreator = function() {
	this.map = new ot.Map({ inspector : { enable: false}});

	that = this;

    $("#tabs").tabs();

		this.previewTile = "";

    $(".tiles").click(function(e) {
        $(".tiles").unbind("mousemove");
				$(".ui-selected").each(function() {
					var element = $(this);
					var id = element.attr("id");

					var t = that.map.tile($(this).attr("id"));

						if(t.tile == that.previewTile){
								return this;
						}
						element.removeClass(t.tile);
						t.tile = that.previewTile;
						element.addClass(that.previewTile);
				});

    });


    $("#addcol").click(function(){

				that.map.addColumn();
        var numCols = that.map.cols();
        if(numCols == 26) {
            $("#addcol").attr("disabled", "true");
        }

        if(numCols == 2){
            $("#rmcol").removeAttr("disabled");
         }

     });


    $("#addrow").click(function(){
				that.map.addRow();

				var numRows = that.map.rows();
        if(numRows == 2){
            $("#rmrow").removeAttr("disabled");
         }

     });
  	$("#rmcol").click(function() {
				
				that.map.removeColumn();
        var numCols = that.map.cols();

        if(numCols == 25) {
            $("#addcol").removeAttr("disabled");
        }
        if(numCols == 1){
            $("#rmcol").attr("disabled", "true");
         }
     });


    $("#rmrow").click(function() {
				that.map.removeRow();
				var numRows = that.map.rows();
        if(numRows == 1){
            $("#rmrow").attr("disabled", "true");
         }
     });

    $("#clear").click(function(e) {
        $(".cell").removeClass("ui-selected");
        $("#selection").hide();
    });

		$("#help").click(function(e) {
			$("#help-window").dialog({ position: 'right'});
			});

		$("#reset").click(function(e) {
		   $(".ui-selected").each(function() {
            if(this.nodeName == "DIV") {
                var id = $(that).attr("id");
								that.map.tile(id).reset();
            }
			});
			 that.grid.paint();
			 $("#selection").hide();

		});

		//Lock/unlock all marked tiles
    $("#tile_activate").click(function() {
        var mode;
         if($("#tile_activate").attr("value") == "Enable") {
            mode = true;
            $("#tile_activate").attr("value", "Disable").switchClass("ui-icon-locked", "ui-icon-unlocked");
          } else {
            mode = false;
            $("#tile_activate").attr("value", "Enable").switchClass("ui-icon-unlocked", "ui-icon-locked");
          }


        $(".ui-selected").each(function() {
            if(this.nodeName == "DIV") {
                var id = $(this).attr("id");
								that.map.tile(id).enabled = mode;
            }
         });

      });

    $("#ot_map").selectable({
        selected: function(event, ui) {
            
            $("#selection").show();
            var id = $(ui.selected).attr("id");
            var cell = that.map.tile(id);

            $("#tile_desc").text(cell.desc);
            $("#tile_note").text(cell.note);

						$('#tile_desc').editable(function(value, settings) { 
							 $(".ui-selected").each(function() {
									if(this.nodeName == "DIV") {
											var id = $(this).attr("id");
											cell = that.map.tile(id);
											cell.desc = value;
									}
							 });

							return(value);
						}, { 
								tooltip   : 'Click to add description...',
								height    : "22px",
						});

						$('#tile_note').editable(function(value, settings) { 
								$(".ui-selected").each(function() {
										if(this.nodeName == "DIV") {
												var id = $(this).attr("id");
												that.map.tile(id).note = value;
												$("#" + id).html("<span class=\"desc\">" + value + "</span>");
										}
								 });

								return(value);
						}, { 
								tooltip   : 'Click to add note...',
								height    : "22px",
						});

				if(cell.enabled == false) {
					$("#tile_activate").attr("value", "Enable").switchClass("ui-icon-unlocked", "ui-icon-locked");
				 } else {
					$("#tile_activate").attr("value", "Disable").switchClass("ui-icon-locked", "ui-icon-unlocked");
				 }
				
				 $(".tiles").mousemove(function(e) {
							var offset = $(this).offset();
							var actualY = e.pageY - offset.top;
							var actualX = e.pageX - offset.left;;
							var x = 1 + Math.floor(actualX / 32.0);
							var y = 1 + Math.floor(actualY / 32.0);
							that.previewTile =  this.id + y + "x" + x;
					});
			}
    });
};

window.status = '';
