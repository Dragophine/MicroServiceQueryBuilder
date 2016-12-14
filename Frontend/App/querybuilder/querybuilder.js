'use strict';

angular.module('queryBuilder.querybuilder', ['ngRoute', 'queryBuilder.services'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/querybuilder', {
    templateUrl: 'querybuilder/querybuilder.html'
  });
}])
.controller('queryBuilderCtrl', ['$requests', '$scope','ngDialog',
	function($requests, $scope, ngDialog) {
    var self = this;

    self.query = {
    	"name":"",
    	"description":"",
    	"category":"",
    	"limitcount": "",
    	"query": {
    		
    	}                
    }

    self.nodeIDStore = {};
    self.relationshipIDStore = {};

    /**
	Transfair changes to graph
    */
    self.transfairToGraph = function(){
    	var $nodeID = 1;
    	var $relationshipID = 0;

    	for (var id in self.relationshipIDStore) {
		  if (self.relationshipIDStore.hasOwnProperty(id)) {
		    self.edges.remove(id);
		  }
		}

    	for (var id in self.nodeIDStore) {
		  if (self.nodeIDStore.hasOwnProperty(id)) {
		    self.nodes.remove(id);
		  }
		}

    	self.nodeIDStore = {};
    	self.relationshipIDStore = {};

    	//reset
    	function transfairToGraphRecursion($parrentid, $relationship){
    		var $node = $relationship['node'];

    		if($node != undefined){
 
    			$nodeID = $nodeID + 1;
    			$relationshipID = $relationshipID +1;
    			//save variable value because $nodeId and $relationship id can be changed by a other
    			//execution
    			var $nID = $nodeID;
    			var $rID = $relationshipID;

    			self.nodes.add([{id: $nID, label: $node['type']}]);
    			if($relationship['direction'] === "INGOING"){
  					self.edges.add([{id: $rID, from: $nID, to: $parrentid, label: $relationship['relationshipType']}]);
    			}
    			else{
    				self.edges.add([{id: $rID, from: $parrentid, to: $nID, label: $relationship['relationshipType']}]);
    			}

    			self.nodeIDStore[$nID] = $node;
    			self.relationshipIDStore[$rID] = $relationshipID;

    			if($node["relationship"] != undefined){
    				for (var i = 0; i < $node["relationship"].length; i++){
					    var rel = $node["relationship"][i];
					    transfairToGraphRecursion($nID, rel);
					}
    			}
    		}
    	}

    	if(self.query["query"] !== undefined &&
    		self.query["query"]["node"] !== undefined){

    			var $rootNode = self.query["query"]["node"];
    		 	//Root node
    			 self.nodes.add([{id: 1, label: $rootNode["type"]}]);
    			 self.nodeIDStore[1] = $rootNode;

    			 //every additional node
    			 if($rootNode["relationship"] != undefined){
    				for (var i = 0; i < $rootNode["relationship"].length; i++){
					    var rel = $rootNode["relationship"][i];
					    transfairToGraphRecursion(1, rel);
					}
    			}
    	}
    };

   
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
	self.executeQueryCallback = function($success, $data, $status) {
		self.hasError = !$success;
		if($success){
			self.table = $data;
		}
		else
		{
			self.error = $data;
		}
	}

	self.executeQuery = function() {
		$requests.getResultFromQueryQueryBuilder(self.query, self.executeQueryCallback);
	}

	self.saveQueryCallback = function($success, $data, $status) {
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
		$requests.saveQuery(self.query, self.saveQueryCallback);
	}

	self.loadQuery = function(){

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

   /**
	Open Dilaog
   */
   self.onNodeClick = function(params) {

        var dialog = ngDialog.open({ template: 'querybuilder/nodeDialogTemplate.html',
        				className: 'ngdialog-theme-default custom-width',
        				controller: 'queryBuilderNodeDialogCtrl',
        				controllerAs: 'ctrl',
        				data: self.nodeIDStore[params["nodes"][0]]});

         dialog.closePromise.then(function (data) {
		     self.transfairToGraph();
		});
        
    };

    self.onEdgeClick = function(params) {
         var dialog = ngDialog.open({ template: 'querybuilder/nodeDialogTemplate.html',
        				className: 'ngdialog-theme-default custom-width',
        				controller: 'queryBuilderRelationshipDialogCtrl',
        				controllerAs: 'ctrl',
        				data: self.relationshipIDStore[params["edges"][0]]});
        dialog.closePromise.then(function (data) {
		     self.transfairToGraph();
		});
        
    };


}]);