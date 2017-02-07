'use strict';

angular.module('queryBuilder.user', ['ngRoute', 'queryBuilder.services'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/user', {
    templateUrl: 'user/user.html'
  });
}])
.controller('userCtrl', ['$requests',
	function($requests) {
	var self = this;
	
	self.email = "";
	self.firstName = "";	
  self.lastName = "";	
  self.existingUsers = [];
  self.authorities = [];


   self.roles = [
    'EXPERTMODE', 
    'CATEGORY', 
    'ALERT', 
    'ALERTSTATISTIC'
  ];


  

	
	/*var missingDataModal = document.getElementById('myModalMissingData');
	var missingDataOkButton = document.getElementById("missingDataOk");*/
	
	/*missingDataOkButton.onclick = function() {
		missingDataModal.style.display = "none";
	}*/
	
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

	self.callback = function($success, $data, $status) {
		self.hasError = !$success;
		if($success){
			// self.existingCategories = $data;
			$requests.getAllUsers(self.getUsers);
		}
		else
		{
			self.error = $data;
		}
	}
	

	
	self.getUsers = function($success, $data, $status) {
		self.hasError = !$success;
		if($success){
			self.existingUsers = $data;
			
		}
		else
		{
			self.error = $data;
		}
	}	
	$requests.getAllUsers(self.getUsers);


    self.getAuthorities = function($success, $data, $status) {
		self.hasError = !$success;
		if($success){
			self.authorities = $data;
            
            //console.log("BBB"+$data);
		}
		else
		{
			self.error = $data;
		}
	}
	self.getFilterAttributes = function($key){	
			for (var i = 0; i < self.authorities.length; i++) {
				if(self.authorities[i].authority === $key){
					return $key;
					
				}
			}
			return undefined;
		};


	self.isFilterAttributesChecked = function($key){
			if(self.getFilterAttributes($key) === undefined){
				console.log("AAAA"+$key+"BBBBBB")
				return false;
			}
			return true;
		};

    self.setFilterAttributes = function($key){
        var filterAttribute = self.getFilterAttributes($key);
			if(filterAttribute !== undefined){
                console.log("DELETE AUTHORITY")
                $requests.deleteAuthority(self.email, $key, self.callback)

            } else {
                console.log("NEW AUTHORITY")
                $requests.addAuthority(self.email, $key, self.callback)
            }

    }

    self.selectLoad = function($query) {
        //console.log("AAA"+$query.email);
        $requests.getAllAuthorities($query.email, self.getAuthorities);
        self.email = $query.email;



    }		
		/*self.name = $query.name;
		self.description = $query.description;
    self.id = $query.id;*/


   /* self.addCategory = function (ev) {
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
		

	
		self.selectedAlertName = self.name;
	}
	
	self.selectDelete = function($query) {		
		$requests.deleteCategory($query.id, self.callback);
	}
	
*/
	


	
}]);