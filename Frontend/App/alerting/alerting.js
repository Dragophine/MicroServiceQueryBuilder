'use strict';

angular.module('queryBuilder.alerting', ['ngRoute', 'queryBuilder.services'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/alerting', {
    templateUrl: 'alerting/alerting.html'
  });
}])
/**
   * This controller handles the main operation in the user interface.
   * It configures and handle all actions in the alert view.
   * 
   * @version 1.0.0
   */
.controller('alertingCtrl', ['$requests',
	function($requests) {
	var self = this;
	
    /**
     * Holds name from alert.
     * @type {string}
     */
	self.name = "";
    /**
     * Holds type from alert. Examples: int, string,...
     * @type {string}
     */
	self.type = "";
    /**
     * Holds filter type from alert. Examples: in, like,...
     * @type {string}
     */
	self.filterType = "";
    /**
     * Holds email address from alert.
     * @type {string}
     */
	self.email = "";
    /**
     * Holds limit value from alert.
     * @type {string}
     */
	self.value = "";
    /**
     * If a value from alert is missing or invalid in save dialog a message will be stored
     * in this array.
     * @type {array}
     */
	self.text = [];
    /**
     * Store the name from loaded alert. When an alert has been loaded, you have the option
     * to create a new alert when saving or to rewrite the loaded alert.
     * @type {string}
     */
	self.selectedAlertName = "";
    /**
     * Store all queries which can be selected in alert view.
     * @type {array}
     */
	self.queries = [];
    /**
     * Holds selected query from alert.
     * @type {string}
     */
	self.selectedQuery = "";	
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
	 * Callback from delete/save alert call. If query was successful refresh alert names.
	 * Otherwise print error.
	 *
	 * @param {boolean} $success - true when there are no errors.
	 * @param {Object} $data - the requested data.
     * @param {number} $status - the actual server status.
	 */
	self.callback = function($success, $data, $status) {
		self.hasError = !$success;
		if($success)
		{
			$requests.getAllAlertNames(self.getAlertsCB);
		}
		else
		{
			self.error = $data;
		}
	}
	
	/**
	 * Callback from get all nodes call. If query was successful save data in variable self.queries.
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
	$requests.loadAllQueries(self.queriesCB);
	
	/**
	 * Callback from get query by name call. If query was successful save data in variable self.selectedQuery.
	 * Otherwise print error.
	 * 
	 * @param {boolean} $success - true when there are no errors.
	 * @param {Object} $data - the requested data (In this case the query).
     * @param {number} $status - the actual server status.
	 */
	self.getQueryByNameCB = function($success, $data, $status){
		self.hasError = !$success;
		if($success){
			self.selectedQuery = $data.name;
		}
		else
		{
			self.error = $data;
		}
	}
	
	/**
	 * Callback from get all alert names call. If query was successful save data in variable self.existingAlerts.
	 * Otherwise print error.
	 * 
	 * @param {boolean} $success - true when there are no errors.
	 * @param {Object} $data - the requested data (In this case the alert names).
     * @param {number} $status - the actual server status.
	 */
	self.getAlertsCB = function($success, $data, $status) {
		self.hasError = !$success;
		if($success){
			self.existingAlerts = $data;
		}
		else
		{
			self.error = $data;
		}
	}	
	$requests.getAllAlertNames(self.getAlertsCB);
	
	/**
	 * Create new alert, if all fields are filled correctly.
	 */
    self.addAlert = function () {
    	checkAllFieldsValidate(true);
        if(self.text != ""){
            missingDataModal.style.display = "block";
        }
        else
		{
			$requests.addAlert(self.name, self.selectedQuery, self.type, self.filterType,
			self.email, self.value, self.getAlertsCB);
			$requests.getAllAlertNames(self.getAlertsCB);
			self.resetValues();
        }
    };
    
	/**
	 * Save already existing alert, if all fields are filled correctly.
	 */
    self.saveAlert = function () {
    	checkAllFieldsValidate(false);
        if(self.text != ""){
            missingDataModal.style.display = "block";
        }
        else
		{
			$requests.saveAlert(self.selectedAlertName, self.name, self.selectedQuery, self.type, self.filterType,
			self.email, self.value, self.callback);
        }
    };
	
	/**
	 * Check if email address is correct.
	 * 
	 * @param {string} email - email address from alert form.
	 */
	function validateEmail(email) {
		var re =  /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
		return re.test(email);
	}
	
	/**
	 * Check if the name from alert form already exists.
	 */
	function existsNameAlready() {
		$requests.getAllAlertNames(self.getAlertsCB);
		
		for (var i = 0; i < self.existingAlerts.length; i++)
	    {
	        if (self.existingAlerts[i].name === self.name)
	        {
	            return true;
	        }
	    }
	    return false;		
	}
	
	/**
	 * Check if all fields in alert form are correctly filled.
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
        if(self.selectedQuery === null || self.selectedQuery === ""){
            self.text.push("Bitte wählen Sie eine gültige Query aus.");
        }
        if(!validateEmail(self.email)){
            self.text.push("Bitte geben Sie eine korrekte Email Adresse ein.");
        }
        if(self.type === null || self.type === ""){
            self.text.push("Bitte geben Sie einen Datentyp ein.");
        }
        if(self.filterType === null || self.filterType === ""){
            self.text.push("Bitte geben Sie einen Filtertyp ein.");
        }
        if(self.value === null || self.value === ""){
            self.text.push("Bitte geben Sie einen gültigen Wert ein.");
        }
	}
	
	/**
	 * Load selected alert.
	 * 
	 * @param {Object} $query - Load this query.
	 */
	self.selectLoad = function($query) {
		$requests.getQueryByName($query.query, self.getQueryByNameCB);
		self.name = $query.name;
		self.type = $query.type;
		self.filterType = $query.filterType;
		self.email = $query.email;
		self.value = $query.value;		
		self.selectedAlertName = self.name;
	}
	
	/**
	 * Delete selected alert.
	 * 
	 * @param {Object} $query - Delete this query.
	 */
	self.selectDelete = function($query) {		
		$requests.deleteAlert($query.name, self.callback);
	}

	/**
	 * Reset all values in alert form.
	 */
	self.resetValues = function() {		
		self.name = "";
		self.type = "";
		self.filterType = "";
		self.email = "";
		self.value = "";
		self.selectedQuery = "";		
		self.selectedAlertName = "";
	}
}]);