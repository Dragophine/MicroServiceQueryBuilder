'use strict';

angular.module('queryBuilder.querybuilderLoadDialog', ['ngRoute'])



.controller('querybuilderLoadDialogCtrl', ['$requests', '$scope', 
    function($requests, $scope) {
        var self = this;

        self.category = undefined;
        self.name = undefined;
        self.description = undefined;

        self.loadedQueries = [ ];    
        self.availableCategories = [];

        self.loadQueriesInBuilderCB = function($success, $data, $status){
            if($success){
                self.loadedQueries = $data;
            }
        }

	    $requests.loadAllQueriesInBuilder( self.loadQueriesInBuilderCB);

        self.selectQuery = function($query){
             $scope.closeThisDialog($query);
        };

        self.deleteQueryCallback = function($success, $data, $status) {
            if($success){
                $requests.loadAllQueriesInBuilder(self.loadQueriesInBuilderCB);
            }
            
        }

        self.deleteQuery = function($id){
            $requests.deleteQueryInBuilder($id, self.deleteQueryCallback);
        }

        /**
        Apply filter
        */
        self.applyFilter = function() {
            $requests.loadSomeQueriesInBuilder(self.loadQueriesInBuilderCB, self.name, self.category, self.description);
         }	

			/**
	Load category
	*/

    self.getCategoriesCallBack = function($success, $data, $status){
        self.hasError = !$success;
        if($success){

            self.availableCategories = $data;
        }
        else
        {
            self.error = $data;
        }
    }

    $requests.getAllCategories(self.getCategoriesCallBack);


    

	
}]);
