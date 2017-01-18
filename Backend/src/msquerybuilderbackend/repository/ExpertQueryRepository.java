package msquerybuilderbackend.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.ExpertQuery;


public interface ExpertQueryRepository extends GraphRepository<ExpertQuery> {
	
	
	
	ExpertQuery findByName(String name);

	
	@Query("MATCH (e:ExpertQuery) return e.name")
	  List<String> getNames();
	
	
	@Query("MATCH (e:ExpertQuery) return e")
	  List<ExpertQuery> getAllExpertQueries();
	
	@Query("MATCH(n:ExpertQuery)-[p]->(c:Category) where  n.description=~ '.*{0}.*' and n.name=~ '.*{1}.*' and c.name=~'.*{2}.*' return n")
	Set<ExpertQuery> searchByParameter(String desc,String name, String category);
}
