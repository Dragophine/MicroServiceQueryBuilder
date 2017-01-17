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
	self.addRows = [];
	self.addRows2 = [];
	
	
	
	
	google.charts.load('current', {packages: ['corechart', 'line']});
	google.charts.setOnLoadCallback(drawBasic);

	function drawBasic() {

	      var data = new google.visualization.DataTable();
	      data.addColumn('date', 'Date');
	      data.addColumn('number', 'Alerts');

//	      data.addRows([
//	        [0, 0],   [1, 10],  [2, 23],  [3, 17],  [4, 18],  [5, 9],
//	        [6, 11],  [7, 27],  [8, 33],  [9, 40],  [10, 32], [11, 35],
//	        [12, 30], [13, 40], [14, 42]
//	      ]);
	      
	      var i;
	      for (i=0; i<self.addRows2.length; i++) {
	    	  data.addRow(new Array (new Date (self.addRows2[i].date), self.addRows2[i].counts));
	      }

	      var options = {
	        hAxis: {
	          title: 'Date',
	          format: 'd/M/yy'
	        },
	        vAxis: {
	          title: 'Alerts'
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
				
//				var i;
				var firstQuery = self.existingAlerts[0];				
				var results = {}, rarr = [], i, date;

				var minDate;
				var maxDate;
				// count alerts per date, get min and max date
				for (i=0; i<firstQuery.dates.length; i++) {
				  // get the date
					var d = new Date(firstQuery.dates[i]);
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
//				    d = new Date(currentDate);
				    var dateFormat = currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + currentDate.getDate();
				    
				    if(results[dateFormat] == null || results[dateFormat] == 0)
			    	{
				    	self.addRows[dateFormat] = 0;
			    	}
				    else
			    	{
				    	self.addRows[dateFormat] = results[dateFormat];
			    	}
				    
				    currentDate.setDate(currentDate.getDate() + 1);
				}

				// you can always convert it into an array of objects, if you must
				for (i in self.addRows) {
				  if (self.addRows.hasOwnProperty(i)) {
					  self.addRows2.push({date:i,counts:self.addRows[i]});
				  }
				}
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
		self.addRows2 = [];
		self.addRows = [];
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

		if(firstQuery != null)
		{
			var results = {}, rarr = [], i, date;

			var minDate;
			var maxDate;
			// count alerts per date, get min and max date
			for (i=0; i<firstQuery.dates.length; i++) {
			  // get the date
				var d = new Date(firstQuery.dates[i]);
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
//			    d = new Date(currentDate);
			    var dateFormat = currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + currentDate.getDate();
			    
			    if(results[dateFormat] == null || results[dateFormat] == 0)
		    	{
			    	self.addRows[dateFormat] = 0;
		    	}
			    else
		    	{
			    	self.addRows[dateFormat] = results[dateFormat];
		    	}
			    
			    currentDate.setDate(currentDate.getDate() + 1);
			}

			// you can always convert it into an array of objects, if you must
			for (i in self.addRows) {
			  if (self.addRows.hasOwnProperty(i)) {
				  self.addRows2.push({date:i,counts:self.addRows[i]});
			  }
			}
			drawBasic();
		}
	}
}]);