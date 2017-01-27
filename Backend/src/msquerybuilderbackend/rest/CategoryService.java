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
	public ResponseEntity<Long> postCategory(@RequestBody Category category) throws Exception {
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
	@Transactional
	public ResponseEntity<Category> upateCategory( @PathVariable String categoryId, @RequestBody Category cat) throws Exception{
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
	public ResponseEntity<Result> deleteCategory( @PathVariable String categoryId){
		categoryBusiness.deleteCategory(categoryId);	
		return new ResponseEntity<Result>(HttpStatus.OK);
    }
}
