package msquerybuilderbackend.business;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import msquerybuilderbackend.entity.Alert;
import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.repository.AlertRepository;
import msquerybuilderbackend.repository.ExpertQueryRepository;

@Component
public class AlertBusiness {

	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	@Autowired
	AlertRepository alertRepository;
	@Autowired
	ExpertQueryRepository expertQueryRepository;
	
	
	public Long createAlert(Alert alert) throws Exception{
	Alert alreadyUsedName= alertRepository.findByName(alert.getName());
		if (alreadyUsedName != null){
			
			return 0L;
		}else{
			AttributeTypes.testTypes(alert);
			alertRepository.save(alert);
			
			Alert newAlert = alertRepository.findByName(alert.getName());
			return newAlert.getId();
		}
	}
	
	
	public List<String> getNameList(){
		 List<String> names = alertRepository.getNames();
		 return names;
	}
	
	public Alert updateAlert(String alertId, Alert al) throws Exception{
		AttributeTypes.testTypes(al);
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
		return alert;
	}
	
	public List<Alert> getAllAlerts(){
		List<Alert> alerts= alertRepository.getAllAlerts();
		return alerts;
	}
	
	public Alert getAlert(String alertId){
		Alert alert=null;
		if (Long.parseLong(alertId) >=0){
			 alert= alertRepository.findOne(Long.parseLong(alertId));
		} else{
			 alert= alertRepository.findByName(alertId);
		}
		
		return alert;
	}
	
	public ResponseEntity<Result> executeAlert(String alertId){
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
	
	
	public void deleteAlert(String alertId){
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
	}
	
	public void executeAllAlerts(){
		StringBuffer F_mailMessageBuffer = new StringBuffer("");
		List<Alert> F_alerts = getAllAlerts();
		if(F_alerts != null)
		{
			for(Alert F_alert : F_alerts)
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
}
