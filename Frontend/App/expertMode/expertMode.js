'use strict';

angular.module('queryBuilder.expertMode', ['ngRoute', 'queryBuilder.services'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/expertmode', {
    templateUrl: 'expertMode/expertMode.html'
  });
}])
.controller('expertModeCtrl', ['$requests',
	function($requests) {
    var self = this;
	
	/** 
		Query
	*/
	self.query = "";

	/** 
		Table json (simply the result)
	*/
	self.table = "";

	/**
		Refres to the json response when an error occured
	*/
	self.error = "";

	/**
		Bolean which concludes if the query has errors
	*/
	self.hasError = false;

	self.callback = function($success, $data, $status) {
		self.hasError = !$success;
		if($success){
			self.table = $data;
		}
		else
		{
			self.error = $data;
		}
	}

	/**
		Start request
	*/
	self.submitQuery = function() {
		$requests.getResultFromQuery(self.query, self.callback);
	}

}]);