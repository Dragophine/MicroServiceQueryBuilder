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
    
    // textArea mit syntax highlighting
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
    
//	self.paramcnt = -1;
	
	self.params = [];
	self.invalidSaveText = [];
	
	/**
	 * Immer wenn sich bei der Query etwas ändert, sollen sich die Parameter automatisch anpassen.
	 */
    self.myCodeMirror.on('change',function(cMirror){
    	  	// Parameter werden aktuell durch { erkannt, eventuell gibt es noch ein schönere Lösung...
    		var query = cMirror.getValue();
//    		var keys = query.split(/[{}]/);
    		var paramcnt = query.split("{").length - 1;
    		var keys = '';
    		var startIndex = 0;
    		var endIndex = 0;
    		
    		// Parameter wieder entfernen und neu generieren
    		self.params = [];
    		
    		/**
    		 * Der erste Parameter wird so ausgelesen, weil auch an der
    		 * Position 0 bereits ein Parameter stehen könnte, obwohl
    		 * das keine korrektes Statement sein wird...
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
		Query fields
	*/
//	self.query = "";
	self.name = "";
	self.description = "";
	self.category = "";
	
	/**
	 * Parameter fields
	 */
//	self.key = "";
//	self.value = "";
//	self.type = "";

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


		self.isObject = function ($obj) {
  		return $obj === Object($obj);
	}
	
	// load query dialog
	self.queries = [];

	// Get the modal
	var modalSave = document.getElementById('myModalSave');
	var modalLoad = document.getElementById('myModalLoad');
	var modalDelete = document.getElementById('myModalDelete');
	
	// Get the button that opens the modal
	var btnSave = document.getElementById("saveQuery");
	var btnLoad = document.getElementById("loadQuery");
	var btnDelete = document.getElementById("deleteQuery");

//	// Get the <span> element that closes the modal
//	var spanSave = document.getElementsByClassName("closeSave")[0];
//	var spanLoad = document.getElementsByClassName("closeLoad")[0];
	
	var saveAbbruch = document.getElementById("saveAbbruch");
	var loadAbbruch = document.getElementById("loadAbbruch");
	var deleteAbbruch = document.getElementById("deleteAbbruch");

	// When the user clicks on the button, open the modal 
	btnSave.onclick = function() {
		self.invalidSaveText = [];
		modalSave.style.display = "block";
		
	}	
	btnLoad.onclick = function() {
		$requests.loadQuery(self.queriesCB);
		modalLoad.style.display = "block";
	}
	btnDelete.onclick = function() {
		$requests.loadQuery(self.queriesCB);
		modalDelete.style.display = "block";
	}

//	// When the user clicks on <span> (x), close the modal
//	spanSave.onclick = function() {
//		// Zurücksetzen der Werte sonst werden sie beim nächsten Öffnen vom Save-Dialog gleich wieder angezeigt.
//		self.name = "";
//		self.description = "";
//		self.category = "";
//		// Schließe Save-Dialog
//		modalSave.style.display = "none";
//	}
//	spanLoad.onclick = function() {
//		modalLoad.style.display = "none";
//	}
	saveAbbruch.onclick = function() {
		// Zurücksetzen der Werte sonst werden sie beim nächsten Öffnen vom Save-Dialog gleich wieder angezeigt.
		self.name = "";
		self.description = "";
		self.category = "";
		// Schließe Save-Dialog
		modalSave.style.display = "none";
	}
	loadAbbruch.onclick = function() {
		modalLoad.style.display = "none";
	}
	deleteAbbruch.onclick = function() {
		modalDelete.style.display = "none";
	}

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
	
	self.queriesCB = function($success, $data, $status){
		self.hasError = !$success;
		if($success)
		{
//			self.queries = $data;
			
			/**
			 * Da die Query auch Parameter, etc. returniert
			 * müssen die Queries rausgesucht werden.
			 */
			self.queries = [];
			
			var i;
			// queries mit Parameter
			for(i = 0; i < $data.length; i++)
			{
				if($data[i].n != null && !contains(self.queries, $data[i].n))
				{
					self.queries.push($data[i].n);
				}
			}
			
			// queries ohne Parameter
			for(i = 0; i < $data.length; i++)
			{
				if($data[i].m != null && !contains(self.queries, $data[i].m))
				{
					self.queries.push($data[i].m);
				}
			}
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
		$requests.loadQuery(self.queriesCB);
		
		if(!existsQueryName(queryName))
		{
			self.invalidSaveText = [];
			$requests.saveQuery(self.myCodeMirror.getValue(), self.params, 
					queryName, self.description, self.category, self.callback);
			// Zurücksetzen der Werte sonst werden sie beim nächsten Öffnen vom Save-Dialog gleich wieder angezeigt.
			self.name = "";
			self.description = "";
			self.category = "";
			// Schließe Save-Dialog
			modalSave.style.display = "none";
		}
		else
		{
			self.invalidSaveText.push("Der Queryname existiert bereits, " +
					"verwenden Sie bitte einen anderen Querynamen.");
		}
	}
	
	self.deleteSelectedQuery = function($query) {

		$requests.deleteQuery($query.query, $query.parameter, 
				$query.name, $query.description, $query.category, self.callback);
		
		// Schließe Delete-Dialog
		modalDelete.style.display = "none";
	}
	
//	self.loadQuery = function() {
//		self.queries = $requests.loadQuery(self.queriesCB);
//		dialogLoadQuery.showModal();
//	}
	
	/**
	 * Hier wird festgelegt was passieren soll, wenn der User ein Load-Dialog einen Eintrag
	 * auswählt.
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
		
		// Schließe Load-Dialog
		modalLoad.style.display = "none";
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