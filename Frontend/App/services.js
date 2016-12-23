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
            url : $serverRestLocation.getValue() + '/expertqueries/execute', 
            headers: {  'Content-Type':'application/json'},
            data: { 
              "query" : $query,
              "parameter" :  $params }
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

    //Execute Query with Querybuilder
    this.getResultFromQueryQueryBuilder = function($query, $callback) {
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue() + '/buildQuery', 
            headers: {  'Content-Type':'application/json'},
            data:      $query
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


    //getNodes 
    this.getNodes = function($callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue() + '/nodetypes', 
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

    //get Keys
    this.getKeys = function($label, $callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue() + '/nodetypes/' + $label + '/keys', 
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


    
    //getRelations with nodes
    this.getRelationsWithNodes = function($label, $callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue()+ '/nodetypes/'+ $label + '/relations/', 
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

    // save query
    this.saveQuery = function($query, $params, $name, $description, $category, $callback) {
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue() + '/expertqueries', 
            headers: {  'Content-Type':'application/json'},
            data: { 
              "query" : $query,
              "parameter" :  $params,
                "name" : $name,
                "description" : $description,
                "category" : $category }
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
    
    // delete query
    this.deleteQuery = function($query, $params, $name, $description, $category, $callback) {
        $http({
            method : 'DELETE',
            url : $serverRestLocation.getValue() + '/expertqueries/' + $name, 
            headers: {  'Content-Type':'application/json'},
            data: { 
              "query" : $query,
              "parameter" :  $params,
                "name" : $name,
                "description" : $description,
                "category" : $category }
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
    
    //load query
    this.loadQuery = function($callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue() + '/expertqueries', 
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
    
    //get all already existing alerts
    this.getAllAlertNames = function($callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue() + '/alerts', 
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
    
    // add alert
    this.addAlert = function($name, $nodeName, $attributeName, $type, $filterType,
    		$email, $value, $callback) {
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue() + '/alerts', 
            headers: {  'Content-Type':'application/json'},
            data: { 
              "name" : $name,
              "nodeName" :  $nodeName,
              "attributeName" : $attributeName,
              "type" : $type,
              "filterType" : $filterType,
              "value" : $value,
              "email" : $email }
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
    
    // save alert
    this.saveAlert = function($existingName, $name, $nodeName, $attributeName, $type, $filterType,
    		$email, $value, $callback) {
        $http({
            method : 'PUT',
            url : $serverRestLocation.getValue() + '/alerts/' + $existingName, 
            headers: {  'Content-Type':'application/json'},
            data: { 
              "name" : $name,
              "nodeName" :  $nodeName,
              "attributeName" : $attributeName,
              "type" : $type,
              "filterType" : $filterType,
              "value" : $value,
              "email" : $email }
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
    
    // delete alert
    this.deleteAlert = function($name, $callback) {
        $http({
            method : 'DELETE',
            url : $serverRestLocation.getValue() + '/alerts/' + $name, 
            headers: {  'Content-Type':'application/json'},
            data: { 
                "name" : $name
                }
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
        
    // execute alert
    this.executeAlert = function($name, $callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue() + '/alerts/' + $name + '/execute', 
            headers: {  'Content-Type':'application/json'},
            data: { 
                "name" : $name
                }
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
}]);
