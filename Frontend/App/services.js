angular.module('queryBuilder.services', ['ngCookies'])

.service('$projectIDService',['$cookieStore', function($cookieStore){
    return {
        get: function () {
            return $cookieStore.get('projectID');
        },
        set: function(value) {
            $cookieStore.put('projectID', value);
        }
    };
}])
.service('$serverRestLocation',  function() {
    var serverRestLocation = "http://localhost:8080";

    this.getValue = function() {
        return serverRestLocation;
    };
})
.service('$requests', ['$serverRestLocation', '$http', function($serverRestLocation, $http) {
    //execute query
    this.getResultFromQuery = function($query, $params, $callback) {
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue() + '/expertModus', 
            headers: {  'Content-Type':'application/json'},
            data: { 
              "query" : $query,
              "parameter" :  $params,
                "name" : "Query1",
                "description" : "Query 11",
                "category" : "abc" }
        })
        .success(function(data, status) {
            console.log(data);
            if($callback !== undefined){
            $callback(true, data, status);
            }
        })
        .error(function(data, status) {
            console.log(data);
            if($callback !== undefined){
           $callback(false, data, status);
            }
        });
    };

    //sample method to send login data
    this.sendLogin = function($email, $passwort, $callback) {
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue() + '/login', 
            headers: { 
                    'Content-Type':'application/json'
            },
            data: { 
              "user" : $email,
              "passwort": $passwort
            }
        })
        .success(function(data, status) {
            $callback(true, data, status); 
        })
        .error(function(data, status) {
            $callback(false, data, status);
        });
    };


     //sample method to send login data
    this.getNodes = function($callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue() + '/listNodes', 
            headers: { 
                    'Content-Type':'application/json'
            }
        })
        .success(function(data, status) {
            $callback(true, data, status); 
        })
        .error(function(data, status) {
            $callback(false, data, status);
        });
    };

    //sample method to send login data
    this.getKeys = function($label, $callback) {
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue() + '/listKeys', 
            headers: { 
                    'Content-Type':'application/json'
            },
            data: { 
              "label" : $label
            }
        })
        .success(function(data, status) {
            $callback(true, data, status); 
        })
        .error(function(data, status) {
            $callback(false, data, status);
        });
    };

     //sample method to send login data
    this.getRelations = function($label, $callback) {
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue()+ '/listRelations', 
            headers: { 
                    'Content-Type':'application/json'
            },
            data: { 
              "label" : $label
            }
        })
        .success(function(data, status) {
            $callback(true, data, status); 
        })
        .error(function(data, status) {
            $callback(false, data, status);
        });
    };


}]);
