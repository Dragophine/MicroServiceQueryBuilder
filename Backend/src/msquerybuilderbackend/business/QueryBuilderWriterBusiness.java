package msquerybuilderbackend.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msquerybuilderbackend.entity.ExpertQuery;
import msquerybuilderbackend.entity.FilterAttribute;
import msquerybuilderbackend.entity.Filters;
import msquerybuilderbackend.entity.Node;
import msquerybuilderbackend.entity.OrderByAttribute;
import msquerybuilderbackend.entity.Parameter;
import msquerybuilderbackend.entity.QueryBuilder;
import msquerybuilderbackend.entity.Relationship;
import msquerybuilderbackend.entity.ReturnAttribute;

public class QueryBuilderWriterBusiness {
	private QueryBuilder queryBuilder;
	private String queryString;
	private Map<String,Object> paramsMap;
	private char synonym;
	private char end;
	private Map<String,String> synonyms;
	private boolean distinct = false;
	private String query;
	private LinkedList<String> filterStatements;
	private LinkedList<String> actualFilterStatements;
	private LinkedList<String> orderStatements;
	private LinkedList<String> returnStatements;
	private LinkedList<String> cypherquery;
	private Node node;
	private ExpertQuery expertQuery;
	
	public QueryBuilderWriterBusiness(){
		
	}
	
	public ExpertQuery writeQueryBuilderString(QueryBuilder qb) throws Exception{
	queryBuilder = qb;
	expertQuery = new ExpertQuery();
	synonyms= new HashMap<String,String>();
	paramsMap= new HashMap<String,Object>();
	filterStatements = new LinkedList<String>();
	actualFilterStatements = new LinkedList<String>();
	orderStatements = new LinkedList<String>();
	returnStatements = new LinkedList<String>();
	cypherquery = new LinkedList<String>();
	synonyms= new HashMap<String,String>();
	synonym = 'a';
	end='z';
	query = "";
	distinct = queryBuilder.getDistinct();
	 node = queryBuilder.getNode();
	
	//TODO erste relation auf optional prüfen!!
	//build the query
	buildNode(node, "", expertQuery);
	
	Iterator<String> it = cypherquery.iterator();
	while (it.hasNext()){
		query += "MATCH " + it.next();
		if (it.hasNext()) query += " ";
	}
	
	//Return-Clause muss vorhanden sein!!
	query += " RETURN";
	if (distinct) query += " DISTINCT ";
	
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
	
	//erstellen des results
//	Result result = null;
//	result = neo4jOperations.query(query, paramsMap, true);
	expertQuery.setQuery(query);
	return expertQuery;
}

	
	private void buildNode(Node node, String s, ExpertQuery expertQuery) throws Exception{
		
		String doublePoint = "";
		if (node.getType() != "") doublePoint = ":";
		
		String nodeString = s + "(" + synonym + doublePoint + node.getType() + ")";
		
		synonyms.put(node.getType(),String.valueOf(synonym));		
		synonym++;
		
		//filter zusammenstellen
		if (!node.getFilterAttributes().isEmpty()){
			solveFilter(node.getFilterAttributes(), node.getType(), expertQuery);
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
			
			if (!filterStatements.isEmpty()) nodeString += " WHERE ";
			Iterator<String> it = filterStatements.iterator();
			while (it.hasNext()){
				nodeString += it.next();
			}
			cypherquery.add(nodeString);
			filterStatements.clear();
			filterStatements.addAll(actualFilterStatements);
			actualFilterStatements.clear();
			return;
		} else {
			Iterator<Relationship> it = node.getRelationship().iterator();
			while (it.hasNext()){
				
				actualFilterStatements.addAll(filterStatements);
				
				buildRelation(it.next(), nodeString, expertQuery);
			}
			return;
		}	
	}
	
	
	private void buildRelation(Relationship relation, String s, ExpertQuery expertQuery) throws Exception{
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
			solveFilter(relation.getFilterAttributes(), relation.getRelationshipType(), expertQuery);
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
			//dieser Zweig wird nie betreten
			return;
		} else {
			
			buildNode(relation.getNode(), s + relationship, expertQuery);
			return;
		}	
	}
	
	private void solveFilter(Set<FilterAttribute> filterSet, String type, ExpertQuery expertQuery) throws Exception{
		int i = 1;
		
		for (FilterAttribute f : filterSet){
			
			Set<Filters> set = f.getFilters();
			List<Filters> list = new ArrayList<Filters>(set);
			Collections.sort(list);
			
			for (Filters fil: list){	
			
			//hier könnte es eventuell zu einem Problem mit der Parameterbezeichnung kommen!!
			String paramName = type + f.getAttributeName() + i;
			i++;
			
			Parameter newParam = new Parameter();
			newParam.setChangeable(fil.getChangeable());
			newParam.setKey(paramName);
			newParam.setValue(fil.getValue());
			newParam.setType(fil.getType());
//			parameter.add(newParam);
			expertQuery.addParameter(newParam);
			AttributeTypes.testTypes(newParam);
//			paramsMap.put(paramName, newParam.getValue());
			
			String statement = "";
			if (fil.getIsBracketOpen()) statement += "(";
			statement += (synonyms.get(type) + "." + f.getAttributeName() + fil.getFilterType() + "{" + paramName + "}");
			if (fil.getIsBracketClosed()) statement += ")";
			if (!fil.getLogic().isEmpty()) statement += " " + fil.getLogic() + " ";
			filterStatements.add(statement);	
			}	
		}
	}
	
	private void solveOrder(Set<OrderByAttribute> obSet, String type){
		for (OrderByAttribute o : obSet){
			String dir = "";
			if (o.getDirection() != ""){
				dir = " " + o.getDirection();
			}
			//TODO evtl noch eine AGGREGATION bei orderby einfügen!!
			orderStatements.add(synonyms.get(type) + "." + o.getAttributeName() + dir);
		}
	}
	
	private void solveReturn (Set<ReturnAttribute> retSet, String type){
		int i = 1;
		//TODO Liste filtern ReturnAttribute
		for (ReturnAttribute r : retSet){
			
			String returnStatement = " ";
//			if (distinct) returnStatement += ("DISTINCT ");
			if (!r.getAggregation().isEmpty()){
				if (r.getAggregation().equalsIgnoreCase("none") == false){
					returnStatement += (r.getAggregation() + "(");
				}
			}
			returnStatement += (synonyms.get(type) + "." + r.getAttributeName());
			if (!r.getAggregation().isEmpty()){
				if (r.getAggregation().equalsIgnoreCase("none") == false){
					returnStatement += ")";
				}
			}
			//TODO eventuell eigenes Attribut für die ALIAS
			
			returnStatement += (" AS " + type + r.getAttributeName() + i);
			i++;
			returnStatements.add(returnStatement);
		}
	}
		

}
