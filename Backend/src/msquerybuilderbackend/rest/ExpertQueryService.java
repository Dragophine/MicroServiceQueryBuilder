package msquerybuilderbackend.rest;

import java.util.HashMap;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import msquerybuilderbackend.business.ExpertQueryBusiness;
import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.ExpertQueryJsonObject;
import msquerybuilderbackend.repository.CategoryRepository;
import msquerybuilderbackend.repository.ExpertQueryRepository;
import msquerybuilderbackend.repository.ParameterRepository;

/** Service class/controller for ExpertQuery REST services
* @author drago
*
*/
@RestController
@Api(tags = {"ExpertQueryService"}, value = "Service for viewing and creating ExpertQueries")
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
		
		
		/**
		 * method which gets the POST-request and calls the ExpertQueryBusiness method
		 * executes a given ExpertQuery with parameters in the neo4j database
		 * @return the result of execution
		 */
			@CrossOrigin 
			//CrossOrigin request allow to call a different server from
			//a certain frontend hosted on a certain address.
			//This means that the address of the server and 
			//the address of client must not be the same.
			//Please add @CrossOrigin to every request.			
		    @RequestMapping(value="/expertqueries/execute",  method=RequestMethod.POST)
			@ApiOperation(value = "Executes a given expertQuery and returns the result of the database",
			notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
		    public ResponseEntity<Result> preExecuteQuery(@RequestBody 
		    		@ApiParam(name = "ExpertQuery to execute",
					value = "the expertQuery which should be executed in the database", required = true)
		    		ExpertQueryJsonObject expertQuery) throws Exception {			
				return new ResponseEntity<Result>(expertQueryBusiness.executeExpertQuery(expertQuery), HttpStatus.OK);
		    }	
			
			
			/**
			 * method which gets the POST-request and calls the ExpertQueryBusiness method
			 * creates a new ExpertQuery with parameters in the neo4j database
			 * @return the neo4j ID of the new ExpertQuery or 0L when name already exists
			 */
			@Transactional
			@CrossOrigin 
		    @RequestMapping(value="/expertqueries",  method=RequestMethod.POST)
			@ApiOperation(value = "Creates a new expertQuery with given content",
			notes = "place for notes", response = Long.class, responseContainer="ResponseEntity")
		    public ResponseEntity<Long> saveQuery(@RequestBody 
		    		@ApiParam(name = "New ExpertQuery to create",
					value = "The Representation of the new expertQuery which should be created", required = true)
		    		ExpertQueryJsonObject expertQueryJsonObject) throws Exception{	    
			   Long newID= expertQueryBusiness.createExpertQuery(expertQueryJsonObject);
			   if (newID==0L)return new ResponseEntity<Long>(0L,HttpStatus.CONFLICT);			
			   return new ResponseEntity<Long>(newID,HttpStatus.CREATED);			
		    }
			
			
			/**
			 * method which gets the DELETE-request and calls the ExpertQueryBusiness method
			 * deletes a specific ExpertQuery with parameters in the neo4j database
			 * @return Statuscode 200
			 */
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/expertqueries/{queryId}",  method=RequestMethod.DELETE)	 
			@ApiOperation(value = "deletes a certain expertQuery with a specific ID",
			notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
		    public ResponseEntity<Result> deleteQuery(@PathVariable String queryId) throws Exception	{
				expertQueryBusiness.deleteExpertQuery(queryId);
				return new ResponseEntity<Result>(HttpStatus.OK);
		    }
			
			
			/**
			 * method which gets the PUT-request and calls the ExpertQueryBusiness method
			 * updates a specific ExpertQuery with parameters in the neo4j database
			 * @return Statuscode 200 if it was successfully, otherwise Statuscode 409
			 */
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/expertqueries/{queryId}",  method=RequestMethod.PUT)	
			@ApiOperation(value = "Updates a certain expertQuery with a specific ID",
			notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
		    public ResponseEntity<Result> updateQuery(@PathVariable String queryId, @RequestBody
		    		@ApiParam(name = "ExpertQuery to be updated",
					value = "expertQuery with new content do be updated", required = true)
		    		ExpertQueryJsonObject updatedQuery) throws Exception	{
				ExpertQuery updatedObject = expertQueryBusiness.updateExpertQuery(queryId, updatedQuery);	
				if(updatedObject==null) return new ResponseEntity<Result>(HttpStatus.CONFLICT);
				return new ResponseEntity<Result>(HttpStatus.OK);
		    }

			
			
			/**
			 * method which gets the GET-request and calls the ExpertQueryBusiness method
			 * queries all ExpertQueries in the neo4j database (without or with filter criteria)
			 * @return a set of ExperTQueryJsonObjects (already converted in the business method)
			 */
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/expertqueries",  method=RequestMethod.GET)
			@ApiOperation(value = "Returns the all expertQueries (without or with filter criteria)",
			notes = "place for notes", response = ExpertQueryJsonObject.class, responseContainer="Set")
		    public ResponseEntity<Set<ExpertQueryJsonObject>> getQueries(@RequestParam(value="name",required=false)
		    @ApiParam(name = "Name to filter by",
			value = "the whole name or pattern in the name to filter", required = false)String name, 
		    @RequestParam(value="category",required=false)
		    @ApiParam(name = "Category to filter by",
			value = "the category of the expertqueries to filter", required = false)String category, 
		    @RequestParam(value="description",required=false)
		    @ApiParam(name = "description to filter",
			value = "the pattern in the desciption to filter", required = false) String description) throws Exception	{
				if((category==null)&&(name==null)&&(description==null)) return new ResponseEntity<Set<ExpertQueryJsonObject>>(expertQueryBusiness.getAllExpertQueries(), HttpStatus.OK);
				return new ResponseEntity<Set<ExpertQueryJsonObject>>(expertQueryBusiness.getExpertQueryBySearch(category,name,description),HttpStatus.OK);
			}
			
			
			/**
			 * method which gets the GET-request and calls the ExpertQueryBusiness method
			 * queries a specific ExpertQuery in the neo4j database
			 * @return the specific ExpertQueryJsonObject (already converted in the business method)
			 */
			@CrossOrigin 
			@Transactional
		    @RequestMapping(value="/expertqueries/{queryId}",  method=RequestMethod.GET)
			@ApiOperation(value = "Returns a certain expertQuery with a specific ID",
			notes = "place for notes", response = ExpertQueryJsonObject.class, responseContainer="ResponseEntity")
		    public ResponseEntity<ExpertQueryJsonObject> getQuery(@PathVariable String queryId) throws Exception	{
				return new ResponseEntity<ExpertQueryJsonObject>(expertQueryBusiness.getExpertQuery(queryId),HttpStatus.OK);
		    }
			
			

}
