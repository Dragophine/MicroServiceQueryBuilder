'use strict';

angular.module('queryBuilder.querybuilderrelationshipdialog', ['ngRoute'])


/**
   * The controller which handles the relationship configuration.
   * It will be opened when a relationship was clicked.
   * 
   * @version 1.0.0
   */
.controller('queryBuilderRelationshipDialogCtrl', ['$requests', '$scope', 
    function($requests, $scope) {
        var self = this;

        self.relationship = $scope.ngDialogData;
        self.name = self.relationship['relationshipType'];
        self.direction = self.relationship['direction'];

        self.isOptionalChecked = function(){
        	return self.relationship['optional'];
        }

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

        $requests.getRelationshipKeys(self.relationship['relationshipType'], self.getRelationshipKeyCB);

        /******************************
        PROPETY SETTING
        /******************************/
        
        //RETURN

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
                var index = self.relationship['returnAttributes'].indexOf(returnAttribute);
                self.relationship['returnAttributes'].splice(index, 1); 

                if(self.getOrderByAttributes($key) !== undefined){
                    self.setOrderByAttributes($key);
                }
            }
            else
            {
                //Adds an attribute
                self.relationship['returnAttributes'].push({
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
            console.log(returnAttribute);
        };

        //ORDER BY 

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
                var index = self.relationship['orderByAttributes'].indexOf(orderByAttribute);
                self.relationship['orderByAttributes'].splice(index, 1);    
            }
            else
            {
                if(self.getReturnAttributes($key) === undefined){
                    self.setReturnAttributes($key);
                }
                //füge attribut hinzu
                self.relationship['orderByAttributes'].push({
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
            var returnFilterAttribute = undefined;
            for (var i = self.relationship['filterAttributes'].length - 1; i >= 0; i--) {
                if(self.relationship['filterAttributes'][i]['attributeName'] === $key){
                    returnFilterAttribute = self.relationship['filterAttributes'][i];
                    break;
                }
            }
            return returnFilterAttribute;
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
        /**
        Toggles the value every time the checkbox is klicked.
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
                                "id":0,         //Fuer Frontend
                                "type":"string",         //int, string...
                                "filterType": "=",  //sowas wie "in","like","=",">"
                                "value":"", 
                                "changeable":false,
                                "isBracketOpen": false,
                                "isBracketClosed": false,
                                "logic":""           //“AND/OR”
                            }
                        ]   //ist der Parameter fix oder in der Verwaltung veränderbar?
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
