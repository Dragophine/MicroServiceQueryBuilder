'use strict';

/**
 * @ngdoc service
 * @name msmfrontendApp.loginservice
 * @description
 * # loginservice
 * Service in the msmfrontendApp.
 */
angular.module('loginService', [])
  .factory('loginservice', function ($http, $q, $serverRestLocation) {
    // Service logic
    // ...

    var service = {};

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

    service.logout = function () {
      $http.defaults.headers.common['Authorization'] = null;
      clearAuth();
      

      var deferred = $q.defer();
      console.log("LKOGOUT");

    };


    service.isPrincipalOwnerOfArchProfile = function (archProfile) {
      return service.principal !== null && archProfile.ownerName === service.principal.username;
    };

    var clearAuth = function () {
      service.authenticated = false;
      service.principal = null;
      service.authorities = null;
    };

    clearAuth();

    return service;
  });
