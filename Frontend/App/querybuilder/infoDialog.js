'use strict';

angular.module('queryBuilder.querybuilderInfoDialog', ['ngRoute'])


   /**
   * This controller helps showing informations to the user in a dialog.
   * The ngDialogData can contain a head and a content.
   * @version 1.0.0
   */
.controller('querybuilderInfoDialogCtrl', ['$requests', '$scope', 
    function($requests, $scope) {
        var self = this;

          /**
	     * The property containing the head and the content.
	     * @type {json}
	     */	
         self.info = $scope.ngDialogData;
       
	
}]);
