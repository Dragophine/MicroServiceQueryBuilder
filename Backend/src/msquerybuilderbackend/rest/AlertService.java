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

import msquerybuilderbackend.entity.Alert;
import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.exception.InvalidTypeException;
import msquerybuilderbackend.repository.AlertRepository;
import msquerybuilderbackend.repository.ExpertQueryRepository;


@RestController
@Component
public class AlertService {

	
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
		List<Alert> alerts= alertRepository.getAllAlerts();
		return new ResponseEntity<List<Alert>>(alerts, HttpStatus.OK);
    }
	

	@CrossOrigin 
	@RequestMapping(value="/alerts/{alertId}",  method=RequestMethod.GET)
	public ResponseEntity<Alert> getAlert( @PathVariable String alertId){
		Alert alert=null;
		if (Long.parseLong(alertId) >=0){
			 alert= alertRepository.findOne(Long.parseLong(alertId));
		} else{
			 alert= alertRepository.findByName(alertId);
		}
		
		return new ResponseEntity<Alert>(alert, HttpStatus.OK);
    }
	
	@CrossOrigin 
	@RequestMapping(value="/alerts/{alertId}/execute",  method=RequestMethod.GET)
	public ResponseEntity<Result> executeAlert( @PathVariable String alertId){
		Alert alert=null;
		Long id = new Long(-1);
		
		try
		{
			id = Long.parseLong(alertId);
		}
		catch(NumberFormatException P_ex)
		{
			/**
			 * Wenn der mitübergebene Wert nicht auf Long umgewandelt werden kann,
			 * ist der mitübergene Wert offensichtlich keine Zahl, muss also der
			 * eindeutige Name sein. 
			 */
		}
		
		if (id >=0){
			 alert= alertRepository.findOne(id);
		} else{
			 alert= alertRepository.findByName(alertId);
		}
		
		if (alert.getQuery() != null)
		{
			String queryName = alert.getQuery();
			ExpertQuery expertQuery = expertQueryRepository.findByName(queryName);
			
			if(expertQuery != null)
			{
				try
				{
					String F_queryString = expertQuery.getQuery();
					if(F_queryString != null)
					{
						/**
						 * Exists parameter?
						 */
						Map<String,Object> paramsMap = new HashMap<String,Object>();
						Result queryResult;
				    	if (expertQuery.getParameter() !=null)
				    	{
				    		for (Parameter p:expertQuery.getParameter())
				    		{	
					    		paramsMap.put(p.getKey(), p.getValue());
					    	}
				    		queryResult = neo4jOperations.query(expertQuery.getQuery(), paramsMap,true);
				    	}
				    	else
				    	{
				    		queryResult = neo4jOperations.query(expertQuery.getQuery(),new HashMap<String, String>(), true);
				    	}
			    		System.out.println("AlertCheck: " + expertQuery.getName() + " returns: " + queryResult);
						return new ResponseEntity<Result>(queryResult, HttpStatus.OK);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
			
//			String[] queryParts = queryString.split("return");
//			if (alert.getType().equalsIgnoreCase("String"))
//			{
//				 queryString = "MATCH (n:"+alert.getNodeName()+") where n."+alert.getAttributeName()+alert.getFilterType()+
//						 "'"+(String)alert.getValue()+"' return n as "+alert.getNodeName();
////				 queryString = "MATCH (n:"+alert.getNodeName()+") where n."+alert.getAttributeName()+alert.getFilterType()+
////						 "'"+(String)alert.getValue()+"' return n as "+alert.getNodeName();
//			}
//			else
//			{
//				 queryString = "MATCH (n:"+alert.getNodeName()+") where n."+alert.getAttributeName()+alert.getFilterType()+
//						 alert.getValue()+" return n as "+alert.getNodeName();
//			}
		}
		return null;
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
	public ResponseEntity<Result> postAlert(@RequestBody Alert alert) throws Exception {
		testTypes(alert);
		alertRepository.save(alert);
		
		return new ResponseEntity<Result>( HttpStatus.OK);
	}
	
	@CrossOrigin 
	@RequestMapping(value="/alerts/name-list",  method=RequestMethod.GET)
	public ResponseEntity<List<String>> getAlertNames(){
		List<String> names = alertRepository.getNames();
		return new ResponseEntity<List<String>>(names, HttpStatus.OK);
    }
	
	@CrossOrigin 
	@RequestMapping(value="/alerts/{alertId}",  method=RequestMethod.PUT)
	@Transactional
	public ResponseEntity<Alert> updateAlert( @PathVariable String alertId, @RequestBody Alert al) throws Exception{
		testTypes(al);
		Alert alert=null;		
		Long id = new Long(-1);
		
		try
		{
			id = Long.parseLong(alertId);
		}
		catch(NumberFormatException P_ex)
		{
			/**
			 * Wenn der mitübergebene Wert nicht auf Long umgewandelt werden kann,
			 * ist der mitübergene Wert offensichtlich keine Zahl, muss also der
			 * eindeutige Name sein. 
			 */
		}
		
		if (id >=0){
			 alert= alertRepository.findOne(id);
		} else{
			 alert= alertRepository.findByName(alertId);
		}
		
		alert.setName(al.getName());
//		alert.setNodeName(al.getNodeName());
//		alert.setAttributeName(al.getAttributeName());
		alert.setQuery(al.getQuery());
		alert.setType(al.getType());
		alert.setFilterType(al.getFilterType());
		alert.setValue(al.getValue());
		alert.setEmail(al.getEmail());
		alertRepository.save(alert);		
		return new ResponseEntity<Alert>(alert, HttpStatus.OK);
    }
	

	
	@CrossOrigin 
	@RequestMapping(value="/alerts/{alertId}",  method=RequestMethod.DELETE)
	@Transactional
	public ResponseEntity<Result> deleteAlert( @PathVariable String alertId){
		Alert alert=null;
		Long id = new Long(-1);
		
		try
		{
			id = Long.parseLong(alertId);
		}
		catch(NumberFormatException P_ex)
		{
			/**
			 * Wenn der mitübergebene Wert nicht auf Long umgewandelt werden kann,
			 * ist der mitübergene Wert offensichtlich keine Zahl, muss also der
			 * eindeutige Name sein. 
			 */
		}
		
		
		if (id >=0){
			 alert= alertRepository.findOne(id);
		} else{
			 alert= alertRepository.findByName(alertId);
		}
		alertRepository.delete(alert);		
		return new ResponseEntity<Result>(HttpStatus.OK);
    }
	
	@Scheduled(fixedRate = 5000)
	public void executeAllAlerts()
	{
		StringBuffer F_mailMessageBuffer = new StringBuffer("");
		ResponseEntity<List<Alert>> F_alerts = getAlerts();
		if(F_alerts != null)
		{
			for(Alert F_alert : F_alerts.getBody())
			{
				ResponseEntity<Result> F_result = executeAlert(F_alert.getId().toString());
				if(F_result != null)
				{
					Iterator<Map<String, Object>> F_res = F_result.getBody().queryResults().iterator();
					while(F_res.hasNext())
					{
						Map<String, Object> F_map = F_res.next();
						for(Object F_obj : F_map.values())
						{
							/**
							 * TODO: Auf FileType überprüfen und gegebenfalls Mail senden
							 * Eventuelle Möglichkeiten um Mails zu senden:
							 * - http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mail.html
							 * - https://java.net/projects/javamail/pages/Home
							 */
						}
					}
				}
				System.out.println("TEST");
			}
		}
		System.out.println("TEST");
	}
	
	
	
	 private void testTypes(Alert a) throws Exception{
	    	switch(a.getType()){
 		case "int":
 		case "integer":
 		case "Integer":
 			try{
 				
			/**
			 * Castet man einen Integer mittels (String) auf einen Stringwert kommt es zu einen Fehler:
			 * java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
			 * at msquerybuilderbackend.rest.AlertService.executeAlert(AlertService.java:95) ~[classes/:na]
			 * --> Stattdessen wird toString() verwendet.
			 */
 			int i = Integer.parseInt(a.getValue().toString());
 			a.setValue(i);
 			}catch (Exception e){
 				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
 				
 			}
 		break;
 		
 		case "double":
 		case "Double":
 			try{
	    			double i = Double.parseDouble((String)a.getValue());
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
 			break;
 		
 		case "char":
 		case "Char":
 			try{
	    			char i=(char) a.getValue();
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
 			break;
 			
 		case "boolean":
 		case "Boolean":
 			try{
	    			boolean i=(boolean) a.getValue();
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
 			break;
 			
 		case "float":
 		case "Float":
 			try{
	    			float i = Float.parseFloat((String)a.getValue());
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
 			break;
 		
 		case "long":
 		case "Long":
 			try{
	    			long i = Long.parseLong((String)a.getValue());
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
 			break;
 			
 		case "short":
 		case "Short":
 			try{
	    			short i = Short.parseShort((String)a.getValue());
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
 			break;
 			
 		case "byte":
 		case "Byte":
 			try{
	    			byte i = Byte.parseByte((String)a.getValue());
	    			a.setValue(i);
	    			}catch (Exception e){
	     				throw new InvalidTypeException("alert with name "+a.getName()+" is not from Type "+a.getType());
	    			}
 			break;
 			
 		default: 
 			
 			break;
 		}
	    }	
}
