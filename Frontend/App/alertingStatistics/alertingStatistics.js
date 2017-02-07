'use strict';

angular.module('queryBuilder.alertingStatistics', ['ngRoute', 'queryBuilder.services'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/alertingStatistics', {
    templateUrl: 'alertingStatistics/alertingStatistics.html'
  });
}])
/**
   * This controller handles the main operation in the user interface.
   * It configures and handle all actions in the alert statistic view.  
   * 
   * @version 1.0.0
   */
.controller('alertingStatisticsCtrl', ['$requests',
	function($requests) {
	var self = this;
	
    /**
     * Holds selected alert name.
     * @type {string}
     */
	self.selectedAlert = "";
    /**
     * Stores all dates between min and max date and all counts per day from selected alert.
     * @type {array}
     */
	self.alertDateAndCount = [];
	/**
	 * Load google charts
	 */
	google.charts.load('current', {packages: ['corechart', 'line']});
	google.charts.setOnLoadCallback(drawBasic);

	/**
	 * Draw a chart with Google charts. The chart shows how many alerts occured per day.
	 * The graph shows the period from the first occurrence of an alert to the last appearance 
	 * of an alert.
	 */
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
	
	/**
	 * Callback from get all alerts call. If query was successful show chart for first alert.
	 * Otherwise print error.
	 *
	 * @param {boolean} $success - true when there are no errors.
	 * @param {Object} $data - the requested data (In this case the alerts).
     * @param {number} $status - the actual server status.
	 */
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
	/**
	 * When the view is called, all alerts with all data are read from graph DB once,
	 * no other read tasks necessary.
	 */
	$requests.getAllAlertNames(self.getAlertsCB);

	/**
	 * User select another alert in comboBox.
	 */
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
	
	/**
	 * This method evaluates the timestamp data. A two-dimensional array is generated that contains
	 * all days from the first appearance of an alert to the last appearance of an alert. The number
	 * of times an alert occurs per day is also counted and stored in the array.
	 * 
     * @param {number} $selectedAlert - the alert which should be shown in chart.
	 */
	function getDataFromAlert($selectedAlert)
	{
		if($selectedAlert != null)
		{
			var zwischenergebnis = [];
			var results = {}, i, date;

			var minDate;
			var maxDate;
			// count alerts per date, get min and max date
			for (i=0; i<$selectedAlert.dates.length; i++) {
			  // get the date without timezone offset
				var d = new Date($selectedAlert.dates[i]);
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

			// create a two dimensional array with date and count data
			for (i in zwischenergebnis) {
			  if (zwischenergebnis.hasOwnProperty(i)) {
				  self.alertDateAndCount.push({date:i,counts:zwischenergebnis[i]});
			  }
			}
			// redraw chart
			drawBasic();
		}
	}
}]);