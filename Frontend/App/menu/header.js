'use strict';

angular.module('queryBuilder.header',[])
.controller('headerCtrl',  function($rootScope, $scope){
	
	var self = this;
	self.loggedin = $rootScope.authenticated;
	console.log(self.loggedin);
	 $scope.items = [
		{'name': 'Query Builder', 'link' : 'querybuilder', "needAuthentication": true},
		{'name': 'Expertmode', 'link' : 'expertmode', "needAuthentication": true},
		{'name': 'Category', 'link' : 'category', "needAuthentication": true},	
		{'name': 'Alerting', 'link' : 'alerting', "needAuthentication": true},
		{'name': 'Alerting Statistik', 'link' : 'alertingStatistics', "needAuthentication": true},
        {'name': 'Register', 'link' : 'register', "needAuthentication": false},
        {'name': 'Login', 'link' : 'login', "needAuthentication": false},
		{'name': 'Logout', 'link' : 'login', "needAuthentication": true}  
    ];



	$scope.$on('login', function () {
		
	self.loggedin = $rootScope.authenticated;
        // handle login event here
    });

	$scope.condition = function(item) { 
		if( item.name == "Login") {
			return !self.loggedin
		} else {
		return (item.needAuthentication == false || (item.needAuthentication == true && self.loggedin)); };
	}

});

/*'use strict';

angular.module('queryBuilder.header',[])
.controller('headerCtrl', function ($rootScope, $scope) {
	var self = this;
	 self.loggedin = rootScope.authenticated;
	 console.log(self.loggedin);
    $scope.items = [
        {'name': 'Home', "needAuthentication": false},
        {'name': 'About', "needAuthentication": false},
        {'name': 'Settings', "needAuthentication": true},
        {'name': 'Logout', "needAuthentication": true}
    ];

	$scope.condition = function(item) { return item.needAuthentication == false || (item.needAuthentication == true && self.loggedin); };
});*/