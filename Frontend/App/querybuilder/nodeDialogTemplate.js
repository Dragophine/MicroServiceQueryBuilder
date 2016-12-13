'use strict';

angular.module('queryBuilder.querybuildernodedialog', ['ngRoute'])



.controller('queryBuilderNodeDialogCtrl', ['$requests', '$scope', 
    function($requests, $scope) {
        var self = this;
	    
        self.node = $scope.ngDialogData;
        self.name = self.node['type'];
        self.keys = [];
        /***
		This methods load the relationships for a specific node.
		*/
		self.loadKeysForNodeCB = function($success, $data, $status){
			self.hasError = !$success;
			if($success){
				self.keys = $data; 
			}
		}

		$requests.getKeys(self.node['type'], self.loadKeysForNodeCB);
}]);
