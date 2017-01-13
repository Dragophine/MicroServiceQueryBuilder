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
	

	@CrossOrigin 
	@RequestMapping(value="/alerts/{alertId}",  method=RequestMethod.GET)
	public ResponseEntity<Alert> getAlert( @PathVariable String alertId){
		return new ResponseEntity<Alert>(alertBusiness.getAlert(alertId), HttpStatus.OK);
    }
	
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
	
	
	
	@CrossOrigin 
	@Transactional
	@RequestMapping(value="/alerts",  method=RequestMethod.POST)
	public ResponseEntity<Long> postAlert(@RequestBody Alert alert) throws Exception {
		Long newID=alertBusiness.createAlert(alert);
		if (newID==0L) return new ResponseEntity<Long>(0L, HttpStatus.CONFLICT);
		return new ResponseEntity<Long>(newID, HttpStatus.OK);
	}
	
	@CrossOrigin 
	@RequestMapping(value="/alerts/name-list",  method=RequestMethod.GET)
	public ResponseEntity<List<String>> getAlertNames(){		
		return new ResponseEntity<List<String>>(alertBusiness.getNameList(), HttpStatus.OK);
    }
	
	@CrossOrigin 
	@RequestMapping(value="/alerts/{alertId}",  method=RequestMethod.PUT)
	@Transactional
	public ResponseEntity<Alert> updateAlert( @PathVariable String alertId, @RequestBody Alert al) throws Exception{			
		return new ResponseEntity<Alert>(alertBusiness.updateAlert(alertId, al), HttpStatus.OK);
    }
	

	
	@CrossOrigin 
	@RequestMapping(value="/alerts/{alertId}",  method=RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<Result> deleteAlert( @PathVariable String alertId){
		alertBusiness.deleteAlert(alertId);		
		return new ResponseEntity<Result>(HttpStatus.OK);
    }
	
	@Scheduled(fixedRate = 5000)
	public void executeAllAlerts()
	{
//		StringBuffer F_mailMessageBuffer = new StringBuffer("");
//		ResponseEntity<List<Alert>> F_alerts = getAlerts();
//		if(F_alerts != null)
//		{
//			for(Alert F_alert : F_alerts.getBody())
//			{
//				ResponseEntity<Result> F_result = executeAlert(F_alert.getId().toString());
//				if(F_result != null)
//				{
//					Iterator<Map<String, Object>> F_res = F_result.getBody().queryResults().iterator();
//					while(F_res.hasNext())
//					{
//						Map<String, Object> F_map = F_res.next();
//						for(Object F_obj : F_map.values())
//						{
//							/**
//							 * TODO: Auf FileType überprüfen und gegebenfalls Mail senden
//							 * Eventuelle Möglichkeiten um Mails zu senden:
//							 * - http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mail.html
//							 * - https://java.net/projects/javamail/pages/Home
//							 */
//						}
//					}
//				}
//				System.out.println("TEST");
//			}
//		}
//		System.out.println("TEST");
		alertBusiness.executeAllAlerts();
	}
	
	
	
	
}
