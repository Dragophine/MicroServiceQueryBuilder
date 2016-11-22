
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

import application.entity.ListEntity;

@RestController
public class ListRelations {
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
		
	@CrossOrigin
	@RequestMapping(value="/listRelations", method=RequestMethod.POST)
	public ResponseEntity<Result> listNodes(@RequestBody ListEntity entity) throws Exception {
		
		String queryRelations = "MATCH (n:" + entity.getLabel() + ") -[r] -> () return distinct type(r) AS Relation";

		Result result = neo4jOperations.query(queryRelations, new HashMap<String, String>());
		
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
}

