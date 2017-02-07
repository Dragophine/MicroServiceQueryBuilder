// WIRD BISHER NICHT VERWENDET, WEIL ES BEIM LADEN UND LÖSCHEN NOCH PROLEME GIBT, WIRD NUR AKTIVIERT WENN GENÜGEND ZEIT ÜBRIG BLEIBT

'use strict';

angular.module('expertMode.loadDialogExpertMode', ['ngRoute'])

/**
 * This controller is in charge of loading, filtering and deleting queries.
 * @version 1.0.0
 */
.controller('loadDialogExpertModeCtrl', ['$requests', '$scope', 
    function($requests, $scope) {
    var self = this;

    /**
     * The entered category for querying the results.
     * @type {string}
     */
    self.category = undefined;
    /**
     * The entered name for querying the results.
     * @type {string} 
     */
    self.name = undefined;
    /**
     * The description category for querying the results.
     * @type {string}
     */
    self.description = undefined;
    /**
     * The loaded querys.
     * @type {string}
     */
    self.loadedQueries = [ ];
    /**
     * The available categories.
     * @type {array}
     */
    self.availableCategories = [];

    /**
     * The callback when all queries were loaded.
     *
     * @param {boolean} $success - true when there are no errors.
     * @param {json} $data - the requested data (In this case the queries).
     * @param {number} $status - the actual server status.
     */
    self.loadQueriesInBuilderCB = function($success, $data, $status){
        if($success){
            self.loadedQueries = $data;
        }
    }

    $requests.loadAllQueries( self.loadQueriesInBuilderCB);

    /**
     * This method closes this dialog and delivers the query.
     *
     * @param {string} $query - delivers the selected query.
     */
    self.selectQuery = function($query){    		
         $scope.closeThisDialog($query);
    };

    /**
     * The callback when the requested query was deleted.
     *
     * @param {boolean} $success - true when there are no errors.
     * @param {json} $data - the requested data (In this case no data will be sent).
    * @param {number} $status - the actual server status.
     */
    self.deleteQueryCallback = function($success, $data, $status) {
        if($success){
        	$requests.loadAllQueries(self.loadQueriesInBuilderCB);
        }
        
    }

    /**
     * This method deletes a query.
     *
     * @param {id} $id - the id of the query which should be deleted.
     */
    self.deleteSelectedQuery = function($query){
		$requests.deleteQuery($query.query, $query.parameter, 
				$query.name, $query.description, $query.category, self.deleteQueryCallback);
    }

    /**
     * This method applies a filter.
     * It uses the input data name, category and description to apply the filter.
     *
     */
    self.applyFilter = function() {
        $requests.loadSomeQueriesInExpertMode(self.loadQueriesInBuilderCB, self.name, self.category, self.description);
     }	

   /**
     * The callback when the requested categories were loaded
     *
     * @param {boolean} $success - true when there are no errors.
     * @param {json} $data - the requested data (In this case the categories).
     * @param {number} $status - the actual server status.
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
