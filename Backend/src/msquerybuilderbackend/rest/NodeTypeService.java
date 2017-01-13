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

@RestController
public class NodeTypeService {

	
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	@Autowired
	NodeTypeBusiness nodeTypeBusiness;
	
	
	@CrossOrigin
	@RequestMapping(value="/nodetypes", method=RequestMethod.GET)
	public ResponseEntity<Result> getNodeTypes() throws Exception {
		return new ResponseEntity<Result>(nodeTypeBusiness.getNodeTypes(), HttpStatus.OK);
	}
	
	
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/{relationship}/{direction}", method=RequestMethod.GET)
	public ResponseEntity<Result> getNodeTypes(@PathVariable String nodeId, @PathVariable String relationship, @PathVariable String direction) throws Exception {
		return new ResponseEntity<Result>(nodeTypeBusiness.getNodeTypesWithCertainRelationshipAndDirection(nodeId, relationship, direction), HttpStatus.OK);
	}
	
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/{relationship}", method=RequestMethod.GET)
	public ResponseEntity<Result> getNodeTypes(@PathVariable String nodeId, @PathVariable String relationship) throws Exception {		
		return new ResponseEntity<Result>(nodeTypeBusiness.getNodeTypesWithCertainRelationship(nodeId, relationship), HttpStatus.OK);
	}
	
	
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/keys", method=RequestMethod.GET)
	public ResponseEntity<Result> getKeys(@PathVariable String nodeId) throws Exception {
		return new ResponseEntity<Result>(nodeTypeBusiness.getKeysOfCertainNodeType(nodeId), HttpStatus.OK);
	}
	
	
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/relations/{direction}", method=RequestMethod.GET)
	public ResponseEntity<Result> getRelations(@PathVariable String nodeId, @PathVariable String direction) throws Exception {	
		return new ResponseEntity<Result>(nodeTypeBusiness.getRelationsOfNodeTypeWithDirection(nodeId, direction), HttpStatus.OK);
	}
	
	
	@CrossOrigin
	@RequestMapping(value="/nodetypes/{nodeId}/relations", method=RequestMethod.GET)
	public ResponseEntity<Result> getRelations(@PathVariable String nodeId) throws Exception {		
		return new ResponseEntity<Result>(nodeTypeBusiness.getAllRelationsOfNodeType(nodeId), HttpStatus.OK);
	}
	
	
}
