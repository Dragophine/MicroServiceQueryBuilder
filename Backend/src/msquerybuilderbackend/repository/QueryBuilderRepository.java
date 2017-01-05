package msquerybuilderbackend.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.QueryBuilder;
 
public interface QueryBuilderRepository extends GraphRepository<QueryBuilder>{
	QueryBuilder findByName(String name);
}
