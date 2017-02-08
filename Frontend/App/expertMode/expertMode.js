'use strict';

angular.module('queryBuilder.expertMode', ['ngRoute', 'queryBuilder.services'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/expertmode', {
    templateUrl: 'expertMode/expertMode.html'
  });
}])
/**
   * This controller handles the main operation in the user interface.
   * It configures and handle all actions in the expert mode.
   * 
   * @version 1.0.0
   */
.controller('expertModeCtrl', ['$requests','$rootScope', 'ngDialog',
	function($requests, $rootScope, ngDialog) {

    var self = this;
    
    /**
     * Uses a "CodeMirror" textArea with syntax highlighting.
     * For further information about CodeMirror look at
     * https://codemirror.net/
     */
    self.myCodeMirror = CodeMirror.fromTextArea(document.getElementById('query'), {
        mode: 'cypher',
        indentWithTabs: true,
        smartIndent: true,
        lineNumbers: true,
        matchBrackets : true,
        autofocus: true,
        theme: 'neo',
        viewportMargin: Infinity
      });
    
    self.query = "";
    /**
     * Store the parameter from selected query in this array.
     * @type {array}
     */
	self.params = [];
    /**
     * If a value from expert query is missing or invalid in save dialog
     * a message will be stored in this array.
     * @type {array}
     */
	self.invalidSaveText = [];
	
	/**
	 * Whenever the query changes, the parameters should be adjusted automatically.
	 * Parameters are currently recognized by "{".
	 * 
	 * @param {Object} cMirror - is the "CodeMirror" textarea from view.
	 */
    self.myCodeMirror.on('change',function(cMirror){
    		self.query = cMirror.getValue();
    		$rootScope.expertQuery = query;
    		var paramcnt = self.query.split("{").length - 1;
    		var keys = '';
    		var startIndex = 0;
    		var endIndex = 0;
    		
    		// Removing and regenerating parameters
    		self.params = [];
    		
    		/**
    		 * The first parameter is read out because a parameter could already 
    		 * be present at position 0, although this will not be a correct 
    		 * statement ...
    		 */
    		startIndex = self.query.indexOf("{", startIndex);
    		endIndex = self.query.indexOf("}", startIndex);
    		keys = self.query.substring(startIndex+1, endIndex);
    		    		
    		var i;
    		for(i = 0; self.params.length < paramcnt; i++)
    		{
    			self.params.push.apply(self.params, [{key: keys, type : "int", value : ""}]);
        		startIndex = self.query.indexOf("{", startIndex+1);
        		endIndex = self.query.indexOf("}", startIndex+1);
        		keys = self.query.substring(startIndex+1, endIndex);
    		}
//    		self.myCodeMirror.refresh();
        	$rootScope.$apply();
    	});
    	
    /**
     * Selectable parameter types.
     */
	self.paramoptions = ["int", "String", "double", "long", "boolean"];
	/** 
	 * The following three variables are query fields in save dialog.
	 */
	self.name = "";
	self.description = "";
	self.category = "";

	//sets actual query from expert mode
	if(	$rootScope.expertQuery !== undefined){
		self.myCodeMirror.setValue($rootScope.expertQuery);
	}
	//set query from querybuilder to expertmode
	else if($rootScope.queryBuilderQueryInCypher !== undefined){
		self.myCodeMirror.setValue($rootScope.queryBuilderQueryInCypher );
	}

	/** 
		Table json (simply the result)
	*/
	self.table = "";
	 /**
     * Holds the error string which is displayed to the user.
     * The table is only shown when the hasError property is set to TRUE.
     * @type {string}
     */
	self.error = "";
	 /**
     * If there was an error during the execution of the query this is set to 
     * true and the error will be displayed. Otherwise the table will be displayed.
     * @type {boolean}
     */
	self.hasError = false;	
	/**
	 * All existing queries. This queries will be shown in load/delete dialog.
     * @type {array}
	 */
	self.queries = [];
	/**
	 * Modal dialog for saving queries.
     * @type {object}
	 */
	var modalSave = document.getElementById('myModalSave');	
	/**
	 * Save button which opens save modal dialog.
     * @type {object} 
	 */
	var btnSave = document.getElementById("saveQuery");
	/**
	 * Load button which opens load/delete ngDialog.
     * @type {object} 
	 */
	var btnLoad = document.getElementById("loadQuery");
	/**
	 * Delete button which opens load/delete ngDialog.
     * @type {object} 
	 */
	var btnDelete = document.getElementById("deleteQuery");
	/**
	 * cancel-button from modal dialog for missing or invalid data.
     * @type {object}
	 */
	var saveAbbruch = document.getElementById("saveAbbruch");

	/**
	 * When the user clicks on the button, open the modal dialog
	 * for saving queries.
	 */
	btnSave.onclick = function() {
		self.invalidSaveText = [];
		modalSave.style.display = "block";
		
	}
	/**
	 * When the user clicks on the button, open the ngDialog
	 * for loading/deleting queries.
	 */
	btnLoad.onclick = function() {
		$requests.loadAllQueries(self.queriesCB);
		
		var dialog = ngDialog.open({ template: 'expertMode/loadDialogExpertMode.html',
			className: 'ngdialog-theme-default custom-width',
			controller: 'loadDialogExpertModeCtrl',
			controllerAs: 'ctrl'});
		
		/**
		 * Close ngDialog is only possible if user select a load button or
		 * select close button. Check if user has select the load button.
		 */
		dialog.closePromise.then(function ($data) {
        	if($data !== undefined && $data.value !== undefined &&
        			$data.value.query !== undefined)
        	{
        		self.select($data.value);
        	}
		});
	}
	/**
	 * When the user clicks on the button, open the ngDialog
	 * for loading/deleting queries.
	 */	
	btnDelete.onclick = function() {
		$requests.loadAllQueries(self.queriesCB);
		
		var dialog = ngDialog.open({ template: 'expertMode/loadDialogExpertMode.html',
			className: 'ngdialog-theme-default custom-width',
			controller: 'loadDialogExpertModeCtrl',
			controllerAs: 'ctrl'});
		
		/**
		 * Close ngDialog is only possible if user select a load button or
		 * select close button. Check if user has select the load button.
		 */
		dialog.closePromise.then(function ($data) {
        	if($data !== undefined && $data.value !== undefined &&
        			$data.value.query !== undefined)
        	{
        		self.select($data.value);
        	}
		});
	}

	/**
	 * User has cancelled the save dialog. Reset values, otherwise they will be 
	 * displayed again by in next save dialog.
	 */
	saveAbbruch.onclick = function() {
		self.name = "";
		self.description = "";
		self.category = "";
		// close save dialog
		modalSave.style.display = "none";
	}

	/**
	 * Callback from execute, delete and save query. If query was successful show result in table.
	 * Otherwise print error.
	 * 
	 * @param {boolean} $success - true when there are no errors.
	 * @param {Object} $data - the requested data.
     * @param {number} $status - the actual server status.
	 */
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
	 * Callback from get all queries call. If query was successful save data in variable self.queries.
	 * Otherwise print error.
	 * 
	 * @param {boolean} $success - true when there are no errors.
	 * @param {Object} $data - the requested data (In this case the queries).
     * @param {number} $status - the actual server status.
	 */
	self.queriesCB = function($success, $data, $status){
		self.hasError = !$success;
		if($success)
		{
			self.queries = $data;
		}
		else
		{
			self.error = $data;
		}
	}

	/**
	 * Execute actual query.
	 */
	self.submitQuery = function() {
		$requests.getResultFromQuery(self.myCodeMirror.getValue(), self.params, self.callback);
	}
	
	/**
	 * Save actual query if all fields in save dialog were filled correctly and query name
	 * do not exists until now.
	 */
	self.saveQuery = function()
	{
		self.invalidSaveText = [];
		var queryName = self.name;
		$requests.loadAllQueries(self.queriesCB);
		
		if(!existsQueryName(queryName))
		{
			console.log(self.categoryName);
			self.invalidSaveText = [];
			$requests.saveQuery(self.myCodeMirror.getValue(), self.params, 
					queryName, self.description, self.categoryName, self.callback);
			// Reset values, otherwise they will be displayed again by in next save dialog.
			self.name = "";
			self.description = "";
			self.category = "";
			// Close save dialog
			modalSave.style.display = "none";
		}
		else
		{
			self.invalidSaveText.push("Der Queryname existiert bereits, " +
					"verwenden Sie bitte einen anderen Querynamen.");
		}
	}
	
	/**
	 * Delete actual query.
	 */
	self.deleteSelectedQuery = function($query)
	{
		$requests.deleteQuery($query.query, $query.parameter, 
				$query.name, $query.description, $query.category, self.callback);
	}
	
	/**
	 * Load selected query.
	 * 
	 * @param {Object} $query - query which should be load.
	 */
	self.select = function($query) {		
		self.myCodeMirror.setValue($query.query);
		
		self.params = [];
		var arrLength = $query.parameter.length;
		var i;
		for(i = 0; self.params.length < arrLength; i++)
		{
    		self.params.push.apply(self.params, [{key: $query.parameter[i].key, type : $query.parameter[i].type,
    			value : $query.parameter[i].value}]);
		}
	}
	
	/**
	 * Check if the queryName already exists.
	 * 
	 * @param {Object} $$queryName - query name which should be checked.
	 */
	function existsQueryName($queryName)
	{
	    for (var i = 0; i < self.queries.length; i++)
	    {
	        if (self.queries[i].name === $queryName)
	        {
	            return true;
	        }
	    }
	    return false;
	}

	/**
	 * Stores all available categories.
     * @type {array} 
	 */
	self.availableCategories = [];
	/**
	 * Actual selected category name.
     * @type {string} 
	 */
	self.categoryName = "";


	/**
	 * Callback from get all categories. If query was successful save data in variable self.availableCategories.
	 * The categories are displayed in the save dialog and in the load/delete dialog.
	 * Otherwise print error.
	 * 
	 * @param {boolean} $success - true when there are no errors.
	 * @param {Object} $data - the requested data (In this case the categories).
     * @param {number} $status - the actual server status.
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