package msquerybuilderbackend.repository;

import java.util.List;
import java.util.Optional;

import msquerybuilderbackend.entity.User;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;


public interface UserRepository extends GraphRepository<User> {

	
	//User findByUsername(String username);
	
	User findByEmail(String email);

	
	@Query("MATCH (u:User) return u.name")
	  List<String> getUsername();
	
	@Query("MATCH (u:User) WHERE u.username = 'Admin' return u")
	List<User> getUser();
	
	@Query("MATCH (u:User) return u")
	  List<User> getAllUsers();
	
	@Query("match(user:User) where user.email={0} return id(user)")
	Iterable<Long> checkForExistingUser(String email);
	
	//Optional<User> findOneByActivationKey(String activationKey);

    //List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Long dateTime);

    //User findOneByResetKey(String resetKey);

    /*User findOneByEmail(String email);

    User findOneByLogin(String login);

    User findOneById(String userId);

    /*@Override
    void delete(User t);
	
	/*@Query("Match(user:User) where user.username='admin' return id(user)")
	Iterable<Long> checkForExistingUser(String email);*/
	
}


/*import java.time.ZonedDateTime;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data Neo4jDB repository for the User entity.
 */
/*public interface UserRepository extends GraphRepository<User> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Long dateTime);

    User findOneByResetKey(String resetKey);

    User findOneByEmail(String email);

    User findOneByLogin(String login);

    User findOneById(String userId);

    @Override
    void delete(User t);

}*/
