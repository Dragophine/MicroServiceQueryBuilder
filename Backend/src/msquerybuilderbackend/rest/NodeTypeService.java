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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import msquerybuilderbackend.business.ExpertQueryBusiness;
import msquerybuilderbackend.business.NodeTypeBusiness;
import msquerybuilderbackend.entity.ListEntity;
import msquerybuilderbackend.repository.AlertRepository;


/** Service class/controller for NodeType REST services
* @author drago
*
*/
@RestController
@Api(tags = {"NodeTypeService"}, value = "Service for viewing NodeTypes from the database")
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
	@ApiOperation(value = "Results all NodeTypes from the database",
	notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
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
	@ApiOperation(value = "Returns all NodeTypes from the database with a specific relationship in a specific direction and a specific ohter NodeType on the other end of the relationship",
	notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
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
	@ApiOperation(value = "Returns all Nodetypes from the database with a specific relationship and a specific other NodeType on the other end of the relationship",
	notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
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
	@ApiOperation(value = "Returns the keys/attributes of a specific NodeType from the database",
	notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
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
	@ApiOperation(value = "Returns all relationship Types from the database of a specific NodeType in a certain direction",
	notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
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
	@ApiOperation(value = "Returns all relationship Types from the databse of a specific NodeType",
	notes = "place for notes", response = Result.class, responseContainer="ResponseEntity")
	public ResponseEntity<Result> getRelations(@PathVariable String nodeId) throws Exception {		
		return new ResponseEntity<Result>(nodeTypeBusiness.getAllRelationsOfNodeType(nodeId), HttpStatus.OK);
	}
	
	
}
