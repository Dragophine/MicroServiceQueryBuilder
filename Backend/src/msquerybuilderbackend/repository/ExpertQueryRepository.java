package msquerybuilderbackend.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.ExpertQuery;

public interface ExpertQueryRepository extends GraphRepository<ExpertQuery> {
	
}
