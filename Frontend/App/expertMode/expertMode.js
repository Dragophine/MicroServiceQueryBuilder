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
        theme: 'neo'
      });
    
//	self.paramcnt = -1;
	
	self.params = [];
	
	/**
	 * Immer wenn sich bei der Query etwas ändert, sollen sich die Parameter automatisch anpassen.
	 */
    self.myCodeMirror.on('change',function(cMirror){
    	  	// Parameter werden aktuell durch { erkannt, eventuell gibt es noch ein schönere Lösung...
    		var query = cMirror.getValue();
//    		var keys = query.split(/[{}]/);
    		var paramcnt = query.split("{").length - 1;
    		
    		if(self.params.length < paramcnt)
			{
        		var i;
        		for(i = 0; self.params.length < paramcnt; i++)
        		{
            		self.params.push.apply(self.params, [{key: "", type : "int", value : ""}]);
        		}
			}
    		else if(self.params.length > paramcnt)
			{
        		var i;
        		for(i = 0; self.params.length > paramcnt; i++)
        		{
        			self.params.pop();
        		}
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
	
	// save query dialog
	var saveButton = document.getElementById("saveQuery"),
	dialogSaveQuery = document.getElementById('dialogSaveQuery'),
	saveAbbruch = document.getElementById("saveAbbruch");
	saveButton.addEventListener('click', zeigeFenster);
	saveAbbruch.addEventListener('click', schließeFensterSave);
	
	function zeigeFenster() {
		dialogSaveQuery.showModal();
	}
	
	function schließeFensterSave() {
		dialogSaveQuery.close();
	}
	
	// load query dialog
	self.queries = [];
	var dialogLoadQuery = document.getElementById('dialogLoadQuery'),
	loadAbbruch = document.getElementById("loadAbbruch");
	loadAbbruch.addEventListener('click', schließeFensterLoad);
	
	function schließeFensterLoad() {
		dialogLoadQuery.close();
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
		if($success){
			self.queries = $data;
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
		$requests.getResultFromQuery(self.myCodeMirror.getValue(), self.params, self.callback);
	}
	
	self.saveQuery = function() {
		$requests.saveQuery(self.myCodeMirror.getValue(), self.params, 
				self.name, self.description, self.category, self.callback);
		schließeFensterSave();
		// Zurücksetzen der Werte sonst werden sie beim nächsten Öffnen vom Save-Dialog gleich wieder angezeigt.
		self.name, self.description, self.category = "";
	}
	
	self.loadQuery = function() {
		self.queries = $requests.loadQuery(self.queriesCB);
		dialogLoadQuery.showModal();
	}
	
	self.select = function($query) {		
		self.myCodeMirror.setValue($query.query);
	}

}]);