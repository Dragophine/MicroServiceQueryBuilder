package msquerybuilderbackend.rest;

import java.util.HashMap;
import java.util.List;

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


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import msquerybuilderbackend.business.CategoryBusiness;
import msquerybuilderbackend.entity.Category;
import msquerybuilderbackend.entity.QueryBuilderJsonStringObject;
import msquerybuilderbackend.repository.CategoryRepository;


/**
 * Service class/controller for Category REST services
 * @author drago
 *
 */
@RestController
@Api(tags = {"CategoryService"}, value = "Service for viewing and creating Categories")
public class CategoryService {

	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	CategoryBusiness categoryBusiness;
	
	
	/**
	 * method which gets the GET-request and calls the categoryBusiness method
	 * queries all categories in the neo4j database
	 * @return a list of all categories
	 */
	@CrossOrigin 
	//CrossOrigin request allow to call a different server from
	//a certain frontend hosted on a certain address.
	//This means that the adress of the server and 
	//the adress of client must not be the same.
	//Pleas add @CrossOrigin to every request.
	@RequestMapping(value="/categories",  method=RequestMethod.GET)
	@ApiOperation(value = "Returns all categories",
	notes = "place for notes", response = Category.class, responseContainer="List")
	public ResponseEntity<List<Category>> getCategories() {
		List<Category> categories= categoryRepository.getAllCategories();
		return new ResponseEntity<List<Category>>(categoryBusiness.getAllCategories(), HttpStatus.OK);
    }
	
	
	
	/**
	 * method which gets the POST-request and calls the categoryBusiness method
	 * creates a new category in the neo4j database
	 * @return the neo4j ID of the new category or 0L if name already exists
	 */
	@CrossOrigin 
	@Transactional
	@RequestMapping(value="/categories",  method=RequestMethod.POST)
	@ApiOperation(value = "creates a new category",
	notes = "place for notes", response = Long.class, responseContainer="ResonseEntity")
	public ResponseEntity<Long> postCategory(@RequestBody
			@ApiParam(name = "new Category to create",
			value = "Representation of the new category which will be created", required = true)
			Category category) throws Exception {
		Long newID=categoryBusiness.createCategory(category);
		if (newID==0L) return new ResponseEntity<Long>( 0L,HttpStatus.CONFLICT);
			return new ResponseEntity<Long>(newID,HttpStatus.CREATED);
		
	}
	
	/**
	 * method which gets the GET-request and calls the categoryBusiness method
	 * queries all names of categories in the neo4j database
	 * @return a list of all names of categories as strings
	 */
	@CrossOrigin 
	@RequestMapping(value="/categories/name-list",  method=RequestMethod.GET)
	@ApiOperation(value = "Get all names of all categories",
	notes = "place for notes", response = String.class, responseContainer = "List")
	public ResponseEntity<List<String>> getCategoriesNames(){
		return new ResponseEntity<List<String>>(categoryBusiness.getNameList(), HttpStatus.OK);
    }
	
	
	/**
	 * method which gets the PUT-request and calls the categoryBusiness method
	 * updates a specific category in the neo4j database
	 * @return Statuscode 200 if it was successfully, otherwise Statuscode 409
	 */
	@CrossOrigin 
	@RequestMapping(value="/categories/{categoryId}",  method=RequestMethod.PUT)
	@ApiOperation(value = "update a certain categoriy with a specific ID",
	notes = "place for notes", response = Category.class, responseContainer = "ResponseEntity")
	@Transactional
	public ResponseEntity<Category> upateCategory( @PathVariable String categoryId, @RequestBody
			@ApiParam(name = "Category with new content do be updated",
			value = "Representation of the updated category with new content which will be updated", required = true) 
			Category cat) throws Exception{
		Category category = categoryBusiness.updateCategory(categoryId, cat);
		if (category==null) return new ResponseEntity<Category>(category, HttpStatus.CONFLICT);
		return new ResponseEntity<Category>(category, HttpStatus.OK);
    }
	
	
	/**
	 * method which gets the DELETE-request and calls the categoryBusiness method
	 * deletes a specific category in the neo4j database
	 * @return Statuscode 200
	 */
	@CrossOrigin 
	@RequestMapping(value="/categories/{categoryId}",  method=RequestMethod.DELETE)
	@Transactional
	@ApiOperation(value = "delete a certain categoriy with a specific ID",
	notes = "place for notes", response = Result.class, responseContainer = "ResponseEntity")
	public ResponseEntity<Result> deleteCategory( @PathVariable String categoryId){
		categoryBusiness.deleteCategory(categoryId);	
		return new ResponseEntity<Result>(HttpStatus.OK);
    }
}
