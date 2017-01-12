package msquerybuilderbackend.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.QueryBuilderJsonStringObject;
 
public interface QueryBuilderJsonStringRepository extends GraphRepository<QueryBuilderJsonStringObject>{
	QueryBuilderJsonStringObject findByName(String name);
}
