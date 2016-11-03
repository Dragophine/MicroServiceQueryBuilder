package tutorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.neo4j.ogm.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }  
    
    @Autowired
	Neo4jOperations neo4jOperations;
   
    
    @RequestMapping("/testCypher")
    public Result testCypher() {

    String testGraph = "Match (n) return distinct labels(n)";
	Result result = neo4jOperations.query(testGraph, new HashMap<String,String>());
	return result;
    }
    
    @RequestMapping("/testNode")
    public Result testNode(@RequestParam(value="label", defaultValue="Service") String label) {
    	
    String testGraph = "Match (n:"+label+") return distinct keys(n)";
	Result result = neo4jOperations.query(testGraph, new HashMap<String, String>());
	
	return result;
    }
}
