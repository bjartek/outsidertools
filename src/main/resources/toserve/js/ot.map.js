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

	alpha : ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z', 'AA','AB','AC','AD','AE','AF','AG','AH','AI','AJ','AK','AL','AM','AN','AO','AP','AQ','AR','AS','AT','AU','AV','AW','AX','AY','AZ', 'BA','BB','BC','BD','BE','BF','BG','BH','BI','BJ','BK','BL','BM','BN','BO','BP','BQ','BR','BS','BT','BU','BV','BW','BX','BY','BZ', 'CA','CB','CC','CD','CE','CF','CG','CH','CI','CJ','CK','CL','CM','CN','CO','CP','CQ','CR','CS','CT','CU','CV','CW','CX','CY','CZ'],

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
	},

	createEmptyGrid: function() {
			for (i=1;i <= this.options.cols;i++) {
				for (j=1;j <= this.options.rows;j++) {
					this.grid.push(this.createCell(i, j));
				}
			}
	},
	reset: function(index) {
		var element = this.grid[index];
		this.grid[index] = this.createCell(element.row, element.col);
	},
	createCell: function(i, j) {
			return {
							id: i + "_" + j,
							row: i,
							col: j,
							tile: this.options.defaultCell,
							desc: "",
							note: "",
							enabled: true
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
		for (i=1;i <= this.options.rows;i++) {
			var indexToSplice = (this.options.cols * i) -1;
			this.grid.splice(indexToSplice, 0, this.createCell(i, this.options.cols));
		}

		this.paint();
		return this.options.cols;
	},
	removeColumn: function() {
		this.options.cols--;
		for (i=1;i <= this.options.rows;i++) {
			var indexToSplice = (this.options.cols * i);
			this.grid.splice(indexToSplice, 1);
		}
		this.paint();
		return this.options.cols;
	},
	addRow: function() {
		this.options.rows++;
		for (i=1;i <= this.options.cols;i++) {
			this.grid.push(this.createCell(this.options.rows, i));
		}
		this.paint();
		return this.options.rows;
	},
	removeRow: function() {
		this.grid.splice(- this.options.cols);
		this.options.rows--;
		this.paint();
		return this.options.rows;
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
