'use strict';

angular.module('queryBuilder.login', [])
  .controller('LoginCtrl', function ($rootScope, $scope, $location, $cookies, loginservice) {

    var vm = this;
    $scope.username;
    $scope.password;
    vm.loggedin = $rootScope.authenticated;

    $rootScope.execlogin = function () {

      loginservice.login(vm.username, vm.password).then(function () {
        updateAuthStatus();
      }, function () {
        $("#loginErrorModal").modal();
      });
    };

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
      console.log("AUTH"+$rootScope.authenticated);
      console.log($rootScope.admin)
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

    // Try to login right away - maybe still logged in (Cookie set)
    //$scope.username = 'user@use.com';
    //$scope.password = 'password';*/
    //$scope.username = 
    /*if($rootScope.principal != undefined) {
    console.log("ZZZZZZZZ"+$rootScope.principal.username);
    vm.username = $rootScope.principal.username;
    }*/
    //vm.password = $rootScope.principal.password;
    //$rootScope.execlogin();

  });

