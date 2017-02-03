package msquerybuilderbackend.rest;
  
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import msquerybuilderbackend.business.QueryBuilderBusiness;
import msquerybuilderbackend.business.QueryBuilderWriterBusiness;
import msquerybuilderbackend.entity.Category;
import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.Node;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.entity.QueryBuilder;
import msquerybuilderbackend.entity.QueryBuilderJsonStringObject;
import msquerybuilderbackend.exception.InvalidTypeException;
import msquerybuilderbackend.repository.CategoryRepository;
import msquerybuilderbackend.repository.ExpertQueryRepository;
import msquerybuilderbackend.repository.ParameterRepository;
import msquerybuilderbackend.repository.QueryBuilderJsonStringRepository;

/** Service class/controller for QueryBuilder (QueryBuilderJsonStringObject) REST services
* @author drago
*
*/
@RestController
@Api(tags = {"QueryBuilderService"}, value = "Service for viewing and creating QueryBuilder objects")
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
		
		/**
		 * method which gets the POST-request and calls the QueryBuilderBusiness method
		 * builds a query from the given QueryBuilder and executes it in the neo4j database
		 * @return the result of execution
		 */
			@CrossOrigin 
			//CrossOrigin request allow to call a different server from
			//a certain frontend hosted on a certain address.
			//This means that the adress of the server and 
			//the adress of client must not be the same.
			//Pleas add @CrossOrigin to every request.
			
		    @RequestMapping(value="/queryBuilder/execute",  method=RequestMethod.POST)
			@ApiOperation(value = "Executes a given QueryBuilder object and returns the result of the database",
			notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
		    public ResponseEntity<Result> preExecuteQuery(@RequestBody 
		    		@ApiParam(name = "QueryBuilder to execute",
					value = "the QueryBuilder object which should be executed in the database", required = true)
		    		QueryBuilder queryBuilder) throws Exception {
				return new ResponseEntity<Result>(queryBuilderBusiness.executeQueryBuilderQuery(queryBuilder), HttpStatus.OK);
		    }	
			
			/**
			 * method which gets the POST-request and calls the QueryBuilderBusiness method
			 * builds a query from the given QueryBuilder
			 * @return the built ExpertQuery (queryString with parameters)
			 */
			 @RequestMapping(value="/queryBuilder/queryString",  method=RequestMethod.POST)
			 @ApiOperation(value = "Genereates/Build a queryString as an ExpertQuery from a given QueryBuilder object and returns it",
				notes = "place for notes", response = ExpertQuery.class, responseContainer="ResponseEntity")
			    public ResponseEntity<ExpertQuery> generateQuery(@RequestBody 
			    		@ApiParam(name = "QueryBuilder used to build the queryString",
						value = "the QueryBuilder object which is the input for generating the queryString as an ExpertQuery", required = true)
			    		QueryBuilder queryBuilder) throws Exception {
					return new ResponseEntity<ExpertQuery>(queryBuilderBusiness.generateQueryBuilderQueryString(queryBuilder), HttpStatus.OK);
			    }
			
			 
				/**
				 * method which gets the POST-request and calls the QueryBuilderBusiness method
				 * builds a query from the given QueryBuilder and creates QueryBuilder and ExpertQuery in the neo4j database
				 * @return the neo4j ID if it was successfully, otherwise 0L 
				 */
			@Transactional
			@CrossOrigin 
		    @RequestMapping(value="/queryBuilder",  method=RequestMethod.POST)
			@ApiOperation(value = "Creates a new QueryBuilder object",
			notes = "place for notes", response = Long.class, responseContainer="ResponseEntity")
		    public ResponseEntity<Long> saveQuery(@RequestBody 
		    		@ApiParam(name = "The new QueryBuilder object to create",
					value = "Representation of the new QueryBuilder object which has to be created", required = true)
		    		QueryBuilder queryBuilder) throws Exception{
				Long newId=queryBuilderBusiness.createQueryBuilder(queryBuilder);
				if (newId==0L) return new ResponseEntity<Long>(0L,HttpStatus.CONFLICT);
				return new ResponseEntity<Long>(newId,HttpStatus.CREATED);		
		    }
			
			
			/**
			 * method which gets the DELETE-request and calls the QueryBuilderBusiness method
			 * deletes a specific QueryBuilder and the mapped ExpertQuery from the neo4j database
			 * @return Statuscode 200
			 */
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}",  method=RequestMethod.DELETE)	
			@ApiOperation(value = "Deletes a certain QueryBuilder object with a specific ID",
			notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
		    public ResponseEntity<Result> deleteQuery(@PathVariable String queryId) throws Exception	{
				queryBuilderBusiness.deleteQueryBuilder(queryId);
				return new ResponseEntity<Result>(HttpStatus.OK);
		    }
			
			
			/**
			 * method which gets the PUT-request and calls the QueryBuilderBusiness method
			 * updates a specific QueryBuilder and mapped ExpertQuery in the neo4j database
			 * @return Statuscode 200 if successfully, otherwise Statuscode 409
			 */
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}",  method=RequestMethod.PUT)	
			@ApiOperation(value = "Updates a certain QueryBuilder object with specific ID with new content",
			notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
		    public ResponseEntity<Result> updateQuery(@PathVariable String queryId, @RequestBody 
		    		@ApiParam(name = "QueryBuilder object to be updated",
					value = "the QueryBuilder object with new content which should updated in the database", required = true)
		    		QueryBuilder updatedQuery) throws Exception	{
				QueryBuilderJsonStringObject updatedObject= queryBuilderBusiness.updateQueryBuilder(queryId, updatedQuery);
				if (updatedObject==null) return new ResponseEntity<Result>(HttpStatus.CONFLICT);
				return new ResponseEntity<Result>(HttpStatus.OK);
		    }
			
			
			/**
			 * method which gets the GET-request and calls the QueryBuilderBusiness method
			 * queries all QueryBuilders from the neo4j database (with or without filter criteria)
			 * @return a set of QueryBuilders (already converted in the Business method)
			 */
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder",  method=RequestMethod.GET)
			@ApiOperation(value = "Returns all QueryBuilder objects (with or without filter criteria)",
			notes = "place for notes", response = QueryBuilder.class, responseContainer="Set")
		    public ResponseEntity<Set<QueryBuilder>> getQueries(@RequestParam(value="name",required=false) String name, @RequestParam(value="category",required=false) String category, @RequestParam(value="description",required=false) String description) throws Exception	{
				if((category==null)&&(name==null)&&(description==null)) 				return new ResponseEntity<Set<QueryBuilder>>(queryBuilderBusiness.getAllQueryBuilder(), HttpStatus.OK);
				return new ResponseEntity<Set<QueryBuilder>>(queryBuilderBusiness.getQueryBuilderBySearch(category,name,description),HttpStatus.OK);
			}
			
			/**
			 * method which gets the GET-request and calls the QueryBuilderBusiness method
			 * queries a specific QueryBuilder from the neo4j database
			 * @return the specific Querybuilder (already converted in the Business method)
			 */
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}",  method=RequestMethod.GET)	 
			@ApiOperation(value = "Returns a certain QueryBuilder object with a specific ID",
			notes = "place for notes", response = QueryBuilder.class, responseContainer="ResponseEntity")
		    public ResponseEntity<QueryBuilder> getQuery(@PathVariable String queryId) throws Exception	{
				return new ResponseEntity<QueryBuilder>(queryBuilderBusiness.getQueryBuilder(queryId),HttpStatus.OK);
		    }
			
			/**
			 * method which gets the GET-request and calls the QueryBuilderBusiness method
			 * executes the query of a specific QueryBuilder
			 * @return the result of execution
			 */
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}/execute",  method=RequestMethod.GET)	 
			@ApiOperation(value = "Executes a certain Query of a specifig QueryBuilder object in the database",
			notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
		    public ResponseEntity<Result> getQueryExecute(@PathVariable String queryId) throws Exception	{
				return new ResponseEntity<Result>(queryBuilderBusiness.getQueryBuilderExecute(queryId),HttpStatus.OK);
		    }
			
			
			/**
			 * method which gets the GET-request and calls the QueryBuilderBusiness method
			 * queries the ExpertQuery of a specific QueryBuilder (are mapped)
			 * @return the ExpertQuery mapped to the specific QueryBuilder
			 */
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/queryBuilder/{queryId}/queryString",  method=RequestMethod.GET)	 
			@ApiOperation(value = "Returns the queryString as an ExpertQuery object of a specifig QueryBuilder object",
			notes = "place for notes", response = ExpertQuery.class, responseContainer="ResponseEntity")
		    public ResponseEntity<ExpertQuery> getQueryString(@PathVariable String queryId) throws Exception	{
				return new ResponseEntity<ExpertQuery>(queryBuilderBusiness.getQueryBuilderQueryString(queryId),HttpStatus.OK);
		    }
			
//			@CrossOrigin 
//			@Transactional
//		    @RequestMapping(value="/queryBuilder",  method=RequestMethod.GET, params={"category","name","description"})	 
//		    public ResponseEntity<List<QueryBuilder>> getQueryBuilderBySearch(@RequestParam(value="category") String category,@RequestParam(value="name") String name, @RequestParam(value="description") String description) throws Exception	{
//				return new ResponseEntity<List<QueryBuilder>>(queryBuilderBusiness.getQueryBuilderBySearch(category,name,description),HttpStatus.OK);
//		    }
			
			
		  
}
