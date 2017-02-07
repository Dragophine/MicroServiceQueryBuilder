package msquerybuilderbackend.business;

import java.util.HashMap;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
/**
 * Class for all activities with neo4j database regarding the Relationshiptypes in the neo4j database
 * @author drago
 *
 */
@Component
public class RelationshipTypeBusiness {
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	
	/**
	 * method which queries all relationship types from the neo4j database excluding the relationship types created by the application itself
	 * @return a Result of the queried relationship types
	 */
	public Result getAllRelationshipTypes(){
		String queryRelations = "MATCH ()-[r]->() where type(r)<>\"HAS_PARAMETER\" and type(r)<>\"HAS_CATEGORY\" AND type(r)<>\"HAS_EXPERTQUERY\" AND type(r)<>\"HAS_AUTHOR\"  return distinct type(r) AS Relationship";
		Result result = neo4jOperations.query(queryRelations, new HashMap<String, String>());	
		return result;
	}

	/**
	 * method which queries the keys (attributes) of a certain relationship type from the neo4j database
	 * @param relationId is the relationship type the keys are queried for
	 * @return a Result of the found keys
	 */
	public Result getKeysOfCertainRelationshipType(String relationId){		
		String queryKeys = "MATCH ()-[n:"+relationId+"]->() UNWIND keys(n) AS key " +
						 "WITH key ORDER BY key RETURN  COLLECT(distinct key) as Keys";
		Result result = neo4jOperations.query(queryKeys, new HashMap<String, String>());		
		return result;
	}
}
