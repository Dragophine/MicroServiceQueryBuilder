'use strict';

angular.module('queryBuilder.login', [])
  .controller('LoginCtrl', function ($rootScope, $scope, $location, $cookies, loginservice) {

    var self = this;

    /**
     * Stores typed-in username.
     * @type {string}
     */
    $scope.username;
    /**
     * Stores typed-in password.
     * @type {string}
     */
    $scope.password;
    /**
     * Holds if a user is already logged in.
     * @type {string}
     */
    self.loggedin = $rootScope.authenticated;

    /**
	  * Executes the login of the user with the typed in username and password
	  */
    $rootScope.execlogin = function () {

      loginservice.login(self.username, self.password).then(function () {
        updateAuthStatus();
      }, function () {
        $("#loginErrorModal").modal();
      });
    };
    
    /**
	  * Executes the logout of the actually logged in user
	  */
    $rootScope.execlogout = function () {

      loginservice.logout();
      $rootScope.admin = false;
      $rootScope.expertMode = false;
      $rootScope.category = false;
      $rootScope.alert = false;
      $rootScope.alertStatistic = false;
      updateAuthStatus();
      $location.path("/");
      $rootScope.$broadcast('login');
    };

    var updateAuthStatus = function () {
      var authenticated = false;
      if (loginservice.principal && loginservice.authorities) {
        var i = 0;
        while (!false && i < loginservice.authorities.length) {
          
          if (loginservice.authorities[i].authority == "ROLE_USER") {
            authenticated = true;
          }
           if (loginservice.authorities[i].authority == "ROLE_ADMIN") {
             $rootScope.admin = true;
            authenticated = true;
          }
           if (loginservice.authorities[i].authority == "EXPERTMODE") {
             $rootScope.expertMode = true;
          }
          if (loginservice.authorities[i].authority == "CATEGORY") {
             $rootScope.category = true;
          }
          if (loginservice.authorities[i].authority == "ALERT") {
             $rootScope.alert = true;
          }
          if (loginservice.authorities[i].authority == "ALERTSTATISTIC") {
             $rootScope.alertStatistic = true;
          }
          i++;
        }
      }
      $rootScope.authenticated = authenticated;
      $rootScope.principal = loginservice.principal;

      $rootScope.$broadcast('authenticationChanged');
      if ($rootScope.authenticated) {
        $rootScope.$broadcast('login');
        $location.path("/querybuilder");
      } else {
        $location.path("/login");
      }
    };


  });

