
package application.request;

import java.util.HashMap;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import msquerybuilderbackend.entity.ListEntity;

@RestController
public class ListNodes {
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
		
	@CrossOrigin
	@RequestMapping(value="/listNodes", method=RequestMethod.GET)
	public ResponseEntity<Result> listNodes() throws Exception {

		
		String queryNodes = "MATCH (n) WHERE labels(n) <> \"ExpertQuery\" AND labels(n) <> \"Parameter\" return DISTINCT labels(n) AS Label";
	
		Result result = neo4jOperations.query(queryNodes, new HashMap<String, String>());
	
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
	
	
	@CrossOrigin
	@RequestMapping(value="/listNodes", method=RequestMethod.POST)
	public ResponseEntity<Result> listNodes(@RequestBody ListEntity entity) throws Exception {
		String queryNodes="";
		if (entity.getDirection().equals("ingoing")){
			 queryNodes = "MATCH (n:" + entity.getLabel() + ") <-["+entity.getRelationship()+"]- (p) return distinct labels(p) AS Label";
			 
		} else {
			queryNodes = "MATCH (n:" + entity.getLabel() + ") -["+entity.getRelationship()+"] -> (p) return distinct labels(p) AS Label";
		}
		

		Result result = neo4jOperations.query(queryNodes, new HashMap<String, String>());
		
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
}