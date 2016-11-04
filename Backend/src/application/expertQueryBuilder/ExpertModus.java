package application.expertQueryBuilder;

import java.util.HashMap;


import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class ExpertModus {
	  @Autowired
		Neo4jOperations neo4jOperations;
		Neo4jTemplate temp;

	    
	  //  @RequestMapping(value="/expertModus", method=RequestMethod.GET)
		@CrossOrigin 
		//CrossOrigin request allow to call a different server from
		//a certain frontend hosted on a certain address.
		//This means that the adress of the server and 
		//the adress of client must not be the same.
		//Pleas add @CrossOrigin to every request.
	    @RequestMapping(value="/expertModus")
	    public Result expertModus(@RequestParam(value="query", defaultValue="Match (n) return distinct labels(n)") String query) {

		Result result = neo4jOperations.query(query, new HashMap<String, String>());
		
		return result;
	    }
}
