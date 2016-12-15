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
	
}]);
