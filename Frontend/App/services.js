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
    var serverRestLocation = "http://localhost:8080/";

    this.getValue = function() {
        return serverRestLocation;
    };
})
.service('$requests',  function() {
    this.getExpertmodusResult = function($query){
        //get projekte
        function getItems (server) {
            $http({
                method : 'GET',
                url : server, 
                headers: {  'Content-Type':'application/json'}
            })
          .success(function(data, status) {
              self.projekte = data;
          })
          .error(function(data, status) {
              okDialog($mdDialog, 'Entschuldigung', 'Es trat ein Fehler auf');
          });
        }

    }
})
