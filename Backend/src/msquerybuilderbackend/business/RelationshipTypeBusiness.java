package msquerybuilderbackend.business;

import java.util.HashMap;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RelationshipTypeBusiness {
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	
	public Result getAllRelationshipTypes(){
		String queryRelations = "MATCH ()-[r]->() where type(r)<>\"HAS_PARAMETER\" and type(r)<>\"HAS_CATEGORY\" AND type(r)<>\"HAS_EXPERTQUERY\"   return distinct type(r) AS Relationship";
		Result result = neo4jOperations.query(queryRelations, new HashMap<String, String>());	
		return result;
	}
	
	public Result getKeysOfCertainRelationshipType(String relationId){		
		String queryKeys = "MATCH ()-[n:"+relationId+"]->() UNWIND keys(n) AS key " +
						 "WITH key ORDER BY key RETURN  COLLECT(distinct key) as Keys";
		Result result = neo4jOperations.query(queryKeys, new HashMap<String, String>());		
		return result;
	}
}
