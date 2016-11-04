'use strict';

angular.module('queryBuilder.login', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/login', {
    templateUrl: 'login/login.html'
  });
}])

.controller('loginCtrl', ['$http', '$mdDialog', '$serverRestLocation' , '$userData', '$location', 
    function($http, $mdDialog, $serverRestLocation, $userData, $location) {
    	this.email = "";
    	this.passwort = "";


        this.login = function (ev) { 
             sendLogin($serverRestLocation.getValue() + "/login", this);
        };
}]);