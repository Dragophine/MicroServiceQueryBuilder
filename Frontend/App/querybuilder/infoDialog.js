'use strict';

angular.module('queryBuilder.querybuilderInfoDialog', ['ngRoute'])



.controller('querybuilderInfoDialogCtrl', ['$requests', '$scope', 
    function($requests, $scope) {
        var self = this;

         self.info = $scope.ngDialogData;
       
	
}]);
