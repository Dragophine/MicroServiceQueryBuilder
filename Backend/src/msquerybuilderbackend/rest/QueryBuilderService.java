package msquerybuilderbackend.rest;
  
import java.io.IOException;
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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import msquerybuilderbackend.business.QueryBuilderBusiness;
import msquerybuilderbackend.entity.Category;
import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.Node;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.entity.QueryBuilder;
import msquerybuilderbackend.entity.QueryBuilderJsonStringObject;
import msquerybuilderbackend.entity.QueryBuilderStringObject;
import msquerybuilderbackend.exception.InvalidTypeException;
import msquerybuilderbackend.repository.CategoryRepository;
import msquerybuilderbackend.repository.ExpertQueryRepository;
import msquerybuilderbackend.repository.ParameterRepository;
import msquerybuilderbackend.repository.QueryBuilderJsonStringRepository;

@RestController
public class QueryBuilderService {

	
	 @Autowired
		Neo4jOperations neo4jOperations;
		Neo4jTemplate temp;
		@Autowired
		QueryBuilderJsonStringRepository queryBuilderJsonStringObjectRepository;
		@Autowired
		ParameterRepository parameterRepository;
		@Autowired
		ExpertQueryRepository expertQueryRepository;
		
		@Autowired
		CategoryRepository categoryRepository;
		
		@Autowired
		QueryBuilderBusiness queryBuilderBusiness;
		
	
			@CrossOrigin 
			//CrossOrigin request allow to call a different server from
			//a certain frontend hosted on a certain address.
			//This means that the adress of the server and 
			//the adress of client must not be the same.
			//Pleas add @CrossOrigin to every request.
			
		    @RequestMapping(value="/queryBuilder/execute",  method=RequestMethod.POST)
		    public ResponseEntity<Result> preExecuteQuery(@RequestBody QueryBuilder queryBuilder) throws Exception {
				return new ResponseEntity<Result>(queryBuilderBusiness.executeQueryBuilderQuery(queryBuilder), HttpStatus.OK);
		    }	
			
			@Transactional
			@CrossOrigin 
		    @RequestMapping(value="/queryBuilder",  method=RequestMethod.POST)	 
		    public ResponseEntity<Long> saveQuery(@RequestBody QueryBuilder queryBuilder) throws Exception{
				Long newId=queryBuilderBusiness.createQueryBuilder(queryBuilder);
				if (newId==0L) return new ResponseEntity<Long>(0L,HttpStatus.CONFLICT);
				return new ResponseEntity<Long>(newId,HttpStatus.OK);		
		    }
			
			
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}",  method=RequestMethod.DELETE)	 
		    public ResponseEntity<Result> deleteQuery(@PathVariable String queryId) throws Exception	{
				QueryBuilderJsonStringObject qbjso=null;
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
					 qbjso= queryBuilderJsonStringObjectRepository.findOne(id);
				} else{
					 qbjso= queryBuilderJsonStringObjectRepository.findByName(queryId);
				}
//				
//				
//				Set<ExpertQuery> expertqueries = qbjso.getExpertQuery();
//				Iterator iter = expertqueries.iterator();
//		
//				while (iter.hasNext()){
//					ExpertQuery eq= (ExpertQuery) iter.next();
				if (qbjso.getExpertQuery()!=null){
					for (Parameter p : qbjso.getExpertQuery().getParameter())
			    	{
				    	parameterRepository.delete(p.getId());
			    	}
					expertQueryRepository.delete(qbjso.getExpertQuery().getId());
				}
				

		    	
		    	
		    	queryBuilderJsonStringObjectRepository.delete(qbjso.getId());
		
			return new ResponseEntity<Result>(HttpStatus.OK);
		    }
			
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}",  method=RequestMethod.PUT)	 
		    public ResponseEntity<Result> updateQuery(@PathVariable String queryId, @RequestBody QueryBuilder updatedQuery) throws Exception	{
				QueryBuilderJsonStringObject qbjso=null;
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
					 qbjso= queryBuilderJsonStringObjectRepository.findOne(Long.parseLong(queryId));
				} else{
					 qbjso= queryBuilderJsonStringObjectRepository.findByName(queryId);
				}
				
				/**
				 * Interpretation der Query in ExpertQuery ausständig wie bei execute
				 */
				 
				if (qbjso.getExpertQuery()!=null){
//			    	for (ExpertQuery q : qbjso.getExpertQuery())
//			    	{			    	
//				    	expertQueryRepository.delete(q.getId());
//			    	}
					expertQueryRepository.delete(qbjso.getExpertQuery());
				}
		    	
		    	
		    	/**
		    	 * eventuell nicht notwendig, falls es durch die Beschreibung des RElationships in der Entity funktioniert
		    	 */
//		    	for (ExpertQuery q : updatedQuery.getExpertQuery())
//		    	{			    	
//			    	expertQueryRepository.save(q);
//		    	}
		    	
//		    	Set<ExpertQuery> updatedQuerySet = new HashSet<ExpertQuery>();
//		    	updatedQuerySet.add(expertQuery);
				
				Category category = categoryRepository.findByName(updatedQuery.getCategory());
		    	qbjso.setDescription(updatedQuery.getDescription());
		    	qbjso.setName(updatedQuery.getName());
		    	qbjso.setCategory(category);
		    	ObjectWriter mapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
				String queryBuilderJsonString = mapper.writeValueAsString(updatedQuery);
				qbjso.setQueryBuilderJson(queryBuilderJsonString);
		    	
		    	/**
		    	 * zusammengebaute ExpertQuery
		    	 */
//		    	newExpertQuery.setName(updatedQuery.getName());
//		    	newExpertQuery.setDescription(updatedQuery.getDescription());
//		    	newExpertQuery.setCategory(category);
//		    	qbjso.setExpertQuery(newExpertQuery);

		    	
		    	
		    	
		    	
		    	
		    	queryBuilderJsonStringObjectRepository.save(qbjso);
		
			return new ResponseEntity<Result>(HttpStatus.OK);
		    }
			
			
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder",  method=RequestMethod.GET)
		    public ResponseEntity<Set<QueryBuilder>> getQueries() throws Exception	{
				Iterable<QueryBuilderJsonStringObject> qbjso= queryBuilderJsonStringObjectRepository.findAll();
				Set<QueryBuilder> querybuilders = new HashSet<QueryBuilder>();
				ObjectMapper mapper = new ObjectMapper();
				for ( QueryBuilderJsonStringObject qb: qbjso ){
					try {


						// Convert JSON string to Object
						String jsonInString = qb.getQueryBuilderJson();
						QueryBuilder queryBuilder = mapper.readValue(jsonInString, QueryBuilder.class);
						queryBuilder.setId(qb.getId());
						querybuilders.add(queryBuilder);

					} catch (JsonGenerationException e) {
						e.printStackTrace();
					} catch (JsonMappingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
				
				
				return new ResponseEntity<Set<QueryBuilder>>(querybuilders, HttpStatus.OK);

		    }
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}",  method=RequestMethod.GET)	 
		    public ResponseEntity<QueryBuilder> getQuery(@PathVariable String queryId) throws Exception	{
				QueryBuilderJsonStringObject qbjso=null;
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
					 qbjso= queryBuilderJsonStringObjectRepository.findOne(Long.parseLong(queryId));
				} else{
					 qbjso= queryBuilderJsonStringObjectRepository.findByName(queryId);
				}
					
				
				ObjectMapper mapper = new ObjectMapper();
				QueryBuilder queryBuilder=null;
					try {
						// Convert JSON string to Object
						String jsonInString = qbjso.getQueryBuilderJson();
						 queryBuilder = mapper.readValue(jsonInString, QueryBuilder.class);
						 queryBuilder.setId(qbjso.getId());

					} catch (JsonGenerationException e) {
						e.printStackTrace();
					} catch (JsonMappingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
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
