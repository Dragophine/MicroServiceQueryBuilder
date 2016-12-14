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
			console.log(self.node);
		};

		self.getOrderByAttributesValue = function($key){
			var orderByAttribute = self.getOrderByAttributes($key);
			/*
			var returnValue = 'asc';
			if(orderByAttribute !== undefined){
				returnValue = orderByAttribute["direction"];
			}
			console.log($key  + ' requests value ' + returnValue);*/
			return orderByAttribute;	
		};

		self.setOrderByAttributesValue = function($key, $value){
			var orderByAttribute = self.getOrderByAttributes($key);
			if(orderByAttribute !== undefined){
				orderByAttribute["direction"] = $value;
			}
			console.log($key + ' set to ' + $value);
			console.log(self.node);
		};
		/*
		Object.defineProperty(self, 'orderByModel', {
		  get: function(key) {
		    return self.getOrderByAttributesValue(key);
		  },
		  set: function(key, value) {
		    self.setOrderByAttributesValue(key, value);
		  }
		});*/
		

		self.getFilterAttributes = function($key){

		};


		self.setFilterAttributes = function($key, value){
			
		};

		
		/*
			"returnAttributes": [],
			"filterAttributes": [],
			"orderByAttributes": [],
			"relationship":[]*/
}]);
