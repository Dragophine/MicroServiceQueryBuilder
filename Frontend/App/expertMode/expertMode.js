'use strict';

angular.module('queryBuilder.expertMode', ['ngRoute', 'queryBuilder.services'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/expertMode', {
    templateUrl: 'expertMode/expertMode.html'
  });
}])
.controller('expertMode', ['$location', '$projectIDService', '$http', "$scope",
'$serverRestLocation', '$timeout', '$q', '$log', '$mdDialog',
	function($location, $projectIDService, $http, $scope,  $serverRestLocation, $timeout, $q, $log, $mdDialog) {
    var self = this;
		


}]);