(function(angular) {
  'use strict';
angular.module('OutputTable', [])
  .controller('Controller', ['$scope', function($scope) {

    $scope.input="B";

    $scope.setData = function (data) {
        $scope.input = data;
       // console.log($scope.input);
        $scope.data = convertTableWidgetData(data);
    }
    //console.log($scope.input);

            
    var widgetData = $scope.input;
    //var data = "";
    //console.log($scope.json);

        
    function convertTableWidgetData(widgetData, div) {
      var headerData = getTableHeader(widgetData);
      var bodyData = getTableBody(widgetData);
      return {tableHeader:headerData, tableBody:bodyData};
    }

    function getTableHeader(data){
      // convert header data
      //console.log(data[0]);
      //console.log(data[1]);
      var headerData = convertTableHeader(data[0], 1);
      // get max level
      var maxLevel = 0;
      for(var i=0;i<headerData.length;i++){
        if(headerData[i].level>maxLevel){
          maxLevel = headerData[i].level;
        }
      }
      // generate headlines
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
        
    function convertTableHeader(data, level) {
      var headerData = [];
      for (var prop in data) {
          //console.log(data[prop]);
        if (typeof data[prop] == 'object' && data[prop] != null) {
          //console.log(data[prop][0]);
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

    function getTableBody(data){
      var tableRows = [];
      for(var j = 0;j < data.length; j++){
        var bodyData = convertTableBody(data[j], 1);
        // get max level
        var maxLevel = 0;
        for(var i=0;i<bodyData.length;i++){
          if(bodyData[i].level>maxLevel){
            maxLevel = bodyData[i].level;
          }
        }
        // generate rows
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

    function convertTableBody(data, level) {
      //console.log(data);
      var bodyData = [];
      for (var prop in data) {
          //console.log(prop);
        if (typeof data[prop] == "object" && data[prop] != null) {
          var subBodyData;
          //console.log(data[prop]);
          //console.log(data[prop].length);
          //for(var i=0;i<data[prop].length;i++){
            subBodyData = convertTableBody(data[prop], level + 1);             
            var colspan = 0;
            for(var j=0;j<subBodyData.length;j++){
              colspan += subBodyData[j].colspan;
              subBodyData[j].elementType = "object";
              bodyData.push(subBodyData[j]);
            }
          //}   
        }
        else{
          bodyData.push({ title: data[prop], level: level, colspan: 1, elementType: "primitive"});
        }
      }
      //console.log(bodyData);
      return bodyData;
    }
  }]
)


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

              scope.$watch('table', function(newvalue, oldvalue) {
                // if(newvalue !== oldvalue){ //everythime--> otherwise there will be an error in the query builder
                   //console.log(newvalue);
                    thisscope.$parent.setData(newvalue);
                //}

              }, true);

              

        }
      };
 });
})(window.angular); 

