'use strict';

angular.module('queryBuilder.querybuilder', ['ngRoute', 'queryBuilder.services'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/querybuilder', {
    templateUrl: 'querybuilder/querybuilder.html'
  });
}])
/**
   * This controller handles the main operation in the user interface.
   * It configures vis.js, opens a dialog when a node or a relationship was clicked, 
   * draws the graph, executes the query and saves or updates the query.  
   * 
   * @version 1.0.0
   */
.controller('queryBuilderCtrl', ['$requests', '$rootScope', '$scope','ngDialog',
	function($requests, $rootScope, $scope, ngDialog) {
    var self = this;

     /**
     * The query which will be built with the query builder.
     * This saves the core information about the query.
     * @type {Object}
     */
    self.query = {
    	"id":"",
    	"name":"",
        "author":  $rootScope.principal.username,
    	"description":"",
    	"category":"",
    	"limitCount": "",
        "skip":"",
    	"distinct":false,
    	"node":""          
    }


    /**
     * The cypher query which is displayed to the user.
     * @type {string}
     */
   	self.displayQuery = "";

   	/**
     * The node ID Store saves the id of the node in the vis.js network with
     * the corresponding node in the query.
     * @type {Object}
     */
    self.nodeIDStore = {};
    /**
     * The relationship ID Store saves the id of the relationship
     * in the vis.js network with
     * the corresponding relationship in the query.
     * @type {Object}
     */
    self.relationshipIDStore = {};
    /**
     * Holds the selected node from the vis network.
     * @type {Object}
     */
    self.selectedNode = undefined;

    /**
     * Holds the selected releation from the vis network.
     * @type {Object}
     */
    self.selectedRelation = undefined;
	 /**
     * Holds the selected releation from the vis network.
     * @type {Object}
     */
	self.availableNodes = "";

	 /**
     * Holds the data for the output table in the querybuilder network.
     * The table is only shown when the hasError property is set to FALSE.
     * @type {Object}
     */
	self.table = "";
	 /**
     * If there was an error during the execution of the query this is set to 
     * true and the error will be displyed. Otherwise the table will be displayed.
     * @type {boolean}
     */
	self.hasError = false;
	 /**
     * Holds the error string which is displayed to the user.
     * The table is only shown when the hasError property is set to TRUE.
     * @type {string}
     */
	self.error = "";

	 /**
     * Holds all available categories.
     * @type {Object}
     */
	self.availableCategories = [];
   
   	/**
   	 * Transfers the actual query into a graph 
   	 * which can be displayed with vis.js.
	 * This method will be called whenever the self.query property
     * changes. 
     * Furthermore, this method creates the ids for the nodes and for the 
     * relationships which are required for the vis.js network.
   	 * In order to find out, which id corresponds to which node or to which relationship in 
   	 * the query a relationshipIDStore and a nodeIDStore is needed.
   	 *
   	 * @param {visnetwork} $network - the network behind the vis graph (optional).
   	 */
    self.transfersToGraph = function($network){
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

    	function transfersToGraphRecursion($parrentid, $relationship){
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
					    transfersToGraphRecursion($nID, rel);
					}
    			}
    		}
    	}

    	if(self.query["node"] !== undefined ){

    			var $rootNode = self.query["node"];
    		 	//Root node
    			 self.nodes.add([{id: 1, label: $rootNode["type"], color: '#009900'}]);
    			 self.nodeIDStore[1] = $rootNode;

    			 //every additional node
    			 if($rootNode["relationship"] != undefined){
    				for (var i = 0; i < $rootNode["relationship"].length; i++){
					    var rel = $rootNode["relationship"][i];
					    transfersToGraphRecursion(1, rel);
					}
    			}
    	}
    	if($network !== undefined){
    		$network.fit();
    		$network.redraw();
    	}
    	
    };

  	 /**
    * This method opens an info dialog. It displays a certain data (head and content)
    * given in the $data. Furthermore, a callback can be specified, when the dialg is closed.
    *
    * @param {!Object} $data - the head an the content for the info dialog.
    * @param {?function} infoClosePromis - The function which should be called when the dialog is closed.
    */
    self.showInfoDialog = function($data, infoClosePromis){
    	var dialog = ngDialog.open({ template: 'querybuilder/infoDialog.html',
        				className: 'ngdialog-theme-default custom-width',
        				controller: 'querybuilderInfoDialogCtrl',
        				controllerAs: 'ctrl',
        				data:$data
        		 });
    	if(infoClosePromis !== undefined){
    		dialog.closePromise.then(infoClosePromis);
    	}
    }
	
     /**
    * This method checks whether a query is valid before updating and saving.
    * Only valid queries should be sent to the server.
    * 
    * @return {Object} The error message or undefined.
    */
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

	  /**
        * The callback when the requested query was loaded.
        * The query is the cypher query which is displayed to the user.
        *
        * @param {boolean} $success - true when there are no errors.
        * @param {Object} $data - the requested data (In this case the query).
        * @param {number} $status - the actual server status.
        */
	self.getQueryCallback = function($success, $data, $status){
		if($success){
			self.displayQuery = $data;
			$rootScope.queryBuilderQueryInCypher = $data.query;
		}
		else
		{
			self.displayQuery = "";
			$rootScope.queryBuilderQueryInCypher = undefined;
		}
    }

    /**
     * This method should be (will be) called every time the query changes.
     * It saves a copy of the actual query to the root scope. This is 
     * necessary if the user switches tabs he wants to have the same 
     * query when he comes back. Furthermore, the cypher query will be created.
     */
    self.onQueryChanged = function(){
    	$rootScope.queryBuilderOldQuery = self.query;
    	$requests.getQueryFromQueryQueryBuilder(self.query, self.getQueryCallback);
    }

   
 	/**
    * The callback is executed when the query is executed.
    * If there is an error in the query the error will be displayed otherwise the
    * result.
    *
    * @param {boolean} $success - true when there are no errors.
    * @param {Object} $data - the requested data (The error or the result of the query.).
    * @param {number} $status - the actual server status.
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

	 /**
     * This method executes a query and displays the result in a table 
     * or displays the error message.
     */
	self.executeQuery = function() {
		self.onQueryChanged();
		$requests.getResultFromQueryQueryBuilder(self.query, self.executeQueryCallback);
	}

	  /**.
        * The callback when user requested to save the query.
        * This method displays a success or an error message to the user.
        * Only new queries can be saved. If an existing should be saved an
        * update needs to be done.
        *
        * @param {boolean} $success - true when there are no errors.
        * @param {Object} $data -. the requested data (The new id of the query).
        * @param {number} $status - the actual server status.
        */
	self.saveQueryCallback = function($success, $data, $status) {
		var $dataForDialog = undefined;
		if($success){
			$dataForDialog = {
				"head":"Successfully saved",
				"content":"The query was saved successfully."
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

	 /**
     * This method saves a query and displays if the process was successful or not.
     * Only new queries can be saved. If an existing should be saved an
     * update needs to be done.
     */
	self.saveQuery = function(){
		var $data =self.checkInputData();

    	if($data === undefined){

    		$requests.saveQueryInBuilder(self.query, self.saveQueryCallback);
    	}
    	else
    	{
    		self.showInfoDialog($data);
    	}
		self.onQueryChanged();
	}

	  /**.
        * The callback when user requested to update the query.
        * This method displays a success or an error message to the user.
        * Before one can do an update, the query must be saved first.
        *
        * @param {boolean} $success - true when there are no errors.
        * @param {Object} $data - useless, because no data was requested.
        * @param {number} $status - the actual server status.
        */
	self.updateQueryCallback = function($success, $data, $status) {
		var $data = undefined;
		if($success){
			$data = {
				"head":"Successfully updated",
				"content":"The query was updated successfully."
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

	/**
	 * This method updates a certain query.
	 * This method displays a success or an error message to the user.
     * Before one can do an update, the query must be saved first.
	 */
	self.updateQuery = function(){
		var $data =self.checkInputData();
		if($data === undefined){
    		$requests.updateQueryInBuilder(self.query, self.query.id, self.updateQueryCallback);
    	}
    	else
    	{
    		self.showInfoDialog($data);
    	}
		self.onQueryChanged();
	}


	/**
	 * This method contains the logic for loading a query.
	 * It sets all the paramerters according to the data of the query.
	 * (Initializes vis.js, load initial nodes if they are required, shows cypher query, e.g.)
	 * 
	 * @param {Object} $query - the query which should be loaded.
	 */
	self.loadQueryLogic = function($query){
		if( $query !== undefined &&
    		$query.name !== undefined &&
    		$query.description !== undefined &&
    		$query.category !== undefined)
    	{
    		self.query = $query;
    		self.selectedNode = undefined;
    		self.selectedRelation = undefined;
    		self.table = "";
    		self.onQueryChanged();
    		self.hasError = false;

    		//checks weather query has already properties or is empty
    		if(
    		   $query.node !== undefined &&
    		   $query.node !== "" &&
    		   $query.node.type !== undefined &&
    		   $query.node.type !== "" )
    		{
    		   	self.availableNodes =  [];
    		}
    		else{
    			$requests.getNodes(self.getNodesCB);
    		}
	     	self.transfersToGraph(self.network);
    	}
	}


	/**
	 * This method opens the load query dialog.
	 * If the user selects a certain query it loads it.
	 */
	self.loadQuery = function(){
		var dialog = ngDialog.open({ template: 'querybuilder/loadDialog.html',
	        				className: 'ngdialog-theme-default custom-width',
	        				controller: 'querybuilderLoadDialogCtrl',
	        				controllerAs: 'ctrl'});


        dialog.closePromise.then(function ($data) {
        	if($data !== undefined)
        	{
        		self.loadQueryLogic($data.value);
        	}
		});
	}


	

   /**.
    * The callback when user requested to delete the query.
    * This method displays a success or an error message to the user.
    *
    * @param {boolean} $success - true when there are no errors.
    * @param {Object} $data - useless, because no data was requested.
    * @param {number} $status - the actual server status.
    */
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

	/**
	 * This method deletes a certain query.
	 * It needs the id from the actual query.
	 */
	self.deleteQuery = function(){
		$requests.deleteQueryInBuilder(self.query.id, self.deleteQueryCallback);
		
	}

	/**
	 * This method creates a new query by overwriting the actutal one.
	 */
	self.newQuery = function(){
		var query = {
			"id":"",
            "name":"",
            "author":  $rootScope.principal.username,
            "description":"",
            "category":"",
            "limitCount": "",
            "skip":"",
            "distinct":false,
            "node":""      
	    }
	    self.loadQueryLogic(query);
	}


	/**
    * The callback when the requested categories were loaded
    *
    * @param {boolean} $success - true when there are no errors.
    * @param {Object} $data - the requested data (In this case the categories).
    * @param {number} $status - the actual server status.
    */
	self.getCategoriesCallback = function($success, $data, $status){
		if($success){
			self.availableCategories = $data;
		}
	}
	/**
	 * This command loads all categories.
	 */
	$requests.getAllCategories(self.getCategoriesCallback);

	 /**
   	 * The callback when the requested nodes were loaded.
     * The nodes are all node types that exist in the database.
     *
     * @param {boolean} $success - true when there are no errors.
     * @param {Object} $data - the requested data (In this case the nodes).
     * @param {number} $status - the actual server status.
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

	 /**
   	 * This method adds the first node to a query.
   	 * The next nodes will be added with the nodeDialog.
     *
     * @param {string} $type - the type of the node.
     */
	self.addNode = function($type){
		/**
		Add initial node
		*/
		self.query['node'] = {
			"type": $type,
			"returnAttributes": [],
			"filterAttributes": [],
			"orderByAttributes": [],
			"relationship":[]
		}
		

		self.availableNodes = [];
		self.onQueryChanged();
		self.transfersToGraph(self.network);
	}

	 /**
   	 * This method searches the selected node and deletes it.
   	 * The selected node is saved in the property self.selectedNode. It firsts
   	 * checks whether the first node is the head node of the query.
   	 * If not, then it searches for the node in the query relationship tree and deletes it.
     */
	self.deleteSelectedNode = function(){
		//if node is head
		if(self.query.node ===  self.selectedNode){
			self.selectedNode = undefined;
			self.selectedRelation = undefined;
			self.query.node = undefined;
			self.table = "";
			self.hasError = false;
			$requests.getNodes(self.getNodesCB);
		}
		//if node is not head
		else{
			if(self.deleteRecursion(self.query.node, self.selectedNode) === 1){
				self.selectedNode = undefined;
				self.selectedRelation = undefined;
			};
		}
		self.onQueryChanged();
		self.transfersToGraph(self.network);
		$scope.$apply();
	}

	/**
   	 * This method deletes the $nodeToDelete in the $nodeToCheck tree.
   	 * It goes along each relationship and searches for the right node.
   	 *
   	 * @param {Object} $nodeToCheck - The node tree.
     * @param {Object} $nodeToDelete - The node which should be deleted.
     * @return {number} O if this is the node which should be deleted. 
     *					1 if the node was deleted. 
     *					2 if the node was not found.
     */
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
	 /**
     * The network used in vis.js.
     * @type {Object}
     */
	self.network = undefined;
	 /**
     * The representation of the nodes in vis.js.
     * @type {Object}
     */
	self.nodes = new vis.DataSet();
	 /**
     * The representation of the edges in vis.js.
     * The edges represnt the relationships.
     * @type {Object}
     */
    self.edges = new vis.DataSet();

    /**
     * The date for the vis.js network. 
     * @type {Object}
     */
    self.network_data = {
        nodes: self.nodes,
        edges: self.edges
    };

     /**
     * The options for the vis.js network.
     * For further documentation, please look at the vis.js website.
     * http://visjs.org/docs/network/
     * @type {Object}
     */
    self.network_options = {
    	
    	width:  '100%',
    	height: '500px',
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
		      edgeMinimization: false,
		      parentCentralization: true,
		      direction: 'UD',        // UD, DU, LR, RL
		      sortMethod: 'directed'   // hubsize, directed
		    }
		},
		interaction:{
		    zoomView: false,
		    navigationButtons: true,
	      	keyboard: true
		}
	};


     /**
     * This method is called whenever a node or an edge was double clicked.
     * It opens the edge or the node dialog.
     *
     * @param {Object} params - The parameters from the vis.js double click event. Further information: http://visjs.org/docs/network/
     * @param {Object} network - The actual network, which is displayed in the query builder.
     */
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
	/**
     * This method is called whenever a node or an edge was selected.
     * It saves the selected node and the selected edge.
	 *
     * @param {Object} params - The parameters from the vis.js Select click event. Further information: http://visjs.org/docs/network/
     * @param {Object} network - The actual network, which is displayed in the query builder.
     */
	self.onSelectClick = function(params, network){
		if(params.nodes.length > 0){
			self.selectedNode = self.nodeIDStore[params.nodes[0]];
		}
		else {
			self.selectedNode = undefined;
		}

		if(params.edges.length > 0){
			self.selectedRelation = self.relationshipIDStore[params.edges[0]];
		}
		else {
			self.selectedRelation = undefined;
		}

		$scope.$apply();
	}


   /********
   Dilaog in vis.js
   *********/
   /**
    *
    * This method checks whether the brackets of the filter attributes were set correctly.
    * e.g. There must be as many opening brackets as closing brackets. One cannot start with a closing
    * bracket.
    * 
    * @param {Object} filterAttributes - The filter attributes.
    * @return {string} Returns a list of wrong attributes (attributes where the brackets were not set correctly). 
    */
   self.checkBrackets = function(filterAttributes){
		var wrongAttributesNames = "";
		if(filterAttributes != undefined){

	   		for (var i = 0; i < filterAttributes.length; i++) {	

				var bracketCount = 0;

				if(filterAttributes[i].filters != undefined){
					for (var k = 0; k < filterAttributes[i].filters.length; k++) {
		    			if(filterAttributes[i].filters[k].isBracketOpen){
		    				bracketCount = bracketCount +1;
		    			}
						if(filterAttributes[i].filters[k].isBracketClosed){
		    				bracketCount = bracketCount - 1;
		    			}

		    			if(bracketCount < 0){
		    				break;
		    			}
		    		}	
				}   

				if(bracketCount != 0){
					if(wrongAttributesNames != ""){
						wrongAttributesNames = wrongAttributesNames + ", ";
					}
					wrongAttributesNames  = wrongAttributesNames + filterAttributes[i].attributeName;
				} 		
			}
	    }
	    return wrongAttributesNames;
   }

      /**
     * This method opens a new node dialog.
     * In order to find the right node, it uses the id provided by vis.js.
     * With this id the actual node can by found based on the self.nodeIDStore.
     *
     * @param {Object} params - The parameters from the vis.js click event. Further information: http://visjs.org/docs/network/
     * @param {Object} network - The actual network, which is displayed in the query builder.
     */
    self.openNodeDialog = function(params, network){
    	var $network = network;
    	var params = params;
    	var node = self.nodeIDStore[params["nodes"][0]];

    	var dialog = ngDialog.open({ template: 'querybuilder/nodeDialogTemplate.html',
        				className: 'ngdialog-theme-default custom-width',
        				controller: 'queryBuilderNodeDialogCtrl',
        				controllerAs: 'ctrl',
        				data: 
                        {
                            "node": node,
                            "author": self.query.author
                        }
                    });


	    dialog.closePromise.then(function (data) {
	    	
	    	var wrongAttributesNames = self.checkBrackets(node.filterAttributes);
	    	
	    	if(wrongAttributesNames != ""){
	    		var $dataForDialog = {
					"head":"Error on closing",
					"content":"Wrong brackes in filter: " + wrongAttributesNames
				};

				var infoClosePromis = function(data){
					self.openNodeDialog(params, $network);
				}
				self.showInfoDialog($dataForDialog, infoClosePromis);
	    		
	    	}
	    	self.onQueryChanged();
			self.transfersToGraph($network);
			 
		});
    };

     /**
     * This method opens a new relationship dialog.
     * In order to find the right relationship, it uses the id provided by vis.js.
     * With this id the actual relationship can by found based on the self.relationshipIDStore.
     *
     * @param {Object} params - The parameters from the vis.js click event. Further information: http://visjs.org/docs/network/
     * @param {Object} network - The actual network, which is displayed in the query builder.
     */
    self.openEdgeDialog = function(params, network){
    		 var edgeId =params["edges"][0];
    		 var relationship =  self.relationshipIDStore[edgeId];
    		 var params = params;
    		 var $network = network;

	         var dialog = ngDialog.open({ template: 'querybuilder/relationshipDialogTemplate.html',
	        				className: 'ngdialog-theme-default custom-width',
	        				controller: 'queryBuilderRelationshipDialogCtrl',
	        				controllerAs: 'ctrl',
	        				data:
                            {
                                "relationship": relationship,
                                "author": self.query.author
                            }
                        });


	        dialog.closePromise.then(function (data) {
			    
		    	var wrongAttributesNames = self.checkBrackets(relationship.filterAttributes);
		    	
		    	if(wrongAttributesNames != ""){
		    		var $dataForDialog = {
						"head":"Error on closing",
						"content":"Wrong brackes in filter: " + wrongAttributesNames
					};

					var infoClosePromis = function(data){
						self.openEdgeDialog(params, $network);
					}
					self.showInfoDialog($dataForDialog, infoClosePromis);
		    		
		    	}
		    	self.onQueryChanged();
				self.transfersToGraph($network);
			});
    }

	/**
	 * Loads the old query from $rootScope if it is available.
	 * If there is no old query available then load the nodes to initialize the query.
	 */
	if($rootScope.queryBuilderOldQuery !== undefined){
        self.loadQueryLogic($rootScope.queryBuilderOldQuery);
    }
    else{
    	$requests.getNodes(self.getNodesCB);
    }


}]);