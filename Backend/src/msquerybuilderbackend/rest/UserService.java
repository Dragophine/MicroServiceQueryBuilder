package msquerybuilderbackend.rest;

import java.security.Principal;

import msquerybuilderbackend.business.UserBusiness;
import msquerybuilderbackend.entity.Category;
import msquerybuilderbackend.entity.User;
import msquerybuilderbackend.entity.UserAuthority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.spring.web.json.Json;

/** Service class/controller for User REST services
* @author drago
*
*/
@RestController
@CrossOrigin
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
	public Principal user(Principal principal) {
		return principal;
	}
	
	/**
	 * method which gets the POST-request and calls the UserBusiness method
	 * creates a User and its authority in the neo4j database
	 * @return Statuscode 200
	 */
	@RequestMapping(value="/user",  method=RequestMethod.POST)
	public ResponseEntity<Long> postUser(@RequestBody User user)  {
		
		user.addAuthority(new UserAuthority("ROLE_USER"));
		
		userBusiness.createUser(user);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
