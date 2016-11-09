package application.expertQueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import application.entity.ExpertQuery;
import application.entity.Parameter;
import application.exception.InvalidParameterTypeException;
import application.repository.ExpertQueryRepository;
import application.repository.ParameterRepository;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;

import java.lang.reflect.Array;
import java.lang.reflect.Type;



@RestController
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
		    @RequestMapping(value="/expertModus")
		    public Result expertModus(@RequestParam(value="query", defaultValue="Match (n) return distinct labels(n)") String query) {

			Result result = neo4jOperations.query(query, new HashMap<String, String>());
			
			return result;
		    }
			

		/**	
		 * Martin: Hab die Methode einstweilen auskommentiert und wieder den alten Stand hergestellt, 
		 * bis wir frontendseitig Parameter können.
		 */
			
//	  //  @RequestMapping(value="/expertModus", method=RequestMethod.GET)
//		@CrossOrigin 
//		//CrossOrigin request allow to call a different server from
//		//a certain frontend hosted on a certain address.
//		//This means that the adress of the server and 
//		//the adress of client must not be the same.
//		//Pleas add @CrossOrigin to every request.
//	    @RequestMapping(value="/expertModus")
////	    public String expertModus(@RequestParam(value="query", defaultValue="Match (n) return distinct labels(n)") String query, @RequestParam(value="parameter") List params) {	 
//	    public ResponseEntity<Result> expertModus(@RequestBody ExpertQuery expertQuery) throws Exception {
//	    	Map<String,Object> paramsMap = new HashMap<String,Object>();
//	    	
//	    	
//	    	for (Parameter p:expertQuery.getParameter()){
//	    	
//					testTypes(p);
//				
//	    		parameterRepository.save(p);  		
//	    		paramsMap.put(p.getKey(), p.getValue());
//	    	}
//	    	expertQueryRepository.save(expertQuery);
//	    	
//		Result result = neo4jOperations.query(expertQuery.getQuery(), paramsMap,true);
//	
//		return new ResponseEntity<Result>(result, HttpStatus.OK);
//	    }
	    
	    
	    private void testTypes(Parameter p) throws Exception{
	    	switch(p.getType()){
    		case "int":
    			try{
    			int i=(int) p.getValue();
    			}catch (Exception e){
    				throw new InvalidParameterTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
    				
    			}
    		break;
    		
    		case "double":
    			try{
	    			double i=(double) p.getValue();
	    			}catch (Exception e){
	      				throw new InvalidParameterTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    		
    		case "char":
    			try{
	    			char i=(char) p.getValue();
	    			}catch (Exception e){
	      				throw new InvalidParameterTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		case "boolean":
    			try{
	    			boolean i=(boolean) p.getValue();
	    			}catch (Exception e){
	      				throw new InvalidParameterTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		case "float":
    			try{
	    			float i=(float) p.getValue();
	    			}catch (Exception e){
	      				throw new InvalidParameterTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    		
    		case "long":
    			try{
	    			long i=(long) p.getValue();
	    			}catch (Exception e){
	      				throw new InvalidParameterTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		case "short":
    			try{
	    			short i=(short) p.getValue();
	    			}catch (Exception e){
	      				throw new InvalidParameterTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		case "byte":
    			try{
	    			byte i=(byte) p.getValue();
	    			}catch (Exception e){
	      				throw new InvalidParameterTypeException("parameter with key "+p.getKey()+" is not from Type "+p.getType());
	    			}
    			break;
    			
    		default: 
    			
    			break;
    		}
	    }
	    
	
}
