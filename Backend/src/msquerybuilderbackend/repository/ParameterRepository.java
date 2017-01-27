package msquerybuilderbackend.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.Parameter;

/**
 * Repository for Parameter objects in the neo4j database
 * @author drago
 *
 */
public interface ParameterRepository extends GraphRepository<Parameter> {
}
