'use strict';

angular.module('queryBuilder.querybuildernodedialog', ['ngRoute'])


	/**
   * The controller which handles the configuration of a node.
   * It will be opened when a node will be clicked.
   * 
   * @version 1.0.0
   */
.controller('queryBuilderNodeDialogCtrl', ['$requests', '$scope', 
    function($requests, $scope) {
        var self = this;
	    
	     /**
	     * The property contains all informations about the node.
	     * This information is handed in from the query builder.
	     * @type {Object}
	     */	
        self.node = $scope.ngDialogData;
         /**
	     * The name of the node.
	     * @type {Object}
	     */	
        self.name = self.node['type'];
         /**
	     *The keys are all possible attributes where one can apply a filter.
	     * @type {Object}
	     */	
        self.keys = [];
         /**
	     * The relations are all existing incoming and outgoing relationship types of a 
         * certain node.
	     * @type {Object}
	     */	
        self.relationships = [];

        /**
        * The callback when the requested keys were loaded.
        * The keys are all possible attributes where one can apply a filter.
        *
        * @param {boolean} $success - true when there are no errors.
        * @param {Object} $data - the requested data (In this case the keys).
        * @param {number} $status - the actual server status.
        */
		self.getKeysCB = function($success, $data, $status){
			self.hasError = !$success;
			if($success){
				self.keys = $data; 
			}
		};
		 /**
         * This method loads all keys.
         * The keys are all possible attributes where one can apply a filter.
         */
		$requests.getKeys(self.node['type'], self.getKeysCB);

		
		/**
        * The callback when the requested relations were loaded.
        * The relations are all existing incoming and outgoing relationship types of this 
        *  node.
        *
        * @param {boolean} $success - true when there are no errors.
        * @param {Object} $data - the requested data (In this case the relations).
        * @param {number} $status - the actual server status.
        */
		self.getRelationsWithNodesCB = function($success, $data, $status){
			self.hasError = !$success;
			if($success){
				self.relationships = $data; 
			}
		};

		 /**
         * This method loads all relations.
         * The relations are all existing incoming and outgoing relationship types of 
         * this node.
         */
		$requests.getRelationsWithNodes(self.node['type'], self.getRelationsWithNodesCB);


		/******************************
		ADD RELATIONSHIP
		/******************************/

		/**
        * This method adds an relationship to this node.
        *
        * @param {string} $type - the relationshiptype.
        * @param {string} $direction - The direction of the relationship (ingoing or outgoing).
        * @param {Object} $node - The node type where the relationship comes from ore goes to.
        */
		self.addRelationship = function($type, $direction, $node){
			self.node['relationship'].push({
					"relationshipType" :$type, 	//hasMethod, hasInstance
					"direction": $direction, 	//ingoing, outgoing
					"optional":false, 			//OPTIONAL MATCH
					"returnAttributes":[],
					"filterAttributes":[],
					"orderByAttributes":[],
					"node": {
						"type": $node,
						"returnAttributes": [],
						"filterAttributes": [],
						"orderByAttributes": [],
						"relationship":[]
					}
				}
			);

		}

		/******************************
		PROPETY SETTING
		/******************************/

		// RETURN ATTRIBUTES

		/**
        * This method searches for a certain return attribute with a certain key.
        *
        * @param {string} $key - The returnattribute key. The key is the same as the property name.
        * @return {Object} The selected return attribute.
        */
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

		/**
        * Checks whether a certain return attribute exists or not.
        *
        * @param {string} $key - The returnattribute key. The key is the same as the property name.
        */
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

				if(self.getOrderByAttributes($key) !== undefined){
					self.setOrderByAttributes($key);
				}
			}
			else
			{
				//füge attribut hinzu
				self.node['returnAttributes'].push({
					"attributeName":$key,
					"aggregation" : "NONE"
				});
			}
		};

		self.setReturnAttributesValue = function($key, $type, $value){
			var returnAttribute = self.getReturnAttributes($key);
			if(returnAttribute !== undefined){
				returnAttribute[$type] = $value;
			}
		};

		// ORDER BY ATTRIBUTES 

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
				if(self.getReturnAttributes($key) === undefined){
					self.setReturnAttributes($key);
				}
				//füge attribut hinzu
				self.node['orderByAttributes'].push({
					"attributeName":$key,
					"id": 1,
					"direction": "asc"
				});
			}
		};

		self.setOrderByAttributesValue = function($key, $type, $value){
			var orderByAttribute = self.getOrderByAttributes($key);
			if(orderByAttribute !== undefined){
				orderByAttribute[$type] = $value;
			}
		};
		
		//FILTER ATTRIBUTES

		self.getFilterAttributes = function($key){	
			for (var i = self.node['filterAttributes'].length - 1; i >= 0; i--) {
				if(self.node['filterAttributes'][i]['attributeName'] === $key){
					return self.node['filterAttributes'][i];
					
				}
			}
			return undefined;
		};


		self.isFilterAttributesChecked = function($key){
			if(self.getFilterAttributes($key) === undefined){
				return false;
			}
			return true;
		};
		/**
		Toggles the value every time the checkbox is klicked.
		*/
		self.setFilterAttributes = function($key){
			var filterAttribute = self.getFilterAttributes($key);
			if(filterAttribute !== undefined){
				
				var index = self.node['filterAttributes'].indexOf(filterAttribute);
				
				if(self.node['filterAttributes'].length - 1 === index && index !== 0){
					self.node['filterAttributes'][index - 1].logic = "";
				}
				self.node['filterAttributes'].splice(index, 1);	
			}
			else
			{
				
				var newFilterAttribute = {
						"attributeName":$key,
						"logic":"",
						"filters": [
							{
								"id":0,			//for Frontend
								"type":"string",		 //int, string...
								"filterType": "=", 	// "in","like","=",">"
								"value":"", 
								"changeable":true,
								"isBracketOpen": false,
								"isBracketClosed": false,
								"logic":""  			//“AND/OR”
							}
						]	//ist der Parameter fix oder in der Verwaltung veränderbar?
				};

				//adds and attribute
				self.node['filterAttributes'].push(
					newFilterAttribute
				);
				//sort alphabetically
				self.node['filterAttributes'] = self.node['filterAttributes'].sort(function(x, y) {
			        if (x['attributeName'] == y['attributeName']) return 0;
			        else if (x['attributeName'] < y['attributeName']) return -1;
			        else return 1;
			    });
				//add logic to index +1 and index - 1 if not available
			    var index = self.node['filterAttributes'].indexOf(newFilterAttribute);

			    if(index > 0){
					if(self.node['filterAttributes'][index - 1].logic === "" ||
						self.node['filterAttributes'][index - 1].logic === undefined){
						self.node['filterAttributes'][index - 1].logic = "AND";
					};
				}
				if(self.node['filterAttributes'].length - 1 > index  ){
					self.node['filterAttributes'][index].logic = "AND";
				}
			}
			console.log(self.node);
		};

		self.setFilterAttributesValue = function($key, $type, $value){
			var filterAttributes = self.getFilterAttributes($key);

			if(filterAttributes != undefined
				&& filterAttributes[$type] !== $value){
				filterAttributes[$type] = $value;
			}

			console.log("Set key: " + $key + ", type: " + $type + 
							+ ", id: " + $id + ", value: " + $value );
		};

		self.getFilterAttributesFilter = function($key, $id){
			var filterAttributes = self.getFilterAttributes($key);

			if(filterAttributes !== undefined && 
				filterAttributes.filters !== undefined){
				for (var i = filterAttributes.filters.length - 1; i >= 0; i--) {
					if(filterAttributes.filters[i].id === $id){
						return filterAttributes.filters[i];	
					}
				}
			}

			return undefined;
		}

		


		self.setFilterAttributesFilterValue = function($key, $id, $type, $value){
			var filterAttributesFilter = self.getFilterAttributesFilter($key, $id);

			if(filterAttributesFilter != undefined
				&& filterAttributesFilter[$type] !== $value){
				filterAttributesFilter[$type] = $value;

				if($type === 'type'){
					filterAttributesFilter['value'] = undefined; 
				}
			}

			console.log("Set key: " + $key + ", type: " + $type + 
							+ ", id: " + $id + ", value: " + $value );

		};

		self.addFilterAttributesFilter = function($key){
			var filterAttributes = self.getFilterAttributes($key);

			if(	filterAttributes !== undefined &&
				filterAttributes !== null &&
				filterAttributes.filters !== undefined &&
				filterAttributes.filters !== null ){
				//sort filters ascending
				filterAttributes.filters.sort(function(x, y) {
			        if (x['id'] == y['id']) return 0;
			        else if (parseInt(x['id']) < parseInt(y['id'])) return -1;
			        else return 1;
			    });
				//there must be at least one filter in the filters array
				//otherwise: invalid state

				if(filterAttributes.filters.length > 0){
					filterAttributes.filters[filterAttributes.filters.length - 1].logic = "AND";
				}

				var id = filterAttributes.filters[filterAttributes.filters.length - 1].id;
				id = id +1;
				filterAttributes.filters.push(
				{
					"id":id,			//for Frontend
					"type":"string",	//int, string...
					"filterType": "=", 	//"in","like","=",">"
					"value":"", 
					"changeable":false,
					"isBracketOpen": false,
					"isBracketClosed": false,
					"logic":""  		//“AND/OR”
				});


			}
		}

		self.deleteFilterAttributesFilter = function($key, $id){
			var filterAttributes = self.getFilterAttributes($key);

			if(filterAttributes !== undefined){
				//There must be at least one filter in the array
				if(filterAttributes.filters != undefined &&
					filterAttributes.filters.length > 1){
					//Delete filter
					var filterAttributesFilter = self.getFilterAttributesFilter($key, $id);
					if(filterAttributesFilter != undefined){
						var index = filterAttributes.filters.indexOf(filterAttributesFilter);
						if(filterAttributes.filters.length - 1 === index && index !== 0){
							filterAttributes.filters[index - 1].logic = "";
						}

						filterAttributes.filters.splice(index, 1);	
					}
				}	
			}
		}
}]);
