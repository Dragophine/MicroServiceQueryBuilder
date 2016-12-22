package msquerybuilderbackend.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.Alert;


public interface AlertRepository  extends GraphRepository<Alert>{

	
	Alert findByName(String name);
	Alert findById(Long id);
	
	@Query("MATCH (a:Alert) return a.name")
	  List<String> getNames();
	
	
	@Query("MATCH (a:Alert) return a")
	  List<Alert> getAllAlerts();
}

