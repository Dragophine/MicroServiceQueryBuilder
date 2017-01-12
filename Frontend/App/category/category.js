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
	
	self.name = "";
	self.description = "";	
  self.id = "";	
  self.existingCategories = [];

	
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
			// self.existingCategories = $data;
			$requests.getAllCategories(self.getCategories);
		}
		else
		{
			self.error = $data;
		}
	}
	

	
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
	
	self.selectLoad = function($query) {		
		self.name = $query.name;
		self.description = $query.description;
    self.id = $query.id;
		/**
		 * Wenn ein Alert mit einem neuen Node geladen wird, müssen die Attribute
		 * zum Node ermittelt werden.
		 */

		
		self.selectedAlertName = self.name;
	}
	
	self.selectDelete = function($query) {		
		$requests.deleteCategory($query.id, self.callback);
	}
	

	


	
}]);