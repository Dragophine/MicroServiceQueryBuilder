package msquerybuilderbackend.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.QueryBuilderJsonStringObject;
 
public interface QueryBuilderJsonStringRepository extends GraphRepository<QueryBuilderJsonStringObject>{
	QueryBuilderJsonStringObject findByName(String name);
	
	@Query("MATCH(n:QueryBuilderJsonStringObject) where n.name=~ '.*{name}.*' return n")
	List<QueryBuilderJsonStringObject> searchByName(@Param("name") String name);
	
	@Query("MATCH(n:QueryBuilderJsonStringObject)-[p]->(c:Category) where c.name='{category}' return c")
	List<QueryBuilderJsonStringObject> searchByCategory(@Param("category")String category);
	
	@Query("MATCH(n:QueryBuilderJsonStringObject) where n.description=~ '.*{desc}.*' return n")
	List<QueryBuilderJsonStringObject> searchByDescription(@Param("desc")String desc);
	
	@Query("MATCH(n:QueryBuilderJsonStringObject)-[p]->(c:Category) where  n.description=~'.*{0}.*' and n.name=~'.*{1}.*' and c.name=~'.*{2}.*' return n")
	List<QueryBuilderJsonStringObject> searchByParameter(String desc,String name, String category);
}
