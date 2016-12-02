'use strict';

angular.module('queryBuilder.querybuilder', ['ngRoute', 'queryBuilder.services'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/querybuilder', {
    templateUrl: 'querybuilder/querybuilder.html'
  });
}])
.controller('queryBuilderCtrl', ['$requests', '$scope',
	function($requests, $scope) {
    var self = this;

    self.query = {
    	"name":"",
    	"description":"",
    	"category":"",
    	"limitcount": "",
    	"query": {
    		
    	}                
    }

    /**
	Transfair changes to graph
    */
    //$scope.$watch('query', self.transfairToGraph, true);

    self.transfairToGraph = function(){
    	//reset

    	if(self.query["query"] !== undefined &&
    		self.query["query"]["node"] !== undefined){
    		 self.nodes.add([
    		 	 {id: 1, label: self.query["query"]["type"]}]);

    	}
    }

    /**
	 self.nodes.add([
    	 {id: 1, label: 'Node 4'},
        
        {id: 2, label: 'Node 2'},
        {id: 3, label: 'Node 3'},

        {id: 5, label: 'Node 5'},
        {id: 6, label: 'Node 1'}]);


    self.edges.add([
    	{id: 1, from: 1, to: 6, label: 'Edge 1'},
        {id: 2, from: 6, to: 2, label: 'Edge 1'},
        {id: 3, from: 6, to: 3, label: 'Edge 1'}

    ]);
    */

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
	Methods on node
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

	self.addNode = function($node){
		/**
		Add initial node
		*/
		self.query['query']['node'] = {
			"type": $node,
			"returnAttributes": [],
			"filterAttributes": [],
			"orderByAttributes": [],
			"relationship":[]
		}
		

		self.availableNodes = [];

		self.transfairToGraph();
	}

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

	

//	$scope.highlight = function(haystack) {
//
//		for (var i = 0; i < self.highlightWords.length; i++) {
//			$scope.trustAsHtml(haystack.replace(new RegExp(self.highlightWords[i], "gi"), function(match) {
//			        return '<span class="highlightedText">' + match + '</span>';
//			}
//	    }));
//	};

	/*
	View oprerations
	*/
	self.changeDirection = function(i){
		if(self.selectedNodesAndRelation[i]["relationship"]['direction'] == 'up'){
			self.selectedNodesAndRelation[i]["relationship"]['direction'] = 'down';
		}
		else if(self.selectedNodesAndRelation[i]["relationship"]['direction'] == 'down'){
			self.selectedNodesAndRelation[i]["relationship"]['direction'] = 'both';
		}
		else if(self.selectedNodesAndRelation[i]["relationship"]['direction'] == 'both'){
			self.selectedNodesAndRelation[i]["relationship"]['direction'] = 'up';
		}	

	}

	/**
	Vis settings
	*/
	self.nodes = new vis.DataSet();
    self.edges = new vis.DataSet();

    self.network_data = {
        nodes: self.nodes,
        edges: self.edges
    };

    self.network_options = {
    	
    	width:  '100%',
    	height: '400px',
		edges:{
		    arrows: {
		      to:     {enabled: true, scaleFactor:1, type:'arrow'},
		      middle: {enabled: false, scaleFactor:1, type:'arrow'},
		      from:   {enabled: false, scaleFactor:1, type:'arrow'}
		    }
		},
		layout: {

			hierarchical: {
		      enabled:true,
		      levelSeparation: 150,
		      nodeSpacing: 100,
		      treeSpacing: 200,
		      blockShifting: true,
		      edgeMinimization: true,
		      parentCentralization: true,
		      direction: 'UD',        // UD, DU, LR, RL
		      sortMethod: 'hubsize'   // hubsize, directed
		    }
		}
	};

   self.onNodeClick = function(params) {
        alert(params["nodes"][0]);
        
    };

    self.onEdgeClick = function(params) {
        
        alert(params["edges"][0]);
    };


}]);