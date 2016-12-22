package msquerybuilderbackend.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.Category;

public interface CategoryRepository extends GraphRepository<Category> {

	
	Category findByName(String name);

	
	@Query("MATCH (c:Category) return c.name")
	  List<String> getNames();
	
	
	@Query("MATCH (c:Category) return c")
	  List<Category> getAllCategories();
}
