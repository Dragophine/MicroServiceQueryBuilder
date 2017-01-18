package msquerybuilderbackend.repository;


import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.UserAuthority;

public interface UserAuthorityRepository extends GraphRepository<UserAuthority> {
	
	@Query("merge (u:UserAuthority{authority:{0}}) return u")
	public UserAuthority mergeAuthority(String authority);
}

