package msquerybuilderbackend.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.Category;

/**
 * Repository for the Category objects in the neo4j database
 * @author drago
 *
 */
public interface CategoryRepository extends GraphRepository<Category> {

	/**
	 * method which queries a certain category by a given a name
	 * @param name is the given name
	 * @return the found category
	 */
	Category findByName(String name);

	/**
	 * method which queries all names of categories
	 * @return a list of all names of categories as Strings
	 */
	@Query("MATCH (c:Category) return c.name")
	List<String> getNames();
	
	/**
	 * method which queries all categories from the neo4j database
	 * @return a list of all found categories
	 */
	@Query("MATCH (c:Category) return c")
	List<Category> getAllCategories();
}
