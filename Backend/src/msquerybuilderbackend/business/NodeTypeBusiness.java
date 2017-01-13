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
public class NodeTypeBusiness {

	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	
	public Result getNodeTypes(){
		
		String queryNodes = "MATCH (n) WHERE labels(n) <> \"ExpertQuery\" AND labels(n) <> \"Parameter\" AND labels(n)<>\"Alert\" AND labels(n)<>\"Category\" AND labels(n) <> \"QueryBuilderJsonStringObject\" return DISTINCT labels(n) AS Label";
	
		Result result = neo4jOperations.query(queryNodes, new HashMap<String, String>());
	
		return result;
	}
	
	public Result getNodeTypesWithCertainRelationshipAndDirection(String nodeId, String relationship, String direction){
		String queryNodes="";
		if (direction.equals("ingoing")){
			 queryNodes = "MATCH (n:" + nodeId + ") <-["+relationship+"]- (p) return distinct labels(p) AS Label";
			 
		} else {
			queryNodes = "MATCH (n:" + nodeId + ") -["+relationship+"] -> (p) return distinct labels(p) AS Label";
		}
		

		Result result = neo4jOperations.query(queryNodes, new HashMap<String, String>());
		
		return result;
	}
	
	public Result getNodeTypesWithCertainRelationship(String nodeId, String relationship){
		String	queryNodes = "MATCH (n:" + nodeId + ") -[f:"+relationship+"] -> (p) return distinct labels(p) AS Label";
		Result result = neo4jOperations.query(queryNodes, new HashMap<String, String>());	
		return result;
	}
	
	public Result getKeysOfCertainNodeType(String nodeId){
		
		String queryKeys = "MATCH (n:" + nodeId + ") UNWIND keys(n) AS key " +
						 "WITH key ORDER BY key RETURN  COLLECT(distinct key) as Keys";

		Result result = neo4jOperations.query(queryKeys, new HashMap<String, String>());
		
		return result;
	}
	
	public Result getRelationsOfNodeTypeWithDirection(String nodeId, String direction){
		String queryRelations="";
		if (direction.equals("ingoing")||direction.equals("INGOING")){
			queryRelations = "MATCH (n:" + nodeId + ")<-[r]- (p) return distinct type(r) AS Relation, labels(p) as Labels";	
		} else if (direction.equals("outgoing") || direction.equals("OUTGOING")) {
			 queryRelations = "MATCH (n:" + nodeId + ") -[r] -> (p) return distinct type(r) AS Relation, labels(p) as Labels";	
		} else {
			queryRelations="match (n:"+nodeId+")-[r]->(p) return distinct type(r) As Relation, labels(p) as Labels, 'OUTGOING' as Direction union MATCH (n:"+nodeId+")<-[k]-(s) return distinct type(k) as Relation, labels(s) as Labels, 'INGOING' as Direction";
		}
		Result result = neo4jOperations.query(queryRelations, new HashMap<String, String>());
		
		return result;
	}
	
	public Result getAllRelationsOfNodeType(String nodeId){
		String queryRelations="match (n:"+nodeId+")-[r]->(p) return distinct type(r) As Relation, labels(p) as Labels, 'OUTGOING' as Direction union MATCH (n:"+nodeId+")<-[k]-(s) return distinct type(k) as Relation, labels(s) as Labels, 'INGOING' as Direction";
		Result result = neo4jOperations.query(queryRelations, new HashMap<String, String>());	
		return result;
	}
	
}
