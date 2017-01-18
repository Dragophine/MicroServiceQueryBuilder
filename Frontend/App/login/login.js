'use strict';

angular.module('queryBuilder.login', [])
  .controller('LoginCtrl', function ($rootScope, $scope, $location, $cookies, loginservice) {

    var vm = this;
    $scope.username;
    $scope.password;

    $rootScope.execlogin = function () {

      loginservice.login(vm.username, vm.password).then(function () {
        updateAuthStatus();
      }, function () {
       // $("#loginErrorModal").modal();
      });
    };

    $rootScope.execlogout = function () {
      loginservice.logout().then(function () {
        //updateAuthStatus();
      }, function () {
        // TODO: Error handling logout
      });
      updateAuthStatus();
    };

    var updateAuthStatus = function () {
      var authenticated = false;
      if (loginservice.principal && loginservice.authorities) {
        //console.log("BBB"+loginservice.authorities.length)
        var i = 0;
        while (!false && i < loginservice.authorities.length) {
          
          if (loginservice.authorities[i].authority == "ROLE_USER") {
            authenticated = true;
          }
          i++;
        }
      }
      $rootScope.authenticated = authenticated;
      $rootScope.principal = loginservice.principal;
  //console.log("ZZZZZZZZ"+$rootScope.principal.username);

      $rootScope.$broadcast('authenticationChanged');
      if ($rootScope.authenticated) {
        $location.path("/#/home");
      } else {
        $location.path("/login");
        $("#loginErrorModal").modal();
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

