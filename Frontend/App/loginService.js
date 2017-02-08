'use strict';

angular.module('loginService', [])
  .factory('loginservice', function ($http, $q, $serverRestLocation) {

    var service = {};

    /**
	  * Service to login a user. Sends a authorization request to the server and if successful add the authorization cookie to the default header
	  * 
	  * @param {String} $username - username of the user which should be logged in.
    * @param {String} $password - password of the user which should be logged in.
	  */
    service.login = function (username, password) {
      $http.defaults.headers.common['Authorization'] = null;

      var url = $serverRestLocation.getValue()+'/authentications/user';
      var headers = username && password ? {
        'Authorization': "Basic " + btoa(username + ":" + password)
      } : {};


      var deferred = $q.defer();
      $http.get(url, { headers: headers }).success(function (user) {
        if (user.authenticated) {
          service.authenticated = true;
          service.principal = user.principal;
          service.authorities = user.authorities;
          $http.defaults.headers.common['Authorization'] = "Basic " + btoa(username + ":" + password);
          deferred.resolve();
        } else {
          clearAuth();
          deferred.reject();
        }
      }).error(function () {
        clearAuth();
        deferred.reject();
      });

      return deferred.promise;
    };

    /**
	  * Service to logout a user. Deletes the default authorization cookie and clears all Authorization
	  * 
	  */
    service.logout = function () {
      $http.defaults.headers.common['Authorization'] = null;
      clearAuth();
    
      var deferred = $q.defer();
    };

    /**
	  * Function to clear all saved authorization states.
	  * 
	  */
    var clearAuth = function () {
      service.authenticated = false;
      service.principal = null;
      service.authorities = null;
    };

    clearAuth();

    return service;
  });
