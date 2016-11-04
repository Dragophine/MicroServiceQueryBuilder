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
    this.getResultFromQuery = function($query, $callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue() + '/expertModus?query=' + $query, 
            headers: {  'Content-Type':'application/json'}
        })
        .success(function(data, status) {
            $callback(true, data, status);
        })
        .error(function(data, status) {
           $callback(false, data, status);
        });
    };

    //sample method to send login data
    this.sendLogin = function($email, $passwort, $callback) {
        $http({
            method : 'POST',
            url : $serverRestLocation + '/login', 
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

}]);
