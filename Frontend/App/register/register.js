(function () {
    'use strict';

    angular
        .module('queryBuilder.register', [])
        .controller('RegisterController', RegisterController);

    RegisterController.$inject = ['$http', '$requests', '$serverRestLocation', '$location', '$rootScope'];
    function RegisterController($http, $requests, $serverRestLocation, $location, $rootScope) {
        var vm = this;

        vm.register = register;

        function register() {

             vm.text = "";

            if(!validateEmail(vm.user.email)){
            vm.text += "Bitte geben Sie eine korrekte Email Adresse ein. \n";
        }
        if(vm.user.password !=  vm.user.passwordRepeat){
            vm.text += "Das Passwort und das wiederholte Passwort müssen übereinstimmen. \n";
        }

        if(vm.text != ""){
            //okDialog($mdDialog, 'Folgendes fehlt', vm.text, ev);
            $("#loginErrorModal").modal();
        }
        else {

            $requests.register(vm.user, vm.callback);

        }
        }


        function validateEmail(email) {
            var re =  /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
            return re.test(email);
        }

        vm.callback = function($success, $data, $status) {
		vm.hasError = !$success;
		if($success){
			$location.path("/expertmode");
		}
		else
		{
			vm.error = $data;
		}
	}
    }


})();