package msquerybuilderbackend.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;

import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.QueryBuilderJsonStringObject;
 

/**
 * Repository for the QueryBuilderJsonStringObject objects in the neo4j database
 * @author drago
 *
 */
public interface QueryBuilderJsonStringRepository extends GraphRepository<QueryBuilderJsonStringObject>{
	
	/**
	 * method which queries a certain QueryBuilderJsonStringObject object by name
	 * @param name is the given name
	 * @return the found QueryBuilderJsonStringObject object
	 */
	QueryBuilderJsonStringObject findByName(String name);
	

	/**
	 * method which queries QueryBuilderJsonStringObject objects with given parameters
	 * @param desc is the pattern the QueryBuilderJsonStringObject objects have to include in the description
	 * @param name is the name or the pattern the QueryBuilderJsonStringObject objects have to be named with or include in the name
	 * @param category is the category the QueryBuilderJsonStringObject objects have to be in
	 * @return a list of QueryBuilderJsonStringObject objects found with the paramters
	 */
	@Query("MATCH(n:QueryBuilderJsonStringObject)-[p]->(c:Category) where  n.description=~{0} and n.name=~{1} and c.name=~{2} return n")
	List<QueryBuilderJsonStringObject> searchByParameter(String desc,String name, String category);
}
