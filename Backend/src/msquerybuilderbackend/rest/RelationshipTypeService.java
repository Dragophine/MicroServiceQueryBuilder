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

import msquerybuilderbackend.business.RelationshipTypeBusiness;

/** Service class/controller for RelationshipType REST services
* @author drago
*
*/
@RestController
public class RelationshipTypeService {
	
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	
	@Autowired
	RelationshipTypeBusiness relationshipTypeBusiness;
	
	/**
	 * method which gets the GET-request and calls the RelationshipTypeBusiness method
	 * queries all relationship types from the neo4j database
	 * @return all relationship types as Result
	 */
	@CrossOrigin
	@RequestMapping(value="/relationshiptypes", method=RequestMethod.GET)
	public ResponseEntity<Result> getRelationshipTypes() throws Exception {
		return new ResponseEntity<Result>(relationshipTypeBusiness.getAllRelationshipTypes(), HttpStatus.OK);
	}
	
	/**
	 * method which gets the GET-request and calls the RelationshipTypeBusiness method
	 * queries the keys/attributes of a specific relationship type from the neo4j database
	 * @return the keys of the relationship type
	 */
	@CrossOrigin
	@RequestMapping(value="/relationshiptypes/{relationId}/keys", method=RequestMethod.GET)
	public ResponseEntity<Result> getKeys(@PathVariable String relationId) throws Exception {	
		return new ResponseEntity<Result>(relationshipTypeBusiness.getKeysOfCertainRelationshipType(relationId), HttpStatus.OK);
	}
	
}
