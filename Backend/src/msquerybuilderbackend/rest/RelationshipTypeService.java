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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RelationshipTypeService {
	
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	
	
	@CrossOrigin
	@RequestMapping(value="/relationshiptypes", method=RequestMethod.GET)
	public ResponseEntity<Result> getRelationshipTypes() throws Exception {

		
		String queryRelations = "MATCH ()-[r]->() where type(r)<>\"HAS_PARAMETER\" and type(r)<>\"HAS_CATEGORY\" return distinct type(r) AS Relationship";
	
		Result result = neo4jOperations.query(queryRelations, new HashMap<String, String>());
	
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
	
	@CrossOrigin
	@RequestMapping(value="/relationshiptypes/{relationId}/keys", method=RequestMethod.GET)
	public ResponseEntity<Result> getKeys(@PathVariable String relationId) throws Exception {
		
		String queryKeys = "MATCH ()-[n:"+relationId+"]->() UNWIND keys(n) AS key " +
						 "WITH key ORDER BY key RETURN  COLLECT(distinct key) as Keys";

		Result result = neo4jOperations.query(queryKeys, new HashMap<String, String>());
		
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
	
}
