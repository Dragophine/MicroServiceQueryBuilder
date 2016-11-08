package application.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import application.entity.ExpertQuery;
import application.entity.Parameter;

public interface ParameterRepository extends GraphRepository<Parameter> {
}
