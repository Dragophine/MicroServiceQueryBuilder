package application.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import application.entity.ExpertQuery;

public interface ExpertQueryRepository extends GraphRepository<ExpertQuery> {
	
}
