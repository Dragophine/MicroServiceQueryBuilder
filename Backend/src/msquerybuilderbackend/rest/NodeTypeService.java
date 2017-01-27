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

import msquerybuilderbackend.business.ExpertQueryBusiness;
import msquerybuilderbackend.business.NodeTypeBusiness;
import msquerybuilderbackend.entity.ListEntity;
import msquerybuilderbackend.repository.AlertRepository;


/** Service class/controller for NodeType REST services
* @author drago
*
*/
@RestController
public class NodeTypeService {

	
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	@Autowired
	NodeTypeBusiness nodeTypeBusiness;
	
	/**
	 * method which gets the GET-request and calls the NodeTypeBusiness method
	 * queries all nodetypes from the neo4j database
	 * @return all nodetypes as Result
	 */
	@CrossOrigin
	@RequestMapping(value="/nodetypes", method=RequestMethod.GET)
	public ResponseEntity<Result> getNodeTypes() throws Exception {
		return new ResponseEntity<Result>(nodeTypeBusiness.getNodeTypes(), HttpStatus.OK);
	}
	
	/**
	 * method which gets the GET-request and calls the NodeTypeBusiness method
	 * queries all nodetypes from the neo4j database with a specific relationship in a certain direction with a specific nodetype on the other side
	 * @return all nodetypes which fulfill the criteria
	 */
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/{relationship}/{direction}", method=RequestMethod.GET)
	public ResponseEntity<Result> getNodeTypes(@PathVariable String nodeId, @PathVariable String relationship, @PathVariable String direction) throws Exception {
		return new ResponseEntity<Result>(nodeTypeBusiness.getNodeTypesWithCertainRelationshipAndDirection(nodeId, relationship, direction), HttpStatus.OK);
	}
	
	/**
	 * method which gets the GET-request and calls the NodeTypeBusiness method
	 * queries all nodetypes from the neo4j database with a specific relationship with a specific nodetype on the other side
	 * @return all nodetypes which fulfill the criteria
	 */
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/{relationship}", method=RequestMethod.GET)
	public ResponseEntity<Result> getNodeTypes(@PathVariable String nodeId, @PathVariable String relationship) throws Exception {		
		return new ResponseEntity<Result>(nodeTypeBusiness.getNodeTypesWithCertainRelationship(nodeId, relationship), HttpStatus.OK);
	}
	
	/**
	 * method which gets the GET-request and calls the NodeTypeBusiness method
	 * queries all keys/attributes of a specific nodetype
	 * @return all keys of the nodetype
	 */
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/keys", method=RequestMethod.GET)
	public ResponseEntity<Result> getKeys(@PathVariable String nodeId) throws Exception {
		return new ResponseEntity<Result>(nodeTypeBusiness.getKeysOfCertainNodeType(nodeId), HttpStatus.OK);
	}
	
	/**
	 * method which gets the GET-request and calls the NodeTypeBusiness method
	 * queries all relationship types from the neo4j database of a specific node type in a certain direction
	 * @return all relationship types of the certain node type in the defined direction
	 */
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/relations/{direction}", method=RequestMethod.GET)
	public ResponseEntity<Result> getRelations(@PathVariable String nodeId, @PathVariable String direction) throws Exception {	
		return new ResponseEntity<Result>(nodeTypeBusiness.getRelationsOfNodeTypeWithDirection(nodeId, direction), HttpStatus.OK);
	}
	
	/**
	 * method which gets the GET-request and calls the NodeTypeBusiness method
	 * queries all relationship types from the neo4j database of a specific nodetype
	 * @return all relationship types of the node type
	 */
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/relations", method=RequestMethod.GET)
	public ResponseEntity<Result> getRelations(@PathVariable String nodeId) throws Exception {		
		return new ResponseEntity<Result>(nodeTypeBusiness.getAllRelationsOfNodeType(nodeId), HttpStatus.OK);
	}
	
	
}
