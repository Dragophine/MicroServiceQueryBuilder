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

    /**
     * This method saves a query from the expert mode. 
     * Only new queries can be saved.
     *
     * @param {String} $query - the query which should be saved.
     * @param {Array} $params - the parameters for the query.
     * @param {String} $name - the entered name for the query.
     * @param {String} $description - the entered description for the query.
     * @param {String} $category - the selected category for the query.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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
    
    
    /**
     * This method deletes a query from the expert mode. 
     *
     * @param {String} $query - the query which should be deleted.
     * @param {Array} $params - the parameters for the query.
     * @param {String} $name - the entered name for the query.
     * @param {String} $description - the entered description for the query.
     * @param {String} $category - the selected category for the query.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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
    
    /**
     * This method load all queries. 
     *
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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
    
    /**
     * This method returns the query object for a specific query name. 
     *
     * @param {String} $name - the entered name for the query.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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
    
    /**
     * This method returns all existing query names. 
     *
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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
    
    /**
     * This method creates a new alert. 
     *
     * @param {String} $name - the entered name for the alert.
     * @param {String} $selectedQuery - the selected query for the alert.
     * @param {String} $type - the selected type for the alert.
     * Examples: int, string,...
     * @param {String} $filterType - the selected filter type for the alert.
     * Examples: in, like,...
     * @param {String} $email - the entered email address for the alert.
     * @param {String} $value - the entered limit value for the alert.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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
    
    /**
     * This method saves a existing alert. 
     *
     * @param {String} $existingName - the name from overridden alert.
     * @param {String} $name - the entered name for the alert.
     * @param {String} $selectedQuery - the selected query for the alert.
     * @param {String} $type - the selected type for the alert.
     * Examples: int, string,...
     * @param {String} $filterType - the selected filter type for the alert.
     * Examples: in, like,...
     * @param {String} $email - the entered email address for the alert.
     * @param {String} $value - the entered limit value for the alert.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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
    
    /**
     * This method deletes a existing alert. 
     *
     * @param {String} $name - the entered name for the alert.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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
        
    /**
     * This method executes a existing alert. 
     *
     * @param {String} $name - the entered name for the alert.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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
     * This method gets all existing categories.
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

    /**
     * This method adds a new category.
     *
     * @param {string} $name -  The name of the category.
     * @param {string} $description -  The description of the category.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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

        
    /**
     * This method updates a existing category.
     *
     * @param {string} $id -  The id of the category which should be changed.
     * @param {string} $name -  The name of the category.
     * @param {string} $description -  The description of the category.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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

    /**
     * This method deletes a existing category.
     *
     * @param {string} $id -  The id of the category which should be deleted.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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
     * This method gets all existing users.
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
     * This method gets all existing authorities of a specific user.
     *
     * @param {string} $id -  The id of the user for who the authorities should be returned.
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

    /**
     * This method adds a authority to a specific user.
     *
     * @param {string} $email -  The email of the user to who the authorities should be added.
     * @param {string} $authority -  The authority which should be added.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
    this.addAuthority = function($email, $authority, $callback) {
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

    /**
     * This method deletes a authority from a specific user.
     *
     * @param {string} $email -  The email of the user from who the authorities should be deleted.
     * @param {string} $authority -  The authority which should be deleted.
     * @param {function($success: number, $data: string, $status: number)} $callback - 
     *            This function is called after the execution in order to send data and the status 
     *              to the calling function.
     */
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
