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

        	self.availableCategories = [];

			/**
	Method for categories
	*/
	self.getCategories = function($success, $data, $status){
		self.hasError = !$success;
		if($success){

			self.availableCategories = $data;
		}
		else
		{
			self.error = $data;
		}
	}
	self.category = "";

	$requests.getAllCategories(self.getCategories);

    	self.changeCategories = function() {
		self.category = self.category;
	}
	self.checkCategory = function($category) {

		return $category == self.category || self.category == ""

	};
}]);
