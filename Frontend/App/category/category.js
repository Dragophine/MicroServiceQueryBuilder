'use strict';

angular.module('queryBuilder.category', ['ngRoute', 'queryBuilder.services'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/category', {
    templateUrl: 'category/category.html'
  });
}])
.controller('categoryCtrl', ['$requests',
	function($requests) {
	var self = this;
	

	/**
     * Holds name from category.
     * @type {string}
     */
	self.name = "";
	/**
     * Holds description from category.
     * @type {string}
     */
	self.description = "";	
	/**
     * Holds id from category.
     * @type {string}
     */
  	self.id = "";
	  /**
     * Store all categories which exists.
     * @type {array}
     */
 	 self.existingCategories = [];
	
	/**
	 * Modal dialog for missing or invalid data.
     * @type {object}
	 */
	var missingDataModal = document.getElementById('myModalMissingData');
	/**
	 * Ok-button from modal dialog for missing or invalid data.
     * @type {object}
	 */
	var missingDataOkButton = document.getElementById("missingDataOk");
	
	/**
	 * Close modal dialog if user select the ok-button in dialog.
	 */
	missingDataOkButton.onclick = function() {
		missingDataModal.style.display = "none";
	}
	
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
	 * Callback from delete/save category call. If query was successful refresh category names.
	 * Otherwise print error.
	 *
	 * @param {boolean} $success - true when there are no errors.
	 * @param {Object} $data - the requested data.
     * @param {number} $status - the actual server status.
	 */
	self.callback = function($success, $data, $status) {
		self.hasError = !$success;
		if($success){
			// self.existingCategories = $data;
			$requests.getAllCategories(self.getCategories);
		}
		else
		{
			self.error = $data;
		}
	}
	
	 /**
	 * Callback from get all categories call. If query was successful save data in variable self.existingCategories.
	 * Otherwise print error.
	 * 
	 * @param {boolean} $success - true when there are no errors.
	 * @param {Object} $data - the requested data (In this case the categories).
     * @param {number} $status - the actual server status.
	 */
	self.getCategories = function($success, $data, $status) {
		self.hasError = !$success;
		if($success){
			self.existingCategories = $data;
		}
		else
		{
			self.error = $data;
		}
	}	
	$requests.getAllCategories(self.getCategories);
	
	/**
	 * Create new category, if all fields are filled correctly.
	 */
    self.addCategory = function (ev) {
    	checkAllFieldsValidate(true);
        if(self.text != ""){
            missingDataModal.style.display = "block";
        }
        else
		{
            $requests.addCategory(self.name, self.description, self.callback);
        }
    };
    
	/**
	 * Updates already existing category, if all fields are filled correctly.
	 */
    self.updateCategory = function (ev) {
    	checkAllFieldsValidate(false);
        if(self.text != ""){
            missingDataModal.style.display = "block";
        }
        else
		{
			$requests.updateCategory(self.id, self.name, self.description, self.callback);
        }
    };
	

	/**
	 * Check if the name from category form already exists.
	 */
	function existsNameAlready() {
		$requests.getAllCategories(self.getCategories);

		for (var i = 0; i < self.existingCategories.length; i++)
	    {
	        if (self.existingCategories[i].name === self.name)
	        {
	            return true;
	        }
	    }
	    return false;
		
	}
	
	/**
	 * Check if all fields in category form are correctly filled.
	 * 
	 * @param {boolean} $checkIfNameExists - Is it necessary to check the name.
	 */
	function checkAllFieldsValidate($checkIfNameExists) {
    	self.text = [];
		if($checkIfNameExists && (self.name === null || self.name === ""))
		{
            self.text.push("Bitte geben Sie einen Namen ein.");
        }
		else if($checkIfNameExists && existsNameAlready())
		{
			self.text.push("Der angegebene Name existiert bereits, geben Sie bitte einen anderen Namen an.");
		}
        
	}
	

	/**
	 * Load selected category.
	 * 
	 * @param {Object} $query - Load this query.
	 */
	self.selectLoad = function($query) {		
		self.name = $query.name;
		self.description = $query.description;
    	self.id = $query.id;		
		self.selectedAlertName = self.name;
	}
	
		/**
	 * Delete selected category.
	 * 
	 * @param {Object} $query - Delete this query.
	 */
	self.selectDelete = function($query) {		
		$requests.deleteCategory($query.id, self.callback);
	}
	
}]);