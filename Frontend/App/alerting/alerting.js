'use strict';

angular.module('queryBuilder.alerting', ['ngRoute', 'queryBuilder.services'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/alerting', {
    templateUrl: 'alerting/alerting.html'
  });
}])
.controller('alertingCtrl', ['$requests',
	function($requests) {
	var self = this;
	
	self.name = "";
	self.type = "";						// int, string...
	self.filterType = "";				// "in","like","=",">"
	self.email = "";
	self.value = "";
	self.availableNodes = [];
	self.availableAttributeNames = [];
	self.text = [];
	self.selectedAlertName = "";
	self.executeAlertResult = "";
	self.queries = [];
	self.selectedQuery = "";
	
	var missingDataModal = document.getElementById('myModalMissingData');
	var missingDataOkButton = document.getElementById("missingDataOk");
	
	missingDataOkButton.onclick = function() {
		missingDataModal.style.display = "none";
	}
	
	/**
	 * Refers to the json response when an error occured.
	 */
	self.error = "";
	
	/**
	 * Boolean which concludes if the query has errors.
	 */
	self.hasError = false;
	
	/**
	 * Callback from delete/save alert call. If query was successful refresh alert names.
	 * Otherwise print error.
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
	
//	/**
//	 * Callback from delete/save alert call. If query was successful refresh alert names.
//	 * Otherwise print error.
//	 */
//	self.executeAlertCB = function($success, $data, $status) {
//		self.hasError = !$success;
//		if($success){
//			self.executeAlertResult = $data;
//		}
//		else
//		{
//			self.error = $data;
//		}
//	}
	
	/**
	 * Callback from get all nodes call. If query was successful save data in variable self.availableNodes.
	 * Otherwise print error.
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
	// M15: Nötig ???
	$requests.getNodes(self.getNodesCB);
	
	/**
	 * Callback from get all nodes call. If query was successful save data in variable self.queries.
	 * Otherwise print error.
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