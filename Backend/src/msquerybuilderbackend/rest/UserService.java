package msquerybuilderbackend.rest;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

import msquerybuilderbackend.business.UserBusiness;
import msquerybuilderbackend.entity.Category;
import msquerybuilderbackend.entity.User;
import msquerybuilderbackend.entity.UserAuthority;
import msquerybuilderbackend.repository.CategoryRepository;
import msquerybuilderbackend.repository.UserAuthorityRepository;
import msquerybuilderbackend.repository.UserRepository;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.spring.web.json.Json;

/** Service class/controller for User REST services
* @author Roman
*
*/
@RestController
@CrossOrigin
@Api(tags = {"UserService"}, value = "Service for viewing and creating User")
public class UserService {
	
	@Autowired
	UserBusiness userBusiness;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserAuthorityRepository userAuthorityRepository;
	
	/**
	 * method which gets the GET-request and calls the UserBusiness method
	 * catches the principal and returns it
	 * @return the principal
	 */
	@CrossOrigin
	@RequestMapping(value="/authentications/user",  method=RequestMethod.GET)
	@ApiOperation(value = "Catches the principal and returns it",
	notes = "place for notes", response = Principal.class)
	public Principal user(Principal principal) {
		return principal;
	}
	
	/**
	 * method which gets the POST-request and calls the UserBusiness method
	 * creates a User and its authority in the neo4j database
	 * @return the unique neo4j ID of the new User
	 */
	@RequestMapping(value="/user",  method=RequestMethod.POST)
	@ApiOperation(value = "Creates a new User and its authority in the database",
	notes = "place for notes", response = Long.class, responseContainer="ResponseEntity")
	public ResponseEntity<Long> postUser(@RequestBody
			@ApiParam(name = "The new User to be created",
			value = "the new user which is created in the database", required = true)
			User user)  {
		System.out.println(user.getEmail().equals("admin@admin.com"));
		if(user.getEmail().equals("admin@admin.com")) {
			user.addAuthority(new UserAuthority("ROLE_ADMIN"));
			user.addAuthority(new UserAuthority("CATEGORY"));
			user.addAuthority(new UserAuthority("EXPERTMODE"));
			user.addAuthority(new UserAuthority("ALERT"));
			user.addAuthority(new UserAuthority("ALERTSTATISTIC"));
		} else {
			user.addAuthority(new UserAuthority("ROLE_USER"));
		}
		
		
		userBusiness.createUser(user);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/users",  method=RequestMethod.GET)
	@ApiOperation(value = "Returns all users",
	notes = "place for notes", response = User.class, responseContainer="List")
	public ResponseEntity<List<User>> getCategories() {
		List<User> users= userRepository.getAllUsers();
		return new ResponseEntity<List<User>>(userBusiness.getAllUsers(), HttpStatus.OK);
    }
	
	/**
	 * method which gets the GET-request for a specific user and calls the UserBusiness method
	 * gets the existing authorities of a user and returns them
	 * @param userId email address of the user for which the authorities should be returned
	 * @return Collection of UserAuthorities
	 * @throws Exception
	 */
	@CrossOrigin 
	@Transactional
    @RequestMapping(value="/user/{userId:.+}",  method=RequestMethod.GET)	 
	@ApiOperation(value = "gets all authorities for a specific user",
	notes = "place for notes", response = UserAuthority.class, responseContainer="List")
    public ResponseEntity<Collection<? extends GrantedAuthority>> getAuthorities (@PathVariable String userId) throws Exception	{
		User user = userBusiness.getUserByEmail(userId);
		System.out.println(userId);
		return new ResponseEntity<Collection<? extends GrantedAuthority>>(user.getAuthorities(), HttpStatus.OK);
    }
	
	/**
	 * method which gets the POST-request for a specific user and calls the UserBusiness method
	 * creates a new user authority for the specific user
	 * @param userId email address of the user for which the authority should be added
	 * @param json json which contains the authority which should be added
	 * @return 
	 * @throws Exception
	 */
	@CrossOrigin 
	@Transactional
	@RequestMapping(value="/user/{userId:.+}/authority",  method=RequestMethod.POST)
	@ApiOperation(value = "creates a new userAuthority",
	notes = "place for notes", response = Long.class, responseContainer="ResonseEntity")
	public ResponseEntity<Long> postAuthority(@PathVariable String userId, @RequestBody
			@ApiParam(name = "new Category to create",
			value = "Representation of the new category which will be created", required = true)
			String json) throws Exception {
		String[] authall = json.split(":");
		String auth = authall[1].substring(1, authall[1].length()-2);
		userBusiness.addAuthority(userId, auth);
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	/**
	 * method which gets the DELETE-request for a specific user and calls the UserBusiness method
	 * deletes a  user authority from the specific user 
	 * @param userId email address of the user for which the authority should be deleted
	 * @param json json which contains the authority which should be deleted
	 * @return
	 * @throws Exception
	 */
	@CrossOrigin 
	@RequestMapping(value="/user/{userId:.+}/authority",  method=RequestMethod.DELETE)
	@Transactional
	@ApiOperation(value = "delete a certain user Authority",
	notes = "place for notes", response = Result.class, responseContainer = "ResponseEntity")
	public ResponseEntity<Result> deleteAuthority( @PathVariable String userId , @RequestBody
			@ApiParam(name = "new Category to create",
			value = "Representation of the new category which will be created", required = true)
			String json) throws Exception{
		String[] authall = json.split(":");
		String auth = authall[1].substring(1, authall[1].length()-2);
		userBusiness.deleteAuthority(userId, auth);	
		return new ResponseEntity<Result>(HttpStatus.OK);
    }
	
}
