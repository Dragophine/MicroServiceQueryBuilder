'use strict';

angular.module('queryBuilder.register', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/register', {
    templateUrl: 'register/register.html',
  });
}])

.controller('registerCtrl', ['$http', '$mdDialog', '$serverRestLocation', '$location', function($http, $mdDialog, $serverRestLocation, $location) {
	this.geschlecht = "Frau";
	this.anrede  = "";
	this.vorname ="";
	this.nachname = "";
	this.email = "";
	this.strasse = "";
	this.hausnummer = 0;
	this.plz = 0;
	this.ort = "";
	this.password = "";
	this.passwordRepeat = "";

	
    this.register = function (ev) {
        this.text = "";
        if(this.vorname === null || this.vorname === ""){
            this.text += "Bitte geben Sie einen Vornamen ein. \n";
        }
        if(this.nachname === null || this.nachname === ""){
            this.text += "Bitte geben Sie einen Nachnamen ein. \n";
        }
        if(!validateEmail(this.email)){
            this.text += "Bitte geben Sie eine korrekte Email Adresse ein. \n";
        }
        if(this.strasse === null || this.strasse === ""){
            this.text += "Bitte geben Sie eine korrekte Strasse ein. \n";
        }
        if(isNaN(this.hausnummer) || this.hausnummer < 1 ){
            this.text += "Bitte geben Sie eine korrekte Hausnummer ein. \n";
        }
        if(isNaN(this.plz) || this.plz < 1000){
            this.text += "Bitte geben Sie eine korrekte Postleitzahl ein. \n";
        }
        if(this.ort === null || this.ort === ""){
            this.text += "Bitte geben Sie einen Ort ein. \n";
        }
        if(this.password === null || this.password.length < 6 ){
            this.text += "Bitte geben Sie einen Passwort mit mindestens 6 Buchstaben ein. \n";
        }
        if(this.password !=  this.passwordRepeat){
            this.text += "Das Passwort und das wiederholte Passwort müssen übereinstimmen. \n";
        }

        if(this.text != ""){
            okDialog($mdDialog, 'Folgendes fehlt', this.text, ev);
        }
        else {
    	   sendData($serverRestLocation.getValue() + "/users", this);
        }
    };
}]);



function validateEmail(email) {
    var re =  /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
    return re.test(email);
}