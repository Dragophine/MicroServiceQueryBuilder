'use strict';

angular.module('queryBuilder.querybuilderLoadDialog', ['ngRoute'])



.controller('querybuilderLoadDialogCtrl', ['$requests', '$scope', 
    function($requests, $scope) {
        var self = this;

        self.loadedQueries = [
        ];    

        self.loadAllQueriesInBuilderCB = function($success, $data, $status){
            if($success){
                self.loadedQueries = $data;
            }
        }

	    $requests.loadAllQueriesInBuilder(self.loadAllQueriesInBuilderCB);

        self.selectQuery = function($query){
             $scope.closeThisDialog($query);
        };

        self.deleteQueryCallback = function($success, $data, $status) {
            if($success){
                $requests.loadAllQueriesInBuilder(self.loadAllQueriesInBuilderCB);
            }
            
        }

        self.deleteQuery = function($id){
            $requests.deleteQueryInBuilder($id, self.deleteQueryCallback);
        }
}]);
