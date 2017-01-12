package application.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.exception.InvalidTypeException;
import msquerybuilderbackend.repository.ExpertQueryRepository;
import msquerybuilderbackend.repository.ParameterRepository;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;

import java.lang.reflect.Array;
import java.lang.reflect.Type;



@RestController
@Deprecated
public class ExpertModus {
	  @Autowired
		Neo4jOperations neo4jOperations;
		Neo4jTemplate temp;
		@Autowired
		ExpertQueryRepository expertQueryRepository;
		@Autowired
		ParameterRepository parameterRepository;
			
	  //  @RequestMapping(value="/expertModus", method=RequestMethod.GET)
		@CrossOrigin 
		//CrossOrigin request allow to call a different server from
		//a certain frontend hosted on a certain address.
		//This means that the adress of the server and 
		//the adress of client must not be the same.
		//Pleas add @CrossOrigin to every request.
	    @RequestMapping(value="/expertModus",  method=RequestMethod.POST)
//	    public String expertModus(@RequestParam(value="query", defaultValue="Match (n) return distinct labels(n)") String query, @RequestParam(value="parameter") List params) {	 
	    public ResponseEntity<Result> expertModus(@RequestBody ExpertQuery expertQuery) throws Exception {
	    	Map<String,Object> paramsMap = new HashMap<String,Object>();
	    	Result result=null;
	    	if (expertQuery.getParameter() !=null)
	    	{
	    		for (Parameter p:expertQuery.getParameter())
	    		{
	    			testTypes(p); 		
		    		paramsMap.put(p.getKey(), p.getValue());
		    		System.out.println(p.getKey() + " "+p.getValue());
		    	}
	    		result= neo4jOperations.query(expertQuery.getQuery(), paramsMap,true);	    		
	    	}
	    	else
	    	{
	    		result = neo4jOperations.query(expertQuery.getQuery(),new HashMap<String, String>(), true);
	    	}
	
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	    }
				
		@CrossOrigin 
	    @RequestMapping(value="/saveQuery",  method=RequestMethod.POST)	 
	    public ResponseEntity<Result> saveQuery(@RequestBody ExpertQuery expertQuery) throws Exception
		{
	    	for (Parameter p : expertQuery.getParameter())
	    	{
				testTypes(p);				
	    		parameterRepository.save(p);
	    	}
	    	expertQueryRepository.save(expertQuery);
	
		return new ResponseEntity<Result>(HttpStatus.OK);
	    }
		
		@CrossOrigin 
	    @RequestMapping(value="/deleteQuery",  method=RequestMethod.DELETE)	 
	    public ResponseEntity<Result> deleteQuery(@RequestBody ExpertQuery expertQuery) throws Exception
		{
	    	for (Parameter p : expertQuery.getParameter())
	    	{
		    	/**
		    	 *  Alle Parameter bei denen der Key, der Value und der Type übereinstimmen 
		    	 *  werden gelöscht.
		    	 */
//	    		parameterRepository.delete(p);
				String queryNodes = "MATCH (a) WHERE a.key IN [\"" + p.getKey() + 
						"\"] AND a.value IN [\"" + p.getValue() + "\"] AND "
								+ "a.type IN [\"" + p.getType() + "\"] DETACH DELETE a";			
				Result result = neo4jOperations.query(queryNodes, new HashMap<String, String>());
	    	}
	    	
	    	/**
	    	 *  Alle Queries bei denen der Name, die Description und die Category übereinstimmen 
	    	 *  werden gelöscht.
	    	 */
//	    	expertQueryRepository.delete(expertQuery);
			String queryNodes = "MATCH (a) WHERE a.name IN [\"" + expertQuery.getName() + 
					"\"] AND a.description IN [\"" + expertQuery.getDescription() + "\"] AND "
							+ "a.category IN [\"" + expertQuery.getCategory() + "\"] DETACH DELETE a";			
			Result result = neo4jOperations.query(queryNodes, new HashMap<String, String>());
	
		return new ResponseEntity<Result>(HttpStatus.OK);
	    }
		
		@CrossOrigin 
	    @RequestMapping(value="/loadQuery",  method=RequestMethod.GET)	 
	    public ResponseEntity<Result> loadQuery() throws Exception
		{
			/**
			 * Workaround: Wenn auch die Parameter aufgelöst und returniert werden in der Query,
			 * dann sind die dazugehörigen Parameter aller Queries die returniert werden auch sichtbar.
			 */
			String queryNodesQuery = "MATCH (n:ExpertQuery) OPTIONAL MATCH (m:ExpertQuery)-[e:HAS_PARAMETER]-(x) RETURN n,m,e,x";			
			Result resultQuery = neo4jOperations.query(queryNodesQuery, new HashMap<String, String>());

//			String queryNodesQuery = "MATCH (n:ExpertQuery) RETURN n";			
//			Result resultQuery = neo4jOperations.query(queryNodesQuery, new HashMap<String, String>());

		return new ResponseEntity<Result>(resultQuery, HttpStatus.OK);
	    }
		
	    
	    
	    private void testTypes(Parameter p) throws Exception{
	    	switch(p.getType()){
    		case "int":
    		case "integer":
    		case "Integer":
    			try{
    			int i = Integer.parseInt((String)p.getValue());
    			p.setValue(i);
    			}catch (Exception e){
    				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
    				
    			}
    		break;
    		
    		case "double":
    		case "Double":
    			try{
	    			double i = Double.parseDouble((String)p.getValue());
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    		
    		case "char":
    		case "Char":
    			try{
	    			char i=(char) p.getValue();
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		case "boolean":
    		case "Boolean":
    			try{
	    			boolean i=(boolean) p.getValue();
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		case "float":
    		case "Float":
    			try{
	    			float i = Float.parseFloat((String)p.getValue());
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    		
    		case "long":
    		case "Long":
    			try{
	    			long i = Long.parseLong((String)p.getValue());
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		case "short":
    		case "Short":
    			try{
	    			short i = Short.parseShort((String)p.getValue());
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		case "byte":
    		case "Byte":
    			try{
	    			byte i = Byte.parseByte((String)p.getValue());
	    			p.setValue(i);
	    			}catch (Exception e){
	      				throw new InvalidTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		default: 
    			
    			break;
    		}
	    }
	    
	
}
