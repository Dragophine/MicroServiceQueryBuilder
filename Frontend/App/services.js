angular.module('queryBuilder.services', ['ngCookies'])
/**
   * This service provides the actual server location.
   * 
   * @version 1.0.0
   */
.service('$serverRestLocation',  function() {
    var serverRestLocation = "http://localhost:8080";

    this.getValue = function() {
        return serverRestLocation;
    };
})
/**
   * This servicesprovides all possible requests to the application.
   * 
   * @version 1.0.0
   */
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

    /**
     * Execute Query with Querybuilder.
     * It should return the table or an error message.
     * 
     * @param {Object} $query - the query which should be executed.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status t
     *              to the calling function.
     */
    this.getResultFromQueryQueryBuilder = function($query, $callback) {
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue() + '/queryBuilder/execute', 
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

     /**
     * This method should return the query in cypher.
     * It can be used to convert a query in the querybuilder
     * from the internal representation to cypher.
     *
     * @param {Object} $query - the query which should be converted.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
    this.getQueryFromQueryQueryBuilder = function($query, $callback) {
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue() + '/queryBuilder/queryString', 
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



    this.register = function($user,  $callback) {  
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue()+ '/user/', 
            headers: {  'Content-Type':'application/json',
        },
            data: { 
                email : $user.email,
                password : $user.password,
                firstName : $user.firstName,
                lastName : $user.lastName}
        })
        .success(function(data, status) {
            $callback(true, data, status); 
        })
        .error(function(data, status) {
            $callback(false, data, status);
        });
        };


    /**
     * This method should return all nodes in the database.
     *
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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

    /**
     * This method should return all keys (attribute names) for a certain node (as label)
     * from the database.
     *
     * @param {string} $label -  The name of the node.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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

     /**
     * This method should return all keys (attribute names) for a certain relationship (as label)
     * from the database.
     *
     * @param {string} $label -  The name of the relationship.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
    this.getRelationshipKeys = function($label, $callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue() + '/relationshiptypes/' + $label + '/keys', 
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
    
      /**
     * This method returns all relationships with their corresponding nodes for 
     * a certain node (as $label).
     *
     * @param {string} $label -  The name of the node.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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
        console.log($category);
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

    /**
     * This method saves a query from the query builder. 
     * Only new queries can be saved. If an existing should be saved an
     * update needs to be done.
     *
     * @param {Object} $query - the query which should be saved.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
    this.saveQueryInBuilder = function($query, $callback) {
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue() + '/queryBuilder', 
            headers: {  'Content-Type':'application/json'},
            data: $query
        })
        .success(function(data, status) {
            if($callback !== undefined){
                $callback(true, data, status);
            }
        })
        .error(function(data, status) {
            if($callback !== undefined){
                 $callback(false, data, status);
            }
        });
    };

    /**
     * This method updates a query from the query builder.
     * Before one can do an update, the query must be saved first.
     *
     * @param {Object} $query - the query which should be updated.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
    this.updateQueryInBuilder = function($query, $id, $callback) {
        $http({
            method : 'PUT',
            url : $serverRestLocation.getValue() + '/queryBuilder/' + $id, 
            headers: {  'Content-Type':'application/json'},
            data: $query
        })
        .success(function(data, status) {
            if($callback !== undefined){
                $callback(true, data, status);
            }
        })
        .error(function(data, status) {
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

    /**
     * This method deletes a query from the query builder.
     * The query is referenced by the id.
     *
     * @param {number} $id - the id of the query which should be deleted.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
     this.deleteQueryInBuilder = function( $id, $callback) {
       $http({
            method : 'DELETE',
            url : $serverRestLocation.getValue() + '/queryBuilder/' + $id, 
            headers: {  'Content-Type':'application/json'}
        })
        .success(function(data, status) {
            if($callback !== undefined){
                $callback(true, data, status);
            }
        })
        .error(function(data, status) {
            if($callback !== undefined){
                 $callback(false, data, status);
            }
        });
    };    
    
    //load query
    this.loadAllQueries = function($callback) {
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

    /**
     * This method loads all saved query builder queries from the database .
     *
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
    this.loadAllQueriesInBuilder = function($callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue() + '/queryBuilder', 
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

      /**
     * This method filters the query builder queries which are saved in the database.
     * One can filter the attributes by name, category and description.
     *
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
    this.loadSomeQueriesInBuilder = function($callback, $name, $category, $description) {
        var filter = "?";
        if($name !== null && $name !== undefined && $name !== ""){
            filter = filter + "name=" + $name;
        }
        if($category !== null && $category !== undefined && $category !== ""){
            if(filter !== "?"){
                 filter = filter + "&";
            }
            filter = filter + "category=" + $category;
        }
        if($description !== null && $description !== undefined && $description !== ""){
            if(filter !== "?"){
                 filter = filter + "&";
            }
            filter = filter + "description=" + $description;
        }
        if(filter != "?"){
            $http({
                method : 'GET',
                url : $serverRestLocation.getValue() + '/queryBuilder' + filter, 
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
        }
        else{
            this.loadAllQueriesInBuilder($callback);
        }
       
    };
    
    //get query by name
    this.getQueryByName = function($name, $callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue() + '/expertqueries/' + $name,
            headers: { 
                    'Content-Type':'application/json'
            },
            data: { 
                "name" : $name
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
    this.addAlert = function($name, $selectedQuery, $type, $filterType,
    		$email, $value, $callback) {
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue() + '/alerts', 
            headers: {  'Content-Type':'application/json'},
            data: { 
              "name" : $name,
              "query" :  $selectedQuery,
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
    this.saveAlert = function($existingName, $name, $selectedQuery, $type, $filterType,
    		$email, $value, $callback) {
        $http({
            method : 'PUT',
            url : $serverRestLocation.getValue() + '/alerts/' + $existingName, 
            headers: {  'Content-Type':'application/json'},
            data: { 
              "name" : $name,
              "query" :  $selectedQuery,
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

    /**
     * This method gets all exsiting categories.
     *
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
    this.getAllCategories = function($callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue() + '/categories', 
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

        // add category
    this.addCategory = function($name, $description, $callback) {
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue() + '/categories', 
            headers: {  'Content-Type':'application/json'},
            data: { 
              "name" : $name,
              "description" :  $description }
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

        
    // update category
    this.updateCategory = function($id, $name, $description, $callback) {
        $http({
            method : 'PUT',
            url : $serverRestLocation.getValue() + '/categories/' + $id, 
            headers: {  'Content-Type':'application/json'},
            data: { 
              "name" : $name,
              "description" :  $description }
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

        // delete categories
    this.deleteCategory = function($id, $callback) {
        $http({
            method : 'DELETE',
            url : $serverRestLocation.getValue() + '/categories/' + $id, 
            headers: {  'Content-Type':'application/json'},
            data: { 
                "name" : $id
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
    
    
    this.loadSomeQueriesInExpertMode = function($callback, $name, $category, $description) {
        var filter = "?";
        if($name !== null && $name !== undefined && $name !== ""){
            filter = filter + "name=" + $name;
        }
        if($category !== null && $category !== undefined && $category !== ""){
            if(filter !== "?"){
                 filter = filter + "&";
            }
            filter = filter + "category=" + $category;
        }
        if($description !== null && $description !== undefined && $description !== ""){
            if(filter !== "?"){
                 filter = filter + "&";
            }
            filter = filter + "description=" + $description;
        }
        if(filter != "?"){
            $http({
                method : 'GET',
                url : $serverRestLocation.getValue() + '/expertqueries/' + filter, 
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
        }
        else{
            this.loadAllQueries($callback);
        }
       
    };

    /**
     * This method gets all exsiting categories.
     *
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
    this.getAllUsers = function($callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue() + '/users', 
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


      /**
     * This method gets all exsiting categories.
     *
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
    this.getAllAuthorities = function($id, $callback) {
        $http({
            method : 'GET',
            url : $serverRestLocation.getValue() + '/user/'+ $id, 
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

            // add category
    this.addAuthority = function($email, $authority, $callback) {
        console.log("ADDD")
        $http({
            method : 'POST',
            url : $serverRestLocation.getValue() + '/user/'+ $email+ '/authority', 
            headers: {  'Content-Type':'application/json'},
            data: { 
              "authority" : $authority }
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

            // delete categories
    this.deleteAuthority = function($email, $authority, $callback) {
        $http({
            method : 'DELETE',
            url : $serverRestLocation.getValue() + '/user/' + $email + '/authority', 
            headers: {  'Content-Type':'application/json'},
            data: { 
                "authority" : $authority
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
