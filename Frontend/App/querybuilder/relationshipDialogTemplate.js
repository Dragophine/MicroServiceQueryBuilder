'use strict';

angular.module('queryBuilder.querybuilderrelationshipdialog', ['ngRoute'])


/**
   * The controller which handles the relationship configuration.
   * It will be opened when a relationship was clicked.
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
.controller('queryBuilderRelationshipDialogCtrl', ['$requests', '$scope', 
    function($requests, $scope) {
        var self = this;

         /**
         * The property contains all informations about the relationship.
         * This information is handed in from the query builder.
         * @type {Object}
         */ 
        self.relationship = $scope.ngDialogData;
        /**
         * The name of the relation.
         * @type {Object}
         */ 
        self.name = self.relationship['relationshipType'];
         /**
         * The direction of the relationship. 
         * A relation can be either ingoing or outgoing.
         * @type {Object}
         */ 
        self.direction = self.relationship['direction'];
         /**
         * Checks whether the certain property is optional or not.
         * 
         * @return {boolean} Returns optional property.
         */ 
        self.isOptionalChecked = function(){
        	return self.relationship['optional'];
        }
         /**
         * Sets the specific optional property.
         *
         * @param {boolean} value - The new value of the optional property.
         */ 
        self.setOptionalChecked = function(value){
        	return self.relationship['optional'] = value;
        }


        /******************************
        LOADING 
        /******************************/
        /**
        * The callback when the requested keys were loaded.
        * The keys are all possible attributes where one can apply a filter.
        *
        * @param {boolean} $success - true when there are no errors.
        * @param {Object} $data - the requested data (In this case the keys).
        * @param {number} $status - the actual server status.
        */
        self.getRelationshipKeyCB = function($success, $data, $status){
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
        $requests.getRelationshipKeys(self.relationship['relationshipType'], self.getRelationshipKeyCB);

        /******************************
        PROPETY SETTING
        /******************************/
        
        //RETURN
        /**
        * This method searches for a certain return attribute with a certain the attribute name.
        * 
        * @param {string} $key - The key of the return attribute. The key is the same as the attribute name.
        * @return {Object} The given return attribute or undefined.
        */
        self.getReturnAttributes = function($key){
            var returnAttribute = undefined;
            for (var i = self.relationship['returnAttributes'].length - 1; i >= 0; i--) {
                if(self.relationship['returnAttributes'][i]['attributeName'] === $key){
                    returnAttribute = self.relationship['returnAttributes'][i];
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
                //Deletes a return attribute
                var index = self.relationship['returnAttributes'].indexOf(returnAttribute);
                self.relationship['returnAttributes'].splice(index, 1); 

                if(self.getOrderByAttributes($key) !== undefined){
                    self.setOrderByAttributes($key);
                }
            }
            else
            {
                //Adds an return attribute
                self.relationship['returnAttributes'].push({
                    "attributeName":$key,
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
            console.log(returnAttribute);
        };

        //ORDER BY 
        /**
        * This method searches for a certain orderby attribute with a certain attribute name (key).
        *
        * @param {string} $key - The key of the orderby attribute. The key is the same as the attribute name.
        * @return {Object} The given orderby attribute or undefined.
        */
        self.getOrderByAttributes = function($key){
            var orderByAttribute = undefined;
            for (var i = self.relationship['orderByAttributes'].length - 1; i >= 0; i--) {
                if(self.relationship['orderByAttributes'][i]['attributeName'] === $key){
                    orderByAttribute = self.relationship['orderByAttributes'][i];
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
                //Deletes an return attribute
                var index = self.relationship['orderByAttributes'].indexOf(orderByAttribute);
                self.relationship['orderByAttributes'].splice(index, 1);    
            }
            else
            {
                if(self.getReturnAttributes($key) === undefined){
                    self.setReturnAttributes($key);
                }
                //Adds an return attribute
                self.relationship['orderByAttributes'].push({
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
        
        //FILTER ATTRIBUTES
        /**
        * This method searches for a certain filter attribute with a certain attribute name (key).
        *
        * @param {string} $key - The key of the filter attribute. The key is the same as the attribute name.
        * @return {Object} The given filter attribute or undefined.
        */
        self.getFilterAttributes = function($key){
            var returnFilterAttribute = undefined;
            for (var i = self.relationship['filterAttributes'].length - 1; i >= 0; i--) {
                if(self.relationship['filterAttributes'][i]['attributeName'] === $key){
                    returnFilterAttribute = self.relationship['filterAttributes'][i];
                    break;
                }
            }
            return returnFilterAttribute;
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
                //lösche return Attribute
                //lösche return Attribute
                var index = self.relationship['filterAttributes'].indexOf(filterAttribute);

                if(self.relationship['filterAttributes'].length - 1 === index && index !== 0){
                    self.relationship['filterAttributes'][index - 1].logic = "";
                }

                self.relationship['filterAttributes'].splice(index, 1); 
            }
            else
            {
                var newFilterAttribute = {
                        "attributeName":$key,
                        "logic":"",
                        "filters": [
                            {
                                "id":0,         //for Frontend
                                "type":"string",         //int, string...
                                "filterType": "=",  // "in","like","=",">"
                                "value":"", 
                                "changeable":false,
                                "isBracketOpen": false,
                                "isBracketClosed": false,
                                "logic":""           //“AND/OR”
                            }
                        ]   //The parameters list can be changed. (new parameters added or removed)
                };
                //add an attribute
                self.relationship['filterAttributes'].push(
                    newFilterAttribute
                );
                 //sort alphabetically
                self.relationship['filterAttributes'] = self.relationship['filterAttributes'].sort(function(x, y) {
                    if (x['attributeName'] == y['attributeName']) return 0;
                    else if (x['attributeName'] < y['attributeName']) return -1;
                    else return 1;
                });
                //add logic to index +1 and index - 1 if not available
                var index = self.relationship['filterAttributes'].indexOf(newFilterAttribute);

                if(index > 0){
                    if(self.relationship['filterAttributes'][index - 1].logic === "" ||
                        self.relationship['filterAttributes'][index - 1].logic === undefined){
                        self.relationship['filterAttributes'][index - 1].logic = "AND";
                    };
                }
                if(self.relationship['filterAttributes'].length - 1 > index  ){
                    self.relationship['filterAttributes'][index].logic = "AND";
                }
                console.log(self.relationship);
            }
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
                            + ", id: " + $id + ", value: " + $value );
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

            if(filterAttributesFilter != undefined && 
                filterAttributesFilter[$type] !== $value){
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

            if( filterAttributes !== undefined &&
                filterAttributes !== null &&
                filterAttributes.filters !== undefined &&
                filterAttributes.filters !== null ){
                //sort filters ascending
                filterAttributes.filters.sort(function(x, y) {
                    if (x['id'] == y['id']) return 0;
                    else if (parseInt(x['id']) < parseInt(y['id'])) return -1;
                    else return 1;
                });

                if(filterAttributes.filters.length > 0){
                    filterAttributes.filters[filterAttributes.filters.length - 1].logic = "AND";
                }
                //there must be at least one filter in the filters array
                //otherwise: invalid state
                var id = filterAttributes.filters[filterAttributes.filters.length - 1].id;
                id = id +1;
                filterAttributes.filters.push(
                {
                    "id":id,            //for Frontend
                    "type":"string",    //int, string...
                    "filterType": "=",  //like "in","like","=",">"
                    "value":"", 
                    "changeable":false,
                    "isBracketOpen": false,
                    "isBracketClosed": false,
                    "logic":""       //“AND/OR”
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
                        if(filterAttributes.filters.length - 1 === index  && index !== 0){
                            filterAttributes.filters[index - 1].logic = "";
                        }
                        
                        filterAttributes.filters.splice(index, 1);  
                    }
                }   
            }
        }
	
}]);
