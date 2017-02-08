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
	

	/**
     * Holds email of selected user.
     * @type {string}
     */
	self.email = "";

	 /**
     * Stores all Users which exists.
     * @type {array}
     */	
  	self.existingUsers = [];

	/**
     * Store all authorities of selected user.
     * @type {array}
     */
  	self.authorities = [];

	/**
     * Store all roles of the user which can be selected and changed.
     * @type {array}
     */
   	self.roles = [
   	 'EXPERTMODE', 
  	 'CATEGORY', 
   	 'ALERT', 
   	 'ALERTSTATISTIC'
  	];

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
			$requests.getAllUsers(self.getUsers);
		}
		else
		{
			self.error = $data;
		}
	}
	/**
	 * Callback from get all users call. If query was successful save data in variable self.existingUsers.
	 * Otherwise print error.
	 * 
	 * @param {boolean} $success - true when there are no errors.
	 * @param {Object} $data - the requested data (In this case the users).
     * @param {number} $status - the actual server status.
	 */
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

	/**
    * This method searches for a certain role with a certain role name (key).
    *
    * @param {string} $key - The key of the role. The key is the same as the role name.
    * @return {Object} The given role name or undefined.
    */
	self.getRoles = function($key){	
		for (var i = 0; i < self.authorities.length; i++) {
			if(self.authorities[i].authority === $key){
				return $key;	
			}
		}
		return undefined;
	};

	/**
    * Checks whether a certain role with a role name (key) exists or not.
    *
    * @param {string} $key - The role key. The key is the same as the role name.
    * @return {boolean} True if the role with the given key exists, otherwise false.
    */
	self.isRoleChecked = function($key){
		if(self.getRoles($key) === undefined){
			return false;
		}
		return true;
	};

	/**
        * Checks whether a certain role with a certain key exists or not.
        * If it exists it removes the role, if not it creates the role.
        * 
        * @param {string} $key - The key of the property which should be added or deleted.
        */
    self.setFilterAttributes = function($key){
        var filterAttribute = self.getRoles($key);
			if(filterAttribute !== undefined){
                $requests.deleteAuthority(self.email, $key, self.callback)

            } else {
                $requests.addAuthority(self.email, $key, self.callback)
            }

    }

	/**
	 * Callback from get all authorities call. If query was successful save data in variable self.authorities.
	 * Otherwise print error.
	 * 
	 * @param {boolean} $success - true when there are no errors.
	 * @param {Object} $data - the requested data (In this case the authorities).
     * @param {number} $status - the actual server status.
	 */
    self.getAuthorities = function($success, $data, $status) {
		self.hasError = !$success;
		if($success){
			self.authorities = $data;
		}
		else
		{
			self.error = $data;
		}
	}

	/**
	 * Load selected authorities of selected user.
	 * 
	 * @param {Object} $query - Load this query.
	 */
    self.selectLoad = function($query) {
        $requests.getAllAuthorities($query.email, self.getAuthorities);
        self.email = $query.email;
    }		
}]);