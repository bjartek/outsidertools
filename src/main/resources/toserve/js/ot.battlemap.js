/* 
 * File        : ot.battlemap.js
 * Author      : Bjarte Stien Karlsen
 * Copyright   : (c) 2009
 *               Do not use (or abuse) without permission
 */
window.status = 'Loading [ot.battlemap.js]';

window.ot = window.ot || { VERSION: '1.0' };

ot.BattleMap = function( args ) {
  this.options = {
	  grid: {},
		element: {},
		div: "#panel",
	  rows: 10,
		cols: 10
	};

  jQuery.extend(true, this.options, args);

	this.map = new ot.Map({ 
		  defaultCell: this.options.defaultCell,
			grid: this.options.grid, 
			rows: this.options.rows, 
			cols: this.options.cols,
			inspector : { previewText: "#ot_tile_inspector", previewTile: "#ot_tile_preview" }
	});
	this.element = this.options.element;
	this.paint();

	battlemap = this;
   
	$("#ot_element").sortable({ 
		containment: 'parent',
   	update: function(event, ui) {
			battlemap.options.order = $(this).sortable('toArray');
		}
	});

   $("#ot_map .ot_element").draggable({ 
        containment: "#ot_map",
        revert: true,
        cursor: 'crosshair',
        cursorAt: { left: 0 },
        revertDuration: 0
    });

	$("div.drop").droppable({
		hoverClass: "hover",
		drop: function(event, ui) { 

			var cell = $("#" + event.target.id);
			if(battlemap.charTile !== event.target.id){
				 $("#" + battlemap.charTile).removeClass("highlight");
				 cell.addClass("highlight");
				 battlemap.charTile = event.target.id;
				 that.inspect_tile(event.target.id);
			 }


			var element = ui.draggable;
			battlemap.element[element.attr("id")].pos = event.target.id;
		 	var parent_element = element.parent();
		 	parent_element.droppable("enable");
			cell.droppable("disable");

	 	   cell.append(element);
 	 	   element.css( {
			   left: "0", 
			   top: "0" 
       });

		},
		activeClass: 'accept'
    });

		$(".drop:has(div)").droppable("disable");

		$("#ot_element").find("li").click(function(i) {
			battlemap.inspect(i.target.id.split("-")[0]);
			});

		$(".ot_element").click(function(i) {
			battlemap.inspect(i.target.id);
		 });
	return this;
};

ot.BattleMap.prototype = {

	char : false,
	charTile : false,
	paint: function() {
		$(this.options.div).html(" <h3>Characters/Monsters</h3> <ul id=\"ot_element\" class=\"ui-sortable\"></ul> <h3>Tile</h3> <div class=\"ot_element\" id=\"ot_tile_preview\"></div> <div id=\"ot_tile_inspector\"> Klikk på en tile for å se info her. </div> <h3>Rolle</h3> <div class=\"editable\" id=\"ot_char_inspector\">Klikk på en spillfigur for å se info her</div>");
		this.paintElements();
  },

	inspect: function(id) {
			if(this.char !== false) {
				$(".highlight").removeClass("highlight");
			}
		//		$("#" + this.char).parent().removeClass("highlight");
		//		$("#" + this.char + "-i").removeClass("highlight");
		//	}
			

			var  parentDiv= $("#" + id).parent();

			this.char = id;
			this.charTile = parentDiv.attr("id");
			parentDiv.addClass("highlight");
			$("#" + id + "-i").addClass("highlight");

			$("#ot_char_inspector").text(this.element[id].info);

			$('#ot_char_inspector').editable(function(value, settings) { 
					battlemap.element[battlemap.char].info = value;
					return(value);
			}, { 
				tooltip   : 'Click to edit...'
			});
	},

	empty: {},

	defaultElement: {
		icon: false,
		text: false,
		show : true,
		info : "", 
		priv_info : ""
	},

 	paintElements: function (){
		for(var element in this.element) {
			if(this.element.hasOwnProperty(element)){
				var obj = $.extend(this.empty, this.defaultElement, this.element[element]);
				this.paintElement(element, obj);
			}
		}
	},

	paintElement: function (id, obj) {

		if(!obj.show) {
			return;
		}
		var cell = $("#" + obj.pos);
		var claz = "ot_element ";

		var body = "";
		if(obj.icon) {
			claz += obj.icon;
				if(obj.text) {
					body = obj.text;
				}
		} else {
			body = id;
		}

		var content = "<div id=\"" + id + "\" class=\"" + claz +"\">" + body + "</div>"; 
		cell.append(content);

		var listContent = "<li id=\"" + id + "-i\">" + obj.name + "</li>";
		$("#ot_element").append(listContent);

	}
};

window.status = '';
