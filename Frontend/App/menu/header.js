'use strict';

angular.module('queryBuilder.header',[])
.controller('headerCtrl',  function($rootScope, $scope){
	
	var self = this;

	/**
     * Stores the authentication status.
     * @type {boolean}
     */
	self.loggedin = $rootScope.authenticated;

	/**
     * Stores the different header items.
     * @type {array}
     */
	 $scope.items = [
		{'name': 'Query Builder', 'link' : 'querybuilder', "needAuthentication": true},
		{'name': 'Expertmode', 'link' : 'expertmode', "needAuthentication": true},
		{'name': 'Category', 'link' : 'category', "needAuthentication": true},	
		{'name': 'Alerting', 'link' : 'alerting', "needAuthentication": true},
		{'name': 'Alert statistics', 'link' : 'alertingStatistics', "needAuthentication": true},
        {'name': 'Register', 'link' : 'register', "needAuthentication": false},
        {'name': 'Login', 'link' : 'login', "needAuthentication": false},
		{'name': 'Logout', 'link' : 'login', "needAuthentication": true},
		{'name': 'User', 'link' : 'user', "needAuthentication": true}    
    ];

	/**
     * Listens to the login broadcast, if authentication changes the header is changed.
     */
	$scope.$on('login', function () {	
	self.loggedin = $rootScope.authenticated;
    });

	/**
	 * Checks if a specific header item should be shown or not.
	 * 
	 * @param {String} item - Name of the item which should be checked.
	 */

	$scope.condition = function(item) { 
		if( item.name == "User") {
			return $rootScope.admin
		}
		if( item.name == "Expertmode") {
			return $rootScope.expertMode || $rootScope.admin
		}
		if( item.name == "Category") {
			return $rootScope.category || $rootScope.admin
		}
		if( item.name == "Alerting") {
			return $rootScope.alert || $rootScope.admin
		}
		if( item.name == "Alert statistics") {
			return $rootScope.alertStatistic || $rootScope.admin
		}
		
		if( item.name == "Login") {
			return !self.loggedin
		} else {
		return (item.needAuthentication == false || (item.needAuthentication == true && self.loggedin)); };
	}

});
