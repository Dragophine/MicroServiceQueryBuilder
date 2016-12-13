'use strict';

// Declare app level module which depends on views, and components
var app = angular.module('queryBuilder', [
  'ngRoute',
  'ngDialog',
  'queryBuilder.header',
  'queryBuilder.services',
  'queryBuilder.login',
  'queryBuilder.expertMode',
  'queryBuilder.register',
  'queryBuilder.querybuilder',
  'queryBuilder.querybuildernodedialog',
  'queryBuilder.querybuilderrelationshipdialog'

])
.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/', {
    templateUrl: 'expertMode/expertMode.html'
  });
}]);

app.directive('visNetwork', function() {
    return {
        restrict: 'E',
        require: '^ngModel',
        scope: {
            ngModel: '=',
            options: '=',
            onNodeClick: '&',
            onEdgeClick: '&'
        },
        link: function($scope, $element, $attrs, ngModel) {
            var network = new vis.Network($element[0], $scope.ngModel, $scope.options || {});
           
            var onNodeClick = $scope.onNodeClick || function(prop) {alert('FAIL')};
            var onEdgeClick = $scope.onEdgeClick || function(prop) {};

            network.on('selectNode', function (params)  {
                onNodeClick({params: params});
            });

            network.on('selectEdge', function(params) {
                onEdgeClick({params: params});
            });
            /*
            network.on('select', function(properties) {
                onEdgeClick(properties);
            });*/

        }

    }
});