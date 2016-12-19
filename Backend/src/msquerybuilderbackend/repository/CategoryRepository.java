package msquerybuilderbackend.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.Category;

public interface CategoryRepository extends GraphRepository<Category> {

}
