
package application.request;

import java.util.HashMap;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
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
public class ListRelations {
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
		
	@CrossOrigin
	@RequestMapping(value="/listRelations", method=RequestMethod.POST)
	public ResponseEntity<Result> listRelations(@RequestBody ListEntity entity) throws Exception {
		String queryRelations="";
		
		
		if (entity.getDirection().equals("ingoing")){
			queryRelations = "MATCH (n:" + entity.getLabel() + ")<-[r]- () return distinct type(r) AS Relation";	
		} else if (entity.getDirection().equals("outgoing")) {
			 queryRelations = "MATCH (n:" + entity.getLabel() + ") -[r] -> () return distinct type(r) AS Relation";	
		} else {
			queryRelations="match (n:"+entity.getLabel()+")-[r]->() return distinct type(r) As Relation, 'OUTGOING' as Direction union OPTIONAL MATCH (n:"+entity.getLabel()+")<-[k]-() return distinct type(k) as Relation, 'INGOING' as Direction";
		}
		

		Result result = neo4jOperations.query(queryRelations, new HashMap<String, String>());
		
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
	
	@CrossOrigin
	@RequestMapping(value="/listRelationsWithNodes", method=RequestMethod.POST)
	public ResponseEntity<Result> listRelationsWithNodes(@RequestBody ListEntity entity) throws Exception {
		String queryRelations="";
		
		
		if (entity.getDirection().equals("ingoing")){
			queryRelations = "MATCH (n:" + entity.getLabel() + ")<-[r]- (p) return distinct type(r) AS Relation, labels(p) as Labels";	
		} else if (entity.getDirection().equals("outgoing")) {
			 queryRelations = "MATCH (n:" + entity.getLabel() + ") -[r] -> (p) return distinct type(r) AS Relation, labels(p) as Labels";	
		} else {
			queryRelations="match (n:"+entity.getLabel()+")-[r]->(p) return distinct type(r) As Relation, labels(p) as Labels, 'OUTGOING' as Direction union OPTIONAL MATCH (n:"+entity.getLabel()+")<-[k]-(s) return distinct type(k) as Relation, labels(s) as Labels, 'INGOING' as Direction";
		}
		

		Result result = neo4jOperations.query(queryRelations, new HashMap<String, String>());


		
		return new ResponseEntity<Result>(result, HttpStatus.OK);
	}
}

