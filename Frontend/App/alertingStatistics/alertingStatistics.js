'use strict';

angular.module('queryBuilder.alertingStatistics', ['ngRoute', 'queryBuilder.services'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/alertingStatistics', {
    templateUrl: 'alertingStatistics/alertingStatistics.html'
  });
}])
.controller('alertingStatisticsCtrl', ['$requests',
	function($requests) {
	var self = this;
	
	self.selectedAlert = "";
	self.dates = [];
	self.alertDateAndCount = [];
	
	
	
	
	google.charts.load('current', {packages: ['corechart', 'line']});
	google.charts.setOnLoadCallback(drawBasic);

	function drawBasic() {

	      var data = new google.visualization.DataTable();
	      data.addColumn('date', 'Date');
	      data.addColumn('number', 'Alerts');
	      
	      var i;
	      for (i=0; i<self.alertDateAndCount.length; i++) {
	    	  data.addRow(new Array (new Date (self.alertDateAndCount[i].date), self.alertDateAndCount[i].counts));
	      }

	      var options = {
	        hAxis: {
	          title: 'Date',
	          format: 'd/M/yy'
	        },
	        vAxis: {
	          title: 'Alerts',
	          format: '#,###',
	          minValue: 0
	        }
	      };

	      var chart = new google.visualization.LineChart(document.getElementById('chart_div'));

	      chart.draw(data, options);
	    }
		
	self.getAlertsCB = function($success, $data, $status) {
		self.hasError = !$success;
		if($success){
			self.existingAlerts = $data;
			if(self.existingAlerts != null && self.existingAlerts.length > 0)
			{
				// default selection -> first alert in array
				self.selectedAlert = self.existingAlerts[0].name;
				var firstQuery = self.existingAlerts[0];				
				getDataFromAlert(firstQuery);
			}			
		}
		else
		{
			self.error = $data;
		}
	}	
	$requests.getAllAlertNames(self.getAlertsCB);

	self.selectAlert = function()
	{
		self.alertDateAndCount = [];
		var x;
		var firstQuery;
		for(x=0; x<self.existingAlerts.length; x++)
		{
			if(self.selectedAlert == self.existingAlerts[x].name)
			{
				firstQuery = self.existingAlerts[x];
				break;
			}
		}
		getDataFromAlert(firstQuery);
	}
	
	function getDataFromAlert($firstQuery)
	{
		if($firstQuery != null)
		{
			var zwischenergebnis = [];
			var results = {}, i, date;

			var minDate;
			var maxDate;
			// count alerts per date, get min and max date
			for (i=0; i<$firstQuery.dates.length; i++) {
			  // get the date without timezone offset
				var d = new Date($firstQuery.dates[i]);
				d = new Date(d.valueOf() + d.getTimezoneOffset() * 60000);
			  date = [d.getFullYear(),d.getMonth()+1,d.getDate()].join("-");
			  results[date] = results[date] || 0;
			  results[date]++;
			  
			  if(minDate == null || minDate > date)
			  {
				  minDate = date;
			  }
			  if(maxDate == null || maxDate < date)
			  {
				  maxDate = date;
			  }
			}
			
			// generate array with all values between min and max date
			var newDates = [],
		    currentDate = new Date(minDate),
		    d;
			var maximumDate = new Date(maxDate);

			while (currentDate <= maximumDate) {
			    var dateFormat = currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + currentDate.getDate();
			    
			    if(results[dateFormat] == null || results[dateFormat] == 0)
		    	{
			    	zwischenergebnis[dateFormat] = 0;
		    	}
			    else
		    	{
			    	zwischenergebnis[dateFormat] = results[dateFormat];
		    	}
			    
			    currentDate.setDate(currentDate.getDate() + 1);
			}

			// you can always convert it into an array of objects, if you must
			for (i in zwischenergebnis) {
			  if (zwischenergebnis.hasOwnProperty(i)) {
				  self.alertDateAndCount.push({date:i,counts:zwischenergebnis[i]});
			  }
			}
			drawBasic();
		}
	}
}]);