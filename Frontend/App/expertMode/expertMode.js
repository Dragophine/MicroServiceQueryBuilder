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
    
	self.params = [];
	self.invalidSaveText = [];
	
	self.loadOrDeleteDialog;
	
	/**
	 * Whenever the query changes, the parameters should be adjusted automatically.
	 * Parameters are currently recognized by "{".
	 * 
	 * @param {Object} cMirror - is the "CodeMirror" textarea from view.
	 */
    self.myCodeMirror.on('change',function(cMirror){
    		var query = cMirror.getValue();
    		$rootScope.expertQuery = query;
    		var paramcnt = query.split("{").length - 1;
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
    		startIndex = query.indexOf("{", startIndex);
    		endIndex = query.indexOf("}", startIndex);
    		keys = query.substring(startIndex+1, endIndex);
    		    		
    		var i;
    		for(i = 0; self.params.length < paramcnt; i++)
    		{
        		self.params.push.apply(self.params, [{key: keys, type : "int", value : ""}]);
        		startIndex = query.indexOf("{", startIndex+1);
        		endIndex = query.indexOf("}", startIndex+1);
        		keys = query.substring(startIndex+1, endIndex);
    		}
    	});
    
	self.paramoptions = ["int", "String"];

	/** 
	 * The following three variables are query fields.
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
	
	// load query dialog
	self.queries = [];

	// Get the modal
	var modalSave = document.getElementById('myModalSave');
	
	// Get the button that opens the modal
	var btnSave = document.getElementById("saveQuery");
	var btnLoad = document.getElementById("loadQuery");
	var btnDelete = document.getElementById("deleteQuery");

	var saveAbbruch = document.getElementById("saveAbbruch");

	// When the user clicks on the button, open the modal 
	btnSave.onclick = function() {
		self.invalidSaveText = [];
		modalSave.style.display = "block";
		
	}	
	btnLoad.onclick = function() {
		$requests.loadAllQueries(self.queriesCB);
		
		var dialog = ngDialog.open({ template: 'expertMode/loadDialogExpertMode.html',
			className: 'ngdialog-theme-default custom-width',
			controller: 'loadDialogExpertModeCtrl',
			controllerAs: 'ctrl'});
		
		dialog.closePromise.then(function ($data) {
        	if($data !== undefined && $data.value !== undefined &&
        			$data.value.query !== undefined)
        	{
        		self.select($data.value);
        	}
		});
	}
	
	btnDelete.onclick = function() {
		$requests.loadAllQueries(self.queriesCB);
		
		var dialog = ngDialog.open({ template: 'expertMode/loadDialogExpertMode.html',
			className: 'ngdialog-theme-default custom-width',
			controller: 'loadDialogExpertModeCtrl',
			controllerAs: 'ctrl'});
		
		dialog.closePromise.then(function ($data) {
        	if($data !== undefined && $data.value !== undefined &&
        			$data.value.query !== undefined)
        	{
        		self.select($data.value);
        	}
		});
	}

	saveAbbruch.onclick = function() {
		// Reset values, otherwise they will be displayed again by in next save dialog.
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
	
	function contains(a, obj)
	{
	    for (var i = 0; i < a.length; i++)
	    {
	        if (a[i].category === obj.category &&
	        		a[i].description === obj.description &&
	        		a[i].name === obj.name)
	        {
	            return true;
	        }
	    }
	    return false;
	}

	/**
		Start request
	*/
	self.submitQuery = function() {
		$requests.getResultFromQuery(self.myCodeMirror.getValue(), self.params, self.callback);
	}
	
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

	self.availableCategories = [];
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

	self.changeCategories = function() {
		self.categoryName = self.categoryName;
	}
	self.checkCategory = function($category) {

		return " "+$category+" " == self.categoryName || self.categoryName == ""
	};
}]);