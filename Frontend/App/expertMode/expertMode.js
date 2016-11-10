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
     * Words to highlight
     */
    self.highlightWords = ["MATCH", "WHERE", "WITH", "RETURN"];
    
	self.paramoptions = ["Integer", "String"];

	self.params = [
		{
				type : "Integer",
				value : ""
		},
		{
			type : "String",
			value : ""
		}
	];
	/** 
		Query fields
	*/
	self.query = "";
	self.parameter = [];
	self.name = "";
	self.description = "";
	self.category = "";	
	
	/**
	 * Parameter fields
	 */
	self.key = "";
	self.value = "";
	self.type = "";

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
	

//	$scope.highlight = function(haystack) {
//
//		for (var i = 0; i < self.highlightWords.length; i++) {
//			$scope.trustAsHtml(haystack.replace(new RegExp(self.highlightWords[i], "gi"), function(match) {
//			        return '<span class="highlightedText">' + match + '</span>';
//			}
//	    }));
//	};

}]);