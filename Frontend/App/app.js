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
  'queryBuilder.querybuilderrelationshipdialog',
  'queryBuilder.querybuilderLoadDialog',
  'queryBuilder.querybuilderInfoDialog',
  'OutputTable',
  'queryBuilder.alerting',
  'queryBuilder.category',

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
            onEdgeClick: '&',
            onSelectClick: '&',
            onDoubleClick: '&'
        },
        link: function($scope, $element, $attrs, ngModel) {
            var $network = new vis.Network($element[0], $scope.ngModel, $scope.options || {});
           
            var onNodeClick = $scope.onNodeClick || function(prop) {};
            var onEdgeClick = $scope.onEdgeClick || function(prop) {};
            var onSelectClick = $scope.onSelectClick || function(prop) {};
            var onDoubleClick = $scope.onDoubleClick || function(prop) {};
 
            $network.on('selectNode', function (params)  {
                onNodeClick({params: params, network: $network});
            });

            $network.on('selectEdge', function(params) {
                onEdgeClick({params: params, network: $network});
            });
            
            $network.on('select', function(params) {
                onSelectClick({params: params, network: $network});
            });

            $network.on('doubleClick', function(params) {
                onDoubleClick({params: params, network: $network});
            });
        }
    }
});

app.directive('ngWatch', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        scope: {
            ngModel: '=', 
            ngWatch: '=', /* whatches an object for change*/

            ngWatchObjectValue: "@" /* set changed value to model*/
        },
        link: function($scope, $element, $attrs, ngModel) {

            $scope.$watch('ngWatch', function(newValue, oldValue) {
                if (newValue){
                  if($scope.ngWatchObjectValue){
                    ngModel.$setViewValue(newValue[$scope.ngWatchObjectValue]);
                  }
                  else{
                    ngModel.$setViewValue(newValue);
                  }
                  ngModel.$render();
                }      
            }, true);
        }
    }
});

