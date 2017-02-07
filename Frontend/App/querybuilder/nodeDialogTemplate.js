'use strict';

angular.module('queryBuilder.querybuildernodedialog', ['ngRoute'])


	/**
   * The controller which handles the configuration of a node.
   * It will be opened when a node will be clicked.
   *
   * In general, this controller helps to add or remove return attributes,
   * orderby attributes and filter attributes.
   * Every certain key (attribute) of a node can have a return, orderby or 
   * filter attribute. If such a attribute is required the json will be 
   * added with the set method.
   * The possible attributes are first loaded into the keys array.
   * 
   * @version 1.0.0
   */
.controller('queryBuilderNodeDialogCtrl', ['$requests', '$scope', '$rootScope','ngDialog',
    function($requests, $scope, $rootScope, ngDialog) {
        var self = this;
	    
	     /**
	     * The property contains all informations about the node.
	     * This information is handed in from the query builder.
	     * @type {Object}
	     */	
        self.node = $scope.ngDialogData.node;

           /**
         * The property contains the author of the query in which the
         * relationship is contained.
         * This information is handed in from the query builder.
         * @type {Object}
         */ 
        self.author = $scope.ngDialogData.author;

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
         * The keys are all possible attributes where one can apply a filter, set it as return attribue or
         * order the results according to it.
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

		/**
	    * This method opens an info dialog. It displays a certain data (head and content)
	    * given in the $data. Furthermore, a callback can be specified, when the dialg is closed.
	    *
	    * @param {!Object} $data - the head an the content for the info dialog.
	    * @param {?function} infoClosePromis - The function which should be called when the dialog is closed.
	    */
	    self.showInfoDialog = function($data, infoClosePromis){
	    	var dialog = ngDialog.open({ template: 'querybuilder/infoDialog.html',
	        				className: 'ngdialog-theme-default custom-width',
	        				controller: 'querybuilderInfoDialogCtrl',
	        				controllerAs: 'ctrl',
	        				data:$data
	        		 });
	    	if(infoClosePromis !== undefined){
	    		dialog.closePromise.then(infoClosePromis);
	    	}
	    }

		/******************************
		Return Attributes
		/******************************/

		/**
        * This method searches for a certain return attribute with a certain the attribute name.
        *
        * @param {string} $key - The key of the return attribute. The key is the same as the attribute name.
        * @return {Object} The given return attribute or undefined.
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
        * Checks whether a certain return attribute with a certain key (attribute name) exists or not.
        *
        * @param {string} $key - The return attribute key. The key is the same as the attribute name.
        * @return {boolean} True if the attribute with the given key exists, otherwise false.
        */
		self.isReturnAttributesChecked = function($key){
			if(self.getReturnAttributes($key) === undefined){
				return false;
			}
			return true;
		};
		/**
        * Checks whether a certain return attribute with a certain key exists or not.
        * If it exists it removes the return attribute, if not it creates a return attribute with 
        * the given key. 
        * Every call toggles a certain return attribute with a certain key.
        * This is necessary because only the keys which should be returned should have an 
        * entry in the returnAttributes array. For one key (attribute name)
        * there can be only one return attribute.
        *
        * @param {string} $key - The key of the property which should be added or deleted.
        */
		self.setReturnAttributes = function($key){
			var returnAttribute = self.getReturnAttributes($key);
			if(returnAttribute !== undefined){
				//Deletes the return attribute
				var index = self.node['returnAttributes'].indexOf(returnAttribute);
				self.node['returnAttributes'].splice(index, 1);	

				if(self.getOrderByAttributes($key) !== undefined){
					self.setOrderByAttributes($key);
				}
			}
			else
			{
				//adds a return attribute
				self.node['returnAttributes'].push({
					"attributeName":$key,
					"alias":"",
					"aggregation" : "NONE"
				});
			}
		};

		/**
        * This method sets a property of a certain return attribute identified by 
        * the key (attribute name).
        *
        * @param {string} $key - The key of the return attribute. The key is the same as the attribute name.
        * @param {string} $type - The property which should be set. 
        * @param {Object} $value - The characteristic of the property ($type).
        */
		self.setReturnAttributesValue = function($key, $type, $value){
			var returnAttribute = self.getReturnAttributes($key);
			if(returnAttribute !== undefined){
				returnAttribute[$type] = $value;
			}
		};

		/******************************
		ORDER BY ATTRIBUTES 
		/******************************/

		/**
        * This method searches for a certain orderby attribute with a certain attribute name (key).
        *
        * @param {string} $key - The key of the orderby attribute. The key is the same as the attribute name.
        * @return {Object} The given orderby attribute or undefined.
        */
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

		/**
        * Checks whether a certain orberby attribute with a certain key (attribute name) exists or not.
        *
        * @param {string} $key - The orberby attribute key. The key is the same as the attribute name.
        * @return {boolean} True if the attribute with the given key exists, otherwise false.
        */
		self.isOrderByAttributesChecked = function($key){
			if(self.getOrderByAttributes($key) === undefined){
				return false;
			}
			return true;
		};

		/**
        * Checks whether a certain orberby attribute with a certain key exists or not.
        * If it exists it removes the orberby attribute, if not it creates a orberby attribute with 
        * the given key. 
        * Every call toggles a certain orberby attribute with a certain key.
        * This is necessary because only the keys which should be returned should have an 
        * entry in the returnAttributes array. For one key (attribute name)
        * there can be only one orberby attribute.
        *
        * @param {string} $key - The key of the property which should be added or deleted.
        */
		self.setOrderByAttributes = function($key){
			var orderByAttribute = self.getOrderByAttributes($key);
			//Toggle attribute
			if(orderByAttribute !== undefined){
				//Deletes the orderby attribute
				var index = self.node['orderByAttributes'].indexOf(orderByAttribute);
				self.node['orderByAttributes'].splice(index, 1);	
			}
			else
			{
				if(self.getReturnAttributes($key) === undefined){
					self.setReturnAttributes($key);
				}
				//Adds the orderby attribute
				self.node['orderByAttributes'].push({
					"attributeName":$key,
					"id": 1,
					"direction": "asc"
				});
			}
		};

		/**
        * This method sets a property of a certain orderby attribute identified by 
        * the key (attribute name).
        *
        * @param {string} $key - The key of the orderby attribute. The key is the same as the attribute name.
        * @param {string} $type - The property which should be set. 
        * @param {Object} $value - The characteristic of the property ($type).
        */
		self.setOrderByAttributesValue = function($key, $type, $value){
			var orderByAttribute = self.getOrderByAttributes($key);
			if(orderByAttribute !== undefined){
				orderByAttribute[$type] = $value;
			}
		};
	

		/******************************
		FILTER ATTRIBUTES
		/******************************/
		/**
        * This method searches for a certain filter attribute with a certain attribute name (key).
        *
        * @param {string} $key - The key of the filter attribute. The key is the same as the attribute name.
        * @return {Object} The given filter attribute or undefined.
        */
		self.getFilterAttributes = function($key){	
			for (var i = self.node['filterAttributes'].length - 1; i >= 0; i--) {
				if(self.node['filterAttributes'][i]['attributeName'] === $key){
					return self.node['filterAttributes'][i];
					
				}
			}
			return undefined;
		};

		/**
        * Checks whether a certain filter attribute with a certain key (attribute name) exists or not.
        *
        * @param {string} $key - The filter attribute key. The key is the same as the attribute name.
        * @return {boolean} True if the attribute with the given key exists, otherwise false.
        */
		self.isFilterAttributesChecked = function($key){
			if(self.getFilterAttributes($key) === undefined){
				return false;
			}
			return true;
		};
		/**
        * Checks whether a certain filter attribute with a certain key exists or not.
        * If it exists it removes the filter attribute, if not it creates a filter attribute with 
        * the given key. 
        * Every call toggles a certain filter attribute with a certain key.
        * This is necessary because only the keys which should be returned should have an 
        * entry in the returnAttributes array. For one key (attribute name)
        * there can be only one filter attribute.
		*
		* Because a filter attribute can also have relations between each other, this 
		* method handles the relations by setting the logic attribute. If two attributes are
		* combined by this logic, the first holds the logic.
		* 
		* If a new attribute is added, 
		* this method also adds the logic to the previous attribute. 
		* If the last attribute is removed the logic
		* of the attribute before it is also removed.
		* 
        * This works because the filter attributes are ordered alphabetically.
        * 
        * @param {string} $key - The key of the property which should be added or deleted.
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

		/**
        * This method sets a property of a certain filter attribute identified by 
        * the key (attribute name).
        *
        * @param {string} $key - The key of the filter attribute. The key is the same as the attribute name.
        * @param {string} $type - The property which should be set. 
        * @param {Object} $value - The characteristic of the property ($type).
        */
		self.setFilterAttributesValue = function($key, $type, $value){
			var filterAttributes = self.getFilterAttributes($key);

			if(filterAttributes != undefined
				&& filterAttributes[$type] !== $value){
				filterAttributes[$type] = $value;
			}

			console.log("Set key: " + $key + ", type: " + $type + 
							", value: " + $value );
		};

		/**
        * A filter attribute contains multiple (at least one) filter. Each filter is 
        * identified by an id. 
        * This method searches for a certain filter identified by an id
        * in a certain filter attribute identified a certain attribute name (key).
        *
        * @param {string} $key - The key of the filter attribute. The key is the same as the attribute name.
        * @param {string} $id - The id of the specific filter. 
        * @return {Object} The given filter attribute or undefined.
        */
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

		
		/**
        * This method sets a property of a certain filter identified by an id in
        * a certain filter attribute identified by an attribute name (key).
        *
        * @param {string} $key - The key of the filter attribute. The key is the same as the attribute name.
        * @param {string} $id - The id of the specific filter. 
        * @param {string} $type - The property which should be set. 
        * @param {Object} $value - The characteristic of the property ($type).
        */
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

		/**
        * This method adds a filter to a filter attribute.
        * The new filter will have a unique id.
        * This method also sorts the filter attributes by the id.
        * The filter attributes were combined with a certain logic (AND, OR) within  
        * a certain filter attributes. This method initializes also the logic.
        *
        * @param {string} $key - The key of the filter attribute in which the filter should be added.
        */
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
					"filterType": "=", 	//like "in","like","=",">"
					"value":"", 
					"changeable":false,
					"isBracketOpen": false,
					"isBracketClosed": false,
					"logic":""  		//“AND/OR”
				});
			}
		}


		/**
        * This method removes a filter identified by an id of a filter attribute
        * identified by the attribute name (key).
        *
        * The filter attributes werew combined with a certain logic (AND, OR). 
        * If required, this method deletes this logic too.
        *
        * @param {string} $key - The key of the filter attribute in which the filter should be removed.
        * @param {string} $id - The id of the specific filter which should be removed. 
        */
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

		 /**
         * This function checks wheather a user has the  
         * permission to edit the items or not.
         *
         * @param {string} $attributeType - The type of the attribute -> "filter", "return", "orderby"
         * @param {string} $key - The key of the attribute for which the permissions should be cheked. 
         * @param {Object} $additionalInfo - Additional informations.
         * @return {boolean} Returns optional property.
         */ 
        self.hasPermissions = function($attributeType, $key, $additionalInfo){
            if(self.author === $rootScope.principal.username){
            	//the author can do everything
                return true;
            }
            else {
            	//permission is only granted to filter attributes
            	if($attributeType === "filter" && $additionalInfo != undefined &&
            		$additionalInfo.type != undefined && $additionalInfo.id != undefined &&
            		$additionalInfo.type != "changeable"){

            		var filterAttributesFilter = self.getFilterAttributesFilter($key, $additionalInfo.id);
            		if(filterAttributesFilter.changeable){
            			return true;
            		}
            		return false;
            		
            	}
                return false;
            }
        }
}]);
