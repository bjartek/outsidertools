/* 
 * File        : ot.map.js
 * Author      : Bjarte Stien Karlsen
 * Copyright   : (c) 2009
 *               Do not use (or abuse) without permission
 */
window.status = 'Loading [ot.map.js]';

window.ot = window.ot || { VERSION: '1.0' };


ot.MapTile = function(id,args ) {
	this.id = id;
	this.defaultCell = {
			tile: "forrest1x1", 
			desc: "",
			note: "",
			enabled: true
		};
	var empty = {};
  this.options = $.extend(true, empty, this.defaultCell, args);
	this.tile = this.options.tile;
	this.desc - this.options.desc;
	this.note = this.options.note;
	this.enabled = this.options.enabled;
}

ot.MapTile.prototype = {
	reset: function () {
		this.tile = this.defaultCell.tile;
		this.desc = this.options.desc;
		this.note = this.options.note;
		this.enable = this.options.enabled;
		return this;
	},
	paint: function() {
		var claz = "cell ";
		var body = "";

		if(this.tile !== false) {
			claz += this.tile;
		}

		if(this.enabled  === true && this.tile !== false) {
			claz += " tile drop";
		}

		if(this.id.indexOf("a") != -1) {
			claz += " first";
		}

		if(this.note !== "") {
			body += "<span class=\"desc\">" + this.note + "</span>";
		}

		var element = "<div id=\"" + this.id  + "\" class=\"" + claz +"\">" + body + "</div>";
		$("#ot_map").append(element);
		return this;
	}
};

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
			enable : true
		},
		defaultCell : {
			tile: "forrest1x1", 
			desc: "",
			note: "",
			enabled: true
		}, 
	grid: {}
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

	paint: function() {

			$("#map").html("<div id=\"ot_map_rows\">\n <div class=\"ot_map_firstrow\"></div>\n </div>\n <div id=\"ot_map_box\">\n <div id=\"ot_map_cols\"></div>\n <div id=\"ot_map\"> </div>\n </div>");

		this.createGrid();
		this.createRows();
		this.createCols();
		this.paintGrid();

		var newWidth =(this.options.cols * 34) + 40;
		$(this.options.div).css("width", newWidth + "px");
	},

	createGrid: function() {
		var g = {};
		var props = {};
		var empty = {};
		for (j=1;j <= this.options.rows;j++) {
			for (i=0;i < this.options.cols;i++) {
				var col = this.alpha[i].toLowerCase();
				var element = col + j;
				if(this.grid[element]){
					props = jQuery.extend(empty, this.options.defaultCell, this.grid[element]);
					g[element] = new ot.MapTile(element, props);
				} else {
					g[element] = new ot.MapTile(element, this.options.defaultCell);
				}
			}

		}
		this.grid = g;
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
				this.grid[element].paint();
			}
		}
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
