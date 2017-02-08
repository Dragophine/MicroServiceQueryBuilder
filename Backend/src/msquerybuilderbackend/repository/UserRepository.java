package msquerybuilderbackend.repository;

import java.util.List;
import java.util.Optional;

import msquerybuilderbackend.entity.User;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

/**
 * Repository for User objects in the neo4j database
 * @author Roman
 *
 */
public interface UserRepository extends GraphRepository<User> {

	
	//User findByUsername(String username);
	
	/**
	 * method which queries a User by a given email
	 * @param email is the given email
	 * @return the found User
	 */
	User findByEmail(String email);

	/**
	 * method which queries all usernames from the neo4j database
	 * @return a list of all usernames as Strings
	 */
	@Query("MATCH (u:User) return u.name")
	  List<String> getUsername();
	
	/**
	 * method which queries the Admin
	 * @return the User Admin
	 */
	@Query("MATCH (u:User) WHERE u.username = 'Admin' return u")
	List<User> getUser();
	
	/**
	 * method which queries all users from the neo4j database
	 * @return a list of all users found in the neo4j database
	 */
	@Query("MATCH (u:User) return u")
	  List<User> getAllUsers();
	
	
	/**
	 * method which queries the id of a user with a given email
	 * @param email is the email the user is queried with
	 * @return the neo4j ID of the user
	 */
	@Query("match(user:User) where user.email={0} return id(user)")
	Iterable<Long> checkForExistingUser(String email);
	
	
}


