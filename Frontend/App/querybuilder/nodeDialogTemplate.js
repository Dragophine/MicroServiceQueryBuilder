'use strict';

angular.module('queryBuilder.querybuildernodedialog', ['ngRoute'])



.controller('queryBuilderNodeDialogCtrl', ['$requests', '$scope', 
    function($requests, $scope) {
        var self = this;
	    
        self.node = $scope.ngDialogData;
        	console.log(self.node);
        self.name = self.node['type'];
        self.keys = [];
        /***
		This methods load the relationships for a specific node.
		*/
		self.loadKeysForNodeCB = function($success, $data, $status){
			self.hasError = !$success;
			if($success){
				self.keys = $data; 
			}
		};

		$requests.getKeys(self.node['type'], self.loadKeysForNodeCB);



		self.getReturnAttributes = function($key){
			var returnAttribute = undefined;
			for (var i = self.node['returnAttributes'].length - 1; i >= 0; i--) {
				if(self.node['returnAttributes'][i]['attributeName'] === $key){
					returnAttribute = self.node['returnAttributes'][i];
					break;
				}
			}
			return returnAttribute;
		};

		self.isReturnAttributesChecked = function($key){
			if(self.getReturnAttributes($key) === undefined){
				return false;
			}
			return true;
		};
		/**
		Toggles the value every time the checkbox is klicked.
		*/
		self.setReturnAttributes = function($key){
			var returnAttribute = self.getReturnAttributes($key);
			if(returnAttribute !== undefined){
				//lösche return Attribute
				var index = self.node['returnAttributes'].indexOf(returnAttribute);
				self.node['returnAttributes'].splice(index, 1);	
			}
			else
			{
				//füge attribut hinzu
				self.node['returnAttributes'].push({
					"attributeName":$key
				});
			}
			console.log(self.node);
		};


		self.getOrderByAttributes = function($key){
			var orderByAttribute = undefined;
			for (var i = self.node['orderByAttributes'].length - 1; i >= 0; i--) {
				if(self.node['orderByAttributes'][i]['attributeName'] === $key){
					orderByAttribute = self.node['orderByAttributes'][i];
					break;
				}
			}
			return orderByAttribute;
		};

		self.isOrderByAttributesChecked = function($key){
			if(self.getOrderByAttributes($key) === undefined){
				return false;
			}
			return true;
		};
		/**
		Toggles the value every time the checkbox is klicked.
		*/
		self.setOrderByAttributes = function($key){
			var orderByAttribute = self.getOrderByAttributes($key);
			//Toggle attribute
			if(orderByAttribute !== undefined){
				//lösche return Attribute
				var index = self.node['orderByAttributes'].indexOf(orderByAttribute);
				self.node['orderByAttributes'].splice(index, 1);	
			}
			else
			{
				//füge attribut hinzu
				self.node['orderByAttributes'].push({
					"attributeName":$key,
					"direction": "asc"
				});
			}
		};

		self.setOrderByAttributesValue = function($key, $value){
			var orderByAttribute = self.getOrderByAttributes($key);
			if(orderByAttribute !== undefined){
				orderByAttribute["direction"] = $value;
			}
		};
		
		

		self.getFilterAttributes = function($key){
			var returnFilterAttribute = undefined;
			for (var i = self.node['filterAttributes'].length - 1; i >= 0; i--) {
				if(self.node['filterAttributes'][i]['attributeName'] === $key){
					returnFilterAttribute = self.node['filterAttributes'][i];
					break;
				}
			}
			console.log(self.node);
			return returnFilterAttribute;
		};


		self.setFilterAttributesValue = function($key, $value){
			var filterAttributes = self.getFilterAttributes($key);
			//delecte when text empty
			if(filterAttributes !== undefined && $value === ""){
				//lösche return Attribute
				var index = self.node['filterAttributes'].indexOf(filterAttributes);
				self.node['filterAttributes'].splice(index, 1);	
			}
			if(filterAttributes !== undefined){
				filterAttributes["value"] = $value;
			}
			else
			{
				//füge attribut hinzu
				self.node['filterAttributes'].push({
						"attributeName":$key,
						"type":"string",		 //int, string...
						"filterType": "in", 	//sowas wie "in","like","=",">"
						"value":$value, 
						"changeable":true/false 
		//ist der Parameter fix oder in der Verwaltung veränderbar?
					}
				);
			}
			console.log(self.node);
		};

		
		/*
			"returnAttributes": [],
			"filterAttributes": [],
			"orderByAttributes": [],
			"relationship":[]*/
}]);
