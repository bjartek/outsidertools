/* 
 * File        : ot.map.js
 * Author      : Bjarte Stien Karlsen
 * Copyright   : (c) 2009
 *               Do not use (or abuse) without permission
 */
window.status = 'Loading [ot.map.js]';

window.ot = window.ot || { VERSION: '1.0' };

ot.Map = function( args ) {
	that = this;
  this.options = {
		rows: 10,
		cols: 10,
		div: "#map",
		inspector: {
			previewTile: false, 
			previewText: false, 
			onInspect: false, 
			enable : false
		},
		defaultCell: "forrest1x1",
		grid: []
	};
	var empty = {};
  this.options = $.extend(true, empty, this.options, args);
	this.grid = this.options.grid;
	this.paint();

	if(this.options.inspector.enable !== false) {
		$(".tile").click(function(i) {
			that.inspect_tile(i.currentTarget.id);
		});
	};

	return this;
};

ot.Map.prototype = {

	alpha : ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'],

	tile: function(id) {
				return this.map.grid(id);
	},
	paint: function() {

			$("#map").html("<div id=\"ot_map_rows\">\n <div class=\"ot_map_firstrow\"></div>\n </div>\n <div id=\"ot_map_box\">\n <div id=\"ot_map_cols\"></div>\n <div id=\"ot_map\"> </div>\n </div>");

		this.createRows();
		this.createCols();

		if(this.grid.length === 0) {
			this.createEmptyGrid();
		}
		this.paintGrid();

		var newWidth =(this.options.cols * 34) + 40;
		$(this.options.div).css("width", newWidth + "px");
	},

	createEmptyGrid: function() {
			for (i=1;i <= this.options.cols;i++) {
				for (j=1;j <= this.options.rows;j++) {
				this.grid.push({
							id: i + "_" + j,
							row: i,
							col: j,
							tile: this.options.defaultCell,
							desc: "",
							note: "",
							enabled: true
				})
			}
		}
	},


	createRows: function() {
		var i=0;
		var container = $("#ot_map_rows");
		for (i=1;i <= this.options.rows;i++) {
			container.append("<div>" + i + "</div>");
		}
	},


	createCols:function() {
		var i=0;
		var container = $("#ot_map_cols");
		for (i=0;i < this.options.cols;i++) {
			container.append("<div>" + this.alpha[i] + "</div>");
		}
	},

	paintGrid: function() {
		for(var element in this.grid) {
			if(this.grid.hasOwnProperty(element)){
				$("#ot_map").append(this.renderGridElement(element, this.grid[element]));
			}
		}
	},
	renderGridElement: function(id, element) {
		var  claz = "cell " + "col" + element.col + " row" + element.row + " ";
		var body = "";

		if(element.tile !== false) {
			claz += element.tile;
		}

		if(element.enabled  === true && element.tile !== false) {
			claz += " tile drop";
		}

		if(element.note !== "") {
			body += "<span class=\"note\">" + element.note + "</span>";
		}

		return  "<div id=\"" + id + "\" class=\"" + claz +"\">" + body + "</div>";
	},

	addColumn: function() {
		this.options.cols++;
		this.paint();
	},
	removeColumn: function() {
		this.options.cols--;
		this.paint();
	},
	addRow: function() {
		this.options.rows++;
		this.paint();
	},
	removeRow: function() {
		this.options.rows--;
		this.paint();
	},
	cols: function() {
		return this.options.cols;
	}, 
	rows: function() {
		return this.options.rows;
	},

	tile: function(id) {
		return this.grid[id];
	},

	setTile: function(id, tile) {
		this.grid[id] = tile;
		return this;
	},

 activeTile : false,

	inspect_tile: function(id) {
		var tile = this.grid[id];

		if(this.options.inspector.previewTile !== false) {
			if(this.activeTile) {
				$(this.options.inspector.previewTile).removeClass(this.activeTile);
			}
			$(this.options.inspector.previewTile).addClass(tile.options.tile);
		}

		if(this.options.inspector.previewText !== false) {
			$(this.options.inspector.previewText).html(id + " " + tile.options.desc);
		}

		if(this.options.inspector.onInspect !== false) {
			this.options.inspector.onInspect(tile);
		}

		this.activeTile = tile.options.tile;
	}
};

window.status = '';
