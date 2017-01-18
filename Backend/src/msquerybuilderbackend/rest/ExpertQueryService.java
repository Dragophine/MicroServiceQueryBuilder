package msquerybuilderbackend.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import msquerybuilderbackend.business.ExpertQueryBusiness;
import msquerybuilderbackend.entity.Alert;
import msquerybuilderbackend.entity.Category;
import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.ExpertQueryJsonObject;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.entity.QueryBuilder;
import msquerybuilderbackend.entity.QueryBuilderJsonStringObject;
import msquerybuilderbackend.exception.InvalidTypeException;
import msquerybuilderbackend.repository.CategoryRepository;
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
		@Autowired
		CategoryRepository categoryRepository;
		@Autowired
		ExpertQueryBusiness expertQueryBusiness;
		
	
			@CrossOrigin 
			//CrossOrigin request allow to call a different server from
			//a certain frontend hosted on a certain address.
			//This means that the adress of the server and 
			//the adress of client must not be the same.
			//Pleas add @CrossOrigin to every request.
			
		    @RequestMapping(value="/expertqueries/execute",  method=RequestMethod.POST)
		    public ResponseEntity<Result> preExecuteQuery(@RequestBody ExpertQueryJsonObject expertQuery) throws Exception {			
				return new ResponseEntity<Result>(expertQueryBusiness.executeExpertQuery(expertQuery), HttpStatus.OK);
		    }	
			
			@Transactional
			@CrossOrigin 
		    @RequestMapping(value="/expertqueries",  method=RequestMethod.POST)	 
		    public ResponseEntity<Long> saveQuery(@RequestBody ExpertQueryJsonObject expertQueryJsonObject) throws Exception{	    
			   Long newID= expertQueryBusiness.createExpertQuery(expertQueryJsonObject);
			   if (newID==0L)return new ResponseEntity<Long>(0L,HttpStatus.CONFLICT);			
			   return new ResponseEntity<Long>(newID,HttpStatus.CREATED);			
		    }
			
			
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/expertqueries/{queryId}",  method=RequestMethod.DELETE)	 
		    public ResponseEntity<Result> deleteQuery(@PathVariable String queryId) throws Exception	{
				expertQueryBusiness.deleteExpertQuery(queryId);
				return new ResponseEntity<Result>(HttpStatus.OK);
		    }
			
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/expertqueries/{queryId}",  method=RequestMethod.PUT)	 
		    public ResponseEntity<Result> updateQuery(@PathVariable String queryId, @RequestBody ExpertQueryJsonObject updatedQuery) throws Exception	{
				ExpertQuery updatedObject = expertQueryBusiness.updateExpertQuery(queryId, updatedQuery);	
				if(updatedObject==null) return new ResponseEntity<Result>(HttpStatus.CONFLICT);
				return new ResponseEntity<Result>(HttpStatus.OK);
		    }
			
			@Deprecated
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
		    public ResponseEntity<Set<ExpertQueryJsonObject>> getQueries(@RequestParam(value="name",required=false) String name, @RequestParam(value="category",required=false) String category, @RequestParam(value="description",required=false) String description) throws Exception	{
				if((category==null)&&(name==null)&&(description==null)) return new ResponseEntity<Set<ExpertQueryJsonObject>>(expertQueryBusiness.getAllExpertQueries(), HttpStatus.OK);
				return new ResponseEntity<Set<ExpertQueryJsonObject>>(expertQueryBusiness.getExpertQueryBySearch(category,name,description),HttpStatus.OK);
			}
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/expertqueries/{queryId}",  method=RequestMethod.GET)	 
		    public ResponseEntity<ExpertQueryJsonObject> getQuery(@PathVariable String queryId) throws Exception	{
				return new ResponseEntity<ExpertQueryJsonObject>(expertQueryBusiness.getExpertQuery(queryId),HttpStatus.OK);
		    }
			
			

}
