package msquerybuilderbackend.repository;


import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

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
}

