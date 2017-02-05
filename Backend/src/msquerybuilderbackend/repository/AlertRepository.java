package msquerybuilderbackend.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import msquerybuilderbackend.entity.Alert;

/**
 * Repository for the alert objects in the neo4j database
 * @author Martin
 */
public interface AlertRepository  extends GraphRepository<Alert>{

	/**
	 * queries a certain Alert by given name
	 * @param name is the name of the certain alert
	 * @return the found alert
	 */
	Alert findByName(String name);
	
	/**
	 * queries a certain Alert by given neo4j ID
	 * @param id is the neo4j ID
	 * @return the found Alert
	 */
	Alert findById(Long id);
	
	
	/**
	 * queries all names of the alerts
	 * @return a list of all names as Strings
	 */
	@Query("MATCH (a:Alert) return a.name")
	List<String> getNames();
	
	/**
	 * queries all alerts from the neo4j database
	 * @return a list of all found alerts
	 */
	@Query("MATCH (a:Alert) return a")
	List<Alert> getAllAlerts();
}

