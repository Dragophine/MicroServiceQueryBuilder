package application.QueryBuilder;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import application.entity.FilterAttribute;
import application.entity.Node;
import application.entity.OrderByAttribute;
import application.entity.Parameter;
import application.entity.QueryBuilder;
import application.entity.QueryBuilderStringObject;
import application.entity.Relationship;
import application.entity.ReturnAttribute;
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
	
	
	
	//TODO: rekursiv gestalten; dabei darauf achten, dass orderby, filter und returnattribute (in filterstring, returnstring, orderstring) rekursiv angepasst werden bzw. die strings erweitert werden
	//TODO: bei Rekursion auch darauf achten, dass die vergabe der synonyme (mit variable synonym) und das einschreiben in die dafür vorgesehene map (synonyms) passiert
	//TODO: Bei Rekursion darauf acht geben, dass die ParamsMap weiterhin ausgefüllt wird
	//TODO: darauf acht geben, dass in der paramsMap nix doppelt eingetragen is als Key. 
	//TODO: darauf acht geben, dass Synonyme nicht mehrfach vergeben werden
	@RequestMapping(value="/buildQuery",  method=RequestMethod.POST)
//	public ResponseEntity<Result> buildQuery(@RequestBody QueryBuilder queryBuilder) throws Exception {
	public String buildQuery(@RequestBody QueryBuilder queryBuilder) throws Exception {
		QueryBuilderStringObject queryBuilderStringObject = new QueryBuilderStringObject();
		Map<String,String> synonyms = new HashMap<String,String>();
		Node firstNode = queryBuilder.getNode();
		char synonym='a';
		char end='z';
		String queryString="MATCH ("+synonym+":"+firstNode.getType()+")";
		synonyms.put(firstNode.getType(),String.valueOf(synonym));		
		synonym++;
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		String finalstring="";
		
		Set<Parameter> parameter = new HashSet<Parameter>(0);
	
		queryString=queryString+" %s ";
		
		String relationship="";
		String filterstring="";
		String returnstring="";
		String orderstring="";
		boolean filter=false;
		boolean order=false;
		boolean returna=false;
		if (!firstNode.getFilterAttributes().isEmpty()){
			filter=true;
			queryString=queryString+" where ";
			int i=1;
			for (FilterAttribute f:firstNode.getFilterAttributes()){
				if (i>1) queryString=queryString+" and ";
				String paramName = firstNode.getType()+f.getAttributeName()+i;
				i++;
				Parameter newParam = new Parameter();
				newParam.setChangeable(f.getChangeable());
				newParam.setKey(paramName);
				newParam.setValue(f.getValue());
				newParam.setType(f.getType());
				parameter.add(newParam);
				paramsMap.put(paramName, f.getValue());
				
				queryString=queryString+ synonyms.get(firstNode.getType())+"."+f.getAttributeName()+f.getFilterType()+"{"+paramName+"} ";
			}
			queryString=queryString+" %s ";
		}
		
		if (!firstNode.getReturnAttributes().isEmpty()){
			returna=true;
			returnstring = returnstring+" return ";
			int i=1;
			for (ReturnAttribute r:firstNode.getReturnAttributes()){
				if (i>1)returnstring=returnstring+", ";
				returnstring=returnstring+r.getReturnType()+" " +synonyms.get(firstNode.getType())+"."+r.getAttributeName();
			}
			
		}
		
		if (!firstNode.getOrderByAttributes().isEmpty()){
			order=true;
			orderstring = orderstring+" order by ";
			int i=1;
			for (OrderByAttribute o:firstNode.getOrderByAttributes()){
				if (i>1)	orderstring=orderstring+", ";
				orderstring=orderstring+synonyms.get(firstNode.getType())+"."+o.getAttributeName()+" "+o.getDirection();
			}
		
		}
		
		

	if (!firstNode.getRelationship().isEmpty()){
			
			for (Relationship r:firstNode.getRelationship()){
				
				if (r.getDirection().equals("outgoing")){
					relationship="-["+synonym+":"+r.getRelationshipType()+"]->";
				}else{
					relationship="<-["+synonym+":"+r.getRelationshipType()+"]-";
				}
				synonyms.put(r.getRelationshipType(),String.valueOf(synonym));		
				synonym++;
				
				
				relationship=relationship+"("+synonym+":"+r.getNode().getType()+") ";
				synonyms.put(r.getNode().getType(), String.valueOf(synonym));
				synonym++;
				
				
				
				if (!r.getFilterAttributes().isEmpty()){
					
					if (filter) filterstring=filterstring+" and ";
					if (!filter) filterstring=filterstring+" where ";
					int i=1;
					for (FilterAttribute f:r.getFilterAttributes()){
						
						if (i>1) filterstring=filterstring+" and ";
						String paramName = r.getRelationshipType()+f.getAttributeName()+i;
						i++;
						Parameter newParam = new Parameter();
						newParam.setChangeable(f.getChangeable());
						newParam.setKey(paramName);
						newParam.setValue(f.getValue());
						newParam.setType(f.getType());
						parameter.add(newParam);
						paramsMap.put(paramName, f.getValue());
						
						filterstring=filterstring+ synonyms.get(r.getRelationshipType())+"."+f.getAttributeName()+f.getFilterType()+"{"+paramName+"} ";
					}
				}
				
				
				if (!r.getReturnAttributes().isEmpty()){
					
					if (returna) returnstring=returnstring+" and ";
					if (!returna) returnstring=returnstring+" return ";
					int i=1;
					for (ReturnAttribute ra:r.getReturnAttributes()){
						if (i>1)returnstring=returnstring+", ";
						returnstring=returnstring+ra.getReturnType()+" " +synonyms.get(r.getRelationshipType())+"."+ra.getAttributeName();
					}
					
				}
				
				
				
				if (!r.getOrderByAttributes().isEmpty()){
					
					if (order) orderstring=orderstring+" and ";
					if (!order) orderstring=orderstring+" order by ";
					
					int i=1;
					for (OrderByAttribute o:r.getOrderByAttributes()){
						if (i>1)	orderstring=orderstring+", ";
						orderstring=orderstring+synonyms.get(r.getRelationshipType())+"."+o.getAttributeName()+" "+o.getDirection();
					}
					
				}
				
				String teststring= String.format(queryString, relationship, filterstring);
				finalstring=teststring+finalstring;
				
			}
		} 
	
		
		
		//Result result = neo4jOperations.query(queryBuilderStringObject.getQueryString(),queryBuilderStringObject.getParamsMap(), true);
	

//	return new ResponseEntity<Result>(result, HttpStatus.OK);
	finalstring=finalstring+returnstring+orderstring;
	return finalstring;
	}
	
	
	private QueryBuilderStringObject buildQueryRecursive(QueryBuilderStringObject qb){
		
		
		return qb;
	}
	
}
