package application.QueryBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

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

import msquerybuilderbackend.entity.FilterAttribute;
import msquerybuilderbackend.entity.Node;
import msquerybuilderbackend.entity.OrderByAttribute;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.entity.QueryBuilder;
import msquerybuilderbackend.entity.QueryBuilderStringObject;
import msquerybuilderbackend.entity.Relationship;
import msquerybuilderbackend.entity.ReturnAttribute;
import msquerybuilderbackend.repository.ExpertQueryRepository;
import msquerybuilderbackend.repository.ParameterRepository;

@RestController
public class QueryBuilderModus {
	@Autowired
	Neo4jOperations neo4jOperations;
	Neo4jTemplate temp;
	@Autowired
	ExpertQueryRepository expertQueryRepository;
	@Autowired
	ParameterRepository parameterRepository;
	
	//required global variables
	Map<String,String> synonyms = new HashMap<String,String>();
	Map<String,Object> paramsMap = new HashMap<String,Object>();
	char synonym='a';
	char end='z';
	Set<Parameter> parameter = new HashSet<Parameter>(0);
	String query = "";
	LinkedList<String> filterStatements = new LinkedList<String>();
	LinkedList<String> orderStatements = new LinkedList<String>();
	LinkedList<String> returnStatements = new LinkedList<String>();
	LinkedList<String> cypherquery = new LinkedList<String>();
	
	
	
	//TODO: rekursiv gestalten; dabei darauf achten, dass orderby, filter und returnattribute (in filterstring, returnstring, orderstring) rekursiv angepasst werden bzw. die strings erweitert werden
	//TODO: bei Rekursion auch darauf achten, dass die vergabe der synonyme (mit variable synonym) und das einschreiben in die dafür vorgesehene map (synonyms) passiert
	//TODO: Bei Rekursion darauf acht geben, dass die ParamsMap weiterhin ausgefüllt wird
	//TODO: darauf acht geben, dass in der paramsMap nix doppelt eingetragen is als Key. 
	//TODO: darauf acht geben, dass Synonyme nicht mehrfach vergeben werden
	@CrossOrigin 
	
	@RequestMapping(value="/buildQuery",  method=RequestMethod.POST)
	public ResponseEntity<Result> buildQuery(@RequestBody QueryBuilder queryBuilder) throws Exception {
//	public String buildQuery(@RequestBody QueryBuilder queryBuilder) throws Exception {
		filterStatements.clear();
		orderStatements.clear();
		returnStatements.clear();
		cypherquery.clear();
		synonyms.clear();
		synonym = 'a';
		String query = "";
		
		QueryBuilderStringObject queryBuilderStringObject = new QueryBuilderStringObject();
		
		Node node = queryBuilder.getNode();
		
		//TODO erste relation auf optional prüfen!!
		buildNode(node, "");
		
		Iterator<String> it = cypherquery.iterator();
		while (it.hasNext()){
			query += "MATCH " + it.next();
			if (it.hasNext()) query += " ";
		}
		
		if (!filterStatements.isEmpty()) query += " WHERE ";
		
		//TODO funktioniert momentan nur mit AND!! Vllt benötigen wir ein neues attribut im json objekt?!
		it = filterStatements.iterator();
		while (it.hasNext()){
			query += it.next();
			if (it.hasNext()) query += " AND ";
		}
		
		query += " RETURN";
		
		it = returnStatements.iterator();
		while (it.hasNext()){
			query += it.next();
			if (it.hasNext()) query += ",";
		}
		
		if (!orderStatements.isEmpty()) query += " ORDER BY ";
		
		it = orderStatements.iterator();
		while (it.hasNext()){
			query += it.next();
			if (it.hasNext()) query += ", ";
		}
		
		if (queryBuilder.getLimitCount() != "") {
			query += " LIMIT " + queryBuilder.getLimitCount();
		}
		
//		return query;

		
		
		//testen des results
		Result result=null;
		result= neo4jOperations.query(query, paramsMap,true);	
		return new ResponseEntity<Result>(result, HttpStatus.OK);
		
		
		
/*		Node firstNode = queryBuilder.getNode();

		String queryString="MATCH ("+synonym+":"+firstNode.getType()+")";

		
		synonyms.put(firstNode.getType(),String.valueOf(synonym));		
		synonym++;
		
		String finalstring="";
		
		
	
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
				
//				String teststring= String.format(queryString, relationship, filterstring);
//				finalstring=teststring+finalstring;
				
			}
		} 
	
	String teststring= String.format(queryString, relationship, filterstring);
	finalstring=teststring+finalstring;	
		//Result result = neo4jOperations.query(queryBuilderStringObject.getQueryString(),queryBuilderStringObject.getParamsMap(), true);
	

//	return new ResponseEntity<Result>(result, HttpStatus.OK);
	finalstring=finalstring+returnstring+orderstring;
	return finalstring;
	
	*/
	}
	
	
	private QueryBuilderStringObject buildQueryRecursive(QueryBuilderStringObject qb){	
		return qb;
	}
	
	
	private void buildNode(Node node, String s){
		
		String doublePoint = "";
		if (node.getType() != "") doublePoint = ":";
		
		String nodeString = s + "(" + synonym + doublePoint + node.getType() + ")";
		
		synonyms.put(node.getType(),String.valueOf(synonym));		
		synonym++;
		
		//filter zusammenstellen
		if (!node.getFilterAttributes().isEmpty()){
			solveFilter(node.getFilterAttributes(), node.getType());
		}
		
		//order zusammenstellen
		if (!node.getOrderByAttributes().isEmpty()){
			solveOrder(node.getOrderByAttributes(), node.getType());
		}
		
		//return zusammenstellen
		if (!node.getReturnAttributes().isEmpty()){
			solveReturn(node.getReturnAttributes(), node.getType());
		}
		
		if (node.getRelationship().isEmpty()){
			cypherquery.add(nodeString);
			return;
		} else {
			Iterator<Relationship> it = node.getRelationship().iterator();
			while (it.hasNext()){
				buildRelation(it.next(), nodeString);
			}
			return;
			//return nodeString + buildRelation(node.getRelationship().iterator().next());
		}	
	}
	
	
	private void buildRelation(Relationship relation, String s){
		String relationship = "";
		
		if (relation.getDirection().equalsIgnoreCase("outgoing")){
			relationship = "-[" + synonym + ":" + relation.getRelationshipType() + "]->";
		} else {
			relationship = "<-[" + synonym + ":" + relation.getRelationshipType()+ "]-";
		}
		synonyms.put(relation.getRelationshipType(),String.valueOf(synonym));		
		synonym++;
		
		//filter zusammenstellen
		if (!relation.getFilterAttributes().isEmpty()){
			solveFilter(relation.getFilterAttributes(), relation.getRelationshipType());
		}
		
		//order zusammenstellen
		if (!relation.getOrderByAttributes().isEmpty()){
			solveOrder(relation.getOrderByAttributes(), relation.getRelationshipType());
		}
		
		//return zusammenstellen
		if (!relation.getReturnAttributes().isEmpty()){
			solveReturn(relation.getReturnAttributes(), relation.getRelationshipType());
		}
		
		if (relation.getNode() == null){
			cypherquery.add(s + relationship);
			return;
		} else {
			
			buildNode(relation.getNode(), s + relationship);
			return;// relationship + buildNode(relation.getNode());
		}	
	}
	
	private void solveFilter(Set<FilterAttribute> filterSet, String type){
		int i = 1;
		
		for (FilterAttribute f : filterSet){
			
			String paramName = type + f.getAttributeName() + i;
			i++;
			
			Parameter newParam = new Parameter();
			newParam.setChangeable(f.getFilter().getChangeable());
			newParam.setKey(paramName);
			newParam.setValue(f.getFilter().getValue());
			newParam.setType(f.getFilter().getType());
			parameter.add(newParam);
			paramsMap.put(paramName, f.getFilter().getValue());
			
			filterStatements.add(synonyms.get(type) + "." + f.getAttributeName() + f.getFilter().getFilterType() + "{" + paramName + "}");
		}
	}
	
	private void solveOrder(Set<OrderByAttribute> obSet, String type){
		for (OrderByAttribute o : obSet){
			String dir = "";
			if (o.getDirection() != ""){
				dir = " " + o.getDirection();
			}
			orderStatements.add(synonyms.get(type) + "." + o.getAttributeName() + dir);
		}
	}
	
	private void solveReturn (Set<ReturnAttribute> retSet, String type){
		for (ReturnAttribute r : retSet){
			returnStatements.add(r.getReturnType() + " " + synonyms.get(type) + "." + r.getAttributeName() + " AS " +
								 type + r.getAttributeName());
		}
	}
	
}

