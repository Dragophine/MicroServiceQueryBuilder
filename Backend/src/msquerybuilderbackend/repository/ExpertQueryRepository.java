package msquerybuilderbackend.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.ExpertQuery;

public interface ExpertQueryRepository extends GraphRepository<ExpertQuery> {
	
	
	
	ExpertQuery findByName(String name);

	
	@Query("MATCH (e:ExpertQuery) return e.name")
	  List<String> getNames();
	
	
	@Query("MATCH (e:ExpertQuery) return e")
	  List<ExpertQuery> getAllExpertQueries();
}
