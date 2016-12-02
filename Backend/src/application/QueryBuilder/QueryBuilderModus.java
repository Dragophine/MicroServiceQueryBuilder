package application.QueryBuilder;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import application.entity.ExpertQuery;
import application.entity.Parameter;
import application.entity.QueryBuilder;
import application.repository.ExpertQueryRepository;
import application.repository.ParameterRepository;

@RestController
public class QueryBuilderModus {
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	@Autowired
	ExpertQueryRepository expertQueryRepository;
	@Autowired
	ParameterRepository parameterRepository;
	
	
	
	@RequestMapping(value="/buildQuery",  method=RequestMethod.POST)
//	public ResponseEntity<Result> buildQuery(@RequestBody QueryBuilder queryBuilder) throws Exception {
	public QueryBuilder buildQuery(@RequestBody QueryBuilder queryBuilder) throws Exception {
	Map<String,Object> paramsMap = new HashMap<String,Object>();
	Result result=null;
	

//	return new ResponseEntity<Result>(result, HttpStatus.OK);
	return queryBuilder;
	}
}
