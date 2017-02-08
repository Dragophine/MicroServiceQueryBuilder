(function () {
    'use strict';

    angular
        .module('queryBuilder.register', [])
        .controller('RegisterController', RegisterController);

    RegisterController.$inject = ['$http', '$requests', '$serverRestLocation', '$location', '$rootScope'];
    function RegisterController($http, $requests, $serverRestLocation, $location, $rootScope) {
        var self = this;

        self.register = register;

        /**
	    * Executes the registration of a new user and checks all fields of the form are correctly filled in
	    */
        function register() {
             self.text = "";
            if(!validateEmail(self.user.email)){
                self.text += "Bitte geben Sie eine korrekte Email Adresse ein. \n";
            }
            if(self.user.password !=  self.user.passwordRepeat){
                self.text += "Das Passwort und das wiederholte Passwort müssen übereinstimmen. \n";
            }
            if(self.text != ""){
                $("#registerErrorModal").modal();
            }
            else {
                $requests.register(self.user, self.callback);
            }
        }

        /**
	    * Validates that the filled in email is a valid email address
	    */
        function validateEmail(email) {
            var re =  /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
            return re.test(email);
        }

        /**
	 * Callback from register call. If query was successful go to login page.
	 * Otherwise print error.
	 *
	 * @param {boolean} $success - true when there are no errors.
	 * @param {Object} $data - the requested data.
     * @param {number} $status - the actual server status.
	 */
        self.callback = function($success, $data, $status) {
		self.hasError = !$success;
		if($success){
			$location.path("/login");
		}
		else
		{
			self.error = $data;
		}
	}
    }


})();