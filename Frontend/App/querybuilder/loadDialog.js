'use strict';

angular.module('queryBuilder.querybuilderLoadDialog', ['ngRoute'])



.controller('querybuilderLoadDialogCtrl', ['$requests', '$scope', 
    function($requests, $scope) {
        var self = this;

        self.loadedQueries = [
            {
                "name":"Name",
                "description":"Frägt den Namen der Servicehosts ab.",
                "category":" Kategorie ",
                "limitcount":"","node":
                {   
                    "type":"ServiceHost",
                    "returnAttributes":[],
                    "filterAttributes":[],
                    "orderByAttributes":[],
                    "relationship":[]
                } ,
                "ngDialogId":"ngdialog4"
            },
            {
                "name":"Abfrage der Summe",
                "description":"Frägt die Summe der Servicehosts ab.",
                "category":" Kategorie ",
                "limitcount":"",
                "node":{
                    "type":"ServiceInstance",
                    "returnAttributes":[
                    {"attributeName":"creationDate","returnType":""},
                    {"attributeName":"basePath","returnType":""}
                    ],
                    "filterAttributes":[
                    {"attributeName":"basePath","type":"string","filterType":"=","value":"Test","changeable":true}
                    ],
                    "orderByAttributes":[{"attributeName":"inactiveSince","direction"
:"desc"}],"relationship":[{"relationshipType":"IS_RUNNING_ON","direction":"OUTGOING","optional":false
,"returnAttributes":[],"filterAttributes":[],"orderByAttributes":[],"node":{"type":"Port","returnAttributes"
:[],"filterAttributes":[],"orderByAttributes":[],"relationship":[]}}],"ngDialogId":"ngdialog2"}},
            {
                "name":"Query ohne Inhalt",
                "description":"Zum Testen des builder",
                "category":" Kategorie ",
                "limitcount":"",
                "node":""
            }
        ];    

        self.loadAllQueriesInBuilderCB = function($success, $data, $status){
            if($success){
                //self.loadedQueries = $data;
            }
        }

	    $requests.loadAllQueriesInBuilder(self.loadAllQueriesInBuilderCB);

        self.selectQuery = function($query){
             $scope.closeThisDialog($query);
        };
}]);
