package msquerybuilderbackend.rest;

import java.util.List;
import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import msquerybuilderbackend.business.AlertBusiness;
import msquerybuilderbackend.entity.Alert;
import msquerybuilderbackend.repository.AlertRepository;
import msquerybuilderbackend.repository.ExpertQueryRepository;

/**
 * Service class/controller for Alert REST services
 * @author Martin
 *
 */
@RestController
@Component
@Api(tags = {"AlertService"}, value = "Service for viewing and creating Alerts")
public class AlertService {

	@Autowired
	AlertBusiness alertBusiness;
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	@Autowired
	AlertRepository alertRepository;
	@Autowired
	ExpertQueryRepository expertQueryRepository;
	
	
	/**
	 * method which gets the GET-request and calls the alertBusiness method
	 * queries all alerts in the neo4j database
	 * @return a list of all alerts
	 */
	@CrossOrigin 
	@RequestMapping(value="/alerts",  method=RequestMethod.GET)
	@ApiOperation(value = "Returns all alerts",
	notes = "place for notes", response = Alert.class, responseContainer="List")
	public ResponseEntity<List<Alert>> getAlerts() {
		return new ResponseEntity<List<Alert>>(alertBusiness.getAllAlerts(), HttpStatus.OK);
    }
	
	/**
	 * method which gets the GET-request and calls the alertBusiness method
	 * queries a specific alert in the neo4j database
	 * @return the specific alert
	 */
	@CrossOrigin 
	@RequestMapping(value="/alerts/{alertId}",  method=RequestMethod.GET)
	@ApiOperation(value = "Returns a certain alert with a specific id",
	notes = "place for notes", response = Alert.class, responseContainer="ResponseEntity")
	public ResponseEntity<Alert> getAlert( @PathVariable String alertId){
		return new ResponseEntity<Alert>(alertBusiness.getAlert(alertId), HttpStatus.OK);
    }
		
	/**
	 * method which gets the GET-request and calls the alertBusiness method
	 * executes a specific alert
	 * @return the result of the execution 
	 */
	@CrossOrigin 
	@RequestMapping(value="/alerts/{alertId}/execute",  method=RequestMethod.GET)
	@ApiOperation(value = "Executes a certain alert with a specific ID",
	notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
	public ResponseEntity<Result> executeAlert( @PathVariable String alertId){
		return alertBusiness.executeAlert(alertId);
    }	
	
	/**
	 * method which gets the POST-request and calls the alertBusiness method
	 * creates an alert in the neo4j database
	 * @return the neo4j ID of the new object or 0L if the name already exists
	 */
	@CrossOrigin 
	@Transactional
	@RequestMapping(value="/alerts",  method=RequestMethod.POST)
	@ApiOperation(value = "Creates a new alert",
	notes = "place for notes", response = Long.class, responseContainer="ResponseEntity")
	public ResponseEntity<Long> postAlert(@RequestBody 
			@ApiParam(name = "new Alert to create",
			value = "Representation of the new Alert which will be created", required = true)
			Alert alert) throws Exception {
		Long newID=alertBusiness.createAlert(alert);
		if (newID==0L) return new ResponseEntity<Long>(0L, HttpStatus.CONFLICT);
		return new ResponseEntity<Long>(newID, HttpStatus.CREATED);
	}
	
	/**
	 * method which gets the GET-request and calls the alertBusiness method
	 * queries all names of alerts in the neo4j database
	 * @return a list of all names of alerts as Strings
	 */
	@CrossOrigin 
	@RequestMapping(value="/alerts/name-list",  method=RequestMethod.GET)
	@ApiOperation(value = "Returns all names of all alerts",
	notes = "place for notes", response = String.class, responseContainer="List")
	public ResponseEntity<List<String>> getAlertNames(){		
		return new ResponseEntity<List<String>>(alertBusiness.getNameList(), HttpStatus.OK);
    }
		
	/**
	 * method which gets the PUT-request and calls the alertBusiness method
	 * updates a specific alert
	 * @return the updated alert
	 */
	@CrossOrigin 
	@RequestMapping(value="/alerts/{alertId}",  method=RequestMethod.PUT)
	@ApiOperation(value = "Updates a certain alert with specific ID",
	notes = "place for notes", response = Alert.class, responseContainer="ResponseEntity")
	@Transactional
	public ResponseEntity<Alert> updateAlert( @PathVariable String alertId, @RequestBody
			@ApiParam(name = "Alert with new content do be updated",
			value = "Representation of the updated Alert with new content which will be updated", required = true) 
			Alert al) throws Exception{			
		return new ResponseEntity<Alert>(alertBusiness.updateAlert(alertId, al), HttpStatus.OK);
    }
	
	/**
	 * method which gets the DELETE-request and calls the alertBusiness method
	 * deletes a specific alert
	 * @return Statuscode 200
	 */
	@CrossOrigin 
	@RequestMapping(value="/alerts/{alertId}",  method=RequestMethod.DELETE)
	@ApiOperation(value = "Deletes a certain alert with specific ID",
	notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
	@Transactional
	public ResponseEntity<Result> deleteAlert( @PathVariable String alertId){
		alertBusiness.deleteAlert(alertId);		
		return new ResponseEntity<Result>(HttpStatus.OK);
    }
		
	/**
	 * method which executes all alerts periodically
	 */
	// Alle 6 Stunden werden die Alerts überprüft
	@Scheduled(fixedRate = 21600000)
//	@Scheduled(fixedRate = 86400000) --> 24 Stunden
	public void executeAllAlerts()
	{
		alertBusiness.executeAllAlerts();
	}	
}
