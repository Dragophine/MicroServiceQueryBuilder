package msquerybuilderbackend.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.Parameter;

public interface ParameterRepository extends GraphRepository<Parameter> {
}
