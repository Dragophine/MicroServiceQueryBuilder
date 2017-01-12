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
    	"id":"",
    	"name":"",
    	"description":"",
    	"category":"",
    	"limitCount": "",
    	"node":""          
    }

    self.nodeIDStore = {};
    self.relationshipIDStore = {};
    /**
	holdes the selected node in the vis network
    */
    self.selectedNode = undefined;
	/**
	Holdes the available nodes at the beginning of the selection prozess.
	*/
	self.availableNodes = "";

	/**
		Result Properties
	*/
	self.table = "";
	self.hasError = false;
	self.error = "";
    /**
	Transfair changes to graph
    */
    self.transfairToGraph = function($network){
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
    			self.relationshipIDStore[$rID] = $relationship;

    			if($node["relationship"] != undefined){
    				for (var i = 0; i < $node["relationship"].length; i++){
					    var rel = $node["relationship"][i];
					    transfairToGraphRecursion($nID, rel);
					}
    			}
    		}
    	}

    	if(self.query["node"] !== undefined ){

    			var $rootNode = self.query["node"];
    		 	//Root node
    			 self.nodes.add([{id: 1, label: $rootNode["type"], x: 0, y: 0}]);
    			 self.nodeIDStore[1] = $rootNode;

    			 //every additional node
    			 if($rootNode["relationship"] != undefined){
    				for (var i = 0; i < $rootNode["relationship"].length; i++){
					    var rel = $rootNode["relationship"][i];
					    transfairToGraphRecursion(1, rel);
					}
    			}
    	}
    	if($network !== undefined){
    		$network.redraw();
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

    self.showInfoDialog = function($data){
    	var dialog = ngDialog.open({ template: 'querybuilder/infoDialog.html',
        				className: 'ngdialog-theme-default custom-width',
        				controller: 'querybuilderInfoDialogCtrl',
        				controllerAs: 'ctrl',
        				data:$data
        		 });
    }
	

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
		var $dataForDialog = undefined;
		if($success){
			$dataForDialog = {
				"head":"Successfully saved",
				"content":"The query was saved successfully saved."
			};
			self.query.id = $data;
		}
		else
		{
			$dataForDialog = {
				"head":"Error when saving",
				"content":"Error when saving the query. Info: " + $data
			};
		}

		 self.showInfoDialog($dataForDialog);

		
	}

	self.saveQuery = function(){
		var $data =self.checkInputData();
    	if($data === undefined){

    		$requests.saveQueryInBuilder(self.query, self.saveQueryCallback);
    	}
    	else
    	{
    		self.showInfoDialog($data);
    	}
		
	}

	self.updateQueryCallback = function($success, $data, $status) {
		var $data = undefined;
		if($success){
			$data = {
				"head":"Successfully updated",
				"content":"The query was saved successfully updated."
			};
		}
		else
		{
			$data = {
				"head":"Error when updating",
				"content":"Error when updating the query. Info: " + $data
			};
		}

		 self.showInfoDialog($data);	
	}

	self.updateQuery = function(){
		var $data =self.checkInputData();
		if($data === undefined){
    		$requests.updateQueryInBuilder(self.query, self.query.id, self.updateQueryCallback);
    	}
    	else
    	{
    		self.showInfoDialog($data);
    	}
		
	}

	self.checkInputData = function(){
		var $data = undefined;
		if(self.query.name === "" || self.query.name === undefined
			|| self.query.name === null){
			$data = {
				"head":"No name",
				"content":"Please enter a name before you save/update the query."
			};
		}
		else if(self.query.description === ""|| self.query.description === undefined
			|| self.query.description === null){
			$data = {
				"head":"No description",
				"content":"Please enter a description before you save/update the query."
			};
		}
		else if(self.query.category === ""|| self.query.category === undefined
			|| self.query.category === null){
			$data = {
				"head":"No category",
				"content":"Please enter a category before you save/update the query."
			};
		}
		else if(self.query.node === ""|| self.query.node === undefined
			|| self.query.node === null){
			$data = {
				"head":"No node",
				"content":"Please enter a node before you save/update the query."
			};
		}
    	return $data;
	}

	self.loadQuery = function(){
		var dialog = ngDialog.open({ template: 'querybuilder/loadDialog.html',
	        				className: 'ngdialog-theme-default custom-width',
	        				controller: 'querybuilderLoadDialogCtrl',
	        				controllerAs: 'ctrl'});


        dialog.closePromise.then(function ($data) {
        	if($data !== undefined &&
        		$data.value !== undefined &&
        		$data.value.name !== undefined &&
        		$data.value.description !== undefined &&
        		$data.value.category !== undefined)
        	{
        		self.query = $data.value;

        		if(
        		   $data.value.node !== undefined &&
        		   $data.value.node !== "" &&
        		   $data.value.node.type !== undefined &&
        		   $data.value.node.type !== "" )
        		{
        		   	self.availableNodes =  [];
        		}
        		else{
        			$requests.getNodes(self.getNodesCB);
        		}
		     	self.transfairToGraph();
        	}
		});
	}
	
	self.deleteQueryCallback = function($success, $data, $status) {
		var $data = undefined;
		if($success){
			$data = {
				"head":"Successfully deleted",
				"content":"The query was saved successfully deleted."
			};
			self.newQuery();
		}
		else
		{
			$data = {
				"head":"Error when deleting",
				"content":"Error when deleting the query. Info: " + $data
			};
		}

		 self.showInfoDialog($data);
	}

	self.deleteQuery = function(){
		$requests.deleteQueryInBuilder(self.query.id, self.deleteQueryCallback);
		
	}

	self.newQuery = function(){
		self.query = {
			"id":"",
	    	"name":"",
	    	"description":"",
	    	"category":"",
	    	"limitCount": "",
	    	 "node":""
	    }
	    $requests.getNodes(self.getNodesCB);
		self.transfairToGraph();
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
		self.query['node'] = {
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


	self.deleteSelectedNode = function(){
		//if node is head
		if(self.query.node ===  self.selectedNode){
			self.selectedNode = undefined;
			self.query.node = undefined;
			$requests.getNodes(self.getNodesCB);
		}
		//if node is not head
		else{
			if(self.deleteRecursion(self.query.node, self.selectedNode) === 1){
				self.selectedNode = undefined;
			};
		}
		
		self.transfairToGraph();
	}

	self.deleteRecursion = function($nodeToCheck, $nodeToDelete){
		if($nodeToCheck === $nodeToDelete){
			return 0; //delte node;
		}
		else {
			for (var i = $nodeToCheck.relationship.length - 1; i >= 0; i--) {
				var res = self.deleteRecursion($nodeToCheck.relationship[i].node, $nodeToDelete);
				if(res === 0) //delete node
				{
					$nodeToCheck.relationship.splice(i, 1);
					return 1;
				}
				else if(res === 1) { //node already deleted
					return 1;
				}
				//if(res === 2) node not found.
			}
			return 2;//not found
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
			randomSeed: 50,

			hierarchical: {
		      enabled:true,
		      levelSeparation: 150,
		      nodeSpacing: 100,
		      treeSpacing: 200,
		      blockShifting: true,
		      edgeMinimization: true,
		      parentCentralization: true,
		      direction: 'UD',        // UD, DU, LR, RL
		      sortMethod: 'directed'   // hubsize, directed
		    }
		},
		interaction:{
		    zoomView: false
		}
	};


	self.onDoubleClick = function(params, network){
		var openNodeDialog = false;
		var openEdgeDialog = false;
		if(params.nodes.length > 0){
			 openNodeDialog = true;
		}
		else if(params.edges.length > 0){
			 openEdgeDialog = true;
		}
		
		if(openNodeDialog){
			self.openNodeDialog (params, network);
		}

		if(!openNodeDialog && openEdgeDialog){
			self.openEdgeDialog (params, network);
		}
	}

	self.onSelectClick = function(params, network){
		if(params.nodes.length > 0){
			self.selectedNode = self.nodeIDStore[params.nodes[0]];
		}
		else {
			self.selectedNode = undefined;
		}
		$scope.$apply();
	}


   /**
	Open Dilaog
   */

    self.openNodeDialog = function(params, network){
    	var $network = network;

    	var dialog = ngDialog.open({ template: 'querybuilder/nodeDialogTemplate.html',
        				className: 'ngdialog-theme-default custom-width',
        				controller: 'queryBuilderNodeDialogCtrl',
        				controllerAs: 'ctrl',
        				data: self.nodeIDStore[params["nodes"][0]]});


	         dialog.closePromise.then(function (data) {
			     self.transfairToGraph($network);
			    
			});
    };

    self.openEdgeDialog = function(params, network){
    		 var edgeId =params["edges"][0];
    		 var relationship =  self.relationshipIDStore[edgeId];
    		 var $network = network;

	         var dialog = ngDialog.open({ template: 'querybuilder/relationshipDialogTemplate.html',
	        				className: 'ngdialog-theme-default custom-width',
	        				controller: 'queryBuilderRelationshipDialogCtrl',
	        				controllerAs: 'ctrl',
	        				data:relationship});


	        dialog.closePromise.then(function (data) {
			     self.transfairToGraph($network);
			});
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
	$requests.getAllCategories(self.getCategories);


}]);