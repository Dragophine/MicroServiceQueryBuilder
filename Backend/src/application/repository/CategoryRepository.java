package application.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import application.entity.Category;

public interface CategoryRepository extends GraphRepository<Category> {

}
