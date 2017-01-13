'use strict';

angular.module('queryBuilder.querybuilderrelationshipdialog', ['ngRoute'])



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
        /***
        This methods load the keys for a specific node.
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
            }
            else
            {
                //füge attribut hinzu
                self.relationship['returnAttributes'].push({
                    "attributeName":$key,
                    "returnType": "",
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
                //füge attribut hinzu
                self.relationship['orderByAttributes'].push({
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
        self.setFilterAttributes = function($key){
            var filterAttribute = self.getFilterAttributes($key);
            if(filterAttribute !== undefined){
                //lösche return Attribute
                //lösche return Attribute
                var index = self.relationship['filterAttributes'].indexOf(filterAttribute);
                self.relationship['filterAttributes'].splice(index, 1); 
            }
            else
            {
                var newFilterAttribute = {
                        "attributeName":$key,
                        "type":"string",         //int, string...
                        "filterType": "=",  //sowas wie "in","like","=",">"
                        "value":"", 
                        "changeable":false  //ist der Parameter fix oder in der Verwaltung veränderbar?
                };
                //füge attribut hinzu
                self.relationship['filterAttributes'].push(
                    newFilterAttribute
                );
            }
            console.log(self.node);
        };

        self.setFilterAttributesDynamic = function($key, $type, $value){
            var filterAttributes = self.getFilterAttributes($key);
            if(filterAttributes !== undefined){
                filterAttributes[$type] = $value;
            }
            console.log("Set key: " + $key + ", type: " + $type + ", value: " + $value );
        };
	
}]);
