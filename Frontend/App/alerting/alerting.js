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
	self.nodeName = "";					// Knotenname. Beispiel: Serivce, ServiceHost, Port, etc.
	self.attributeName = "";			// Attributename. Beispiel: host, creationDate, etc.
	self.type = "";						//int, string...
	self.filterType = "";				//sowas wie "in","like","=",">"
	self.email = "";
	self.value = "";
	self.availableNodes = [];
	self.availableAttributeNames = [];
	self.text = [];
	self.selectedAlertName = "";
	self.executeAlertResult = "";
	
	var missingDataModal = document.getElementById('myModalMissingData');
	var missingDataOkButton = document.getElementById("missingDataOk");
	
	missingDataOkButton.onclick = function() {
		missingDataModal.style.display = "none";
	}
	
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
			// self.existingAlerts = $data;
			$requests.getAllAlertNames(self.getAlertsCB);
		}
		else
		{
			self.error = $data;
		}
	}
	
	self.executeAlertCB = function($success, $data, $status) {
		self.hasError = !$success;
		if($success){
			self.executeAlertResult = $data;
		}
		else
		{
			self.error = $data;
		}
	}
	
	/**
	Methods on node
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
	$requests.getNodes(self.getNodesCB);
	
	/**
	Methods on attribute
	*/
	self.getAttributesCB = function($success, $data, $status){
		self.hasError = !$success;
		if($success){
			self.availableAttributeNames = $data;
		}
		else
		{
			self.error = $data;
		}
	}
	
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
	
    self.addAlert = function (ev) {
    	checkAllFieldsValidate(true);
        if(self.text != ""){
            missingDataModal.style.display = "block";
        }
        else
		{
			$requests.addAlert(self.name, self.nodeName, self.attributeName, self.type, self.filterType,
			self.email, self.value, self.getAlertsCB);
			$requests.getAllAlertNames(self.getAlertsCB);
			self.resetValues();
        }
    };
    
    self.saveAlert = function (ev) {
    	checkAllFieldsValidate(false);
        if(self.text != ""){
            missingDataModal.style.display = "block";
        }
        else
		{
			$requests.saveAlert(self.selectedAlertName, self.name, self.nodeName, self.attributeName, self.type, self.filterType,
			self.email, self.value, self.callback);
        }
    };
	
	function validateEmail(email) {
		var re =  /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
		return re.test(email);
	}
	
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
        if(self.nodeName === null || self.nodeName === ""){
            self.text.push("Bitte geben Sie einen Knotenname ein.");
        }
        if(self.attributeName === null || self.attributeName === ""){
            self.text.push("Bitte geben Sie einen Attributename ein.");
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
	
	self.selectLoad = function($query) {		
		self.name = $query.name;
		self.nodeName = $query.nodeName;
		/**
		 * Wenn ein Alert mit einem neuen Node geladen wird, müssen die Attribute
		 * zum Node ermittelt werden.
		 */
		$requests.getKeys(self.nodeName, self.getAttributesCB);
		self.attributeName = $query.attributeName;
		self.type = $query.type;
		self.filterType = $query.filterType;
		self.email = $query.email;
		self.value = $query.value;
		
		self.selectedAlertName = self.name;
	}
	
	self.selectDelete = function($query) {		
		$requests.deleteAlert($query.name, self.callback);
	}
	
	self.setNode = function() {		
		$requests.getKeys(self.nodeName, self.getAttributesCB);
	}
	
	self.resetValues = function($query) {		
		self.name = "";
		self.nodeName = "";
		self.attributeName = "";
		self.type = "";
		self.filterType = "";
		self.email = "";
		self.value = "";
		
		self.selectedAlertName = "";
	}
	
	setInterval(function ()
	{
		if(self.existingAlerts == null || self.existingAlerts.length <= 0)
		{
			/**
			 * Wenn es noch keine definierten Alerts gibt soll
			 * periodisch geprüft werden ob welche existieren.
			 */
			$requests.getAllAlertNames(self.getAlertsCB);
		}
		else
		{
			for (var i = 0; i < self.existingAlerts.length; i++)
		    {
				$requests.executeAlert(self.existingAlerts[i].name, self.executeAlertCB);
				if(self.executeAlertResult != null && self.executeAlertResult.length > 0)
				{
					sendMail(self.existingAlerts[i].email, self.existingAlerts[i].name,
							self.existingAlerts[i].nodeName, self.existingAlerts[i].attributeName,
							self.existingAlerts[i].filterType, self.existingAlerts[i].value);
				}
				
		    }
		}
	}, 10000);
	
	function sendMail($email, $name, $nodeName, $attributeName, $filterType, $value) {
//	    var link = "mailto:" + $email +
//	             //+ "?cc=myCCaddress@example.com"
//	             + "&subject=" + escape("Alert " + $name + " hat kritischen Wert passiert")
//	             + "&body=" + escape("Der Alert " + $name + " hat den kritischen Wert von " +
//	            		 $nodeName + "." + $attributeName + " " + $filterType + " " + $value +
//	            		 " passiert. Bitte kontrollieren Sie ehestmöglich das System.")
//	    ;
//
//	    window.location.href = link;
	}
	
}]);