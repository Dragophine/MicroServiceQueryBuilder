'use strict';

// Declare app level module which depends on views, and components
angular.module('queryBuilder', [
  'ngRoute',
  'queryBuilder.header',
  'queryBuilder.services',
  'queryBuilder.login',
  'queryBuilder.expertMode',
  'queryBuilder.register',
  'queryBuilder.querybuilder'
])
.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/', {
    templateUrl: 'expertMode/expertMode.html'
  });
}]);