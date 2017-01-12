package msquerybuilderbackend.rest;

import java.util.HashMap;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import msquerybuilderbackend.entity.ListEntity;
import msquerybuilderbackend.repository.AlertRepository;

@RestController
public class NodeTypeService {

	
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	
	
	
	@CrossOrigin
	@RequestMapping(value="/nodetypes", method=RequestMethod.GET)
	public ResponseEntity<Result> getNodeTypes() throws Exception {

		
		String queryNodes = "MATCH (n) WHERE labels(n) <> \"ExpertQuery\" AND labels(n) <> \"Parameter\" AND labels(n)<>\"Alert\" AND labels(n)<>\"Category\" AND labels(n) <> \"QueryBuilder\" AND labels(n) <> \"Node\" AND labels(n) <> \"FilterAttribute\" AND labels(n) <> \"ReturnAttribute\" AND labels(n) <> \"OrderByAttribute\" AND labels(n) <> \"Relationship\" return DISTINCT labels(n) AS Label";
	
		Result result = neo4jOperations.query(queryNodes, new HashMap<String, String>());
	
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
	
	
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/{relationship}/{direction}", method=RequestMethod.GET)
	public ResponseEntity<Result> getNodeTypes(@PathVariable String nodeId, @PathVariable String relationship, @PathVariable String direction) throws Exception {
		String queryNodes="";
		if (direction.equals("ingoing")){
			 queryNodes = "MATCH (n:" + nodeId + ") <-["+relationship+"]- (p) return distinct labels(p) AS Label";
			 
		} else {
			queryNodes = "MATCH (n:" + nodeId + ") -["+relationship+"] -> (p) return distinct labels(p) AS Label";
		}
		

		Result result = neo4jOperations.query(queryNodes, new HashMap<String, String>());
		
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
	
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/{relationship}", method=RequestMethod.GET)
	public ResponseEntity<Result> getNodeTypes(@PathVariable String nodeId, @PathVariable String relationship) throws Exception {
		String	queryNodes = "MATCH (n:" + nodeId + ") -[f:"+relationship+"] -> (p) return distinct labels(p) AS Label";
		Result result = neo4jOperations.query(queryNodes, new HashMap<String, String>());
		
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
	
	
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/keys", method=RequestMethod.GET)
	public ResponseEntity<Result> getKeys(@PathVariable String nodeId) throws Exception {
		
		String queryKeys = "MATCH (n:" + nodeId + ") UNWIND keys(n) AS key " +
						 "WITH key ORDER BY key RETURN  COLLECT(distinct key) as Keys";

		Result result = neo4jOperations.query(queryKeys, new HashMap<String, String>());
		
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
	
	
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/relations/{direction}", method=RequestMethod.GET)
	public ResponseEntity<Result> getRelations(@PathVariable String nodeId, @PathVariable String direction) throws Exception {
		String queryRelations="";
		
		
		if (direction.equals("ingoing")||direction.equals("INGOING")){
			queryRelations = "MATCH (n:" + nodeId + ")<-[r]- (p) return distinct type(r) AS Relation, labels(p) as Labels";	
		} else if (direction.equals("outgoing") || direction.equals("OUTGOING")) {
			 queryRelations = "MATCH (n:" + nodeId + ") -[r] -> (p) return distinct type(r) AS Relation, labels(p) as Labels";	
		} else {
			queryRelations="match (n:"+nodeId+")-[r]->(p) return distinct type(r) As Relation, labels(p) as Labels, 'OUTGOING' as Direction union MATCH (n:"+nodeId+")<-[k]-(s) return distinct type(k) as Relation, labels(s) as Labels, 'INGOING' as Direction";
		}
		
		

		Result result = neo4jOperations.query(queryRelations, new HashMap<String, String>());
		
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
	
	
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/relations", method=RequestMethod.GET)
	public ResponseEntity<Result> getRelations(@PathVariable String nodeId) throws Exception {
		String queryRelations="match (n:"+nodeId+")-[r]->(p) return distinct type(r) As Relation, labels(p) as Labels, 'OUTGOING' as Direction union MATCH (n:"+nodeId+")<-[k]-(s) return distinct type(k) as Relation, labels(s) as Labels, 'INGOING' as Direction";
		Result result = neo4jOperations.query(queryRelations, new HashMap<String, String>());
		
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
	
	
}
