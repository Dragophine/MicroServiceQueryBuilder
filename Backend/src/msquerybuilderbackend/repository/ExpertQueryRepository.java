package msquerybuilderbackend.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import msquerybuilderbackend.entity.ExpertQuery;

/**
 * Repository for the ExpertQuery objects in the neo4j database
 * @author drago
 *
 */
public interface ExpertQueryRepository extends GraphRepository<ExpertQuery> {
		
	/**
	 * method which queries a certain ExpertQuery object by name
	 * @param name is the given name
	 * @return the found ExpertQuery object
	 */
	ExpertQuery findByName(String name);

	/**
	 * method which queries all names of the ExpertQuery objects
	 * @return a list of all names as Strings
	 */
	@Query("MATCH (e:ExpertQuery) return e.name")
	List<String> getNames();
	
	/**
	 * method which queries all ExpertQuery objects from the neo4j database
	 * @return a list of all found ExpertQuery objects
	 */
	@Query("MATCH (e:ExpertQuery) return e")
	List<ExpertQuery> getAllExpertQueries();
	
	/**
	 * method which queries ExpertQuery objects with specific parameter
	 * @param desc is the pattern which the ExpertQuery objects have to contain in the description
	 * @param name is the name or the pattern which the ExpertQuery objects are named with or include in the name
	 * @param category is the category the ExpertQuery objects have to be in
	 * @return a Set of ExpertQuery objects with the given parameter
	 */
	@Query("MATCH(n:ExpertQuery)-[p]->(c:Category) where  n.description=~{0} and n.name=~{1} and c.name=~{2} return n")
	Set<ExpertQuery> searchByParameter(String desc,String name, String category);
}
