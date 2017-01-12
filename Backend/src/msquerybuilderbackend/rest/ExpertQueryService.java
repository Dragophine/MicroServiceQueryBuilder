package msquerybuilderbackend.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import msquerybuilderbackend.entity.QueryBuilderJsonStringObject;
import msquerybuilderbackend.exception.InvalidTypeException;
import msquerybuilderbackend.repository.ExpertQueryRepository;
import msquerybuilderbackend.repository.ParameterRepository;

@RestController
public class ExpertQueryService {

	
	 @Autowired
		Neo4jOperations neo4jOperations;
		Neo4jTemplate temp;
		@Autowired
		ExpertQueryRepository expertQueryRepository;
		@Autowired
		ParameterRepository parameterRepository;
		
	
			@CrossOrigin 
			//CrossOrigin request allow to call a different server from
			//a certain frontend hosted on a certain address.
			//This means that the adress of the server and 
			//the adress of client must not be the same.
			//Pleas add @CrossOrigin to every request.
			
		    @RequestMapping(value="/expertqueries/execute",  method=RequestMethod.POST)
		    public ResponseEntity<Result> preExecuteQuery(@RequestBody ExpertQuery expertQuery) throws Exception {
				
				
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
			
			@Transactional
			@CrossOrigin 
		    @RequestMapping(value="/expertqueries",  method=RequestMethod.POST)	 
		    public ResponseEntity<Long> saveQuery(@RequestBody ExpertQuery expertQuery) throws Exception{
		    
		    ExpertQuery alreadyUsedName= expertQueryRepository.findByName(expertQuery.getName());
			if (alreadyUsedName != null){
				
				return new ResponseEntity<Long>(0L,HttpStatus.CONFLICT);	
			}else{
			
//		    	for (Parameter p : expertQuery.getParameter())
//		    	{
//					testTypes(p);				
//		    		parameterRepository.save(p);
//		    	}
		    	expertQueryRepository.save(expertQuery);
		    	ExpertQuery newExpertQuery = expertQueryRepository.findByName(expertQuery.getName());
		
		    	return new ResponseEntity<Long>(newExpertQuery.getId(),HttpStatus.OK);
			}
		    }
			
			
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/expertqueries/{queryId}",  method=RequestMethod.DELETE)	 
		    public ResponseEntity<Result> deleteQuery(@PathVariable String queryId) throws Exception	{
				ExpertQuery expertQuery=null;
				Long id = new Long(-1);
				
				try
				{
					id = Long.parseLong(queryId);
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
					 expertQuery= expertQueryRepository.findOne(id);
				} else{
					 expertQuery= expertQueryRepository.findByName(queryId);
				}
		    	for (Parameter p : expertQuery.getParameter())
		    	{
			    	parameterRepository.delete(p.getId());
		    	}
		    	
		    	expertQueryRepository.delete(expertQuery.getId());
		
			return new ResponseEntity<Result>(HttpStatus.OK);
		    }
			
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/expertqueries/{queryId}",  method=RequestMethod.PUT)	 
		    public ResponseEntity<Result> updateQuery(@PathVariable String queryId, @RequestBody ExpertQuery updatedQuery) throws Exception	{
				ExpertQuery expertQuery=null;
				Long id = new Long(-1);
				
				try
				{
					id = Long.parseLong(queryId);
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
					 expertQuery= expertQueryRepository.findOne(Long.parseLong(queryId));
				} else{
					 expertQuery= expertQueryRepository.findByName(queryId);
				}
		    	for (Parameter p : expertQuery.getParameter())
		    	{			    	
			    	parameterRepository.delete(p.getId());
		    	}
		    	
		    	for (Parameter p : updatedQuery.getParameter())
		    	{			    	
			    	parameterRepository.save(p);
		    	}
		    	
		    	
		    	expertQuery.setDescription(updatedQuery.getDescription());
		    	expertQuery.setName(updatedQuery.getName());
		    	expertQuery.setQuery(updatedQuery.getQuery());
		    	expertQuery.setCategory(updatedQuery.getCategory());
		    	expertQuery.setParameter(updatedQuery.getParameter());
		    	
		    	expertQueryRepository.save(expertQuery);
		
			return new ResponseEntity<Result>(HttpStatus.OK);
		    }
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/expertqueriesold",  method=RequestMethod.GET)
		    public ResponseEntity<Result> getQueriesOld() throws Exception	{
//		    public ResponseEntity<List<ExpertQuery>> getQueries() throws Exception	{
//				List<ExpertQuery> expertQueries=expertQueryRepository.getAllExpertQueries();
//		
//			return new ResponseEntity<List<ExpertQuery>>(expertQueries,HttpStatus.OK);
				
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
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/expertqueries",  method=RequestMethod.GET)
		    public ResponseEntity<Iterable<ExpertQuery>> getQueries() throws Exception	{
				Iterable<ExpertQuery> expertqueries= expertQueryRepository.findAll();
				return new ResponseEntity<Iterable<ExpertQuery>>(expertqueries, HttpStatus.OK);
//				String queryNodesQuery = "MATCH (expertquery:ExpertQuery) return expertquery";			
//				Result resultQuery = neo4jOperations.query(queryNodesQuery, new HashMap<String, String>());

//	
//			return new ResponseEntity<Result>(resultQuery, HttpStatus.OK);
		    }
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/expertqueries/{queryId}",  method=RequestMethod.GET)	 
		    public ResponseEntity<ExpertQuery> getQuery(@PathVariable String queryId) throws Exception	{
				ExpertQuery expertQuery=null;
				Long id = new Long(-1);
				
				try
				{
					id = Long.parseLong(queryId);
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
					 expertQuery= expertQueryRepository.findOne(Long.parseLong(queryId));
				} else{
					 expertQuery= expertQueryRepository.findByName(queryId);
				}

			return new ResponseEntity<ExpertQuery>(expertQuery,HttpStatus.OK);
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
