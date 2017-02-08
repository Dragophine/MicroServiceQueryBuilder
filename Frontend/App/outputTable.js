(function(angular) {
  'use strict';
angular.module('OutputTable', [])
  .controller('Controller', ['$scope', function($scope) {

    /**
	  * Function for changing the data from which the table should be built.
	  * 
    * @param {Object} $data - Data from which the ouptput table should be built.
	  */
    $scope.setData = function (data) {
        $scope.input = data;
        $scope.data = convertTableWidgetData(data);
    }

    /**
	  * Function for converting the Data from the database to a table.
	  * 
    * @param {Object} $widgetData - Data from which the ouptput table should be built.
	  */                
    function convertTableWidgetData(widgetData) {
      var headerData = getTableHeader(widgetData);
      var bodyData = getTableBody(widgetData);
      return {tableHeader:headerData, tableBody:bodyData};
    }

    /**
	  * Function creating the header of the table.
	  * 
    * @param {Object} $widgetData - Data from which the ouptput table should be built.
	  */  
    function getTableHeader(data){
      var headerData = convertTableHeader(data[0], 1);
      var maxLevel = 0;
      for(var i=0;i<headerData.length;i++){
        if(headerData[i].level>maxLevel){
          maxLevel = headerData[i].level;
        }
      }
      var tableColumns = [];
      for (var level = 1; level <= maxLevel; level++) {
        var headerRow = [];
        for (var i = 0; i < headerData.length; i++) {
          if (headerData[i].level == level) {
            var rowSpan = 1;
            if (headerData[i].elementType == "primitive") {
              rowSpan = maxLevel - headerData[i].level + 1;
            }
            headerRow.push({title: headerData[i].title,colspan:headerData[i].colspan,rowspan:rowSpan});
          }
        }
        tableColumns.push(headerRow);
      }
      return tableColumns;
    }

    /**
	  * Function for converting the data to headings for the output table.
	  * 
    * @param {Object} $data - Data from which the ouptput table should be built.
    * @param {Object} $level - Level of the table in which the heading should be.
	  */      
    function convertTableHeader(data, level) {
      var headerData = [];
      for (var prop in data) {
        if (typeof data[prop] == 'object' && data[prop] != null) {
          var subHeaderData = convertTableHeader(data[prop], level + 1);                
          var colspan = 0;
          for(var i=0;i<subHeaderData.length;i++){
            colspan += subHeaderData[i].colspan;
            headerData.push(subHeaderData[i]);
          }
          headerData.push({ title: prop, level: level, colspan: colspan, elementType: "object"});
        }
        else{
          headerData.push({ title: prop, level: level, colspan: 1, elementType: "primitive"});
        }
      }
      return headerData;
    }

    /**
	  * Function creating the body of the table.
	  * 
    * @param {Object} $widgetData - Data from which the ouptput table should be built.
	  */  
    function getTableBody(data){
      var tableRows = [];
      for(var j = 0;j < data.length; j++){
        var bodyData = convertTableBody(data[j], 1);
        var maxLevel = 0;
        for(var i=0;i<bodyData.length;i++){
          if(bodyData[i].level>maxLevel){
            maxLevel = bodyData[i].level;
          }
        }
        for (var level = 1; level <= maxLevel; level++) {
          var row = [];
          for (var i = 0; i < bodyData.length; i++) {
            if (bodyData[i].level == level) {
              var rowSpan = 1;
              if (bodyData[i].elementType == "primitive") {
                rowSpan = maxLevel - bodyData[i].level + 1;
              }
              row.push({title: bodyData[i].title,colspan:bodyData[i].colspan,rowspan:rowSpan});
            }
          }
          tableRows.push(row);
        }
      }
      return tableRows;
    }

    /**
	  * Function for converting the data to body rows and columns for the output table.
	  * 
    * @param {Object} $data - Data from which the ouptput table should be built.
    * @param {Object} $level - Level of the table in which the body data should be.
	  */
    function convertTableBody(data, level) {
      var bodyData = [];
      for (var prop in data) {
        if (typeof data[prop] == "object" && data[prop] != null) {
          var subBodyData;
            subBodyData = convertTableBody(data[prop], level + 1);             
            var colspan = 0;
            for(var j=0;j<subBodyData.length;j++){
              colspan += subBodyData[j].colspan;
              subBodyData[j].elementType = "object";
              bodyData.push(subBodyData[j]);
            }  
        }
        else{
          bodyData.push({ title: data[prop], level: level, colspan: 1, elementType: "primitive"});
        }
      }
      return bodyData;
    }
  }]
)

  /**
	* Directive to denote how the table should look like.
	* 
	*/
 .directive('outputtable', function() {
     return {
        scope: {
            table: '=',
            setData: '&',
            data: '='
        },
        template : '<table  class="table table-responsive"><thead><tr ng-repeat="headline in $parent.data.tableHeader"><th ng-repeat="headlineColumn in headline" rowspan="{{headlineColumn.rowspan}}" colspan="{{headlineColumn.colspan}}">{{headlineColumn.title}}</th></tr></thead><tbody><tr ng-repeat="row in $parent.data.tableBody"><td ng-repeat="rowColumn in row" rowspan="{{rowColumn.rowspan}}" colspan="{{rowColumn.colspan}}">{{rowColumn.title}}</td></tr></tbody></table>',
    
        link: function(scope, el, attr) {
              var thisscope = scope;

              /**
	            * Function to watch for changes on the data, if changes happens the data is set to the new data.
	            * 
              * @param {Object} $newvalue - new value of the data.
              * @param {Object} $oldvalue - old value of the data.
	            */
              scope.$watch('table', function(newvalue, oldvalue) {
            	  if(newvalue !== undefined)
        		  {
                    thisscope.$parent.setData(newvalue);
        		  }
              }, true);
        }
      };
 });
})(window.angular); 

