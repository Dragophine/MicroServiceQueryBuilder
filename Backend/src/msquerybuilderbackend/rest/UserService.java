package msquerybuilderbackend.rest;

import java.security.Principal;

import msquerybuilderbackend.business.UserBusiness;
import msquerybuilderbackend.entity.Category;
import msquerybuilderbackend.entity.User;
import msquerybuilderbackend.entity.UserAuthority;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.spring.web.json.Json;

/** Service class/controller for User REST services
* @author 
*
*/
@RestController
@CrossOrigin
@Api(tags = {"UserService"}, value = "Service for viewing and creating User")
public class UserService {
	
	@Autowired
	UserBusiness userBusiness;
	
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
		
		user.addAuthority(new UserAuthority("ROLE_USER"));
		
		userBusiness.createUser(user);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
