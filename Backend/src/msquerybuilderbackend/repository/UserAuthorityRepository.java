package msquerybuilderbackend.repository;


import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.QueryBuilderJsonStringObject;
import msquerybuilderbackend.entity.UserAuthority;


/**
 * Repository for UserAuthority objects  in the neo4j database
 * @author 
 *
 */
public interface UserAuthorityRepository extends GraphRepository<UserAuthority> {

	/**
	 * method which merges authorities and returns them
	 * @param authority
	 * @return
	 */
	@Query("merge (u:UserAuthority{authority:{0}}) return u")
	public UserAuthority mergeAuthority(String authority);
	
	/**
	 * method which adds a relation between a specific user and a specific user authority
	 * @param email of the user
	 * @param authority user authority
	 * @return
	 */
	@Query ("MATCH (u:User), (r:UserAuthority ) where u.email=~{0} and r.authority=~{1} CREATE (u)-[:HAS_AUTHORITY]->(r)")
	public UserAuthority addAuthority(String email, String authority);
	
	/**
	 * method which deletes a relation between a specific user and a specific user authority
	 * @param email of the user
	 * @param authority user authority
	 * @return
	 */
	@Query ("MATCH(n:User)-[p]->(c:UserAuthority) where  n.email=~{0} and c.authority=~{1}  DELETE p")
	public UserAuthority deleteAuthority(String email, String authority);
	
}

