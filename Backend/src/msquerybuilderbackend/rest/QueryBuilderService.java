package msquerybuilderbackend.rest;
  
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.neo4j.ogm.annotation.Relationship;
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

import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.Node;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.entity.QueryBuilder;
import msquerybuilderbackend.exception.InvalidTypeException;
import msquerybuilderbackend.repository.ExpertQueryRepository;
import msquerybuilderbackend.repository.ParameterRepository;
import msquerybuilderbackend.repository.QueryBuilderRepository;

@RestController
public class QueryBuilderService {

	
	 @Autowired
		Neo4jOperations neo4jOperations;
		Neo4jTemplate temp;
		@Autowired
		QueryBuilderRepository queryBuilderRepository;
		@Autowired
		ParameterRepository parameterRepository;
		@Autowired
		ExpertQueryRepository expertQueryRepository;
		
	
			@CrossOrigin 
			//CrossOrigin request allow to call a different server from
			//a certain frontend hosted on a certain address.
			//This means that the adress of the server and 
			//the adress of client must not be the same.
			//Pleas add @CrossOrigin to every request.
			
		    @RequestMapping(value="/queryBuilder/execute",  method=RequestMethod.POST)
		    public ResponseEntity<Result> preExecuteQuery(@RequestBody QueryBuilder queryBuilder) throws Exception {
		    	Map<String,Object> paramsMap = new HashMap<String,Object>();
		    	Result result=null;
		    	
//		    		result = neo4jOperations.query(queryBuilder.getQuery(),new HashMap<String, String>(), true);
		    	
		
			return new ResponseEntity<Result>(result, HttpStatus.OK);
		    }	
			
			@Transactional
			@CrossOrigin 
		    @RequestMapping(value="/queryBuilder",  method=RequestMethod.POST)	 
		    public ResponseEntity<Result> saveQuery(@RequestBody QueryBuilder queryBuilder) throws Exception{
			
			QueryBuilder alreadyUsedName= queryBuilderRepository.findByName(queryBuilder.getName());
			if (alreadyUsedName != null){
				
				return new ResponseEntity<Result>(HttpStatus.CONFLICT);	
			}else{
				
/**
 * Interpretation des Querybuilders wie bei execute ausständig
 */
		//		queryBuilder.addExpertQuery(expertquery);
				queryBuilder.setExpertQuery(null);
		    	queryBuilderRepository.save(queryBuilder);
		
			return new ResponseEntity<Result>(HttpStatus.OK);
			}
		    }
			
			
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}",  method=RequestMethod.DELETE)	 
		    public ResponseEntity<Result> deleteQuery(@PathVariable String queryId) throws Exception	{
				QueryBuilder queryBuilder=null;
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
					 queryBuilder= queryBuilderRepository.findOne(id);
				} else{
					 queryBuilder= queryBuilderRepository.findByName(queryId);
				}
				Set<ExpertQuery> expertqueries = queryBuilder.getExpertQuery();
				Iterator iter = expertqueries.iterator();
		
				while (iter.hasNext()){
					ExpertQuery eq= (ExpertQuery) iter.next();
					for (Parameter p : eq.getParameter())
			    	{
				    	parameterRepository.delete(p.getId());
			    	}
					expertQueryRepository.delete(eq.getId());
				}
				

		    	
		    	
		    	queryBuilderRepository.delete(queryBuilder.getId());
		
			return new ResponseEntity<Result>(HttpStatus.OK);
		    }
			
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}",  method=RequestMethod.PUT)	 
		    public ResponseEntity<Result> updateQuery(@PathVariable String queryId, @RequestBody QueryBuilder updatedQuery) throws Exception	{
				QueryBuilder queryBuilder=null;
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
					 queryBuilder= queryBuilderRepository.findOne(Long.parseLong(queryId));
				} else{
					 queryBuilder= queryBuilderRepository.findByName(queryId);
				}
				
				/**
				 * Interpretation der Query in ExpertQuery ausständig wie bei execute
				 */
				 
				if (queryBuilder.getExpertQuery()!=null){
			    	for (ExpertQuery q : queryBuilder.getExpertQuery())
			    	{			    	
				    	expertQueryRepository.delete(q.getId());
			    	}
				}
		    	
		    	
		    	/**
		    	 * eventuell nicht notwendig, falls es durch die Beschreibung des RElationships in der Entity funktioniert
		    	 */
//		    	for (ExpertQuery q : updatedQuery.getExpertQuery())
//		    	{			    	
//			    	expertQueryRepository.save(q);
//		    	}
		    	
		    	Set<ExpertQuery> updatedQuerySet = new HashSet<ExpertQuery>();
//		    	updatedQuerySet.add(expertQuery);
		    	queryBuilder.setDescription(updatedQuery.getDescription());
		    	queryBuilder.setName(updatedQuery.getName());
		    	queryBuilder.setExpertQuery(updatedQuerySet);
		    	queryBuilder.setCategory(updatedQuery.getCategory());
		    	queryBuilder.setLimitcount(updatedQuery.getLimitCount());
		    	queryBuilder.setNode(updatedQuery.getNode());
		    	
		    	
		    	
		    	
		    	
		    	queryBuilderRepository.save(queryBuilder);
		
			return new ResponseEntity<Result>(HttpStatus.OK);
		    }
			
			
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder",  method=RequestMethod.GET)
		    public ResponseEntity<Iterable<QueryBuilder>> getQueries() throws Exception	{
				Iterable<QueryBuilder> queryBuilder= queryBuilderRepository.findAll();
				return new ResponseEntity<Iterable<QueryBuilder>>(queryBuilder, HttpStatus.OK);

		    }
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}",  method=RequestMethod.GET)	 
		    public ResponseEntity<QueryBuilder> getQuery(@PathVariable String queryId) throws Exception	{
				QueryBuilder queryBuilder=null;
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
					 queryBuilder= queryBuilderRepository.findOne(Long.parseLong(queryId));
				} else{
					 queryBuilder= queryBuilderRepository.findByName(queryId);
				}

			return new ResponseEntity<QueryBuilder>(queryBuilder,HttpStatus.OK);
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
