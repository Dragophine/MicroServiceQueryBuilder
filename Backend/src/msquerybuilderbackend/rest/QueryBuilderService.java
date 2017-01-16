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
				queryBuilderBusiness.deleteQueryBuilder(queryId);
				return new ResponseEntity<Result>(HttpStatus.OK);
		    }
			
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}",  method=RequestMethod.PUT)	 
		    public ResponseEntity<Result> updateQuery(@PathVariable String queryId, @RequestBody QueryBuilder updatedQuery) throws Exception	{
				QueryBuilderJsonStringObject updatedObject= queryBuilderBusiness.updateQueryBuilder(queryId, updatedQuery);
				if (updatedObject==null) return new ResponseEntity<Result>(HttpStatus.CONFLICT);
				return new ResponseEntity<Result>(HttpStatus.OK);
		    }
			
			
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder",  method=RequestMethod.GET)
		    public ResponseEntity<Set<QueryBuilder>> getQueries() throws Exception	{
				return new ResponseEntity<Set<QueryBuilder>>(queryBuilderBusiness.getAllQueryBuilder(), HttpStatus.OK);
		    }
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}",  method=RequestMethod.GET)	 
		    public ResponseEntity<QueryBuilder> getQuery(@PathVariable String queryId) throws Exception	{
				return new ResponseEntity<QueryBuilder>(queryBuilderBusiness.getQueryBuilder(queryId),HttpStatus.OK);
		    }
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}/execute",  method=RequestMethod.GET)	 
		    public ResponseEntity<Result> getQueryExecute(@PathVariable String queryId) throws Exception	{
				return new ResponseEntity<Result>(queryBuilderBusiness.getQueryBuilderExecute(queryId),HttpStatus.OK);
		    }
			
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}/queryString",  method=RequestMethod.GET)	 
		    public ResponseEntity<ExpertQuery> getQueryString(@PathVariable String queryId) throws Exception	{
				return new ResponseEntity<ExpertQuery>(queryBuilderBusiness.getQueryBuilderQueryString(queryId),HttpStatus.OK);
		    }
			
			
		  
}
