'use strict';

angular.module('queryBuilder.querybuilder', ['ngRoute', 'queryBuilder.services'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/querybuilder', {
    templateUrl: 'querybuilder/querybuilder.html'
  });
}])
.controller('queryBuilderCtrl', ['$requests',
	function($requests) {
    var self = this;

    self.selectedNodesAndRelation = [];




    /**
     * Initial Properties
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
		Query Properties
	*/
	self.availableNodes = "";
	self.availableRelationships = "";
	/**
		Result Properties
	*/
	self.table = "";
	self.hasError = false;
	self.error = "";

	/**
	Query Operations
	*/
	self.executeQueryCB = function($success, $data, $status) {
		self.hasError = !$success;
		if($success){
			self.table = $data;
		}
		else
		{
			self.error = $data;
		}
	}

	self.saveQuery = function(){
		$requests.getResultFromQuery(self.query, self.executeQueryCB);
	}

	self.loadQuery = function(){

	}

	self.submitQuery = function() {
		
	}
	
	/**

	This method requests the required items from 
	a certain 
	*/
	self.getNodesCB = function($success, $data, $status){
		self.hasError = !$success;
		if($success){
			self.availableNodes = $data;
		}
		else
		{
			self.error = $data;
		}
	}

	$requests.getNodes(self.getNodesCB);


	/***
	This methods load the relationships for a specific node.
	*/
	self.loadRelationshipForNodeCB = function($success, $data, $status){
		self.hasError = !$success;
		if($success){
			self.availableRelationships = $data;
		}
		else
		{
			self.error = $data;
		}
	}

	self.loadRelationshipForNode = function($node){

		$requests.getRelations($node, self.loadRelationshipForNodeCB);
	}


	/***
	This methods load the relationships for a specific node.
	*/
	self.loadKeysForNodeCB = function($success, $data, $status){
		self.hasError = !$success;
		if($success){
			self.selectedNodesAndRelation[self.selectedNodesAndRelation.length - 1]["keys"] = $data; 
		}
		else
		{
			self.error = $data;
		}
	}

	self.loadKeysForNode = function($node){
		$requests.getKeys($node, self.loadKeysForNodeCB);
	}

	self.addNode = function($node){
		self.selectedNodesAndRelation.push({
			"node" : $node,
			"relation" : "",
			"keys" : ""
		});
		

		self.availableNodes = [];
		self.loadRelationshipForNode($node);
		self.loadKeysForNode($node);
	}

	self.addRelation = function($relation){
		self.selectedNodesAndRelation[self.selectedNodesAndRelation.length - 1]["relation"] = $relation;

		$requests.getNodes(self.getNodesCB);
		self.availableRelationships = [];

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