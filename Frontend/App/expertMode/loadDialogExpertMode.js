// WIRD BISHER NICHT VERWENDET, WEIL ES BEIM LADEN UND LÖSCHEN NOCH PROLEME GIBT, WIRD NUR AKTIVIERT WENN GENÜGEND ZEIT ÜBRIG BLEIBT

'use strict';

angular.module('expertMode.loadDialogExpertMode', ['ngRoute'])



.controller('loadDialogExpertModeCtrl', ['$requests', '$scope', 
    function($requests, $scope) {
        var self = this;

        self.category = undefined;
        self.name = undefined;
        self.description = undefined;
//        self.params = undefined;
        
        // textArea mit syntax highlighting
        self.myCodeMirror = document.getElementById('query');
        
        self.loadedQueries = [ ];    
        self.availableCategories = [];

        self.loadQueriesInBuilderCB = function($success, $data, $status){
            if($success){
                self.loadedQueries = $data;
            }
        }

	    $requests.loadAllQueries( self.loadQueriesInBuilderCB);

        self.selectQuery = function($query){
        	self.myCodeMirror.setValue($query.query);
    		self.params = [];
    		var arrLength = $query.parameter.length;
    		var i;
    		for(i = 0; self.params.length < arrLength; i++)
    		{
        		self.params.push.apply(self.params, [{key: $query.parameter[i].key, type : $query.parameter[i].type,
        			value : $query.parameter[i].value}]);
    		}
    		
             $scope.closeThisDialog($query);
        };

        self.deleteQueryCallback = function($success, $data, $status) {
            if($success){
            	$requests.loadAllQueries(self.loadQueriesInBuilderCB);
            }
            
        }

        self.deleteSelectedQuery = function($query){
    		$requests.deleteQuery($query.query, $query.parameter, 
    				$query.name, $query.description, $query.category, self.callback);
        }

        /**
        Apply filter
        */
        self.applyFilter = function() {
            $requests.loadSomeQueriesInExpertMode(self.loadQueriesInBuilderCB, self.name, self.category, self.description);
         }	

			/**
	Load category
	*/

    self.getCategoriesCallBack = function($success, $data, $status){
        self.hasError = !$success;
        if($success){

            self.availableCategories = $data;
        }
        else
        {
            self.error = $data;
        }
    }

    $requests.getAllCategories(self.getCategoriesCallBack);


    

	
}]);
