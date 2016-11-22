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
    
	self.paramcnt = -1;
	
	self.params = [];
	
	/**
	 * Immer wenn sich bei der Query etwas ändert, sollen sich die Parameter automatisch anpassen.
	 */
    self.myCodeMirror.on('change',function(cMirror){
    	  	// Parameter werden aktuell durch {{ erkannt, eventuell gibt es noch ein schönere Lösung...
    		var query = cMirror.getValue();
    		self.paramcnt = query.split("{{").length - 1;
    		
    		if(self.params.length < self.paramcnt)
			{
        		var i;
        		for(i = 0; self.params.length < self.paramcnt; i++)
        		{
            		self.params.push.apply(self.params, [{type : "Integer", value : ""}]);
        		}
			}
    		else if(self.params.length > self.paramcnt)
			{
        		var i;
        		for(i = 0; self.params.length > self.paramcnt; i++)
        		{
        			self.params.pop();
        		}
			}
    	});
    
	self.paramoptions = ["Integer", "String"];

	/** 
		Query fields
	*/
	self.query = "";
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
		$requests.getResultFromQuery(self.myCodeMirror.getValue(), self.params, self.callback);
	}

}]);