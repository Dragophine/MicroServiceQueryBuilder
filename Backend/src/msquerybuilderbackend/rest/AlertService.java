package msquerybuilderbackend.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.transaction.Transaction;
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

import msquerybuilderbackend.business.AlertBusiness;
import msquerybuilderbackend.entity.Alert;
import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.entity.QueryBuilderJsonStringObject;
import msquerybuilderbackend.exception.InvalidTypeException;
import msquerybuilderbackend.repository.AlertRepository;
import msquerybuilderbackend.repository.ExpertQueryRepository;

/**
 * Service class/controller for Alert REST services
 * @author Martin
 *
 */
@RestController
@Component
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
	//CrossOrigin request allow to call a different server from
	//a certain frontend hosted on a certain address.
	//This means that the adress of the server and 
	//the adress of client must not be the same.
	//Pleas add @CrossOrigin to every request.
	@RequestMapping(value="/alerts",  method=RequestMethod.GET)
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
	public ResponseEntity<Result> executeAlert( @PathVariable String alertId){
		return alertBusiness.executeAlert(alertId);
    }
	
//	@CrossOrigin 
//	@RequestMapping(value="/alerts/{alertId}/execute",  method=RequestMethod.GET)
//	public ResponseEntity<Result> executeAlert( @PathVariable String alertId){
//		Alert alert=null;
//		Long id = new Long(-1);
//		
//		try
//		{
//			id = Long.parseLong(alertId);
//		}
//		catch(NumberFormatException P_ex)
//		{
//			/**
//			 * Wenn der mitübergebene Wert nicht auf Long umgewandelt werden kann,
//			 * ist der mitübergene Wert offensichtlich keine Zahl, muss also der
//			 * eindeutige Name sein. 
//			 */
//		}
//		
//		if (id >=0){
//			 alert= alertRepository.findOne(id);
//		} else{
//			 alert= alertRepository.findByName(alertId);
//		}
//		
//		String queryString="";
//		if (alert.getType().equals("String") || alert.getType().equals("string")){
//			 queryString = "MATCH (n:"+alert.getNodeName()+") where n."+alert.getAttributeName()+alert.getFilterType()+"'"+(String)alert.getValue()+"' return n as "+alert.getNodeName();
//		}else{
//			 queryString = "MATCH (n:"+alert.getNodeName()+") where n."+alert.getAttributeName()+alert.getFilterType()+alert.getValue()+" return n as "+alert.getNodeName();
//		}
//		Result result= neo4jOperations.query(queryString, new HashMap<String, String>());
//		return new ResponseEntity<Result>(result, HttpStatus.OK);
//    }
	
	
	/**
	 * method which gets the POST-request and calls the alertBusiness method
	 * creates an alert in the neo4j database
	 * @return the neo4j ID of the new object or 0L if the name already exists
	 */
	@CrossOrigin 
	@Transactional
	@RequestMapping(value="/alerts",  method=RequestMethod.POST)
	public ResponseEntity<Long> postAlert(@RequestBody Alert alert) throws Exception {
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
	@Transactional
	public ResponseEntity<Alert> updateAlert( @PathVariable String alertId, @RequestBody Alert al) throws Exception{			
		return new ResponseEntity<Alert>(alertBusiness.updateAlert(alertId, al), HttpStatus.OK);
    }
	

	/**
	 * method which gets the DELETE-request and calls the alertBusiness method
	 * deletes a specific alert
	 * @return Statuscode 200
	 */
	@CrossOrigin 
	@RequestMapping(value="/alerts/{alertId}",  method=RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<Result> deleteAlert( @PathVariable String alertId){
		alertBusiness.deleteAlert(alertId);		
		return new ResponseEntity<Result>(HttpStatus.OK);
    }
	
	
	/**
	 * method which executes all alerts periodically
	 */
	// Alle 24 Stunden werden die Alerts überprüft
//	@Scheduled(fixedRate = 5000)
	@Scheduled(fixedRate = 86400000)
	public void executeAllAlerts()
	{
		alertBusiness.executeAllAlerts();
	}	
}
