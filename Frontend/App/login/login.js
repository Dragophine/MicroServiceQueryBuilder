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

        function sendLogin (server, controller) {
         	$http({
         		method : 'POST',
         		url : server, 
         		headers: { 
                    'Content-Type':'application/json'
                },
         		data: { 
         			"user" : controller.email,
         			"passwort": controller.passwort
         		}
         	})
            .success(function(data, status) {
                $userData.setEmail(controller.email);
                $userData.setPassword(controller.passwort);
                $userData.setSession(data["sessionid"]);
                $userData.setName(data["vorname"]);
                $userData.setId(data["id"]);
                $location.path('/projektUebersicht');
             })
            .error(function(data, status) {
                okDialog($mdDialog, 'Fehler bei anmelden', 'Bitte überprüfen Sie die Emailadresse und das Passwort.', null);
            });
        };

        this.login = function (ev) { 
             sendLogin($serverRestLocation.getValue() + "/login", this);
        };
}]);