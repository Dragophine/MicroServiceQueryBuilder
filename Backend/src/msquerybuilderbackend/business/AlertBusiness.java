package msquerybuilderbackend.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.*;
import javax.activation.*;
import javax.mail.internet.*;
import javax.mail.util.*;

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
/**
 * Class for all activities with neo4j database regarding the entity Alert
 * @author Martin
 *
 */

@Component
public class AlertBusiness {
	
	public static final String EQUALS = "EQUALS";
	public static final String GREATER = "GREATER";
	public static final String SMALLER = "SMALLER";	
	public static final String GREATER_EQUAL = "GREATER EQUAL";
	public static final String SMALLER_EQUAL = "SMALLER EQUAL";
	public static final String EXISTS = "EXISTS";
	public static final String NOT_EXISTS = "NOT EXISTS";
	public static final String COUNT = "COUNT";
	public static final String DATE = "DATE";
	public static final String DOUBLE = "DOUBLE";

	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	@Autowired
	AlertRepository alertRepository;
	@Autowired
	ExpertQueryRepository expertQueryRepository;
	
	/**
	 * checks whether the name of the given alert already exits and returns 0L if true,
	 * otherwise the types of the alerts are tested and the alert saved
	 * @param alert is the alert to create in the neo4j database
	 * @return the neo4j id of the new created alert
	 * @throws Exception
	 */
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
	
	
	/**
	 * method which generates a List of all names of alerts
	 * @return a list of all names of alerts as Strings
	 */
	public List<String> getNameList(){
		 List<String> names = alertRepository.getNames();
		 return names;
	}
	
	/**
	 * method which updates a given alert in the neo4j database
	 * @param alertId is the id of the alert to be updated; can be the unique name or the neo4j ID
	 * @param al is the alert object with the new content
	 * @return the updated alert
	 * @throws Exception when the types given in the alert are not true (i.e. given type integer but the value is a string)
	 */
	public Alert updateAlert(String alertId, Alert al) throws Exception {
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
		alert.setDates(al.getDates());
		alertRepository.save(alert);	
		return alert;
	}
	
	
	/**
	 * method which queries all alerts from the neo4j database
	 * @return a list with all alerts which are in the neo4j database
	 */
	public List<Alert> getAllAlerts(){
		List<Alert> alerts= alertRepository.getAllAlerts();
		return alerts;
	}
	
	/**
	 * method which queries a certain alert from the neo4j database
	 * @param alertId is the id from the alert to look for; can be the neo4j ID or the unique name
	 * @return an alert object with content of the in the neo4j database found alert
	 */
	public Alert getAlert(String alertId){
		Alert alert=null;
		if (Long.parseLong(alertId) >=0){
			 alert= alertRepository.findOne(Long.parseLong(alertId));
		} else{
			 alert= alertRepository.findByName(alertId);
		}
		
		return alert;
	}
	
	/**
	 * method which queries a certain alert and executes it in the neo4j database
	 * @param alertId is the id of the alert to be executed; can be the unique name or the neo4j ID
	 * @return a neo4j result of the alert's executed query
	 */
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
	
	
	/**
	 * method which deletes a certain alert in the neo4j database
	 * @param alertId is the id of the alert to be deleted; can be the neo4j ID or the unique name
	 */
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
	
	/**
	 * method which executesAllAlerts; is needed for the periodic execution of alerts
	 */
	public void executeAllAlerts(){
		List<Alert> F_alerts = getAllAlerts();
		if(F_alerts != null)
		{
			for(Alert F_alert : F_alerts)
			{
				StringBuffer F_mailMessageBuffer = new StringBuffer("");
				ResponseEntity<Result> F_result = executeAlert(F_alert.getId().toString());
				if(F_result != null)
				{
					Iterable<Map<String, Object>> F_resultArrayList = F_result.getBody().queryResults();
					Iterator<Map<String, Object>> F_res = F_resultArrayList.iterator();
					while(F_res.hasNext())
					{
						Map<String, Object> F_map = F_res.next();
						// Die Variable stellt sicher, dass der COUNT nur einmal per Alert geprüft wird.
						boolean Fb_countWasCheckedForAlert = false; 
						for(Object F_obj : F_map.values())
						{
							String Fs_alertFilterType = F_alert.getFilterType();
							if(Fs_alertFilterType.equalsIgnoreCase(EQUALS))
							{
								if(F_obj.equals(F_alert.getValue()))
								{
									F_mailMessageBuffer.append(F_alert.getName() + 
											" zeichnete einen Alert auf! Der returnierte Wert der "
											+ "Query ist gleich dem beim Alert hinterlegten Wert! " +
											F_alert.getQuery() + " returnierte den gleichen Wert. "
													+ "Aktueller Wert: " + F_obj + ".\n");													
								}
							}
							else if(F_obj != null && Fs_alertFilterType.equalsIgnoreCase(GREATER))
							{
								boolean Fb_addMessageToBuffer = shouldAddToMessageBuffer(F_obj.toString(), F_alert);									
								if(Fb_addMessageToBuffer)
								{
									F_mailMessageBuffer.append(F_alert.getName() + 
											" zeichnete einen Alert auf! Die Query " +
											F_alert.getQuery() + " returnierte einen Wert "
													+ "größer " + F_alert.getValue().toString() + 
													". Aktueller " + "Wert: " + F_obj.toString() + ".\n");
								}
							}
							else if(Fs_alertFilterType.equalsIgnoreCase(SMALLER) && F_obj != null)
							{									
								boolean Fb_addMessageToBuffer = shouldAddToMessageBuffer(F_obj.toString(), F_alert);
								if(Fb_addMessageToBuffer)
								{
									F_mailMessageBuffer.append(F_alert.getName() + 
											" zeichnete einen Alert auf! Die Query " +
											F_alert.getQuery() + " returnierte einen Wert "
													+ "kleiner " + F_alert.getValue() + ". Aktueller "
															+ "Wert: " + F_obj.toString() + ".\n");
								}
							}
							else if(Fs_alertFilterType.equalsIgnoreCase(GREATER_EQUAL) && F_obj != null)
							{
								boolean Fb_addMessageToBuffer = shouldAddToMessageBuffer(F_obj.toString(), F_alert);
								if(Fb_addMessageToBuffer)
								{
									F_mailMessageBuffer.append(F_alert.getName() + 
											" zeichnete einen Alert auf! Die Query " +
											F_alert.getQuery() + " returnierte einen Wert "
													+ "größer gleich " + F_alert.getValue() + ". Aktueller "
															+ "Wert: " + F_obj.toString() + ".\n");
								}
							}
							else if(Fs_alertFilterType.equalsIgnoreCase(SMALLER_EQUAL) && F_obj != null)
							{
								boolean Fb_addMessageToBuffer = shouldAddToMessageBuffer(F_obj.toString(), F_alert);
								if(Fb_addMessageToBuffer)
								{
									F_mailMessageBuffer.append(F_alert.getName() + 
											" zeichnete einen Alert auf! Die Query " +
											F_alert.getQuery() + " returnierte einen Wert "
													+ "kleiner gleich " + F_alert.getValue() + ". Aktueller "
															+ "Wert: " + F_obj.toString() + ".\n");
								}
							}
							else if(Fs_alertFilterType.equalsIgnoreCase(EXISTS))
							{
								if(F_obj != null)
								{
									F_mailMessageBuffer.append(F_alert.getName() + 
											" zeichnete einen Alert auf! Die Query " +
											F_alert.getQuery() + " returniert einen "
													+ "existierenden Wert! Returnierter "
													+ "Wert: " + F_obj + "\n");
								}
							}
							else if(Fs_alertFilterType.equalsIgnoreCase(NOT_EXISTS))	
							{
								if(F_obj == null)
								{
									F_mailMessageBuffer.append(F_alert.getName() + 
											" zeichnete einen Alert auf! Die Query " +
											F_alert.getQuery() + " returnierte keinen "
													+ "existierenden Wert!\n");
								}
							}
							else if(Fs_alertFilterType.equalsIgnoreCase(COUNT) && !Fb_countWasCheckedForAlert)	
							{
								try
								{
									Integer Fi_actualValue = Integer.valueOf(F_obj.toString());
									
									if(Fi_actualValue == F_map.values().size())
									{
										F_mailMessageBuffer.append(F_alert.getName() + 
												" zeichnete einen Alert auf! Die Query " + F_alert.getQuery() +
												"returnierte " + Fi_actualValue + "Ergebnisse.\n");	
									}
									Fb_countWasCheckedForAlert = true;
								}
								catch(NumberFormatException P_ex)
								{
									/**
									 * Ein Vergleich kann nur gemacht werden, wenn der hinterlegte 
									 * Wert beim Alert in einen Integerwert umgewandelt werden kann.
									 * Wenn das nicht gegeben ist wird die Überprüfung übersprungen.
									 */
								}
							}
							else
							{
								// noop - Unbekannter Filtertyp
							}
						}
					}
					
					/**
					 * Wenn der Wert nicht existiert wird ein leeres Array returniert,
					 * darum muss hier die Prüfung gemacht werden, weil hasNext() in
					 * der while-Schleife false liefert.
					 */
					if(F_alert.getFilterType().equalsIgnoreCase(NOT_EXISTS))	
					{
						if(F_resultArrayList instanceof ArrayList &&
								((ArrayList)F_resultArrayList).size() == 0)
						{
							
							F_mailMessageBuffer.append(F_alert.getName() + 
									" zeichnete einen Alert auf! Die Query " +
									F_alert.getQuery() + " returnierte keinen "
											+ "existierenden Wert!\n");
						}
					}
				}
				
				/**
				 * Prüfen ob ein Mail gesendet werden muss
				 */
				if(F_mailMessageBuffer.length() > 0)
				{
					sendEmail(F_mailMessageBuffer.toString(), F_alert.getEmail(), F_alert.getName());
					F_alert.addDate(Calendar.getInstance().getTime());
					alertRepository.save(F_alert);
				}
			}
		}
	}
	
	
	/**
	 * method which checks if an alert constraint was reached and therefore if the message should be added to the buffer
	 * @return true or false 
	 * 
	 */
	public boolean shouldAddToMessageBuffer(String Ps_actualValue, Alert P_alert)
	{
		boolean Fb_addMessageToBuffer = false;
		try
		{
			if(P_alert.getType().equalsIgnoreCase(DOUBLE))
			{
				Double Fd_alertValue = Double.valueOf(P_alert.getValue().toString());
				Double Fd_actualValue = Double.valueOf(Ps_actualValue);
				Fb_addMessageToBuffer = checkCompareToValue(Fd_actualValue.compareTo(Fd_alertValue),
						P_alert.getFilterType());
			}
			else if(P_alert.getType().equalsIgnoreCase(DATE))
			{
				String[] Fs_alertValue = P_alert.getValue().toString().split(".");
				String[] Fs_actualValue = Ps_actualValue.split(".");
				java.util.Date F_alertValue = new Date(Integer.valueOf(Fs_alertValue[0]),
						Integer.valueOf(Fs_alertValue[1]),
						Integer.valueOf(Fs_alertValue[2]));
				java.util.Date F_actualValue = new Date(Integer.valueOf(Fs_actualValue[0]),
						Integer.valueOf(Fs_actualValue[1]),
						Integer.valueOf(Fs_actualValue[2]));
				Fb_addMessageToBuffer = checkCompareToValue(F_actualValue.compareTo(F_alertValue),
						P_alert.getFilterType());
			}
			else
			{
				Integer Fi_alertValue = Integer.valueOf(P_alert.getValue().toString());
				Integer Fi_actualValue = Integer.valueOf(Ps_actualValue);
				Fb_addMessageToBuffer = checkCompareToValue(Fi_actualValue.compareTo(Fi_alertValue),
						P_alert.getFilterType());
			}
		}
		catch(NumberFormatException P_ex)
		{
			/**
			 * Ein Vergleich kann nur gemacht werden, wenn der aktuelle
			 * Wert und der hinterlegte Wert beim Alert in den angegebenen Wert
			 * beim Alert umgewandelt werden können. Wenn das nicht gegeben ist
			 * wird die Überprüfung übersprungen.
			 */
		}
		return Fb_addMessageToBuffer;
	}
	
	
	
	public boolean checkCompareToValue(int Pi_compareToValue, String Ps_filterType)
	{
		boolean Fb_addToMessageBuffer = false;
		if(Ps_filterType.equalsIgnoreCase(GREATER))
		{
			Fb_addToMessageBuffer = Pi_compareToValue > 0 ? true : false;
		}
		else if(Ps_filterType.equalsIgnoreCase(SMALLER))
		{
			Fb_addToMessageBuffer = Pi_compareToValue < 0 ? true : false;
		}
		else if(Ps_filterType.equalsIgnoreCase(GREATER_EQUAL))
		{
			Fb_addToMessageBuffer = Pi_compareToValue >= 0 ? true : false;
		}
		else if(Ps_filterType.equalsIgnoreCase(SMALLER_EQUAL))
		{
			Fb_addToMessageBuffer = Pi_compareToValue <= 0 ? true : false;
		}
		return Fb_addToMessageBuffer;
	}
	
	public void sendEmail(String Ps_text, String Ps_empfaenger, String Ps_alertName)
	{
		String Fs_gmailUser = "QuerybuilderSE";
		String Fs_gmailPW = "QueryBuilderSEPR";
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(Fs_gmailUser, Fs_gmailPW);
				}
			});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(Fs_gmailUser));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(Ps_empfaenger));
			message.setSubject("[Querybuilder] Alertmeldung vom Alert " + Ps_alertName);
			String Fs_emailText = "Lieber QueryBuilderUser,\nFolgende Werte lösten den Alert "
					+ "aus:\n\n" + Ps_text;
			message.setText(Fs_emailText);

			Transport.send(message);

			System.out.println("Der Alert " + Ps_alertName + " wurde ausgelöst. " + Ps_empfaenger +
					" wurde per Email benachrichigt! Emailtext: \n" + Fs_emailText);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
