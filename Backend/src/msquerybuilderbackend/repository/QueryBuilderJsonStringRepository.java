package msquerybuilderbackend.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.QueryBuilderJsonStringObject;
 
public interface QueryBuilderJsonStringRepository extends GraphRepository<QueryBuilderJsonStringObject>{
	QueryBuilderJsonStringObject findByName(String name);
	
	@Query("MATCH(n:QueryBuilderJsonStringObject)-[p]->(c:Category) where n.name=~'.*{0}.*' return n")
	List<QueryBuilderJsonStringObject> searchByName( String name);
	
	@Query("MATCH(n:QueryBuilderJsonStringObject)-[p]->(c:Category) where c.name=~'.*{0}.*' return n")
	List<QueryBuilderJsonStringObject> searchByCategory(String category);
	
	@Query("MATCH(n:QueryBuilderJsonStringObject)-[p]->(c:Category) where n.description=~'.*{0}.*' return n")
	List<QueryBuilderJsonStringObject> searchByDescription(String desc);
	
	@Query("MATCH(n:QueryBuilderJsonStringObject)-[p]->(c:Category) where  n.description=~{0} and n.name=~{1} and c.name=~{2} return n")
	List<QueryBuilderJsonStringObject> searchByParameter(String desc,String name, String category);
}
